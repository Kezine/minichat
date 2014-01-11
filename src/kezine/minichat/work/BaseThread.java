package kezine.minichat.work;

import java.util.logging.Level;
import kezine.minichat.data.ThreadStatus;
import kezine.minichat.tools.LoggerManager;

/**
 * Classe de base des threads de l'application
 * @author Kezine
 */
public abstract class BaseThread extends Thread
{
    private String _ErrorMessage;
    private ThreadStatus _Status;
    
    public BaseThread(String name)
    {
        super(name);
        setStatus(ThreadStatus.INITED);
    }
    
    synchronized public final ThreadStatus getStatus()
    {
        return _Status;
    }
    synchronized protected final void setStatus(ThreadStatus status)
    {
        _Status = status;
    }
    synchronized public final String getErrorMessage()
    {
        return _ErrorMessage;
    }
    synchronized protected final void setErrorMessage(String errorMessage,Level level)
    {
        _ErrorMessage = errorMessage;
        LoggerManager.getMainLogger().log(level,_ErrorMessage);
    }
    synchronized protected final void setErrorMessage(String errorMessage)
    {
        setErrorMessage(errorMessage,Level.WARNING);
    }
    synchronized public final void stopThread()
    {
        setStatus(ThreadStatus.STOPPING);
    }
    @Override
    public void start()
    {
        LoggerManager.getMainLogger().info("Thread " + getName() + " is starting");
        setStatus(ThreadStatus.RUNNING);
        super.start();
    }
}
