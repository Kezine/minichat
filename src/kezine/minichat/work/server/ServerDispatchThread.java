package kezine.minichat.work.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import kezine.minichat.work.BaseThread;
import org.apache.log4j.Level;
/**
 * Thread servant d'entrée pour le serveur de chat. Accepte les connections sur le port "Serveur" et transfert la connection dans fifo du pool de thread.
 * Gère le création/suression de ce mème pool.
 * @author Kezine
 */
public class ServerDispatchThread extends BaseThread
{
    private final ServerSocket _ServerSocket;
    private final ServerMonitor _ServerMonitor;
    private final LinkedList<Socket> _PendingClients;
    private final ArrayList<ServerPoolThread> _PoolThreads;
    private final int _PoolSize;
    
    public ServerDispatchThread(ServerMonitor server,ServerSocket serverSocket,int poolAcceptSize)
    {
        super("ServerDispatchThread");
        _ServerMonitor = server;
        _ServerSocket = serverSocket;
        _PendingClients = new LinkedList<>();
        _PoolSize = poolAcceptSize;
        _PoolThreads = new ArrayList<>(_PoolSize);
    }
    public int getListeningPort()
    {
        return _ServerSocket.getLocalPort();
    }
    public String getInetAddress()
    {
        return _ServerSocket.getInetAddress().toString();
    }
    @Override
    public void run() 
    {
        if(_ServerSocket.isBound())
        {
            generatePool(_PoolSize);
            while(getStatus().equals(ThreadStatus.RUNNING))
            {
                try
                {
                    String address = null;
                    //Met un timeout a l'accept, pour pouvoir tester l'etat du thread.
                    _ServerSocket.setSoTimeout(1000);
                    Socket temp = _ServerSocket.accept();
                    synchronized(this)
                    {
                        if(_ServerMonitor.isServerLocked())
                        {
                            try
                            {
                                temp.close();
                            }catch(Exception ex){}
                           _ServerMonitor.tryLock();
                        }
                        else
                        {
                            _PendingClients.add(temp);
                            address = temp.getInetAddress().toString();
                            this.notify();
                        }
                        
                    }
                    if(address != null)
                       getLogger().info("New client : " + address + " added in pending list");
                
                }
                catch(SocketTimeoutException ex)
                {
                    /*Rien a faire, se produit lors du timeout du accept.
                    * Permet de tester l'etat du thread => eventuellement le stopper
                    */
                }
                catch(IOException ex)
                {
                    setErrorMessage("Erreur while acception new client : " + ex);
                }
            }
            closePool();
            try 
            {
                _ServerSocket.close();
                setStatus(ThreadStatus.STOPPED);
            } 
            catch (IOException ex) 
            {
                setErrorMessage("Erreur while closing ServerSocket : " + ex);
                setStatus(ThreadStatus.STOPPED_WITH_ERROR);
            }
        }
        else
        {
            setErrorMessage("ServerSocket is not bound !",Level.FATAL);
            setStatus(ThreadStatus.STOPPED_WITH_ERROR);
        }
       getLogger().info("Thread terminated");
    }
    /**
     * Arrète les threads du pool
     */
    private void closePool()
    {
       getLogger().info("Closing thread pool");
        for(ServerPoolThread thread : _PoolThreads)
        {
            thread.stopThread();
        }
       getLogger().info("Closing thread : Waiting ...");
        for(ServerPoolThread thread : _PoolThreads)
        {
            while(thread.getStatus() != ThreadStatus.STOPPED && thread.getStatus() != ThreadStatus.STOPPED_WITH_ERROR)
            {
                try {sleep(10);} catch (InterruptedException ex) {}//Attente de l'arret du pool
            }
        }
       getLogger().info("Thread pool closed");
    }
    /**
     * Vide la fifo du pool de threads
     */
    synchronized public void clearPendingSocket()
    {
        while(_PendingClients.size() !=0)
        {
            try 
            {
                _PendingClients.removeFirst().close();
            } catch (IOException ex) {}
        }
    }
    /**
     * Génère le pool de threads
     * @param poolSize Nombre de threads dans le pool
     */
    private void generatePool(int poolSize)
    {
       getLogger().info("Generating thread pool");
        for(int i = 0; i < poolSize; i++)
        {
            ServerPoolThread temp = new ServerPoolThread(i,this,_ServerMonitor);
            _PoolThreads.add(temp);
            temp.start();
        }
    }
    /**
     * Récupère le premier socket de la fifo du pool, ou null si vide
     * @return Un socket en attente.
     */
    public synchronized Socket getPendingSocket() 
    {
        try 
        {
            wait(1000);
            if(_PendingClients.size() > 0)
                return  _PendingClients.removeFirst();
            else
            {
                //LoggerManager.getMainLogger().info("Wait timeout");
                return null;
            }
        } 
        catch (InterruptedException ex) 
        {
            //LoggerManager.getMainLogger().info("Wait of Thread interrupted");
            return null;
        }
    }
    /**
     * @return La taille du pool de threads
     */
    public int getPoolSize()
    {
        return _PoolSize;
    }
 }