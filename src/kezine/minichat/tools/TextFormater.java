package kezine.minichat.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TextFormater extends Formatter
{

	@Override
	public String format(LogRecord record)
	{
		int lastIndex = record.getSourceClassName().lastIndexOf(".");
		lastIndex = record.getSourceClassName().lastIndexOf(".", lastIndex - 1);
		String message = getDate(record.getMillis()) + "[" + record.getLevel().getName() + "] "+Thread.currentThread().getName()+" (" + record.getSourceClassName().substring(lastIndex + 1, record.getSourceClassName().length()) + " - " + record.getSourceMethodName() + "): "
				+ record.getMessage() + "\n";
                                if(record.getParameters() != null)
                                    for(Object o : record.getParameters())
                                    {
                                        message += "\t=> " + o + "\n";
                                    }
		// TODO afficher le printStackTrace pour les warning et severe
		return message;
	}

	/**
	 * Appelé au debut de l'utilisation du handler qui l'utilise
	 */
	@Override
	public String getHead(Handler h)
	{
		return "";
	}

	/**
	 * Appelé a la fin de l'utilisation du handler (fermeture) qui l'utilise
	 */
	@Override
	public String getTail(Handler h)
	{
		return "";
	}

	private String getDate(long millisecs)
	{
		SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy(HH:mm)");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

}
