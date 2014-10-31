package digitalisp.android.comunication;

public enum MSG 
{	
	ON_READ(1),
	ON_ERROR(2),
	ON_MESSAGE(3),
	ON_STATE_CHANGE(4)
	;
	
	private final int id;
	MSG(int id) {
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
