package kezine.minichat.events;

import java.util.EventListener;
import kezine.minichat.work.BaseThread;


/**
 *
 * @author Kezine
 */
public interface ServerEventListener extends EventListener{
    void ServerDataChanged();
    void ServerStateChanged(BaseThread.ThreadStatus status);
}
