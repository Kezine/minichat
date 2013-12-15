package kezine.minichat.work;

import java.io.IOException;
import java.net.Socket;
import kezine.minichat.data.Client;
import kezine.minichat.data.ThreadStatus;
import kezine.minichat.tools.LoggerManager;

/**
 *
 * @author Kezine
 */
public class ServerPoolThread extends BaseThread
{
    private ServerDispatchThread _Sdt;
    private ServerMonitor _ServerMonitor;
    private int _PoolId;
    
    public ServerPoolThread(int poolId, ServerDispatchThread sdt, ServerMonitor serverMonitor)
    {
        super("ServerPoolThread-"+poolId);
        _PoolId = poolId;
        _Sdt = sdt;
        _ServerMonitor = serverMonitor;
    }
    
    /*
     * Pas veroullié en cas de verouillage du Serveur monitor pck
     * si il y a eu un veroullage entre temps le client sera droppé au dispatch
     * ou le thread sera bloqué a la recup de socket
     */
    @Override
    public void run()
    {
        Socket currentSocket;
        while(getStatus().equals(ThreadStatus.RUNNING))
        {
           
           currentSocket = _Sdt.getPendingSocket();
           /*
            * Si il y a eu un timeout (1s)=> on n'a pas de nouveau socket => on test si on dois pas se terminer
            */
           if(currentSocket != null)
           {
               try(Client client = new Client(currentSocket);)
               {
                   client.OpenConnection();
                   //TODO : ajouter la gestion de l'infos / ajout topic
               }
               catch(Exception ex)
               {
                   LoggerManager.getMainLogger().warning("Erreur : " + ex);
               }
           }
        }
        setStatus(ThreadStatus.STOPPED);
        LoggerManager.getMainLogger().info("Thread terminated");
    }
    
    synchronized public final int getPoolId()
    {
        return _PoolId;
    }
}
