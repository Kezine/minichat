package kezine.minichat.work;

import kezine.minichat.data.Topic;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import kezine.minichat.data.User;

/**
 *
 * @author Kezine
 */
public final class Server 
{
    private int _MaxClient;
    private int _ListeningPort;
    private String _ServerName;
    private ServerDispatchThread _ServerDispatchThread;
    private ArrayList<TopicThread> _TopicThreads;
    private boolean _ServerLocked;
    private HashMap<User,ServerClientThread> _Users;
    
    public Server()
    {
        this(81,20,"Default server name");
    }
    public Server(int listeningPort, int maxClient, String serverName)
    {
        setMaxClient(maxClient);
        setListeningPort(listeningPort);
        setServerName(serverName);
        initTopics();
    }
    
    synchronized private void initTopics()
    {
        _TopicThreads = new ArrayList<TopicThread>(20);
        //_TopicThreads.add(new _TopicThreads);
    }
    
    synchronized public void closeAllConnections(String message)
    {
        ServerClientThread ctTemp = null;
        setServerLocked(true);
        for(User user : _Users.keySet())
        {
            ctTemp = _Users.get(user);
            ctTemp.closeConnection(message);
            _Users.remove(user);
        }
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
        _ServerDispatchThread = new ServerDispatchThread(this,new ServerSocket(getListeningPort(),getMaxClient()+2));
        _ServerDispatchThread.start();
    }
    
    synchronized public void stopServer(String message)
    {
        
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
    }
    synchronized public String getServerName()
    {
        return _ServerName;
    }
    synchronized public void setServerName(String name)
    {
        _ServerName = name;
    }
}
