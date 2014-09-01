package kezine.minichat.work.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import kezine.minichat.data.Message;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.BaseThread;

/**
 *
 * @author Kezine
 */
public class ChatSender extends BaseThread
{
    private LinkedList<Message> _MessageQueue;
    private OutputStream _OutputStream;
    public ChatSender(String name, OutputStream outputStream) 
    {
        super(name);
        _MessageQueue = new LinkedList<>();
        _OutputStream = outputStream;
    }
    
    @Override
    public void run()
    {
        ObjectOutputStream oos = null;
        Message message = null;
        try 
        {
            oos = new ObjectOutputStream(_OutputStream);
            setStatus(ThreadStatus.RUNNING);
        } 
        catch (IOException ex) 
        {
           setErrorMessage("Error while initializing OutputStream : " + ex.getMessage(), Level.SEVERE);
           setStatus(ThreadStatus.STOPPED_WITH_ERROR);
        }
        while(getStatus() == ThreadStatus.RUNNING)
        {
            message = getFirsMessage();
            if(message != null)
            {
                try 
                {
                    oos.writeObject(message);
                    LoggerManager.getMainLogger().info("Message sent");
                    
                } 
                catch (IOException ex) 
                {
                    setErrorMessage("Error while trying to send a message : "+ex.getMessage(), Level.SEVERE);
                    setStatus(ThreadStatus.FAILED);
                }
            }
            else
            {
                try 
                {
                    Thread.sleep(1000);
                } 
                catch (InterruptedException ex) {}
            }
        }
        if(getStatus() == ThreadStatus.STOPPING || getStatus() == ThreadStatus.FAILED)
        {
            try 
            {
                if(oos != null)
                    oos.close();
                setStatus(ThreadStatus.STOPPED);
            } 
            catch (IOException ex) 
            {
                setErrorMessage("Error while closing OutputStream : " + ex.getMessage(), Level.WARNING);
                setStatus(ThreadStatus.STOPPED_WITH_ERROR);
            }
        }
        
    }
    @Override
    synchronized public void stopThread()
    {
        super.stopThread();
        this.interrupt();
    }
    synchronized public void sendMessage(Message message)
    {
        _MessageQueue.add(message);
        this.interrupt();
    }
    
    synchronized private Message getFirsMessage()
    {
        if(_MessageQueue.size() > 0)
            return _MessageQueue.removeFirst();
        else
            return null;
    }
}
