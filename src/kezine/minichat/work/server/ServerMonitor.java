package kezine.minichat.work.server;

import kezine.minichat.data.Topic;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import kezine.minichat.Tools;
import kezine.minichat.data.Client;
import kezine.minichat.data.Message;
import kezine.minichat.data.ServerInfos;
import kezine.minichat.data.User;
import kezine.minichat.events.ChatEvent;
import kezine.minichat.events.ChatEventListener;
import kezine.minichat.events.ServerEventListener;
import kezine.minichat.events.ThreadEventListener;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.BaseThread;

/**
 * Classe gérant l'architecture/synchronisation/construction/création
 * des threads "Serveur". Elle joue aussi le role de moniteurs pour certains threads
 * @author Kezine
 */
public final class ServerMonitor implements ChatEventListener
{
    private ServerDispatchThread _ServerDispatchThread;
    private ArrayList<TopicThread> _TopicThreads;
    
    private Thread _ServerStatusThreadPooler;
        
    private int _MaxClient;
    private int _ListeningPort;
    private String _ServerName;
    private boolean _ServerLocked;
    private HashMap<User,TopicThread> _Users;
    private boolean _AllowAnonymous;
    private ServerInfos _ServerInfos;
    private EventListenerList _Listeners;
    
    public ServerMonitor()
    {
        this(81,20,"Default server name",true);
    }
    public ServerMonitor(int listeningPort, int maxClient, String serverName,boolean allowAnonymous)
    {
        setMaxClient(maxClient);
        _ListeningPort = listeningPort;
        setServerName(serverName);
        _Users = new HashMap<>(_MaxClient);
        _AllowAnonymous = allowAnonymous;
        _Listeners = new EventListenerList();
        _ServerInfos = null;
        _ServerDispatchThread = null;
    }
    
    /**
     * Initialise les topics a partir de ceux en mémoire (Génere en plus un "pardefaut")
     * Lance les threads dedié à la gestion de ceux-ci
     */
    synchronized private void initTopics()
    {
        _TopicThreads = new ArrayList<>(20);
        _TopicThreads.add(new TopicThread(new Topic(), this));
        for(TopicThread topicT : _TopicThreads)
        {
            topicT.addChatEventListener(this);
            topicT.start();
        }
    }
    synchronized public boolean addTopic(Topic topic)
    {
        for(TopicThread tt : _TopicThreads)
        {
            if(tt.getTopicInfo().equals(topic))
                return false;
        }
        TopicThread tt = new TopicThread(topic, this);
        _TopicThreads.add(tt);
        tt.addChatEventListener(this);
        tt.start();
        return true;
    }
    synchronized public void sendMessage(Message message)
    {
        if(message.getType().equals(Message.MessageType.SERVER_BROADCAST))
        {
            
        }
    }
    /**
     * Ejecte un utilisateur du serveur
     * @param user L'utilisateur à éjecter
     * @param message Message à l'intention de l'utilisateur
     * @return true si l'utilisateur à été correctement éjecté, false en cas d'echec (Utilisateur deconnecté dans l'intervale)
     */
    synchronized public boolean kickUser(User user, String message)
    {
        
        return false;
    }
    /*
     * Verouille temporairement le serveur et donne l'ordre au topic 
     * de se débarasser des connections en cours
     */
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
    /**
     * Relance le serveur
     */
    synchronized public void restartServer()
    {
        closeAllConnections("Server is restarting ...");
        
    }
    /**
     * Lance le serveur
     * @throws IOException Si il y a une erreur lors de la création du {@link ServerSocket}
     */
    synchronized public void startServer() throws IOException
    {
        fireServerStateChanged(BaseThread.ThreadStatus.INITED);
        initTopics();
         
        _ServerDispatchThread = new ServerDispatchThread(this, new ServerSocket(getListeningPort()), (getMaxClient()/2)+1);       
        _ServerDispatchThread.start();
        
        fireServerStateChanged(BaseThread.ThreadStatus.RUNNING);
        
         _ServerStatusThreadPooler = new Thread(new StatusPooler());
        _ServerStatusThreadPooler.start();    
        LoggerManager.getMainLogger().info("Server listenig on " + _ServerDispatchThread.getInetAddress()+":"+_ServerDispatchThread.getListeningPort());
    }
    /**
     * Arrete le serveur
     * @param message Raison de l'arret du serveur
     */
    synchronized public void stopServer(String message)
    {
        
        if(_ServerDispatchThread == null)
            throw new Error("Server is not started");
        if(message == null)
            throw new IllegalArgumentException("Stop Message can be void, but cannot be null");
        fireServerStateChanged(BaseThread.ThreadStatus.STOPPING);
        for(TopicThread topicT : _TopicThreads)
        {            
            topicT.stopThread();
        }
        _ServerDispatchThread.stopThread();
    }
    
    /**
     * @return Le nombre maximum de client actif
     */
    synchronized public int getMaxClient() 
    {
        return _MaxClient;
    }
    /**
     * Modifie le nombre maximum de client actif
     * @param maxClient nombre maximum de client actif (>0)
     */
    synchronized public final void setMaxClient(int maxClient) 
    {
        _MaxClient = maxClient;
    }
    /**
     * 
     * @return Le port d'ecoute du serveur
     */
    synchronized public int getListeningPort() 
    {
        return _ListeningPort;
    }
    
    /**
     * 
     * @return True si le serveur est verouillé
     */
    synchronized public boolean isServerLocked()
    {
        return _ServerLocked;
    }
    /**
     * Modifie le verouillage du serveur et debloque les threads en attente du deverouillage(si verouillé)
     * @param isServerLocked True pour verouiller le serveur
     */
    synchronized public void setServerLocked(boolean isServerLocked)
    {
        _ServerLocked = isServerLocked;
        
        if(!_ServerLocked)
        {
            notifyAll();
            LoggerManager.getMainLogger().info("Server is Unlocked");
        }
        else
            LoggerManager.getMainLogger().info("Server is locked");
    }
    /**
     * 
     * @return Le nom du serveur
     */
    synchronized public String getServerName()
    {
        return _ServerName;
    }
    /**
     * Modifie le nom du serveur
     * @param name Le nom du serveur
     */
    synchronized public void setServerName(String name)
    {
        if(name != null)
            if(name.length() <= 30)
                _ServerName = name;
            else
                throw new IllegalArgumentException("Name too long, max length is 30");
        else
            _ServerName = "UnNamed";
    }
    
    /**
     * 
     * @return True si le serveur accepte les utilisateurs anonymes
     */
    synchronized public boolean isAllowAnonymous() 
    {
        return _AllowAnonymous;
    }
    /**
     * 
     * Specifie si le serveur accepte les utilisateurs anonymes
     * @param allowAnonymous True si il accepte les utilisateurs anonymes
     */
    public void setAllowAnonymous(boolean allowAnonymous) 
    {
        _AllowAnonymous = allowAnonymous;
    }
    
    /**
     * Transfère un socket client au topic passé en paramètre.
     * Si le serveur est verouillé, cette méthode ferme la connection avec le client.
     * @param user Les information de l'utilisateur authentifié
     * @param client Le socket du client
     * @param topic Le topic(Utilisation uniquement du nom) qui va gérer ce client
     */
    synchronized public void dispatchClient(User user, Client client, Topic topic)
    {
        if(isServerLocked())
           try{client.close();}catch(Exception ex){}
        else
        {
            TopicThread tpd = null;
            Topic topicInfos = null;
            short status = 0;
            for(TopicThread tp : _TopicThreads)
            {
                if(tp.getTopicInfo().getName().equals("Default topic"))
                {
                    tpd = tp;
                    topicInfos = tpd.getTopicInfo();
                }
                if(tp.getTopicInfo().getName().equals(topic.getName()))
                {
                    if(tp.addUser(user, client))
                    {
                        status = 1;
                        topicInfos = topic;
                    }
                    else
                        status = -1;
                    break;
                    
                }
            }
            if(status == 0)
            {
                if(!tpd.addUser(user, client))
                    status = -1;
            }
            if(status == -1)
            {
                LoggerManager.getMainLogger().warning("Attempt to add existing user("+user.getUsername()+") into the topic \""+ topicInfos.getName() + "\"");
                try {client.close();} catch (Exception ex) {}
            }
            else
            {
                LoggerManager.getMainLogger().info("User("+user.getUsername()+") Dispatched to topic \""+ tpd.getName() +"\" IP :" + client.getSocket().getInetAddress());

            }
        }
    }
    /**
     * Verouille le thread qui appelle cette fonction.
     */
    synchronized public void tryLock()
    {
        try 
        {
            LoggerManager.getMainLogger().config("Locking ...");
            wait();
            //wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
         LoggerManager.getMainLogger().config("Unlocked!");
    }
    /**
     * Recupère les information serveurs et les met en "Cache"
     */
    synchronized private void retrieveServerInfo()
    {
        ArrayList<Topic> topics = new ArrayList<>();        
        HashMap<User,String> users = new HashMap<>();
        for(TopicThread topicT : _TopicThreads)
        {
            topics.add(topicT.getTopicInfo());
            for(User user : topicT.getUsers())
            {
                users.put(user, topicT.getTopicInfo().getName());
            }
        }
        _ServerInfos = (ServerInfos)Tools.copy(new ServerInfos(topics,users, getServerName()));
    }
    /**
     * Retourne les informations serveurs en cache. Si le cache st invalidé, récupère les nouvelles informations.
     * @return Les informations sur le serveur
     */
    public ServerInfos getServerInfos()
    {
        if(_ServerInfos == null)
            retrieveServerInfo();
        return _ServerInfos;
    }
    public void addServerEventListener(ServerEventListener listener)
    {
        _Listeners.add(ServerEventListener.class,listener);
    }
    public void removeServerEventListener(ServerEventListener listener)
    {
        _Listeners.remove(ServerEventListener.class, listener);
    }
    public void fireServerStateChanged(BaseThread.ThreadStatus status)
    {
        for(ServerEventListener listener : _Listeners.getListeners(ServerEventListener.class))
        {
            listener.ServerStateChanged(status);
        }
    }
    public void fireServerDataChanged()
    {
        for(ServerEventListener listener : _Listeners.getListeners(ServerEventListener.class))
        {
            listener.ServerDataChanged();
        }
    }
    public void clearListeners()
    {
        _Listeners = new EventListenerList();
    }

    @Override
    public void processChatEvent(ChatEvent event) 
    {
        LoggerManager.getMainLogger().info("["+event.getType()+"] Username : "+((User)event.getComplement()).getUsername());
    }

        
    private class StatusPooler implements Runnable
    {

        @Override
        public void run() 
        {
            boolean isAllClosed;
            do
            {           
                isAllClosed = true;
                for(TopicThread tt : _TopicThreads)
                {
                    if(!tt.getStatus().equals(BaseThread.ThreadStatus.STOPPED) && !tt.getStatus().equals(BaseThread.ThreadStatus.STOPPED_WITH_ERROR))
                    {
                        isAllClosed = false;
                        break;
                    }             

                }
                if(isAllClosed && (!_ServerDispatchThread.getStatus().equals(BaseThread.ThreadStatus.STOPPED) && !_ServerDispatchThread.getStatus().equals(BaseThread.ThreadStatus.STOPPED_WITH_ERROR)))
                    isAllClosed = false;
                try {Thread.sleep(100);} catch (InterruptedException ex) {} 
                
            }while(!isAllClosed);
            _ServerDispatchThread = null;
            fireServerStateChanged(BaseThread.ThreadStatus.STOPPED);
            LoggerManager.getMainLogger().info("Server Closed");            
        }
        
    }
}
