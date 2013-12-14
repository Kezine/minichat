package kezine.minichat.work;

import kezine.minichat.data.ChatEvent;

/**
 *
 * @author Kezine
 */
public interface MessageListener 
{
    void processMessage(String message);
    void processConnectionEvent(ChatEvent event);
}
