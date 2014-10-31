package digitalisp.android.comunication.frame;

public enum MSG {
	
	CNT_SUCCESSED(100),
	CNT_FAILED(101),
	CNT_NO_BTH(102),
	CNT_LOST(103),
	CNT_EXIT(104),
	SVC_INITIALIZED(200),
	SVC_READ(201),
	SVC_WRITE(202),
	SVC_START(203),
	SVC_START_READING(204),
	SVC_START_WRITING(205),
	SVC_STOP(206),
	SVC_STOP_READING(207),
	SVC_STOP_WRITING(208),
	FRAME_MSG(300)
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
