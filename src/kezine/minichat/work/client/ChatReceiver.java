package kezine.minichat.work.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import kezine.minichat.data.Message;
import kezine.minichat.events.ChatEvent;
import kezine.minichat.events.ChatEventListener;
import kezine.minichat.work.BaseThread;
import org.apache.log4j.Level;

/**
 *
 * @author Kezine
 */
public class ChatReceiver extends BaseThread 
{
    private InputStream _InputStream;
    public ChatReceiver(String name, InputStream inputStream) 
    {
        super(name);
        _InputStream = inputStream;
    }
    
    @Override
    public void run()
    {
        ObjectInputStream ois = null;
        Message message = null;
        try 
        {
            ois = new ObjectInputStream(_InputStream);
            setStatus(ThreadStatus.RUNNING);
        } 
        catch (IOException ex) 
        {
           setErrorMessage("Error while initializing InputStream : " + ex.getMessage(), Level.FATAL);
           setStatus(ThreadStatus.STOPPED_WITH_ERROR);
        }
        while(getStatus() == ThreadStatus.RUNNING)
        {
           
            try 
            {
                message = (Message)ois.readObject();
                fireChatEventOccured(new ChatEvent(this, ChatEvent.ChatEventType.MESSAGE, "ChatMessage", message));
            }
            catch(ClassNotFoundException ex)
            {
                getLogger().log(Level.WARN, "Trying to read a message but recieved unknow data : {0}", ex);
            }
            catch (IOException ex) 
            {
                setErrorMessage("Error while trying to read a message : "+ex.getMessage(), Level.FATAL);
                setStatus(ThreadStatus.FAILED);
            }
            catch(Exception ex)
            {
                setErrorMessage("Error while trying to read a message : "+ex.getMessage(), Level.FATAL);
                setStatus(ThreadStatus.FAILED);
            }
            
        }
        if(getStatus() == ThreadStatus.STOPPING || getStatus() == ThreadStatus.FAILED)
        {
            try 
            {
                if(ois != null)
                    ois.close();
                setStatus(ThreadStatus.STOPPED);
            } 
            catch (IOException ex) 
            {
                setErrorMessage("Error while closing InputStream : " + ex.getMessage(), Level.WARN);
                setStatus(ThreadStatus.STOPPED_WITH_ERROR);
            }
        }
        
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
