package kezine.minichat.events;

import java.util.EventListener;

/**
 *
 * @author Kezine
 */
public interface ChatEventListener extends EventListener
{
    void processChatEvent(ChatEvent event);
}
