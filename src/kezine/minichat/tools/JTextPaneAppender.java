package kezine.minichat.tools;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Kezine
 */
public class JTextPaneAppender  extends AppenderSkeleton 
{
    private final JTextPane textPane;
   
    public JTextPaneAppender(JTextPane textPane) 
    {
        if( textPane == null)
            throw new IllegalArgumentException("TextPane cannot be null");
        this.textPane = textPane;        
    }
    protected void append(LoggingEvent event) 
    {        
        StyledDocument sd = textPane.getStyledDocument();
        System.err.println("Append ?");
        try
        {
                if (sd.getLength() >= 10000)
                        sd.remove(0, sd.getLength());
                //sd.insertString(sd.getLength(), getLayout().format(event), null);
                sd.insertString(sd.getLength(), event.getMessage().toString() + '\n', null);
        }
        catch (BadLocationException e)
        {
            System.err.println("Oups : " + e.getMessage());
        }
    }
    
    
    public void close() 
    {
        
    }
    
    public boolean requiresLayout() 
    {
        return false;
    }
    
}



