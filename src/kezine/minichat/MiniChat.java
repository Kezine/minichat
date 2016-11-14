package kezine.minichat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import javax.swing.SwingUtilities;
import kezine.minichat.ui.client.ClientMainFrame;
import kezine.minichat.ui.server.ServerMainFrame;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Point d'entrée de l'application "Minichat"
 * @author Kezine
 */
public class MiniChat 
{
    public static void main(String[] args) throws IOException 
    {
        int argsValue = 0;
        boolean interfaceTypeChosen = false;
        System.out.println("Java Version: " + System.getProperties().getProperty("java.version"));
        System.out.println("Java Home: " + System.getProperties().getProperty("java.home"));
        System.out.println("Working Directory: " + System.getProperties().getProperty("user.dir"));
	System.out.println("User Directory: " + System.getProperties().getProperty("user.home"));
        
        Properties p = new Properties();
        try 
        {
            String fileLocation = System.getProperties().getProperty("user.dir") + File.separatorChar + "log4j.properties";
            System.out.println("Looking for log4j properties file at " + fileLocation);
            p.load(new FileInputStream(fileLocation));
            PropertyConfigurator.configure(p);
            Logger.getRootLogger().info("log4J configured");
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
                {
                    @Override
                    public void uncaughtException(Thread t, Throwable e)
                    {
                        e.printStackTrace();
                        Logger.getLogger(MiniChat.class).warn("Uncaugth exception from thread \""+t.getName()+"\" handled in main.", e);
                    }
                });
        } 
        catch (IOException e) 
        {
           System.err.println("Cannot Load log4j properties");
           e.printStackTrace();
        }
        if(args.length > 2)
        {
            Logger.getLogger(MiniChat.class).fatal("Too many arguments !");
            System.exit(1);
        }
        for(int i = 0; i < args.length; i++)
        {
            switch(args[i].toUpperCase())
            {
                case "-S":  if(interfaceTypeChosen)
                            {
                                System.out.println("Incorrect use of argument");
                                System.exit(2);
                            }
                            argsValue = 2 << 1;
                            interfaceTypeChosen = true;
                    break;
                case "-C":  if(interfaceTypeChosen)
                            {
                                System.out.println("Incorrect use of argument");
                                System.exit(2);
                            }
                            //argsValue = 3 << 1;
                            //interfaceTypeChosen = true;
                    break;
                case "-G":  argsValue = 1;
                    break;
                default :   System.out.println("Unknow argument : " + args[i]);
                            System.exit(3);
            }
        }
        if(interfaceTypeChosen & 2 == 2)
        {
            startServerApplication((argsValue & 1) == 1);
        }
        else
        {
            startClientApplication((argsValue & 1) == 1);
        }
    }
    
    public static void startClientApplication(boolean isGraphical)
    {
        Logger.getLogger(MiniChat.class).info("Starting client application");        
        if(isGraphical)
        {
            Logger.getLogger(MiniChat.class).info("Starting graphical interface");
            final ClientMainFrame cf = new ClientMainFrame();
            Tools.setLookAndFeel(cf, "Windows");
            SwingUtilities.invokeLater(new Runnable() {               
                @Override
                public void run() {
                    cf.setVisible(true);
                }
            });
        }
        else
        {
            
        }
    }
    public static void startServerApplication(boolean isGraphical) throws IOException
    {
        Logger.getLogger(MiniChat.class).info("Starting server application");
        
        if(isGraphical)
        {
            Logger.getLogger(MiniChat.class).info("Starting graphical interface");
            final ServerMainFrame sf = new ServerMainFrame();
            Tools.setLookAndFeel(sf, "Windows");
            SwingUtilities.invokeLater(new Runnable() {               
                @Override
                public void run() {
                    sf.setVisible(true);
                }
            });
        }
        else
        {
            
        }
    }
}
