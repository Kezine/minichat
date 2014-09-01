package kezine.minichat.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Gère les informations de connection permetant le dialogue avec un client.
 * @author Kezine
 */
public class Client //implements AutoCloseable
{
    private ObjectOutputStream _ObjectOutputStream;
    private ObjectInputStream _ObjectInputStream;
    private BufferedInputStream _BufferedInputStream;
    private Socket _Socket;
    private boolean _Opened;
    
    public Client(Socket socket)
    {
        if(socket == null)
            throw new IllegalArgumentException("Socket cannot be null");
        _Socket = socket;
        _ObjectOutputStream = null;
        _ObjectInputStream = null;
        _Opened = false;
    }
    /**
     * Ouvre les flux de comunication avec le client.
     * @throws IOException En cas d'erreur d'ouverture des flux de communication.
     * @throws Exception Si le socket n'est pas valide (closed or not bound).
     */
    public void OpenConnection() throws IOException, Exception
    {
        if((_Socket.isClosed() && !_Socket.isBound()) || (!_Socket.isClosed() && _Socket.isBound()))// && _Socket.isBound())
        {
            if(_Opened)
                return;
            _BufferedInputStream = new BufferedInputStream(_Socket.getInputStream());
            _ObjectInputStream = new ObjectInputStream(_BufferedInputStream);
            _ObjectOutputStream = new ObjectOutputStream(_Socket.getOutputStream());
            _Opened = true;
        }
        else
            throw new Exception("Socket is not valid (closed or not bound)");
    }
    public int inputAvailable() throws IOException
    {
        if(_BufferedInputStream != null)
            return _BufferedInputStream.available();
        return 0;
    }
    /**
     * 
     * @return Le flux de comunication sortant du client
     */
    public ObjectOutputStream getObjectOutputStream() 
    {
       return _ObjectOutputStream;
    }
    /**
     * 
     * @return Le flux de comunication entrant du client
     */
    public ObjectInputStream getObjectInputStream() 
    {
       return _ObjectInputStream;
    }
    /**
     * 
     * @return  Le socket du client
     */
    public Socket getSocket() 
    {
        return _Socket;
    }

    //@Override
    public void close() throws Exception 
    {
        //Un seul appel à la fermeture d'un des 2 flux ferme tout
        /*if(_ObjectInputStream != null)
            _ObjectInputStream.close();*/
        if(_ObjectOutputStream != null)
            _ObjectOutputStream.close();
        /*if(!_Socket.isClosed())
            _Socket.close();*/
    }
}
