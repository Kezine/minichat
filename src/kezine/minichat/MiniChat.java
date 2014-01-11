/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kezine.minichat;

import java.io.IOException;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.ServerMonitor;

/**
 * Point d'entrée de l'application "Minichat"
 * @author Kezine
 */
public class MiniChat {

    public static void main(String[] args) throws IOException 
    {
        int argsValue = 0;
        boolean interfaceTypeChosen = false;
        if(args.length > 2)
        {
            LoggerManager.getMainLogger().severe("Too many arguments !");
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
                case "-G":  argsValue = 1 << 1;
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
        LoggerManager.getMainLogger().info("Starting client application");        
        if(isGraphical)
        {
            LoggerManager.getMainLogger().info("Starting graphical interface");
        }
    }
    public static void startServerApplication(boolean isGraphical) throws IOException
    {
        LoggerManager.getMainLogger().info("Starting server application");
        ServerMonitor sm = new ServerMonitor();
        sm.startServer();
        if(isGraphical)
        {
            LoggerManager.getMainLogger().info("Starting graphical interface");
        }
    }
}
