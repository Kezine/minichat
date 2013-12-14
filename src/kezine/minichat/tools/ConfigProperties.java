package kezine.minichat.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import kezine.minichat.Tools;


/**
 * Gère le fichier properties de l'application
 * 
 * @author Destiné Loïc
 */
public class ConfigProperties
{

	private final static String PROPERTIES_FILE_NAME = "Config.properties";
	private static Properties _Properties = new Properties();
	public static Boolean DEBUG;
	public static String LOG_FILE_PATH;
	public static String LOG_FILE_NAME;
        public static Boolean LOG_FILE_ENABLED;
        
	     
	static
	{
            if (!ReloadProperties())
                    System.exit(1);
	}

	public static boolean ReloadProperties()
	{
            try
            {
                _Properties.clear();
                _Properties.load(new FileInputStream(getNomFichierComplet()));
                initMembers();
            }
            catch (FileNotFoundException ex)
            {
                InitFile();
            }
            catch (IOException ex)
            {
                LoggerManager.getMainLogger().log(Level.SEVERE, "Not able to load Main properties : {0}", ex.getMessage());
                return false;
            }
            return true;
	}

	private static String getNomFichierComplet()
	{
            String nomFichier;
            nomFichier = Tools.getDirectory() + System.getProperty("file.separator") + "Properties" + System.getProperty("file.separator") + PROPERTIES_FILE_NAME;
            return nomFichier;
	}

	private static void InitFile()
	{
            _Properties = new Properties();
            _Properties.setProperty("DEBUG", "FALSE");
            _Properties.setProperty("LOG_FILE_PATH", Tools.getDirectory() + "Logs" + System.getProperty("file.separator"));
            _Properties.setProperty("LOG_FILE_NAME", "Log.txt");
            _Properties.setProperty("LOG_FILE_ENABLED", "TRUE");

            new File(Tools.getDirectory() + "Properties").mkdir();

            initMembers();
            saveChanges("");
	}

	private static void updatePropetries()
	{
            _Properties.setProperty("DEBUG", Boolean.toString(DEBUG));
            _Properties.setProperty("LOG_FILE_PATH", LOG_FILE_PATH);
            _Properties.setProperty("LOG_FILE_NAME", LOG_FILE_NAME);
            _Properties.setProperty("LOG_FILE_ENABLED", Boolean.toString(LOG_FILE_ENABLED));
        }

	private static void initMembers()
	{
            String temp = _Properties.getProperty("DEBUG");
            if (temp.compareToIgnoreCase("TRUE") == 0)
            {
                DEBUG = true;
            }
            else
            {
                DEBUG = false;
            }
            LOG_FILE_PATH = _Properties.getProperty("LOG_FILE_PATH");
            LOG_FILE_NAME = _Properties.getProperty("LOG_FILE_NAME");
            temp = _Properties.getProperty("LOG_FILE_ENABLED");
            if (temp.compareToIgnoreCase("TRUE") == 0)
            {
                LOG_FILE_ENABLED = true;
            }
            else
            {
                LOG_FILE_ENABLED = false;
            }
        }

	public static void saveChanges(String comments)
	{
            try
            {
                updatePropetries();
                _Properties.store(new FileOutputStream(getNomFichierComplet()), comments);
            }
            catch (FileNotFoundException ex)
            {
                LoggerManager.getMainLogger().log(Level.SEVERE, "Not able to save properties(FileNotFoundException) : {0}", ex.getMessage());
            }
            catch (IOException ex)
            {
                LoggerManager.getMainLogger().log(Level.SEVERE, "Not able to save properties(IOException) : {0}", ex.getMessage());
            }
	}

	public static String getLoadedProperties()
	{
            String propertiesLoaded = "";
            for (Entry<Object, Object> element : _Properties.entrySet())
            {
                propertiesLoaded += element.getKey() + " : " + element.getValue() + "\n";
            }
            return propertiesLoaded;
	}

	public static void printLoadedProperties()
	{
            System.out.println(getLoadedProperties());
	}

	public static Properties getProperties()
	{
            return _Properties;
	}
}
