package kezine.minichat.work;

import kezine.minichat.data.Topic;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kezine.minichat.Tools;
import kezine.minichat.data.Client;
import kezine.minichat.data.ServerInfos;
import kezine.minichat.data.User;
import kezine.minichat.tools.LoggerManager;

/**
 *
 * @author Kezine
 */
public final class ServerMonitor 
{
    private int _MaxClient;
    private int _ListeningPort;
    private String _ServerName;
    private ServerDispatchThread _ServerDispatchThread;
    private ArrayList<TopicThread> _TopicThreads;
    private boolean _ServerLocked;
    private HashMap<User,TopicThread> _Users;
    private boolean _AllowAnonymous;
    private ServerInfos _ServerInfos;
    public ServerMonitor()
    {
        this(81,20,"Default server name",true);
    }
    public ServerMonitor(int listeningPort, int maxClient, String serverName,boolean allowAnonymous)
    {
        setMaxClient(maxClient);
        setListeningPort(listeningPort);
        setServerName(serverName);
        _Users = new HashMap<>(_MaxClient);
        _AllowAnonymous = allowAnonymous;
    }
    
    synchronized private void initTopics()
    {
        _TopicThreads = new ArrayList<TopicThread>(20);
        _TopicThreads.add(new TopicThread(new Topic(), this));
        for(TopicThread topicT : _TopicThreads)
        {
            topicT.start();
        }
    }
    
    synchronized public void closeAllConnections(String message)
    {
        setServerLocked(true);
        for(TopicThread topic : _TopicThreads)
        {
            topic.closeAllConnections(message);
        }
        _ServerDispatchThread.clearPendingSocket();
        setServerLocked(false);
    }
    
    synchronized public int getOnlineClientsCounts()
    {
        return 0;
    }
    
    synchronized public void restartServer()
    {
        closeAllConnections("Server is restarting ...");
        
    }
    synchronized public void startServer() throws IOException
    {
        _ServerDispatchThread = new ServerDispatchThread(this, new ServerSocket(getListeningPort()), (getMaxClient()/2)+1);
        initTopics();
        _ServerDispatchThread.start();
    }
    
    synchronized public void stopServer(String message)
    {
        for(TopicThread topicT : _TopicThreads)
        {
            topicT.stopThread();
        }
        _ServerDispatchThread.stopThread();
        LoggerManager.getMainLogger().info("Closing application");
        System.exit(0);
    }

    synchronized public int getMaxClient() 
    {
        return _MaxClient;
    }

    synchronized public final void setMaxClient(int maxClient) 
    {
        _MaxClient = maxClient;
    }

    synchronized public int getListeningPort() 
    {
        return _ListeningPort;
    }

    synchronized public final void setListeningPort(int listeningPort) 
    {
        _ListeningPort = listeningPort;
    }
    synchronized public boolean isServerLocked()
    {
        return _ServerLocked;
    }
    synchronized public void setServerLocked(boolean isServerLocked)
    {
        _ServerLocked = isServerLocked;
        if(!_ServerLocked)
            notifyAll();
    }
    synchronized public String getServerName()
    {
        return _ServerName;
    }
    synchronized public void setServerName(String name)
    {
        _ServerName = name;
    }
    
    synchronized public boolean isAllowAnonymous() 
    {
        return _AllowAnonymous;
    }

    synchronized public void setAllowAnonymous(boolean allowAnonymous) 
    {
        _AllowAnonymous = allowAnonymous;
    }
    
    synchronized public void dispatchClient(User user, Client client, Topic topic)
    {
        if(isServerLocked())
           try{client.close();}catch(Exception ex){}
        else
        {
            //TODO : ajouter le nouveau clien au topic
        }
    }
    synchronized public void tryLock()
    {
        try 
        {
            LoggerManager.getMainLogger().config("Locking ...");
            wait();
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
         LoggerManager.getMainLogger().config("Unlocked!");
    }
    synchronized private void retrieveServerInfo()
    {
        HashMap<User,Topic> users = new HashMap<>();
        for(TopicThread topicT : _TopicThreads)
        {
            for(User user : topicT.getUsers())
            {
                users.put(user, topicT.getTopicInfo());
            }
        }
        _ServerInfos = (ServerInfos)Tools.copy(new ServerInfos(users, getServerName()));
    }
    synchronized public ServerInfos getServerInfos()
    {
        if(_ServerInfos == null)
            retrieveServerInfo();
        return _ServerInfos;
    }
    
}
