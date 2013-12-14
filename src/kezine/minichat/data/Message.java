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
    private Object _Message;

    
    public enum MessageType
    {
        SERVER_INFO("Serveur Infos"),CLIENT_LOGIN("Client Login"),SERVER_MESSAGE("Server Message"),CHAT_MESSAGE("Chat message");
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
    
    public Message()
    {
        
    }
    public Message(MessageType type, Object message)
    {
        setType(type);
        setMessage(message);
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
