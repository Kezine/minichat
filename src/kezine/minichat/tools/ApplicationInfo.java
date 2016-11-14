package kezine.minichat.tools;

import java.io.IOException;
import java.io.InputStream;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public  class ApplicationInfo
{
	private int version;
	private int build;
	private String appPublisher;
	private String appUrl;
	private String appName;
	
	public ApplicationInfo()
	{
		this(0,0,"","","");
	}
	public ApplicationInfo(int version, int build, String appPublisher, String appUrl, String appName)
	{
		super();
		this.version = version;
		this.build = build;
		this.appPublisher = appPublisher;
		this.appUrl = appUrl;
		this.appName = appName;
	}

	public static ApplicationInfo load(InputStream iniInfo) throws InvalidFileFormatException, IOException
	{
		Wini info = new Wini(iniInfo);
		ApplicationInfo output = new ApplicationInfo();
		output.setAppName(info.get("Info", "AppName"));
		try
		{
			output.setVersion(Integer.parseInt(info.get("Info", "AppVersion")));
			output.setBuild(Integer.parseInt(info.get("Info", "Build")));
		}
		catch(NumberFormatException ex)
		{
			throw new InvalidFileFormatException("AppVersion and Build fields should be valid integer");
		}		
		output.setAppPublisher(info.get("Info", "AppPublisher"));
		output.setAppUrl(info.get("Info", "AppURL"));
		return output;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public int getBuild()
	{
		return build;
	}

	public void setBuild(int build)
	{
		this.build = build;
	}

	public String getAppPublisher()
	{
		return appPublisher;
	}

	public void setAppPublisher(String appPublisher)
	{
		this.appPublisher = appPublisher;
	}

	public String getAppUrl()
	{
		return appUrl;
	}

	public void setAppUrl(String appUrl)
	{
		this.appUrl = appUrl;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	
}
