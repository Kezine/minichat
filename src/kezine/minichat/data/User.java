package kezine.minichat.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Kezine
 */
public class User implements Serializable
{
    private String _Username;
    private Color _PreferedColor;
    
    public User()
    {
        this("Anonymous",Color.PINK);
    }
    
    public User(String username, Color preferedColor)
    {
        setUsername(username);
        setPreferedColor(preferedColor);
    }
    
        
    public String getUsername() 
    {
        return _Username;
    }

    public final void setUsername(String username) 
    {
        _Username = username;
    }

    public Color getPreferedColor() 
    {
        return _PreferedColor;
    }

    public final void setPreferedColor(Color preferedColor) 
    {
        _PreferedColor = preferedColor;
    }

    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this._Username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null) 
        {
            return false;
        }
        if (getClass() != obj.getClass()) 
        {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this._Username, other._Username)) 
        {
            return false;
        }
        return true;
    }
 }
