package kezine.minichat.events;

import java.util.EventListener;
import kezine.minichat.work.BaseThread.ThreadStatus;

/**
 *
 * @author Kezine
 */
public interface ThreadEventListener extends EventListener{
    void ThreadStatusChanged(Object source,ThreadStatus status);
}
