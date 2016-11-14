package kezine.minichat.work;

import javax.swing.event.EventListenerList;
import kezine.minichat.events.ThreadEventListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Classe de base des threads de l'application
 * @author Kezine
 */
public abstract class BaseThread extends Thread
{
    /**
    * Gère les diffèrent status dans lequels les threads de l'application peuvent se trouver.
    * @author Kezine
    */
    private final Logger logger = Logger.getLogger(this.getClass());
    public enum ThreadStatus
    {
       INITED("Innited"),RUNNING("Running"),STOPPING("Stopping"),FAILED("Failed"),STOPPED("Stopped"),STOPPED_WITH_ERROR("StoppedWithError");
       private final String name;
       private ThreadStatus(String typeName)
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
    private String _ErrorMessage;
    private ThreadStatus _Status;
    protected EventListenerList _Listeners;
    
    public BaseThread(String name)
    {
        super(name);        
        _Listeners = new EventListenerList();
        _Status = ThreadStatus.INITED;
    }
    
    synchronized public final ThreadStatus getStatus()
    {
        return _Status;
    }
    synchronized protected final void setStatus(ThreadStatus status)
    {
        _Status = status;
        fireThreadStateChanged(status);
    }
    synchronized public final String getErrorMessage()
    {
        return _ErrorMessage;
    }
    synchronized protected final void setErrorMessage(String errorMessage,Level level)
    {
        _ErrorMessage = errorMessage;
        logger.log(level,_ErrorMessage);
    }
    synchronized protected final void setErrorMessage(String errorMessage)
    {
        setErrorMessage(errorMessage,Level.WARN);
    }
    synchronized public  void stopThread()
    {
        setStatus(ThreadStatus.STOPPING);
    }
    @Override
    public void start()
    {
        logger.info("Thread "+getName()+" is starting");
        setStatus(ThreadStatus.RUNNING);
        super.start();
    }
    
    public void addThreadEventListener(ThreadEventListener listener)
    {
        _Listeners.add(ThreadEventListener.class,listener);
    }
    public void removeThreadEventListener(ThreadEventListener listener)
    {
        _Listeners.remove(ThreadEventListener.class, listener);
    }
    private void fireThreadStateChanged(ThreadStatus status)
    {
        for(ThreadEventListener listener : _Listeners.getListeners(ThreadEventListener.class))
        {
            listener.ThreadStatusChanged(this,status);
        }
    }
    
    protected final Logger getLogger()
    {
        return logger;
    }
}
