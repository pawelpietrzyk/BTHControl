package digitalisp.android.comunication.frame;

public enum TOF {
	UNKNOWN(0),
	INIT(73),
	MESSAGE(77),	
	SIGN(83);
	private final int id;
	TOF(int id) {
		this.id = id;
	}
	public int getId()
	{
		return this.id;
	}
	public byte getByte()
	{
		return (byte)this.id;
	}
}
