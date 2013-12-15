package kezine.minichat.data;

import kezine.minichat.events.ChatEvent;

/**
 *
 * @author Kezine
 */
public interface MessageListener 
{
    void processMessage(String message);
    void processConnectionEvent(ChatEvent event);
}
