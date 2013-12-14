package kezine.minichat.work;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import kezine.minichat.data.ThreadStatus;
import kezine.minichat.tools.LoggerManager;

/**
 *
 * @author Kezine
 */
public class ServerDispatchThread extends Thread
{
    private ServerSocket _ServerSocket;
    private String _ErrorMessage;
    private ThreadStatus _Status;
    private Server _Server;
    
   
    public ServerDispatchThread(Server server,ServerSocket serverSocket)
    {
        super("ServerDispatchThread");
        _Server = server;
        _ServerSocket = serverSocket;
    }
    @Override
    public void run() 
    {
        if(_ServerSocket.isBound())
        {
            Socket clientSocket;
            setStatus(ThreadStatus.RUNNING);
            while(getStatus().equals(ThreadStatus.RUNNING))
            {
                try
                {
                    clientSocket = _ServerSocket.accept();
                    if(_Server.getOnlineClientsCounts() == _Server.getMaxClient())
                    {
                        DataInputStream dis = clientSocket.getInputStream();
                    }
                }
                catch(IOException ex)
                {
                    setErrorMessage("Erreur while acception new client : " + ex);
                }
            }
            setStatus(ThreadStatus.STOPPING);
            try 
            {
                _ServerSocket.close();
            } 
            catch (IOException ex) 
            {
                setErrorMessage("Erreur while closing ServerSocket : " + ex);
                setStatus(ThreadStatus.STOPPED_WITH_ERROR);
            }
        }
        else
        {
            setErrorMessage("ServerSocket is not bound !",Level.SEVERE);
            setStatus(ThreadStatus.STOPPED_WITH_ERROR);
        }
    }
    synchronized public  ThreadStatus getStatus()
    {
        return _Status;
    }
    synchronized private void setStatus(ThreadStatus status)
    {
        _Status = status;
    }
    synchronized public String getErrorMessage()
    {
        return _ErrorMessage;
    }
    synchronized private void setErrorMessage(String errorMessage,Level level)
    {
        _ErrorMessage = errorMessage;
        LoggerManager.getMainLogger().log(level,_ErrorMessage);
    }
    synchronized private void setErrorMessage(String errorMessage)
    {
        setErrorMessage(errorMessage,Level.WARNING);
    }
}
