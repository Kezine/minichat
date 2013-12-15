package kezine.minichat.data;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Kezine
 */
public class Message implements Serializable 
{
    private MessageType _Type;
    private User _Sender;
    private Object _Destination;
    private Object _Message;

    
    public enum MessageType
    {
        SERVER_INFO("Serveur Infos"),SERVER_BROADCAST("Server Broadcast"),PRIVATE_MESSAGE("Private Message"),CLIENT_LOGIN("Client Login"),CHAT_MESSAGE("Chat message");
        private String name;
        private MessageType(String typeName)
        {
                name = typeName;
        }
        /**
         * @return Le nom du type
         */
        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }
        
    }
    
    public Message(MessageType type, Object message)
    {
        this(type, message, null, (Object)null);
    }
    public Message(MessageType type, Object message,User sender, User destination)
    {
        this(type, message, sender, (Object)destination);
    }
    public Message(MessageType type, Object message,User sender, Topic destination)
    {
        this(type, message, sender, (Object)destination);
    }
    private Message(MessageType type, Object message,User sender, Object destination)
    {
        setType(type);
        setMessage(message);
        setSender(sender);
        setDestination(destination);
    }
    public MessageType getType() 
    {
        return _Type;
    }

    public final void setType(MessageType type) 
    {
        _Type = type;
    }

    public Object getMessage() 
    {
        return _Message;
    }

    public final void setMessage(Object message) 
    {
        _Message = message;
    }
    
    public User getSender() 
    {
        return _Sender;
    }

    public final void setSender(User sender) 
    {
        _Sender = sender;
    }

    public Object getDestination() 
    {
        return _Destination;
    }

    public final void setDestination(Object destination) 
    {
        _Destination = destination;
    }
    
    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 29 * hash + (this._Type != null ? this._Type.hashCode() : 0);
        hash = 29 * hash + Objects.hashCode(this._Message);
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
        final Message other = (Message) obj;
        if (this._Type != other._Type) 
        {
            return false;
        }
        if (!Objects.equals(this._Message, other._Message)) 
        {
            return false;
        }
        return true;
    }
    
    
    
}
