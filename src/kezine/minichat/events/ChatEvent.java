package kezine.minichat.events;

/**
 *
 * @author Kezine
 */
public class ChatEvent 
{
    private Object _Source;
    private ChatEventType _Type;
    private String _Message;
    private Object _Complement;
    
    public enum ChatEventType
    {
        MESSAGE,COMMAND,CONNECTION,DISCONNECTION
    }
    public ChatEvent(Object source,ChatEventType type, String message,Object complement)
    {
        if(source == null)
            throw new IllegalArgumentException("Event source cannot be null");
        _Source = source;
        _Type = type;
        _Message = message;
        _Complement = complement;
    }
    public Object getEventSource()
    {
        return _Source;
    }
    public ChatEventType getType() 
    {
        return _Type;
    }
    public String getMessage()
    {
        return _Message;
    }
    public Object getComplement()
    {
        return _Complement;
    }
}
