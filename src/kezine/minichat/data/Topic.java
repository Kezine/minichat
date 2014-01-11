package kezine.minichat.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Gère les informations relatives au topic.
 * @author Kezine
 */
public class Topic implements Serializable
{
    private String _Name;
    private String _WelcomeMessage;
    private String _Description;
    private boolean _Locked;
    private int _MaxSize;
       
    public Topic()
    {
        this("Default toppic", "Welcome my friend", "This is the default topic", false, 20);
    }
    
    public Topic(String name, String welcomeMessage, String description, boolean isLocked, int maxSize)
    {
        setName(name);
        setWelcomeMessage(welcomeMessage);
        setDescription(description);
        setLocked(isLocked);
        setMaxSize(maxSize);
    }
    
    synchronized public String getName() 
    {
        return _Name;
    }

    synchronized public final void setName(String name) 
    {
        _Name = name;
    }

    synchronized public String getWelcomeMessage() 
    {
        return _WelcomeMessage;
    }

    synchronized public final void setWelcomeMessage(String welcomeMessage) 
    {
        _WelcomeMessage = welcomeMessage;
    }

    synchronized public String getDescription() 
    {
        return _Description;
    }

    synchronized public final void setDescription(String description) 
    {
        _Description = description;
    }
    
    synchronized public boolean isLocked() 
    {
        return _Locked;
    }

    synchronized public final void setLocked(boolean locked) 
    {
       
        _Locked = locked;
    }

    synchronized public int getMaxSize() 
    {
        return _MaxSize;
    }

    synchronized public final void setMaxSize(int maxSize) 
    {
        
        _MaxSize = maxSize;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this._Name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Topic other = (Topic) obj;
        if (!Objects.equals(this._Name, other._Name)) {
            return false;
        }
        return true;
    }
    
    
}
