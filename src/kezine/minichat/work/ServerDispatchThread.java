package kezine.minichat.work;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import kezine.minichat.data.ThreadStatus;
import kezine.minichat.tools.LoggerManager;
/**
 *
 * @author Kezine
 */
public class ServerDispatchThread extends BaseThread
{
    private ServerSocket _ServerSocket;
    private ServerMonitor _ServerMonitor;
    private LinkedList<Socket> _PendingClients;
    private ArrayList<ServerPoolThread> _PoolThreads;
       
    public ServerDispatchThread(ServerMonitor server,ServerSocket serverSocket,int poolAcceptSize)
    {
        super("ServerDispatchThread");
        _ServerMonitor = server;
        _ServerSocket = serverSocket;
        _PendingClients = new LinkedList<>();
        _PoolThreads = new ArrayList<>(poolAcceptSize);
    }
    @Override
    public void run() 
    {
        if(_ServerSocket.isBound())
        {
            generatePool(_PoolThreads.size());
            while(getStatus().equals(ThreadStatus.RUNNING))
            {
                try
                {
                    String address = null;
                    synchronized(this)
                    {
                        Socket temp = _ServerSocket.accept();
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
                        LoggerManager.getMainLogger().info("New client : " + address + " added in pending list");
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
            setErrorMessage("ServerSocket is not bound !",Level.SEVERE);
            setStatus(ThreadStatus.STOPPED_WITH_ERROR);
        }
        LoggerManager.getMainLogger().info("Thread terminated");
    }
    private void closePool()
    {
        LoggerManager.getMainLogger().info("Closing thread pool");
        for(ServerPoolThread thread : _PoolThreads)
        {
            thread.stopThread();
        }
    }
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
    private void generatePool(int poolSize)
    {
        LoggerManager.getMainLogger().info("Generating thread pool");
        for(int i = 0; i < poolSize; i++)
        {
            ServerPoolThread temp = new ServerPoolThread(i,this,_ServerMonitor);
            _PoolThreads.add(temp);
            temp.start();
        }
    }
    public synchronized Socket getPendingSocket() 
    {
        try 
        {
            wait(1000);
            return  _PendingClients.removeFirst();
        } 
        catch (InterruptedException ex) 
        {
            LoggerManager.getMainLogger().config("Wait of Thread " + Thread.currentThread().getName() + " interrupted");
            return null;
        }
    }
 }
