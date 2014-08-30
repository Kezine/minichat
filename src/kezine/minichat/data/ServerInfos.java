package kezine.minichat.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gère les informations sur le serveur.
 * @author Kezine
 */
public class ServerInfos implements Serializable
{
    public ArrayList<Topic> _Topics;
    public HashMap<User,String> _Users;
    public String _ServerName;
    
    public ServerInfos(ArrayList<Topic> topics,HashMap<User,String> users, String serverName)
    {
        _Topics = topics;
        _Users = users;
        _ServerName = serverName;
    }
}
