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
/**
 * Classe gérant l'architecture/synchronisation/construction/création
 * des threads "Serveur". Elle joue aussi le role de moniteurs pour certains threads
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
        _ListeningPort = listeningPort;
        setServerName(serverName);
        _Users = new HashMap<>(_MaxClient);
        _AllowAnonymous = allowAnonymous;
    }
    
    /**
     * Initialise les topics a partir de ceux en mémoire (Génere en plus un "pardefaut")
     * Lance les threads dedié à la gestion de ceux-ci
     */
    synchronized private void initTopics()
    {
        _TopicThreads = new ArrayList<TopicThread>(20);
        _TopicThreads.add(new TopicThread(new Topic(), this));
        for(TopicThread topicT : _TopicThreads)
        {
            topicT.start();
        }
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
        _ServerDispatchThread = new ServerDispatchThread(this, new ServerSocket(getListeningPort()), (getMaxClient()/2)+1);
        initTopics();
        _ServerDispatchThread.start();
    }
    /**
     * Arrete le serveur
     * @param message Raison de l'arret du serveur
     */
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
            notifyAll();
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
    synchronized public void setAllowAnonymous(boolean allowAnonymous) 
    {
        _AllowAnonymous = allowAnonymous;
    }
    
    /**
     * 
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
            //TODO : ajouter le nouveau clien au topic
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
            wait();
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
    /**
     * Retourne les informations serveurs en cache. Si le cache st invalidé, récupère les nouvelles informations.
     * @return Les informations sur le serveur
     */
    synchronized public ServerInfos getServerInfos()
    {
        if(_ServerInfos == null)
            retrieveServerInfo();
        return _ServerInfos;
    }
    
}
