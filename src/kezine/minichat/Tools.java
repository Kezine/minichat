/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kezine.minichat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kezine Test
 */
public class Tools {
        private static String _Repertoire = null;
	public final static int DRAWED_FROM_TOP_LEFT = 1;
        public final static int DRAWED_FROM_TOP_RIGHT = 2;
        public final static int DRAWED_FROM_BOTTOM_LEFT = 3;
        public final static int DRAWED_FROM_BOTTOM_RIGHT = 4;
        
        
        /**
	 * @return Url du repertoire d'execution du programme
	 */
	public static String getDirectory()
	{
            if (_Repertoire == null)
            {

                    /*_Repertoire = Tools.class.getProtectionDomain().getCodeSource().getLocation().getFile();
                    // on enlève le "file:/"
                    _Repertoire = _Repertoire.substring(_Repertoire.indexOf("/") + 1);
                    // on enleve le nom du jar
                    _Repertoire = _Repertoire.substring(0, _Repertoire.lastIndexOf("/") + 1);
                    _Repertoire = _Repertoire.replace('/', System.getProperty("file.separator").charAt(0));
                    _Repertoire = _Repertoire.replaceAll("%c3%a9", "é");
                    try
                    {
                            _Repertoire = new String(_Repertoire.getBytes("UTF-8"));
                    }
                    catch (UnsupportedEncodingException e)
                    {
                            e.printStackTrace();
                    }*/
                _Repertoire = System.getProperty("user.dir") + System.getProperty("file.separator");
            }
            return _Repertoire;
	}

	/**
	 * @return Nom du package "Racine" du projet
	 */
	public static String getPackageName()
	{
            String packageName = "";
            try
            {
                    packageName = Class.forName(Tools.class.getName()).getPackage().getName();
                    packageName = "/" + packageName.replace('.', '/');
            }
            catch (ClassNotFoundException ex)
            {
                    System.err.println("Not able to retrieve package information : " + ex.getMessage());
                    System.exit(1);
            }
            return packageName;
	}

	public static String stackTraceToString(StackTraceElement[] stackTrace, int maxLenght, int nbrTab)
	{
            StringBuilder retour = new StringBuilder();
            for (StackTraceElement element : stackTrace)
            {
                    retour.append(element + "\n");
            }
            return getTabbString(retour.toString(), maxLenght, nbrTab).toString();
	}

	/**
	 * Permet de formater un texte pour mettre une tabulation devant
	 * 
	 * @param Le
	 *            message a formater
	 * @param La
	 *            longueur maximale d'une ligne
	 * @param Le
	 *            nombre de tabulation devant chaque ligne
	 * @return Le message formaté
	 */
	public static StringBuilder getTabbString(String message, int maxLenght, int nbrTab)
	{
            StringBuilder retour = new StringBuilder();
            String tab = "";
            String tempSubstring;
            int parcourt = 0;
            int fin;
            if (nbrTab < 1 || nbrTab > 10)
            {
                    tab = "\t";
            }
            else
            {
                    for (int i = 0; i < nbrTab; i++)
                    {
                            tab += "\t";
                    }
            }
            do
            {
                    if ((parcourt + maxLenght) > message.length())
                    {
                            fin = message.length();
                    }
                    else
                    {
                            fin = parcourt + maxLenght;
                    }
                    tempSubstring = message.substring(parcourt, fin);
                    // On test un retour a la ligne pour remettre le parcourt a zero => eviter lesdouble retour etc
                    int tempIndex = tempSubstring.indexOf("\n");
                    if (tempIndex == -1)
                    {
                            retour.append(tab);
                            retour.append(tempSubstring + "\n");
                    }
                    else
                    {
                            if (parcourt != tempIndex || parcourt == 0)
                            {
                                    retour.append(tab + tempSubstring.substring(0, tempIndex + 1));
                            }
                            fin = parcourt + tempIndex + 1;//
                    }
                    parcourt = fin;
            } while (fin < message.length());
            return retour;
	}

		
	/**
	 * Permet de recupérer un double à partir d'une String de manière fiable
	 * 
	 * @param value
	 *            La String à parser
	 * @param defaultValue
	 *            La valeur double par defaut si la value est Null ou Invalide
	 * @return La valeur double de la String ou la defaultValue si une erreur
	 *         s'est produite
	 */
	public static double getDoubleFromString(String value, double defaultValue)
	{
            if (value != null)
            {
                    if (!value.equals(""))
                    {
                            try
                            {
                                    return Double.parseDouble(value);
                            }
                            catch (NumberFormatException ex)
                            {
                                    System.err.println("Conversion error,incorrect parameter data found (" + value + "). Using default value : " + defaultValue);
                            }
                    }
            }
            return defaultValue;
	}

	/**
	 * Verifie la classe passée en parametre et au besoin l'instancie avec le
	 * constructeur par defaut
	 * 
	 * @param object
	 *            Objet a verifier
	 * @param className
	 *            Le nom de l'objet passé en parametre Objet
	 * @return Un objet non null
	 */
	@SuppressWarnings("rawtypes")
	public static Object checkObject(Object object, Class className)
	{
            if (object == null)
            {
                    try
                    {
                        System.err.println("Object used is null ,it is supposed to be a " + className.getName() + " object");
                        return className.newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException ex)
                    {
                            throw new RuntimeException("Unable to instaciate class : " + className.getName() + "\n" + ex.getStackTrace());
                    }
            }
            return object;
	}

	/**
	 * Verifie que le nombre rentré est entre les bornes min et max
	 * 
	 * @param value
	 *            Valeur a tester
	 * @param min
	 *            Valeur minimale de value
	 * @param max
	 *            Valeur maximale de value
	 * @param defaultValue
	 *            Valeur par defaut retourné en cas d'erreur
	 * @return Valeur verifiee
	 */
	public static double checkBoundedValue(double value, double min, double max, double defaultValue)
	{
            if (value >= min && value <= max)
            {
                    return value;
            }
            else
            {
                    return defaultValue;
            }

	}

	/**
	 * Verifie que le nombre rentré est entre les bornes min et max et fournis
	 * un feedback par le logger
	 * 
	 * @param value
	 *            Valeur a tester
	 * @param min
	 *            Valeur minimale de value
	 * @param max
	 *            Valeur maximale de value
	 * @param defaultValue
	 *            Valeur par defaut retourné en cas d'erreur
	 * @param location
	 *            endroit d'utilisation de la méthode
	 * @return Valeur verifiee
	 */
	public static double checkBoundedValueWithFeedBack(double value, double min, double max, double defaultValue, String location)
	{
            if (value >= min && value <= max)
            {
                    return value;
            }
            else
            {
                                     System.err.println("(" + location + ")Double value out of range [" + min + "," + max + "] (" + value + "). Using default value : " + defaultValue);
                    return defaultValue;
            }
	}

	public static Object copy(Object orig)
	{
            Object obj = null;
            try
            {
                // Write the object out to a byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(orig);
                // out.flush();
                // out.close();

                // Make an input stream from the byte array and read
                // a copy of the object back in.
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
                obj = in.readObject();
            }
            catch (IOException | ClassNotFoundException ex)
            {
                 System.err.println("Deep Copy error ("+ex.getClass().getName()+") : " + ex.getMessage());
            }
            return obj;
	}

		
	/**
	 * Charge les fichiers d'un repertoire
	 * 
	 * @param directory
	 *            Repertoire a explorer
	 * @param extension
	 *            Extension des fichiers a rechercher
	 * @param withDirectory
	 *            True pour recupérer les dossier avec
	 * @return La liste des fichiers trouvé
	 */
	public static ArrayList<File> loadDirectoryContent(File directory, String extension, boolean withDirectory)
	{
            ArrayList<File> files = new ArrayList<File>();
            if (directory.exists())
            {
                    for (File f : directory.listFiles())
                    {
                            if ((f.isFile() && f.getName().endsWith(extension) && !withDirectory) || withDirectory)
                            {
                                    files.add(f);
                            }
                    }
            }
            return files;
	}

	public static Image scaleImage(Image source, int width, int height)
	{
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(source, 0, 0, width, height, null);
            g.dispose();
            return img;
	}

	public static void setImage(JLabel label, Image image)
	{
            if (image.getWidth(null) > label.getWidth() || image.getHeight(null) > label.getHeight())
            {
                    double ratio1 = (double) label.getWidth() / (double) image.getWidth(null);
                    double ratio2 = (double) label.getHeight() / (double) image.getHeight(null);
                    label.setIcon(new ImageIcon(scaleImage(image, (int) (image.getWidth(null) * Math.min(ratio1, ratio2)), (int) (image.getHeight(null) * Math.min(ratio1, ratio2)))));
            }
            else
            {
                    label.setIcon(new ImageIcon(image));
            }
	}

	/**
	 * Verifie si la collection contient la chaine passée en parametre
	 * 
	 * @param list
	 *            la collection à parcourir
	 * @param value
	 *            la chaine a rechercher
	 * @param isCaseSensitive
	 *            si la recherche doit être sensible a la case
	 * @return true si la chaine a été trouvée.
	 */
	public static boolean containsString(Collection<String> list, String value, boolean isCaseSensitive)
	{
            for (String string : list)
            {
                    if ((isCaseSensitive && string.equals(value)) || (!isCaseSensitive && string.equalsIgnoreCase(value)))
                            return true;
            }
            return false;
	}
	/**
	 * Change le LAF et met a jour la frame passée en parametre
	 * @param frame La frame a mettre a jour
	 * @param lafName Le nom du Look and Feel a utiliser
	 */
	public static void setLookAndFeel(Frame frame,String lafName)
	{
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
            {
                    if (lafName.equals(laf.getName()))
                    {
                            try
                            {
                                    UIManager.setLookAndFeel(laf.getClassName());
                            }
                            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
                            {
                                    String error = "Error while trying to set Look And Feel \""+lafName+"\"";
                                    if(frame != null)
                                        JOptionPane.showMessageDialog(frame, error, "Laf Error", JOptionPane.ERROR_MESSAGE);
                                    Logger.getLogger(Tools.class).warn(error);

                            }
                    }
            }
            if(frame != null)
                SwingUtilities.updateComponentTreeUI(frame);
        }
	
	/*
	 * public static double getIntermediateRadius(RingShell rs,double height) {
	 * double ratio = (Math.max(rs.getBottomRadius(), rs.getTopRadius()) -
	 * Math.min(rs.getBottomRadius(), rs.getTopRadius())) / rs.getHeight();
	 * if(rs.getBottomRadius() == rs.getTopRadius()) { return
	 * rs.getBottomRadius(); } else if(rs.getTopRadius() < rs.getBottomRadius())
	 * { return rs.getBottomRadius() - (ratio*height); } else { return
	 * rs.getBottomRadius() + (ratio*height); } }
	 */
	
	/**
	 * Verifie l'unicité de la clé passée en parametre et retourne une clé
	 * unique
	 * 
	 * @param key
	 *            Clé a verifier
	 * @return La clé passée en parametre si elle est unique, ou une clé générée
	 *         a partir du parametre si elle existe dejà
	 */
	public static String getUniquePrimary(String key,Collection<String> collection,boolean isCaseSensitive)
	{
            int i = 1;
            String tempName = key;
            /**
             * Verifie l'unicité du nom et en gerere un unique au besoin
             */
            while (!checkPrimaryUnicity(tempName,collection,isCaseSensitive))
            {
                    tempName = key + "-" + i;
                    i++;
            }
            return tempName;
	}

	public static boolean checkPrimaryUnicity(String tempName,Collection<String> collection,boolean isCaseSensitive)
	{
            for(String element : collection)
            {
                    if(element.compareToIgnoreCase(tempName) == 0)
                    {
                            if(isCaseSensitive && element.compareTo(tempName) == 0)
                            {
                                    return false;
                            }
                            else
                            {
                                    return false;
                            }
                    }
            }
            return true;
	}
        public static Rectangle2D getBackgroundRectangle(String string, Point stringPosition, Font font, Graphics2D g, int padding)
        {
            return getBackgroundRectangle(string, stringPosition, font, g, padding, padding, padding, padding);
        }
        public static  Point getBaselineCoordinateForBounded(String string, Rectangle2D rectangle, Font font , Graphics2D g, int leftPadding, int topPadding)
        {
            FontMetrics fm = g.getFontMetrics(font);
            return new Point((int)rectangle.getX() + leftPadding, (int)rectangle.getY() + fm.getAscent() + topPadding);
        }
        public static Rectangle2D getBackgroundRectangle(String string, Point stringPosition, Font font , Graphics2D g, int topPadding, int bottomPadding, int leftPadding, int rightPadding)
        {
            // -2 car le bounding ajoute une marge de 1
            FontMetrics fm = g.getFontMetrics(font);
            return new Rectangle((int)stringPosition.getX() - leftPadding , (int)stringPosition.getY() - fm.getAscent() - topPadding, fm.stringWidth(string) + leftPadding + rightPadding,fm.getAscent() + fm.getDescent() + topPadding + bottomPadding);
        }
        public static void DrawBoundedString(String string, Point stringPosition,int drawingStartingPoint,Color backgroundColor, Color fontColor, int topPadding, int bottomPadding, int leftPadding, int rightPadding, Font font , Graphics2D g)
        {
            Rectangle2D bounds = getBackgroundRectangle(string, stringPosition, font, g, topPadding, bottomPadding, leftPadding, rightPadding);
            g.setColor(backgroundColor);
        /*    public int DRAWED_FROM_TOP_LEFT = 1;
        public int DRAWED_FROM_TOP_RIGHT = 2;
        public int DRAWED_FROM_BOTTOM_LEFT = 3;
        public int DRAWED_FROM_BOTTOM_RIGHT = 4;*/
            switch(drawingStartingPoint)
            {
                case DRAWED_FROM_TOP_LEFT: bounds = new Rectangle((int)stringPosition.getX() - (int)bounds.getWidth(), (int)stringPosition.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
                    break;
                case DRAWED_FROM_BOTTOM_LEFT: bounds = new Rectangle((int)stringPosition.getX(), (int)stringPosition.getY() - (int)bounds.getHeight(), (int)bounds.getWidth(), (int)bounds.getHeight());
                    break;
                case DRAWED_FROM_BOTTOM_RIGHT: bounds = new Rectangle((int)stringPosition.getX() - (int)bounds.getWidth(), (int)stringPosition.getY() - (int)bounds.getHeight(), (int)bounds.getWidth(), (int)bounds.getHeight());
                    break;
            }
            g.drawRect((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
            g.fillRect((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
            Point base = getBaselineCoordinateForBounded(string, bounds, font, g, leftPadding, topPadding);
            g.setColor(fontColor);
            g.drawChars(string.toCharArray(), 0, string.length(),(int)base.getX(), (int)base.getY());
        }
}
