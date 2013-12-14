package kezine.minichat.tools;

public class CustomCommonException extends Exception
{
	private static final long serialVersionUID = 1L;
	private int _Identifier;
	private String _Complement;
	public CustomCommonException(String message)
	{
		this(message,-1,"");
	}
	public CustomCommonException(String message, int identifier,String complement)
	{
		super(message);
		_Identifier = identifier;
		_Complement =complement;
	}
	public int getIdentifier()
	{
		return _Identifier;
	}
	public String getComplement()
	{
		return _Complement;
	}
}
