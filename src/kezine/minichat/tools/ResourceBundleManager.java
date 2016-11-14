package kezine.minichat.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class ResourceBundleManager
{
	protected final Logger logger = Logger.getLogger(getClass());
	private final HashMap<String,ResourceBundle> resources = new HashMap<>();
	private final HashMap<String,Locale> specificLocales = new HashMap<>(); 
	private String rootDirectory;
	private Locale locale;
	
	/**
	 * Initialise le manager avec la {@link Locale} par defaut du syst�me
	 * @param rootDirectory Le repertoire racine des � partir duquel les bundle sont recherch�
	 */
	public ResourceBundleManager(String rootDirectory)
	{
		this(rootDirectory, Locale.getDefault());
	}
	
	/**
	 * Initialise le manager
	 * @param rootDirectory  Le repertoire racine des � partir duquel les bundle sont recherch�
	 * @param locale La {@link Locale} par defaut � affecter au manager
	 */
	public ResourceBundleManager(String rootDirectory, Locale locale)
	{
		setRootDirectory(rootDirectory);
		setDefaultLocale(locale);
	}		
	
	/**
	 * Ajoute un bundle au manager, gestion de son chargement/mise a jour
	 * @param bundleName Le nom du bundle a partir du rootDirectory pass� au constructeur ou via la m�thode {@link #setRootDirectory(String)}
	 */
	synchronized public void addManagedBundle(String bundleName)
	{
		addManagedBundle(bundleName, getDefaultLocale());
	}
	/**
	 * Ajoute un bundle au manager, gestion de son chargement/mise a jour
	 * @param bundleName Le nom du bundle a partir du rootDirectory pass� au constructeur ou via la m�thode {@link #setRootDirectory(String)}
	 * @param specificLocale La locale specifique � affecter au bundle
	 */
	synchronized public void addManagedBundle(String bundleName,Locale specificLocale)
	{
		if(specificLocale == null)
			throw new IllegalArgumentException("specificLocale cannot be null");
		if(bundleName == null)
			throw new IllegalArgumentException("bundleName cannot be null");		
		resources.put(bundleName, null);
		specificLocales.put(bundleName, specificLocale);
	}
	/**
	 * Enleve un bundle du manager
	 * @param bundleName le nom du bundle
	 * @return true si le bundle �tait effectivement g�r�
	 */
	synchronized public boolean removeManagedBundle(String bundleName)
	{
		specificLocales.remove(bundleName);
		return resources.remove(bundleName) != null;
	}
	/**
	 *  Enleve un bundle du manager
	 * @param bundle Le {@link ResourceBundle} � enlever du manager
	 * @return true si le bundle �tait effectivement g�r�
	 */
	synchronized public boolean removeManagedBundle(ResourceBundle bundle)
	{
		for(Entry<String, ResourceBundle> entry : resources.entrySet())
		{
			if(entry.getValue() == bundle)
			{
				specificLocales.remove(entry.getKey());
				resources.remove(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Lance manuellement le chargement des bundles g�r�.</br>
	 * Optionnel car les {@link ResourceBundle} sont automatiquement charg� (si ils ne le sont pas encore) lors de l'appel {@link #getBundle(String)} ou {@link #getBundle(String, Locale)}
	 */
	synchronized public void loadBundles()
	{
		for(String key : resources.keySet())
		{
			resources.put(key, loadBundle(key,specificLocales.get(key)));
		}
	}
	/**
	 * Supprime les bundle charg�,charg� mais garde en memoire la liste de ceux-ci.</br>
	 * Lors d'un nouvel appel au m�thodes {@link #getBundle(String)} , {@link #getBundle(String, Locale)} , {@link #loadBundles()} ou {@link #loadBundle(String, Locale)}, les {@link ResourceBundle} seront a nouveau charg� � partir du disque.
	 */
	synchronized public void clearResources()
	{
		for(Entry<String, ResourceBundle> entry : resources.entrySet())
		{
			entry.setValue(null);
		}		
	}
	/**
	 * Reinitialise les locale specifique � la valeur par defaut. </br>
	 * Supprime les {@link ResourceBundle} utilisant une autre locale que celle par defaut, mais la liste de ceux-ci est gard� en m�moire pour les rechager avec la {@link Locale} par defaut.
	 */
	synchronized public void clearSpecificLocale()
	{
		for(Entry<String, Locale> entry : specificLocales.entrySet())
		{
			if(!entry.getValue().equals(getDefaultLocale()))
			{
				entry.setValue(getDefaultLocale());
				resources.put(entry.getKey(),null);
			}
		}		
	}
	/**
	 * Set la {@link Locale} par defaut utilis� lors de l'appel a {@link #getBundle(String)} pour une ressource dont une {@link Locale} specifique n'a pas �t� definie.</br>
	 * soit avec les methodes {@link #setSpecificLocale(String, Locale)}, {@link #addManagedBundle(String,Locale)} ou {@link #getBundle(String, Locale)}.
	 * @param defaultLocale La {@link Locale} � utiliser par defaut.
	 */
	synchronized public void setDefaultLocale(Locale defaultLocale)
	{
		if(defaultLocale == null)
			throw new IllegalArgumentException("defaultLocale cannot be null");
		for(Entry<String,Locale> entry : specificLocales.entrySet())
		{
			if(entry.getValue().equals(getDefaultLocale()))
				entry.setValue(defaultLocale);
		}
		clearResources();
		this.locale = defaultLocale;		
	}
	
	/**
	 * Sp�cifie le repertoire racine a partir duquel sont recherch� les bundle. Cette m�thode supprime les {@link ResourceBundle} charg� mais garde en memoire la liste de ceux ci.</br>
	 * Voir m�thode {@link #clearResources()}
	 * @param rootDirectory Le repertoire racine a partir duquel sont recherch� les bundle. 
	 */
	synchronized public void setRootDirectory(String rootDirectory)
	{
		if(rootDirectory == null)
			throw new IllegalArgumentException("rootDirectory cannot be null");
		clearResources();
		this.rootDirectory = rootDirectory;
	}
	/**
	 * Charge manuellement un bundle, mais sans l'ajouter au {@link ResourceBundle} g�r� par l'objet.
	 * @param name Le nom du bundle (charg� � partir du repertoire racine specifi� par {@link #setRootDirectory(String)} ou {@link #ResourceBundleManager(String)})
	 * @param locale La {@link Locale} a utiliser pour charger le Bundle
	 * @return Le {@link ResourceBundle} charg�. Si le {@link ResourceBundle} n'est pas disponible dans la {@link Locale} sp�cifi�, une tentative de chargement avec la {@link Locale} "en" sera effectu�e
	 * @throws RuntimeException Si le {@link ResourceBundle} n'a pu �tre charg�.
	 */
	protected ResourceBundle loadBundle(String name,Locale locale)
	{
		Locale usedLocale = locale;
		if(usedLocale == null)
			usedLocale = this.locale;
		ResourceBundle output;
		try
		{
			output = ResourceBundle.getBundle(rootDirectory + "."+name,locale);
		}
		catch(MissingResourceException ex)
		{
			logger.warn("Locale file \""+name+"\" not found for locale " + locale +" using english Locale");
			try
			{
				output = ResourceBundle.getBundle(rootDirectory + "."+name+"_en");
			}
			catch(MissingResourceException ex2)
			{
				throw new RuntimeException("Cannot load resource bundle " + rootDirectory + "."+name+"_" + Locale.getDefault()+" , object is set to null");
			}
		}
		logger.info("Default locale : "+Locale.getDefault()+"  Using : " + name + "_" + ((output.getLocale().toString().isEmpty())?"en": output.getLocale()));
		return output;
	}
	/**
	 * R�cup�re le bundle manag� par l'objet avec une {@link Locale} specifique.</br>
	 * Si la locale pass� en param�tre est deja utilis� pour ce Bundle l'objet est simplement retourn�.</br>
	 * Sinon il est recharg� avec la nouvele Locale
	 * @param bundleName Le nom du bundle (voir {@link #setRootDirectory(String)}
	 * @param locale La locale specifique pour ce bundle
	 * @return Le {@link ResourceBundle} ou null si ce bundle n'as pas �t� ajout� avec la m�thode {@link #addManagedBundle(String)} ou {@link #addManagedBundle(String, Locale)}
	 * @throws RuntimeException Si ce bundle n'a pu �tre r�cup�r�.
	 */
	synchronized public ResourceBundle getBundle(String bundleName,Locale locale)
	{
		if(resources.containsKey(bundleName))
		{
			Locale usedLocale = locale;
			Locale temp = specificLocales.get(bundleName);//Recherche d'une locale specifique au bundle
			
			if(usedLocale == null)
				usedLocale = getDefaultLocale();
			
			ResourceBundle output = null;
			if(!temp.equals(usedLocale))//Si c'est une diff�rente
			{
				specificLocales.put(bundleName, usedLocale);//On met la nouvelle
				logger.debug("Using new specific locale " + usedLocale.toString() + " for bundle " + bundleName);
			}
			else 
				output  = resources.get(bundleName);//Sinon on recup�re le bundle deja charg�
			
			if(output == null)//Si pas bonne locale, ou pas encore charg�
			{
				output = loadBundle(bundleName,usedLocale);
				resources.put(bundleName, output);
			}
			return output;
		}
		return null;
	}
	/**
	 * R�cup�re le bundle manag� par l'objet.</br>
	 * Si le bundle est d�ja charg� l'objet est simplement retourn�.</br>
	 * Sinon il est charg� par cette m�thode.
	 * @param bundleName Le nom du bundle (voir {@link #setRootDirectory(String)}
	 * @return Le {@link ResourceBundle} ou null si ce bundle n'as pas �t� ajout� avec la m�thode {@link #addManagedBundle(String)} ou {@link #addManagedBundle(String, Locale)}
	 * @throws RuntimeException Si ce bundle n'a pu �tre r�cup�r�.
	 */
	synchronized public ResourceBundle getBundle(String bundleName)
	{
		return getBundle(bundleName, specificLocales.get(bundleName));
	}
	/**
	 * Retourne la liste des {@link Locale} disponible pour un bundle sous forme de {@link String}
	 * @param bundle Le bundle pour lequel on veut connaitre les {@link Locale} disponibles
	 * @return Un liste de string repr�sentant les {@link Locale} disponibles pour ce bundle. Une liste vide si aucune disponible
	 */
	public  List<String> getAvailableLocales(String bundle)
	{
		ArrayList<String> output = new ArrayList<>();
		for(Locale locale : getAvailableLocalesObject(bundle))
			output.add(locale.toString());		
		return output;
	}
	/**
	 * Retourne la liste des {@link Locale} disponible pour un bundle.
	 * @param bundle Le bundle pour lequel on veut connaitre les {@link Locale} disponibles
	 * @return Un liste des {@link Locale} disponibles pour ce bundle. Une liste vide si aucune disponible
	 */
	public  List<Locale> getAvailableLocalesObject(String bundle)
	{
		ArrayList<Locale> output = new ArrayList<>();
		Locale[] locales =  Locale.getAvailableLocales();
		for(Locale locale : locales)
		{
			try
			{
				if(ResourceBundle.getBundle(rootDirectory + '.' + bundle, locale).getLocale().equals(locale))
					output.add(locale);
			}
			catch(Throwable ex){}			
		}
		return output;
	}
	/**
	 * Specifie une locale specifique pour ce bundle.
	 * @param bundleName Le nom du bundle pour lequel on veut une {@link Locale} sp�cifique
	 * @param locale La {@link Locale} sp�cifique pour ce {@link ResourceBundle}
	 * @return true si ce bundle est manag� par l'objet, false si il n'est pas manag�
	 * @throws RuntimeException Si le bundle ne sais pas �tre charg� avec cette locale. (voir {@link #getBundle(String,Locale)})
	 */
	public boolean setSpecificLocale(String bundleName,Locale locale)
	{
		if(resources.containsKey(bundleName))
		{
			if(!specificLocales.containsKey(bundleName) || !specificLocales.get(bundleName).equals(locale))
			{
				specificLocales.put(bundleName, locale);
				resources.put(bundleName,loadBundle(bundleName, locale));
			}
			return true;
		}
		return false;
	}
	/**
	 * Retourne la {@link Locale} r��llement  utilis� par le bundle (ex: si il n'a pu �tre charg� avec la locale specifique)
	 * @param bundleName Le nom du bundle pour lequel il faut recup�rer la {@link Locale}
	 * @return la {@link Locale}  du {@link ResourceBundle} ou null si ce bundle n'est pas g�r�.
	 */
	public Locale getEffectiveLocale(String bundleName)
	{
		if(resources.containsKey(bundleName))
			return resources.get(bundleName).getLocale();
		else return null;
	}
	
	/**
	 * Retourne la {@link Locale} sp�cifique utilis� lors de la tentative de chargement du bundle.
	 * @param bundleName Le nom du bundle pour lequel il faut recup�rer la {@link Locale}
	 * @return la {@link Locale} sp�cifique du {@link ResourceBundle} ou null si ce bundle n'est pas g�r�.
	 */
	public Locale getSpecificLocale(String bundleName)
	{
		if(specificLocales.containsKey(bundleName))
			return specificLocales.get(bundleName);
		else return null;
	}
	/**
	 * Retourne la {@link Locale} sp�cifique utilis� lors de la tentative de chargement du bundle.
	 * @param bundle Le  bundle pour lequel il faut recup�rer la {@link Locale} specifique
	 * @return la {@link Locale} sp�cifique du {@link ResourceBundle} ou null si ce bundle n'est pas g�r�.
	 */
	public Locale getSpecificLocale(ResourceBundle bundle)
	{
		for(Entry<String, ResourceBundle> entry : resources.entrySet())
		{
			if(entry.getValue() == bundle)
			{
				return specificLocales.get(entry.getKey());
			}
		}
		return null;
	}
	/**
	 * 
	 * @return Le r�pertoire racine a partir duquel sont charg� les {@link ResourceBundle}
	 */
	public String getRootDirectory()
	{
		return rootDirectory;
	}
	/**
	 * 
	 * @return La {@link Locale} par defaut.
	 */
	public Locale getDefaultLocale()
	{
		return locale;
	}
		
}
