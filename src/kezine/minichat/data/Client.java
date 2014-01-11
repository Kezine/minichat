package kezine.minichat.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Gère les informations de connection permetant le dialogue avec un client.
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
    /**
     * Ouvre les flux de comunication avec le client.
     * @throws IOException En cas d'erreur d'ouverture des flux de communication.
     * @throws Exception Si le socket n'est pas valide (closed or not bound).
     */
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
    /**
     * 
     * @return Le flux de comunication sortant du client
     */
    public DataOutputStream getDataOutputStream() 
    {
        return _DataOutputStream;
    }
    /**
     * 
     * @return Le flux de comunication entrant du client
     */
    public DataInputStream getDataInputStream() 
    {
        return _DataInputStream;
    }
    /**
     * 
     * @return  Le socket du client
     */
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
