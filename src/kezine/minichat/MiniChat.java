/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kezine.minichat;

import kezine.minichat.tools.LoggerManager;

/**
 *
 * @author Kezine
 */
public class MiniChat {

    public static void main(String[] args) 
    {
        if(args.length == 2)
        {
            
        }
        else if(args.length > 2)
            LoggerManager.getMainLogger().severe("Too many arguments !");
    }
    
    public static void startClientApplication(boolean isGraphical)
    {
        
        
        
        if(isGraphical)
        {
            
        }
    }
    public static void startServerApplication(boolean isGraphical)
    {
        
        if(isGraphical)
        {
            
        }
    }
}
