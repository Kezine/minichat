package kezine.minichat.work.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import kezine.minichat.data.Client;
import kezine.minichat.data.Message;
import kezine.minichat.data.ServerInfos;
import kezine.minichat.data.Topic;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.BaseThread;

/**
 * Thread qui gère le dialogue initial avec un client : authentification, demande d'infos.
 * Avant le transfert aux threads des topic.(si besoin)
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
                    try
                    {
                        ObjectInputStream ois = new ObjectInputStream(client.getDataInputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(client.getDataOutputStream());
                        Message message = (Message)ois.readObject();
                        if(message.getType().equals(Message.MessageType.CLIENT_LOGIN))
                        {
                            //TODO : Securité =>test longueur de la taille du nom du topic/taille message reçu
                            Topic topic = new Topic(message.getDestination().toString(), null, null, true, 0);
                            message.setMessage("Ok,Dispatched to topic");
                            oos.writeObject(message);
                            _ServerMonitor.dispatchClient(message.getSender(), client, topic);
                            LoggerManager.getMainLogger().info("User("+message.getSender().getUsername()+") dispatched. IP : " + client.getSocket().getInetAddress());
                        }
                        else if(message.getType().equals(Message.MessageType.SERVER_INFO))
                        {
                            ServerInfos si = _ServerMonitor.getServerInfos();
                            message.setMessage(si);
                            oos.writeObject(message);
                            LoggerManager.getMainLogger().info("Server information sent to " + client.getSocket().getInetAddress());
                        }
                        else
                        {
                            LoggerManager.getMainLogger().warning("Invalid message type \""+message.getType()+"\" from " + client.getSocket().getInetAddress());
                        }
                    }
                    catch(ClassNotFoundException ex)
                    {
                        LoggerManager.getMainLogger().warning("Expected Message class, but received unknow data from " + client.getSocket().getInetAddress());
                    }
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
    /**
     * Retourne l'id unique du thread(pour un pool)
     * @return 
     */
    synchronized public final int getPoolId()
    {
        return _PoolId;
    }
}
