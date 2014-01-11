package kezine.minichat.data;

import java.util.HashMap;

/**
 * Gère les informations sur le serveur.
 * @author Kezine
 */
public class ServerInfos 
{
    public HashMap<User,Topic> _Users;
    public String _ServerName;
    
    public ServerInfos(HashMap<User,Topic> users, String serverName)
    {
        _Users = users;
        _ServerName = serverName;
    }
}
