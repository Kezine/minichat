package kezine.minichat.work.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.event.EventListenerList;
import kezine.minichat.data.Client;
import kezine.minichat.data.Message;
import kezine.minichat.data.Topic;
import kezine.minichat.data.User;
import kezine.minichat.events.ChatEvent;
import kezine.minichat.events.ChatEventListener;
import kezine.minichat.work.BaseThread;

/**
 * Gère le dialogue avec les utilisateurs du topic. Les utilisateurs son déja authentifié.
 * Le thread transmet les messages entre utilisateur, et entre serveur <=> utilisateurs.
 * @author Kezine
 */
public class TopicThread extends BaseThread
{
    private final Topic _Topic;
    private final ConcurrentHashMap<User, Client> _Users;
    private final ServerMonitor _ServerMonitor;
    private final LinkedList<Message> _PendingMessages;
        
    public TopicThread(Topic topic,ServerMonitor serverMonitor)
    {
        super("TopicThread-"+topic.getName());
        _Topic = topic;
        _Users = new ConcurrentHashMap<>();
        _ServerMonitor = serverMonitor;
        _PendingMessages = new LinkedList<>();
    }
    @Override
    public void run()
    {
        while(getStatus().equals(ThreadStatus.RUNNING))
        {
            /*if(_ServerMonitor.isServerLocked())//Fonctionne, mais inutile
                _ServerMonitor.tryLock();
            else
            {*/
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
                            if(client.inputAvailable() > 0)
                            {
                                ois = client.getObjectInputStream();
                                try 
                                {
                                    Message message = (Message)ois.readObject();
                                    Message.MessageType type = message.getType();
                                    if(type == Message.MessageType.CHAT_MESSAGE)
                                    {
                                        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, message.getMessage().toString(), user));
                                        /*
                                         * Broadcast du message à tout les membres du topic, sauf les déconnecté
                                         * Et l'envoyeur évidement
                                         */
                                        for(User luser : _Users.keySet())
                                        {
                                            if(!_InvalidClient.containsKey(user))// && !luser.equals(user))
                                            {
                                                oos = _Users.get(luser).getObjectOutputStream();
                                                System.out.println("Envois");
                                                try
                                                {
                                                    oos.writeObject(new Message(type, message.getMessage(), luser, new User()));
                                                }
                                                catch(IOException ex)
                                                {
                                                    _InvalidClient.put(luser,_Users.get(luser));
                                                    System.out.println("invalid");
                                                }
                                            }
                                        }
                                    }
                                    else if(type == Message.MessageType.PRIVATE_MESSAGE)
                                    {
                                        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, message.getMessage().toString(), user));
                                        try
                                        {
                                            User destination = (User)message.getDestination();
                                            Client cdestination = _Users.get(user);
                                            if(cdestination != null)
                                            {
                                               oos = cdestination.getObjectOutputStream();
                                               oos.writeObject(message);
                                            }
                                            else
                                            {
                                               getLogger().warn("Invalid message destination :"+destination.getUsername() + "(user not found) \""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                            }
                                        }
                                        catch(ClassCastException ex)
                                        {
                                           getLogger().warn("Invalid message destination content\""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                        }
                                    }
                                    else if (type == Message.MessageType.SERVER_INFO)
                                    {
                                        fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, "User requested server Info (IP:"+client.getSocket().getInetAddress()+")", user));
                                        oos = client.getObjectOutputStream();
                                        oos.writeObject(new Message(Message.MessageType.SERVER_INFO, _ServerMonitor.getServerInfos()));
                                    }
                                    else
                                    {
                                        getLogger().warn("Invalid message type \""+type+"\" from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                    }
                                } 
                                catch (ClassNotFoundException ex) 
                                {
                                    getLogger().warn("Expected Message class, but received unknow data from " + client.getSocket().getInetAddress() + "("+user.getUsername()+")");
                                }
                            }
                        } 
                        catch (IOException ex) 
                        {
                            getLogger().info(ex.toString());
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
                                    oos = _Users.get(user).getObjectOutputStream();
                                    oos.writeObject(message);
                                } 
                                catch (IOException ex) 
                                {
                                     getLogger().info(ex.toString());
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
                                        oos = _Users.get(user).getObjectOutputStream();
                                        oos.writeObject(message);
                                    } 
                                    catch (IOException ex) 
                                    {
                                        getLogger().info(ex.toString());
                                        _InvalidClient.put(user, client);
                                    }
                                }
                                else
                                {
                                    getLogger().warn("Server tried to send a private message to Invalidate User");
                                }
                            }
                        }
                    }
                        
                }
                for(User user : _InvalidClient.keySet())
                {
                    _Users.remove(user);
                    fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.DISCONNECTION, null, user));
                    try 
                    {
                        _InvalidClient.get(user).close();
                    }
                    catch (Exception ex) 
                    {
                        getLogger().warn("Error while closing clent stream : " + ex.getMessage());
                    }
                }
            //}
        }        
        closeAllConnections("Server Closed");
        setStatus(ThreadStatus.STOPPED);
        getLogger().info("Thread terminated");
    }
    /**
     * Ferme toutes le connections actives en notifiant le clients.
     * @param message La raison de la fermeture (Transmise au client)
     */
    public synchronized void closeAllConnections(String message)
    {
        for(User user : _Users.keySet())
        {
            try
            {
                Client temp = _Users.get(user);
                try(ObjectOutputStream oos = temp.getObjectOutputStream())
                {
                    oos.writeObject(new Message(Message.MessageType.SERVER_INFO, message));
                    oos.flush();
                    oos.close();
                }
                temp.close();
            }
            catch(Exception ex)
            {
                getLogger().warn("Closing all : " + ex);
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
}
