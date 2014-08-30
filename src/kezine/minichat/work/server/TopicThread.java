package kezine.minichat.work.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import kezine.minichat.data.Client;
import kezine.minichat.data.Message;
import kezine.minichat.data.Topic;
import kezine.minichat.data.User;
import kezine.minichat.events.ChatEvent;
import kezine.minichat.events.ChatEventListener;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.BaseThread;

/**
 * Gère le dialogue avec les utilisateurs du topic. Les utilisateurs son déja authentifié.
 * Le thread transmet les messages entre utilisateur, et entre serveur <=> utilisateurs.
 * @author Kezine
 */
public class TopicThread extends BaseThread
{
    private Topic _Topic;
    private HashMap<User, Client> _Users;
    private ServerMonitor _ServerMonitor;
    private EventListenerList _Listeners;
    private LinkedList<Message> _PendingMessages;
        
    public TopicThread(Topic topic,ServerMonitor serverMonitor)
    {
        super("TopicThread-"+topic.getName());
        _Topic = topic;
        _Users = new HashMap<>();
        _ServerMonitor = serverMonitor;
        _Listeners = new EventListenerList();
        _PendingMessages = new LinkedList<>();
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
                /**
                 * Permet de stocker la liste des clients qui se sont deconnecté.
                 * Détecté lors d'une tentative d'ecriture sur le socket. Reparcouru a la fin four la fermeture propre des sockets
                 */
                HashMap<User, Client> _InvalidClient = new HashMap();
                ObjectInputStream ois = null;
                ObjectOutputStream oos = null;
                Client client;
                //TODO : zone non synchronisée
                for(User user : _Users.keySet())
                {
                    /*
                     * Ne pas traiter les client eventuellement invalidé par un envoit de message
                     * invalidé => deconnecté
                     */
                    if(!_InvalidClient.containsKey(user))
                    {
                        client = _Users.get(user);
                        try 
                        {
                            if(client.getDataInputStream().available() > 0)
                            {
                                ois = new ObjectInputStream(client.getDataInputStream());
                                try 
                                {
                                    Message message = (Message)ois.readObject();
                                    Message.MessageType type = message.getType();
                                    if(type.equals(Message.MessageType.CHAT_MESSAGE))
                                    {
                                        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, message.getMessage().toString(), user));
                                        /*
                                         * Broadcast du message à tout les membres du topic, sauf les déconnecté
                                         * Et l'envoyeur évidement
                                         */
                                        for(User luser : _Users.keySet())
                                        {
                                            if(!_InvalidClient.containsKey(user) && !luser.equals(user))
                                            {
                                                oos = new ObjectOutputStream(_Users.get(luser).getDataOutputStream());
                                                try{oos.writeObject(new Message(Message.MessageType.CHAT_MESSAGE, new Object[]{luser,message.getMessage()}));}
                                                catch(IOException ex)
                                                {
                                                    _InvalidClient.put(luser,_Users.get(luser));
                                                }
                                            }
                                        }
                                    }
                                    else if(type.equals(Message.MessageType.PRIVATE_MESSAGE))
                                    {
                                         fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, message.getMessage().toString(), user));
                                         try
                                         {
                                             User destination = (User)message.getDestination();
                                             Client cdestination = _Users.get(user);
                                             if(cdestination != null)
                                             {
                                                oos = new ObjectOutputStream(cdestination.getDataOutputStream());
                                                oos.writeObject(message);
                                             }
                                             else
                                             {
                                                LoggerManager.getMainLogger().warning("Invalid message destination :"+destination.getUsername() + "(user not found) \""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                              }
                                         }
                                         catch(ClassCastException ex)
                                         {
                                             LoggerManager.getMainLogger().warning("Invalid message destination content\""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");

                                         }
                                    }
                                    else if (type.equals(Message.MessageType.SERVER_INFO))
                                    {
                                        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, "User requested server Info (IP:"+client.getSocket().getInetAddress()+")", user));
                                        oos = new ObjectOutputStream(client.getDataOutputStream());
                                        oos.writeObject(new Message(Message.MessageType.SERVER_INFO, _ServerMonitor.getServerInfos()));
                                    }
                                    else
                                    {
                                        LoggerManager.getMainLogger().warning("Invalid message type \""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                    }
                                } 
                                catch (ClassNotFoundException ex) 
                                {
                                    LoggerManager.getMainLogger().warning("Expected Message class, but received unknow data from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                }
                            }
                        } 
                        catch (IOException ex) 
                        {
                            LoggerManager.getMainLogger().info(ex.toString());
                            _InvalidClient.put(user, client);
                        }
                        //On ne ferme pas pck il ne faut pas fermer le flux originel
                        /*try
                        {
                            if(oos != null)
                            {
                                oos.close();
                                oos = null;
                            }
                        }
                        catch(Exception ex){}
                        try
                        {
                            if(ois != null)
                            {
                                ois.close();
                                ois = null;
                            }
                        }
                        catch(Exception ex){}*/
                    }
                }
                for(Message message : _PendingMessages)
                {
                    Message.MessageType type = message.getType();
                    
                    if(type.equals(Message.MessageType.SERVER_BROADCAST) || type.equals(Message.MessageType.CHAT_MESSAGE))
                    {
                        for(User user : _Users.keySet())
                        {
                            /*
                             * Ne pas traiter les client eventuellement invalidé par un envoit de message
                             * invalidé => deconnecté
                             */
                            if(!_InvalidClient.containsKey(user))
                            {
                                client = _Users.get(user);
                                try 
                                {
                                    oos = new ObjectOutputStream(_Users.get(user).getDataOutputStream());
                                    oos.writeObject(message);
                                } 
                                catch (IOException ex) 
                                {
                                     LoggerManager.getMainLogger().info(ex.toString());
                                     _InvalidClient.put(user, client);
                                }
                            }
                        }
                    }
                    else if(type.equals(Message.MessageType.PRIVATE_MESSAGE))
                    {
                        for(User user : _Users.keySet())
                        {
                            if(((User)message.getDestination()).equals(user))
                            {
                                /*
                                 * Ne pas traiter les client eventuellement invalidé par un envoit de message
                                 * invalidé => deconnecté
                                 */
                                if(!_InvalidClient.containsKey(user))
                                {
                                    client = _Users.get(user);
                                    try 
                                    {
                                        oos = new ObjectOutputStream(_Users.get(user).getDataOutputStream());
                                        oos.writeObject(message);
                                    } 
                                    catch (IOException ex) 
                                    {
                                         LoggerManager.getMainLogger().info(ex.toString());
                                         _InvalidClient.put(user, client);
                                    }
                                }
                                else
                                {
                                    LoggerManager.getMainLogger().warning("Server tried to send a private message to Invalidate User");
                                }
                            }
                        }
                    }
                        
                }
                for(User user : _InvalidClient.keySet())
                {
                    _Users.remove(user);
                    fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.DISCONNECTION, null, user));
                    try {_InvalidClient.get(user).close();} catch (Exception ex) {Logger.getLogger(TopicThread.class.getName()).log(Level.SEVERE, null, ex);}
                }
            }
        }        
        closeAllConnections("Server Closed");
        setStatus(ThreadStatus.STOPPED);
        LoggerManager.getMainLogger().info("Thread terminated");
    }
    /**
     * Ferme toutes le connections actives en notifiant le clients.
     * @param message La raison de la fermeture (Transmise au client)
     */
    public synchronized void closeAllConnections(String message)
    {
        for(User user : _Users.keySet())
        {
            try(Client temp = _Users.get(user);)
            {
                
                try(ObjectOutputStream oos = new ObjectOutputStream(temp.getDataOutputStream());)
                {
                    oos.writeObject(new Message(Message.MessageType.SERVER_INFO, message));
                    oos.flush();
                    oos.close();
                }
                temp.close();
            }
            catch(Exception ex)
            {
                LoggerManager.getMainLogger().warning("Closing all : " + ex);
            }
        }
        _Users.clear();
    }
    /**
     * retourne les utilisateurs du topic
     * @return Les utilisateurs du topic
     */
    synchronized public Set<User> getUsers()
    {
        return _Users.keySet();
    }
    /**
     * Ajoute un utilisateur au topic
     * @param user Les informations sur l'utilisateur
     * @param client Les données permetant le dialogue avec cet utilisateur
     * @return False si l'utilisateur est déja présent dans le topic
     */
    synchronized public boolean addUser(User user,Client client)
    {
        if(_Users.containsKey(user))
            return false;
        _Users.put(user,client);
        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.CONNECTION, null, user));
        return true;
    }
    /**
     * Enlève un utilisateur du topic
     * @param user L'utilisateur à enlever du topic
     * @return False si l'utilisateur n'est pas présent dans le topic
     */
    synchronized public boolean removeUser(User user)
    {
        if(!_Users.containsKey(user))
            return false;
        _Users.remove(user);
        return true;
    }
    /**
     * Retourne le nombre d'utilisateur du topic
     * @return le nombre d'utilisateur du topic
     */
    synchronized public int getUsersCount()
    {
        return _Users.size();
    }
    /**
     * Retourne les informations sur le topic
     * @return les informations sur le topic
     */
    synchronized public Topic getTopicInfo()
    {
        return _Topic;
    }
    public void addChatEventListener(ChatEventListener listener)
    {
        _Listeners.add(ChatEventListener.class,listener);
    }
    public void removeChatEventListener(ChatEventListener listener)
    {
        _Listeners.remove(ChatEventListener.class, listener);
    }
    public void fireChatEventOccured(ChatEvent event)
    {
        for(ChatEventListener listener : _Listeners.getListeners(ChatEventListener.class))
        {
            listener.processChatEvent(event);
        }
    }
    public void clearListeners()
    {
        _Listeners = new EventListenerList();
    }
    
}
