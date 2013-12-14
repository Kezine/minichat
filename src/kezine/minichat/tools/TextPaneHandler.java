package kezine.minichat.tools;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class TextPaneHandler extends Handler
{
	private JTextPane _TextePane;

	public TextPaneHandler()
	{
		this(null);
	}

	public TextPaneHandler(JTextPane textePane)
	{
		super();
		setTextPane(textePane);
		setLevel(Level.ALL);
	}

	public void setTextPane(JTextPane textPane)
	{
		_TextePane = textPane;
	}

	@Override
	public void close() throws SecurityException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void flush()
	{
		if (_TextePane != null)
		{
			StyledDocument sd = _TextePane.getStyledDocument();
			try
			{
				sd.remove(0, sd.getLength());
			}
			catch (BadLocationException e)
			{
			}
		}
	}

	@Override
	public void publish(LogRecord record)
	{
		if (_TextePane != null && record.getLevel().intValue() >= getLevel().intValue())
		{
			if (getFilter() == null || (getFilter() != null && getFilter().isLoggable(record)))
			{
				StyledDocument sd = _TextePane.getStyledDocument();
				try
				{
					if (sd.getLength() >= 10000)
						sd.remove(0, sd.getLength());
					sd.insertString(sd.getLength(), getFormatter().format(record), null);
				}
				catch (BadLocationException e)
				{
					System.out.println("Oups : " + e.getMessage());
				}
			}
		}
	}

}
