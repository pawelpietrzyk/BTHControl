package com.pawel.android.BTHControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;

public class BTHDevice {
	
	private List<BTHDeviceStateListener> _dsList = new ArrayList<BTHDeviceStateListener>();
	private List<OnVolumeChangeListener> _vcList = new ArrayList<OnVolumeChangeListener>();
	private List<onDeviceException> exList = new ArrayList<onDeviceException>();
	private String _address;
	private BluetoothAdapter _adapter;
	private BTHConnection _connection;
	private BTHDeviceState _state;
	private int _volumeMax;
	private int _volumeMin;
	private int _volumeLevel;
	private boolean _volumeMute;	
	
	
	public BTHDevice(String adres) {
		this._address = adres;
		this._adapter = BluetoothAdapter.getDefaultAdapter();
		setState(BTHDeviceState.Disconnected);
		if (this._adapter == null) {
			
		}
	}
	public BTHDevice(String adres, boolean init) {
		this._address = adres;
		this._adapter = BluetoothAdapter.getDefaultAdapter();
		setState(BTHDeviceState.Initialized);
		if (this._adapter == null) {
			
		} else {
			Connect();			
		}
		
	}
	
	public int Connect()
	{
		try {
			
		
			if ((_address != "") && (_address != "NODEVICE")) {
				setState(BTHDeviceState.Connecting);
				this._connection = new BTHConnection(_address, Settings.mUUID, this.devHandler);
				this._connection.start();
				
			} else {return 1; }
		} catch (Exception ex) {
			fireException(ex);
			return 2;
		}
		return 0;
	}
	public int Disconnect()
	{
		if (_connection != null) {
			try {
				setState(BTHDeviceState.Disconecting);
				exit();
				this._connection.StopReading();
				this._connection.cancel();
				setState(BTHDeviceState.Disconnected);
			} catch (Exception ex) {
				return 2;
			}			
		} else { return 1; }		
		return 0;
	}	
	public void setState(BTHDeviceState newState) {
		this._state = newState;
		fireStateChange();
	}	
	public BTHDeviceState getState() {
		return this._state;
	}	
	public int setVolume(float[] data) {
		try {
			if (data.length == 0) {
				Disconnect();			
			}
			if (data.length > 2) {
				_volumeMax = (int)(data[0] * 100);
				_volumeMin = (int)(data[1] * 100);
				_volumeLevel = (int)(data[2] * 100);
				_fireVolChange();
			}
		} catch (Exception ex) {
			return 1;
		}
		return 0;
	}
	public int setVolume(Object[] data) {
		try {
			if (data.length == 0) {
				Disconnect();			
			}
			if (data.length > 2) {
				Float f1 = (Float)data[0];
				Float f2 = (Float)data[1];
				Float f3 = (Float)data[2];				
				_volumeMax = (int)(f1.floatValue() * 100);
				_volumeMin = (int)(f2.floatValue() * 100);
				_volumeLevel = (int)(f3.floatValue() * 100);
				_fireVolChange();
			}
		} catch (Exception ex) {
			return 1;
		}
		return 0;
	}
	
	public void setVolumeLevel(float value, boolean received) {
		this._volumeLevel = (int)(value * 100);
		if (received) { _fireVolChange(); }
		else { _send(TOF.MESSAGE); }
	}
	public void setVolumeLevel(float value) {
		setVolumeLevel(value, false);
	}
	public void setVolumeLevel(int value) {
		setVolumeLevel(value, false);
	}
	public void setVolumeLevel(int value, boolean received) {
		this._volumeLevel = value;
		if (received) { _fireVolChange(); }
		else { _send(TOF.MESSAGE); }
	}
	
	public int setVolumeLevel(Object obj) {
		try {
			if (obj != null) {
				Float f = (Float)obj;
				setVolumeLevel(f.floatValue(), true);				
				return 0;
			}
		} catch (Exception ex) {
			return 2;
		}
		return 1;		
	}
	public Object getVolumeLevel() {
		try {
			//Float fl = Float.valueOf(((float)this._volumeLevel / (float)100));
			//return fl;
			return (this._volumeLevel / 100);
		} catch (Exception ex) {
			this.fireException("getVolumeLevel: " + ex.getMessage());
			return 0;
		}	
	}
	public int setVolumeMax(Object obj) {
		try {
			if (obj != null) {
				Float f = (Float)obj;
				_volumeMax = (int)(f.floatValue() * 100);				
				return 0;
			}
		} catch (Exception ex) {
			return 2;
		}
		return 1;		
	}
	public Object getVolumeMax() {
		try {			
			//return Float.valueOf((float)this._volumeMax);
			return this._volumeLevel;
		} catch (Exception ex) {
			this.fireException("getVolumeMax: " + ex.getMessage());
			return 0;
		}
	}
	public int setVolumeMin(Object obj) {
		try {
			if (obj != null) {
				Float f = (Float)obj;
				_volumeMin = (int)(f.floatValue() * 100);
				return 0;
			}
		} catch (Exception ex) {
			return 2;
		}
		return 1;		
	}
	public Object getVolumeMin() {
		try {
			//return Float.valueOf((float)this._volumeMin);
			return this._volumeMin;
		} catch (Exception ex) {
			this.fireException("getVolumeMin: " + ex.getMessage());
			return 0;
		}
	}
	public void setVolumeMute(boolean value) {
		setVolumeMute(value, false);
	}
	public void setVolumeMute(boolean value, boolean received) {		
		this._volumeMute = value;
		if (received) { this._fireVolChange(); }
		else { this._send(TOS.MUTE); }
	}

	public int setVolumeMute(Object obj) {
		try {
			if (obj != null) {
				Byte b = (Byte)obj;
				if (b.byteValue() == (byte)0) {
					setVolumeMute(false, true);
				}
				if (b.byteValue() == (byte)1) {
					setVolumeMute(true, true);
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
			return this._volumeMute;
		} catch (Exception ex) {
			return null;
		}
	}
	
	
	public void volumeUP() { this._send(TOS.UP); }
	public void volumeDOWN() { this._send(TOS.DOWN); }
	public void volumeMUTE() { this._send(TOS.MUTE); }
	public void exit() { this._send(TOS.EXIT); }
	public void initialization() { this._send(TOF.INIT); }
	
	
	
	
	
	public synchronized void addOnVolumeChangeListener(OnVolumeChangeListener l) {
		_vcList.add(l);
	}
	public synchronized void removeOnVolumeChangeListener(OnVolumeChangeListener l) {
		_vcList.remove(l);
	}
	private synchronized void _fireVolChange() {
		VolumeChangeEvent event = new VolumeChangeEvent(this, _volumeLevel, _volumeMin, _volumeMax, _volumeMute);
		if (_vcList != null) {
			Iterator<OnVolumeChangeListener> i = _vcList.iterator();
			while (i.hasNext()) {
				((OnVolumeChangeListener)i.next()).onVolumeChange(event);
			}
		}		
	}
	
	
	public synchronized void addStateListener(BTHDeviceStateListener l) {
		_dsList.add(l);
	}
	public synchronized void removeStateListener(BTHDeviceStateListener l) {
		_dsList.remove(l);
	}
	
	private synchronized void fireStateChange() {
		DeviceStateEvents state = new DeviceStateEvents(this, getState());
		if (_dsList != null) {
			Iterator<BTHDeviceStateListener> list = _dsList.iterator();
			while (list.hasNext()) {
				((BTHDeviceStateListener)list.next()).bthDeviceStateReceived(state);
			}
		}
		
	}
	
	public synchronized void addExceptionListener(onDeviceException l) {
		exList.add(l);
	}
	public synchronized void removeExceptionListener(onDeviceException l) {
		exList.remove(l);
	}
	
	private synchronized void fireException(Exception ex) {
		//DeviceStateEvents state = new DeviceStateEvents(this, getState());
		if (exList != null) {
			Iterator<onDeviceException> list = exList.iterator();
			while (list.hasNext()) {
				((onDeviceException)list.next()).onException(ex);
			}
		}
		
	}
	private synchronized void fireException(String msg) {
		//DeviceStateEvents state = new DeviceStateEvents(this, getState());
		if (exList != null) {
			Iterator<onDeviceException> list = exList.iterator();
			while (list.hasNext()) {
				((onDeviceException)list.next()).onException(msg);
			}
		}
		
	}
	
	private int _send(TOS signal) {
		try {
			BTHFrame data = _createFrame(signal);
			if (data != null && _connection !=null) {
				_connection.WriteData(data.getBytes());
				return 0;
			}			
		} catch (Exception ex) {
			return 2;
		}
		return 1;				
	}
	private int _send(TOF typeOfFrame) {
		try {
			BTHFrame data = _createFrame(typeOfFrame);
			if (data == null)
			{
				//fireException("_send: data is null");
				return 2;
			}
			fireException("_send: ");
			if (_connection !=null) {
				//fireException("SEND typeOfFrame");
				_connection.WriteData(data.getBytes());
				return 0;
			}			
		} catch (Exception ex) {
			fireException(ex);
			return 2;
		}
		return 1;				
	}
	
	private int _reveived(BTHFrame frame) {
		try {
			//fireException("RECEIVED");
			if (frame == null) {
				fireException("FRAME IS NULL");
				return 2; 
			}
			
			
			Object[] objs = frame.getObjects();
			if (objs != null) {
				//fireException(String.valueOf(objs.length));
				//String frm = frame.getFormat();
				//fireException(frm);
				
				
				Byte b1 = (Byte)objs[0];
				switch (getType(b1.byteValue())) {
				case INIT:
					fireException("INIT_FRAME");
					setVolumeLevel(objs[1]);
					setVolumeMax(objs[2]);
					setVolumeMin(objs[3]);
					setState(BTHDeviceState.Connected);
					_send(TOF.INIT);
					break;
				case MESSAGE:
					setVolumeLevel(objs[1]);
					setVolumeMax(objs[2]);
					setVolumeMin(objs[3]);
					setVolumeMute(objs[4]);					
					break;				
				case SIGN: 
					Byte b2 = (Byte)objs[1];
					switch (getSignType(b2.byteValue())) {
					case UP: break;
					case DOWN: break;
					case MUTE: break;
					case EXIT: Disconnect(); break;
					}
					break;
				}
				
			} else {
				fireException("objects are null");
			}
		} catch (Exception ex) {
			fireException(ex);
			return 1;
		}		
		return 0;
	}
	
	private BTHFrame _createFrame(TOF type, TOS sign) {
		try {
			Object[] objs = null;
			String format = "";
			switch (type) {
			case INIT:
				format = "BFFF";
				objs = new Object[4]; 
				objs[0] = Byte.valueOf(TOF.INIT.getByte());
				//objs[0] = TOF.INIT.getByte();
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
				//this.fireException("_createFrame: objs are not null");
				BTHFrame fr = new BTHFrame(this.devHandler, objs, format); 
				return fr; 
				//return null;
			}
			else 
			{
				this.fireException("_createFrame: objs are null");
				return null;
			}
			
		} 
		catch (Exception ex) 
		{
			this.fireException(ex);
			return null;
		}		
	}
	private BTHFrame _createFrame(TOF type) {		
		return _createFrame(type, TOS.UNK);
	}
	private BTHFrame _createFrame(TOS sign) {		
		return _createFrame(TOF.SIGN, sign);
	}
	private TOF getType(byte type)
	{
		if (type == TOF.INIT.getByte()) {
			return TOF.INIT;
		}
		if (type == TOF.MESSAGE.getByte()) {
			return TOF.MESSAGE;
		}
		return TOF.UNKNOWN;
	}
	private TOS getSignType(byte type) {
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
		
	private final Handler devHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
        	try { 
        	if (msg.what == MSG.CNT_NO_BTH.getId()) {
            	
            }
            if (msg.what == MSG.CNT_SUCCESSED.getId()) {
            	fireException("CNT_SUCCESSED");
            	if (_connection != null) {
            		_connection.InitServices();            	
            	}
            }
            if (msg.what == MSG.CNT_FAILED.getId()) {
            	fireException("CNT_FAILED");
            }
            if (msg.what == MSG.SVC_INITIALIZED.getId()) {
            	fireException("SVC_INITIALIZED");
            	
            	BTHService svc = (BTHService)msg.obj;
            	if (svc != null) {
            		if (svc.GetType() == "READ") {
            			_connection.StartReading();
            		}
            	}
            }
            if (msg.what == MSG.SVC_START_READING.getId()) {
            	fireException("SVC_START_READING");
            	//initialization();
            }
            if (msg.what == MSG.SVC_STOP_READING.getId()) {
            	fireException("SVC_STOP_READING");
            	//initialization();
            }
            if (msg.what == MSG.SVC_READ.getId()) {
            	byte[] readBuf = (byte[]) msg.obj;
            	fireException("SVC_READ" + String.valueOf(readBuf));
            	BTHFrame fr = new BTHFrame(devHandler, readBuf); 
            	_reveived(fr);
            }
            if (msg.what == MSG.SVC_WRITE.getId()) {
            	
            }
            if (msg.what == MSG.FRAME_MSG.getId()) {
            	fireException(msg.obj.toString());
            }
            	
        	
        	} catch (Exception ex) {
        		fireException(ex);
        	}
        	
        }
	};
	
}
