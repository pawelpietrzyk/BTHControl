package digitalisp.android.comunication.frame;

import java.nio.ByteBuffer;

import com.pawel.android.BTHControl.TOF;
import com.pawel.android.BTHControl.TOS;

import android.os.Handler;


public class Frame {	
	private final byte START_BYTE_VALUE = 66;
	private final byte END_BYTE_VALUE = 69;	
	
		
	private String _format;
	private ByteBuffer dataBytes;
	private Object[] _objs;	
	private boolean isReady;	
	private FRD destination;
	
	
	public Frame() 
	{		
		this.isReady = false;	
	
	}
	public Frame(byte[] buff) 
	{		
		this.isReady = false;
		this.destination = FRD.READ;
		try {
		if (this.initReceivedFrame(buff) == 0) {
	
			this.decodeFrame(dataBytes); 
	
			this.isReady = true;
		} else {
			this.isReady = false;			
		}
		} catch (Exception ex) {
		
			this.isReady = false;
			setObjects(null);		
		}
	}
	
	public Frame(Object[] objs, String f) {		
		this.isReady = false;
		
		this.destination = FRD.WRITE;
		setObjects(objs);
		
		if (this.initToSendFrame(objs, f) == 0) 
		{
			this.isReady = true;
		} 
		else 
		{
			this.isReady = false;
		}
		
	}
	
	public void setObjects(Object objs[]) {
		this._objs = objs;
	}
	public Object[] getObjects() {
		return this._objs;
	}
	public byte[] getBytes() {
		return dataBytes.array();
	}
	public void setFormat(String f) {
		this._format = f;
	}
	public String getFormat() {
		return this._format;
	}
	public void setDestination(FRD dest) {
		this.destination = dest;
	}
	public FRD getDestination() {
		return this.destination;
	}
	public boolean getReady() {
		return this.isReady;
	}
			
	public ByteBuffer codeFrame(Object[] data, String format)	
	{		
		try 
		{
			int ccf = format.length();
			//Log("Code fmt: " + String.valueOf(ccf));
			int cap = getCapacity(format);
			//Log("Code cap: " + String.valueOf(cap));
			ByteBuffer tbuf;
			tbuf = ByteBuffer.allocate(ccf + cap + 1);
			tbuf.put((byte)ccf);
			tbuf.put(format.getBytes());
			if (ccf > data.length)
			{
				
				return null;
			}
			for (int i = 0; i < ccf; i++) 
			{
				char c = format.charAt(i);
				//Log("Code. Char: " + String.valueOf(c));
				switch (c) 
				{
					case 'B':
						//	Boolean bc = (Boolean)data[i];
						//boolean b = bc.booleanValue();
						//if (b) { tbuf.put((byte)1); } else { tbuf.put((byte)0); }
						Byte b = (Byte)data[i];
						//Log("Byte: " + String.valueOf(b.byteValue()));
						tbuf.put(b.byteValue());
						break;
					case 'F':
						//Log("Float: " + data[i].toString());						
						float f;
						try
						{
							Float fc = (Float)data[i];
							f = fc.floatValue();
							//Log("Float: " + String.valueOf());
							
						}
						catch (Exception ex)
						{
							f = 0;
						}
						tbuf.putFloat(f);
						//Log("Float put: " + String.valueOf(f));
						break;
					default: break;
				}
			}
			
			return tbuf;
		}
		catch (Exception ex) 
		{
			
			return null;
		}
	}	
	
	public void decodeFrame(ByteBuffer buf)
	{
		//try {
		if (buf == null) {
			
			return;
		}
		byte count = buf.get();
		
		
		byte bformat[] = new byte[count];
		
		buf.get(bformat, 0, count);
		String format = String.valueOf(bformat);
		
		
		Object data[] = new Object[count];
		
		//this.Log(format);
		for (int i = 0; i < count; i++) 
		{
			//char c = format.charAt(i);
			//Log("Char: " + String.valueOf(bformat[i]));
			
			if ((char)bformat[i] == 'B')
			{
				//Log("B letter");
				data[i] = buf.get();
			}
			if ((char)bformat[i] == 'F') 
			{
				//Log("F letter");
				data[i] = buf.getFloat();
			}
						
		}
		
		this._format = format;
		this._objs = data;
		
		//return data;
		//}
		//catch (Exception ex)
		//{
		//	this.Log(ex.getMessage());
		//	return null;
		//}
		
	}
	
	private int getCapacity(String format) {
		int count = 0;
		for (int i = 0; i < format.length(); i++) {
			char c = format.charAt(i);
			switch (c) {
			case 'B': count += 1; break;
			case 'I': count += 4; break;
			case 'F': count += 4; break;
			case 'L': count += 1; break;				
			}
		}
		return count;
	}
	private int initReceivedFrame(byte[] buff)
	{
		try {
			//this.Log("In initReceivedFrame, bufsize: "+String.valueOf(buff.length));
			int bcc = -1;
			int start = -1;		
			boolean in = false;
			for (int i = 0; i < buff.length; i++) {			
				if (buff[i] == END_BYTE_VALUE) {
					in = false;				
					break;					
				}
				
				if (in) {
					bcc++;
				}				
				if (buff[i] == START_BYTE_VALUE) {
					if (!in) {					
						in = true;
						start = i;
						bcc = 0;
					}
				}
			}
			if (start == -1) { return 1; }
			start++;			
			dataBytes = ByteBuffer.wrap(buff, start, bcc);
			//this.Log("Start: " + String.valueOf(start) + ", Count: " + String.valueOf(bcc));
			//this.Log("DataBytes: " + String.valueOf(dataBytes.array()));
			} catch (Exception ex) {
		
				return 1;
			}
			return 0;
	}
	
	private int initToSendFrame(Object[] data, String format) {		
		if ((data != null) && (format != "")) {
			ByteBuffer buf = this.codeFrame(data, format);
			this.dataBytes = ByteBuffer.allocate(buf.capacity() + 2);
			
			this.dataBytes.put(START_BYTE_VALUE);
			this.dataBytes.put(buf.array());
			this.dataBytes.put(END_BYTE_VALUE);
			
			return 0;
		}
		return 1;
	}
		
	
	
	public static TOF getType(byte type)
	{
		if (type == TOF.INIT.getByte()) {
			return TOF.INIT;
		}
		if (type == TOF.MESSAGE.getByte()) {
			return TOF.MESSAGE;
		}
		return TOF.UNKNOWN;
	}
	public static TOS getSignType(byte type) {
		if (type == TOS.UP.getByte()) {
			return TOS.UP;
		}
		if (type == TOS.DOWN.getByte()) {
			return TOS.DOWN;
		}
		if (type == TOS.MUTE.getByte()) {
			return TOS.MUTE;
		}
		return TOS.UNK;
	}
}
