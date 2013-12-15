package kezine.minichat.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Kezine
 */
public class Client implements AutoCloseable
{
    private DataOutputStream _DataOutputStream;
    private DataInputStream _DataInputStream;
    private Socket _Socket;
    private boolean _Opened;
    
    public Client(Socket socket)
    {
        if(socket == null)
            throw new IllegalArgumentException("Socket cannot be null");
        _Socket = socket;
        _DataOutputStream = null;
        _DataInputStream = null;
        _Opened = false;
    }
    
    public void OpenConnection() throws IOException, Exception
    {
        if(_Socket.isClosed() && _Socket.isBound())
        {
            if(_Opened)
                return;
            _DataInputStream = new DataInputStream(_Socket.getInputStream());
            _DataOutputStream = new DataOutputStream(_Socket.getOutputStream());
            _Opened = true;
        }
        else
            throw new Exception("Socket is not valid (closed or not bound)");
    }
    
    public DataOutputStream getDataOutputStream() 
    {
        return _DataOutputStream;
    }

    public DataInputStream getDataInputStream() 
    {
        return _DataInputStream;
    }

    public Socket getSocket() 
    {
        return _Socket;
    }

    @Override
    public void close() throws Exception 
    {
        if(_DataInputStream != null)
            _DataInputStream.close();
        if(_DataOutputStream != null)
            _DataOutputStream.close();
        _Socket.close();
    }
}
