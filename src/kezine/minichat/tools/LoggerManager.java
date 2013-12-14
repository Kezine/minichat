package kezine.minichat.tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import kezine.minichat.tools.TextPaneHandler;

/**
 * Gère l'initialisation du logger et des handler de messages de log
 * Fichier/console/JList
 * 
 * @author Destiné Loïc
 */
public class LoggerManager
{
	static private ConsoleHandler _ErrorConsole = null;
	static private TextPaneHandler _InfoPane = null;
	static private TextPaneHandler _LogPane = null;
	static private FileHandler _LogFile = null;
	static private TextFormater _TextFormater = null;
	static final public String LOGGER = "MainLogger";
	static private Logger logger = Logger.getLogger(LOGGER);

	public static Logger getMainLogger()
	{
		if (_TextFormater == null)
		{
			init();
		}
		return logger;
	}

	/**
	 * Initialise handlers fichier/console A invoquer pour avoir les Log
	 * formatés
	 */
	public static void init()
	{
		logger.setUseParentHandlers(false);
		_TextFormater = new TextFormater();
		_ErrorConsole = new ConsoleHandler();
		_InfoPane = new TextPaneHandler();
		_LogPane = new TextPaneHandler();
		_InfoPane.setFormatter(_TextFormater);
		_LogPane.setFormatter(_TextFormater);
		_ErrorConsole.setFormatter(_TextFormater);
		logger.addHandler(_ErrorConsole);
		logger.addHandler(_InfoPane);
		logger.addHandler(_LogPane);
		try
		{
			_LogFile = new FileHandler(ConfigProperties.LOG_FILE_PATH + ConfigProperties.LOG_FILE_NAME, true);// Append
		}
		catch (IOException ex)
		{
			logger.warning("Not able to get log file name from properties, trying to use default \"tempLog.txt\"");
			try
			{
				_LogFile = new FileHandler("tempLog.txt");
			}
			catch (IOException ex2)
			{
				logger.warning("File logging not enabled due to IOException : " + ex2.getMessage() + "\n" + ex2.getStackTrace());
			}
		}
		if (_LogFile != null)
		{
			_LogFile.setFormatter(_TextFormater);
			logger.addHandler(_LogFile);
		}
	}

	public static TextPaneHandler getInfoPaneHandler()
	{
		return _InfoPane;
	}

	public static TextPaneHandler getLogPaneHandler()

	{
		return _LogPane;
	}

	public static void setInfoTextPane(JTextPane pane)
	{
		_InfoPane.setTextPane(pane);
	}

	public static void setLogTextPane(JTextPane pane)
	{
		_LogPane.setTextPane(pane);
	}
}
