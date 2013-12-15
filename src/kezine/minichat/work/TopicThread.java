package kezine.minichat.work;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import kezine.minichat.data.Client;
import kezine.minichat.data.Message;
import kezine.minichat.data.ThreadStatus;
import kezine.minichat.data.Topic;
import kezine.minichat.data.User;
import kezine.minichat.tools.LoggerManager;

/**
 *
 * @author Kezine
 */
public class TopicThread extends BaseThread
{
    private Topic _Topic;
    private HashMap<User, Client> _Users;
    private ServerMonitor _ServerMonitor;
    public TopicThread(Topic topic,ServerMonitor serverMonitor)
    {
        super("TopicThread-"+topic.getName());
        _Topic = topic;
        _Users = new HashMap<>();
        _ServerMonitor = serverMonitor;
    }
    
    @Override
    public void run()
    {
        while(getStatus().equals(ThreadStatus.RUNNING))
        {
            if(_ServerMonitor.isServerLocked())
                _ServerMonitor.tryLock();
            else
            {
                
            }
        }
        LoggerManager.getMainLogger().info("Thread terminated");
    }
    
    public synchronized void closeAllConnections(String message)
    {
        for(User user : _Users.keySet())
        {
            try(Client temp = _Users.get(user))
            {
                temp.OpenConnection();
                ObjectOutputStream oos = new ObjectOutputStream(temp.getDataOutputStream());
                oos.writeObject(new Message(Message.MessageType.SERVER_INFO, message));
                oos.flush();
                oos.close();
                temp.close();
            }
            catch(Exception ex)
            {
                LoggerManager.getMainLogger().warning("Closing all : " + ex);
            }
        }
        _Users.clear();
    }
    
    synchronized public Set<User> getUsers()
    {
        return _Users.keySet();
    }
    
    synchronized public boolean addUser(User user,Client client)
    {
        if(_Users.containsKey(user))
            return false;
        _Users.put(user,client);
        return true;
    }
    
    synchronized public boolean removeUser(User user)
    {
        if(!_Users.containsKey(user))
            return false;
        _Users.remove(user);
        return true;
    }
    
    synchronized public int getUsersCount()
    {
        return _Users.size();
    }
    
    
}
