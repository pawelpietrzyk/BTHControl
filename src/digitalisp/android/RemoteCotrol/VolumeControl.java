package digitalisp.android.RemoteCotrol;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import com.pawel.android.BTHControl.BTHDeviceState;
import com.pawel.android.BTHControl.BTHFrame;
import com.pawel.android.BTHControl.TOF;
import com.pawel.android.BTHControl.TOS;

import digitalisp.android.comunication.frame.Frame;
import digitalisp.android.providers.Finanses.Categories;

public class VolumeControl {

	public int volumeMax;
	public int volumeMin;
	public int volumeLevel;
	public boolean volumeMute;
	public State state;
	public Action volumeAction;
	
	public VolumeControl()
	{
		
	}
	
	public synchronized State getState()
	{
		return state;
	}
	public synchronized String getStateString()
	{
		return getState().name();
	}
	public synchronized Action getAction()
	{
		return this.volumeAction;
		
	}
	public synchronized String getActionString()
	{
		return this.getAction().name();
		
	}
	
	public static VolumeControl createFromFrame(Frame _frame)
	{
		VolumeControl vc = new VolumeControl();		
		
		if (_frame != null)
		{
			Object[] objs = _frame.getObjects();
			if (objs != null) 
			{
				Byte b1 = (Byte)objs[0];
				switch (Frame.getType(b1.byteValue())) 
				{
				case INIT:
					vc.state = State.Action;
					vc.setVolume(objs);					
					break;
				case MESSAGE:
					vc.state = State.Message;
					vc.setVolume(objs);
					vc.setVolumeMute(objs[4]);									
					break;				
				case SIGN: 
					vc.state = State.Action;
					Byte b2 = (Byte)objs[1];
					switch (Frame.getSignType(b2.byteValue())) 
					{
					case UP: 
						vc.volumeAction = Action.Up; 
						break;
					case DOWN:
						vc.volumeAction = Action.Down;
						break;
					case MUTE: 
						vc.volumeAction = Action.Mute;
						break;
					case EXIT: 
						vc.volumeAction = Action.Exit;
						break;
					}
					break;
				}
				
			} 
			else 
			{			
			}
			
		}	
		return vc;
	}
	
	
	public int setVolume(float[] data) 
	{
		try 
		{			
			if (data.length > 2) 
			{
				volumeMax = (int)(data[0] * 100);
				volumeMin = (int)(data[1] * 100);
				volumeLevel = (int)(data[2] * 100);				
			}
		} catch (Exception ex) {
			return 1;
		}
		return 0;
	}
	public int setVolume(Object[] data) {
		try 
		{		
			if (data.length > 2) {
				Float f1 = (Float)data[0];
				Float f2 = (Float)data[1];
				Float f3 = (Float)data[2];				
				volumeMax = (int)(f1.floatValue() * 100);
				volumeMin = (int)(f2.floatValue() * 100);
				volumeLevel = (int)(f3.floatValue() * 100);
				
			}
		} catch (Exception ex) {
			return 1;
		}
		return 0;
	}
	
	public void setVolumeLevel(float value) 
	{
		this.volumeLevel = (int)(value * 100);		
	}	
	
	public void setVolumeLevel(int value) 
	{
		this.volumeLevel = value;
	}
	
	public int setVolumeLevel(Object obj) {
		try 
		{
			if (obj != null) 
			{
				Float f = (Float)obj;
				setVolumeLevel(f.floatValue());				
				return 0;
			}
		} 
		catch (Exception ex) 
		{
			return 2;
		}
		return 1;		
	}
	public Object getVolumeLevel()
	{
		try 
		{		
			return (this.volumeLevel / 100);
		} 
		catch (Exception ex) 
		{		
			return 0;
		}	
	}
	public int setVolumeMax(Object obj) 
	{
		try 
		{
			if (obj != null) 
			{
				Float f = (Float)obj;
				volumeMax = (int)(f.floatValue() * 100);				
				return 0;
			}
		} 
		catch (Exception ex) 
		{
			return 2;
		}
		return 1;		
	}
	public Object getVolumeMax() 
	{
		try 
		{		
			return this.volumeLevel;
		} 
		catch (Exception ex) 
		{		
			return 0;
		}
	}
	public int setVolumeMin(Object obj) {
		try 
		{
			if (obj != null) 
			{
				Float f = (Float)obj;
				volumeMin = (int)(f.floatValue() * 100);
				return 0;
			}
		} 
		catch (Exception ex) 
		{
			return 2;
		}
		return 1;		
	}
	public Object getVolumeMin() {
		try 
		{			
			return this.volumeMin;
		} 
		catch (Exception ex) 
		{		
			return 0;
		}
	}
	
	public void setVolumeMute(boolean value) 
	{		
		this.volumeMute = value;		
	}

	public int setVolumeMute(Object obj) {
		try {
			if (obj != null) {
				Byte b = (Byte)obj;
				if (b.byteValue() == (byte)0) {
					setVolumeMute(false);
				}
				if (b.byteValue() == (byte)1) {
					setVolumeMute(true);
				}	
				
				return 0;
			}
		} catch (Exception ex) {
			return 2;
		}
		return 1;		
	}
	public Object getVolumeMute() {
		try {
			return this.volumeMute;
		} catch (Exception ex) {
			return null;
		}
	}

	//TODO przenieœæ kod do klasy Frame po utworzeniu uniwersalnej metody z obs³ug¹ formatowania 
	public Frame createFrame(TOF type, TOS sign) {
		try {
			Object[] objs = null;
			String format = "";
			switch (type) {
			case INIT:
				format = "BFFF";
				objs = new Object[4]; 
				objs[0] = Byte.valueOf(TOF.INIT.getByte());			
				objs[1] = getVolumeLevel();
				objs[2] = getVolumeMax();
				objs[3] = getVolumeMin();
				break;
			case MESSAGE: 
				objs = new Object[5];
				format = "BFFFB";
				objs[0] = Byte.valueOf(TOF.MESSAGE.getByte());
				objs[1] = getVolumeLevel();
				objs[2] = getVolumeMax();
				objs[3] = getVolumeMin();
				objs[4] = getVolumeMute();
				break;			
			case SIGN: 
				objs = new Object[2];
				format = "BB";
				objs[0] = Byte.valueOf(TOF.SIGN.getByte());
				objs[1] = Byte.valueOf(sign.getByte());
				break;
			}
			
			if (objs != null) 
			{				
				Frame fr = new Frame(objs, format); 
				return fr; 
				
			}
			else 
			{				
				return null;
			}
			
		} 
		catch (Exception ex) 
		{
			return null;
		}		
	}
	public Frame createFrame(TOF type) {		
		return createFrame(type, TOS.UNK);
	}
	public Frame createFrame(TOS sign) {		
		return createFrame(TOF.SIGN, sign);
	}
	
	
	public ByteBuffer packVolumeControl()
	{
		Frame frame = new Frame();
		if (this != null)
		{
			switch (this.getState())
			{
			case Initialize:
				frame = this.createFrame(TOF.INIT);
				break;
			case Message:
				frame = this.createFrame(TOF.MESSAGE);
				break;
			case Action:
				switch (this.volumeAction)
				{
				case Up:
					frame = this.createFrame(TOS.UP);
					break;
				case Down:
					frame = this.createFrame(TOS.DOWN);
					break;
				case Mute:
					frame = this.createFrame(TOS.MUTE);
					break;
				case Exit:
					frame = this.createFrame(TOS.EXIT);
					break;
				default: break;
				}
				break;				
			default: break;
				
			}
		}
		return ByteBuffer.wrap(frame.getBytes());
	}
	
	public JSONObject pack()
	{
		
		try {
			JSONObject json = new JSONObject();
			json.put("type", state);
			json.put("action", volumeAction);
			json.put("max", volumeMax);
			json.put("min", volumeMin);
			json.put("level", volumeLevel);
			return json;
		} catch (JSONException e) {
			return null;
		}		
	}
	public ByteBuffer packBuffer()
	{
		JSONObject obj = this.pack();
		if (obj != null)
		{			
			ByteBuffer buff = ByteBuffer.wrap(obj.toString().getBytes());
			return buff;
		}
		return null;
	}
	public final static String STATE_INIT = "digitalisp.android.VolumeControl.STATE_INIT";
	public final static String ACTION_VOLUME_UP = "digitalisp.android.VolumeControl.ACTION.VOLUME_UP";
	public final static String ACTION_VOLUME_DOWN = "digitalisp.android.VolumeControl.ACTION.VOLUME_DOWN";
	public final static String ACTION_VOLUME_MUTE = "digitalisp.android.VolumeControl.ACTION.VOLUME_MUTE";
	
	public final static String TYPE_INIT = "init";
	public final static String TYPE_CMD = "cmd";
	public final static String TYPE_MSG = "msg";
	
	public final static int ACTION_UP = 1;
	public final static int ACTION_DOWN = 2;
	public final static int ACTION_MUTE = 3;
	public final static int ACTION_EXIT = 4;
	
	
	
	
	
	
	

	
}
