package digitalisp.android.RemoteCotrol;

import java.nio.ByteBuffer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pawel.android.BTHControl.R;

import digitalisp.android.RemoteCotrol.VolumeControlService.VolumeControlBinder;
import digitalisp.android.comunication.CState;
import digitalisp.android.comunication.ConnectionInfo;
import digitalisp.android.comunication.IConnection;
import digitalisp.android.comunication.RemoteDevicesRecord;
import digitalisp.android.comunication.User;

public class VolumeControlMgr implements IVolumeControl
{
	private final String tag = "VolumeControlMgr";
	public RemoteDevicesRecord remoteDevice;
	public View uiView;
	private Context mContext;
	private Cursor mCursor;
	private int recId;
	private volatile int mConnectionId;
	public CState connectionState;
	User user;
	private VolumeControlService mConnectionService;
	private VolumeControl vcObj;
	private boolean mServiceBound;
	
	private TextView connectionStateEdit;
	private TextView volumeStateEdit;	
	private Button mUpBtn;
	private Button mDownBtn;
	private Button mMuteBtn;
	
	public VolumeControlMgr()
	{
		vcObj = new VolumeControl();
	}
	public VolumeControlMgr(VolumeControlService service, Context _context, View _view, Cursor c)
	{		
		mConnectionService = service;
		mContext = _context;
		vcObj = new VolumeControl();
		this.initFromView(_view);
		this.initFromCursor(c);
		mConnectionService.setConnectionInterface(connectionInterface);			
	}
	public synchronized Handler getHandler()
	{
		return this.handler;
	}
	public synchronized void setConnectionId(int _id)
	{
		mConnectionId = _id;
	}
	public synchronized int getConnectionId()
	{
		return mConnectionId;
	}
	
	private boolean initFromView(View _view)
	{
		if (_view != null)
		{
			this.uiView = _view;
			this.connectionStateEdit = (TextView)_view.findViewById(R.id.txtDeviceState);
			this.volumeStateEdit = (TextView)_view.findViewById(R.id.txtDeviceVolume);
			mUpBtn = (Button)_view.findViewById(R.id.btnUp);
			mDownBtn = (Button)_view.findViewById(R.id.btnDown);
			mMuteBtn = (Button)_view.findViewById(R.id.btnMute);			
			mUpBtn.setOnClickListener(upListener);
			mDownBtn.setOnClickListener(downListener);
			mMuteBtn.setOnClickListener(muteListener);
			
			connectionState = CState.Disconnected;
			
			return true;
		}
		return false;
	}
	private boolean initFromCursor(Cursor c)
	{
		if (c != null)
		{
			this.mCursor = c;
			this.remoteDevice = new RemoteDevicesRecord(c);
			
			return true;
		}
		return false;
	}
	
	private IConnection connectionInterface = new IConnection()
	{
		public void onInit(int id, int _state)
		{
			try
			{
			//CState state = CState.values()[_state];		
			//setConnectionId(id);			
			//updateConnectionState(state);
			
			//mConnectionService.Connect(connectionId, user);
			}
			catch (Exception ex)
			{
				Log.d(tag, ex.getMessage());
			}
		}
	};
	/*
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			VolumeControlBinder binder =  (VolumeControlBinder)service;
			mConnectionService = binder.getService();
			mServiceBound = true;
			mConnectionService.setConnectionInterface(connectionInterface);
			initConnection();						
		}
		
		public void onServiceDisconnected(ComponentName arg0)
		{
			mServiceBound = false;
		}
	};
	*/
	
	
	public int getRecId()
	{		
		if (this.remoteDevice != null)
		{
			return this.remoteDevice.mID;
		}
		return -1;
	}
	
	public void initCommunication()
	{
		initConnection();
		
	}
	
	public void startCommunication()
	{
		
		if (mConnectionService != null)
		{
			mConnectionService.Connect(mConnectionId, user);
		}
		
	}
	public void stopComunication()
	{
		if (mConnectionService != null)
		{
			//setVolumeExit();
			mConnectionService.Disconnect(mConnectionId);
			//if (mServiceBound)
			
			//{
				//mContext.unbindService(serviceConnection);
			//}
			
		}
	}
	
	private void initConnection()
	{
		if (mConnectionService != null)
		{
			//mConnectionService.setVolumeControlReceiver(0, this);
			//User user = new User("Comunication", "digitalisp", "empty");
			mConnectionService.InitConnection(remoteDevice, handler, this);
			//mConnectionService.Connect(connectionId, user);
			
		}
	}
	private void sendToService(ByteBuffer buff)
	{
		if (mConnectionService != null)
		{
			mConnectionService.Send(mConnectionId, buff);
		}
	}
	
	
	
	public void setVolumeUp()
	{
		if (vcObj != null)
		{
			vcObj.state = State.Action;
			vcObj.volumeAction = Action.Up;
			this.sendToService(vcObj.packBuffer());
			Log.d(tag, "Volume Up: " + this.toString());
		}
		
	}
	public void setVolumeDown()
	{
		
		if (vcObj != null)
		{
			
			vcObj.state = State.Action;
			vcObj.volumeAction = Action.Down;
			this.sendToService(vcObj.packBuffer());
			Log.d(tag, "Volume Down: " + this.toString());
		}
		
	}
	public void setVolumeMute(boolean _mute)
	{
		if (vcObj != null)
		{
			vcObj.state = State.Action;
			vcObj.volumeAction = Action.Mute;
			vcObj.volumeMute = _mute;
			this.sendToService(vcObj.packBuffer());
			Log.d(tag, "Mute: " + this.toString());
		}
		
	}
	
	public void setVolumeExit()
	{
		if (vcObj != null)
		{
			vcObj.state = State.Action;
			vcObj.volumeAction = Action.Exit;			
			this.sendToService(vcObj.packBuffer());
		}
		
	}
	
	public void volumeChange(VolumeControl _volume) 
	{
		if (_volume != null)
		{
			updateVolumeState(_volume.volumeLevel);
		}
	}
	

	public void volumeUp() {
		// TODO Auto-generated method stub
		
	}

	public void volumeDown() {
		// TODO Auto-generated method stub
		
	}

	public void mute(boolean _mute) {
		// TODO Auto-generated method stub
		
	}
	public void volumeInitialize(VolumeControl _volume)
	{
		
	}
	public void volumeStatusChange(CState s)
	{
		connectionState = s;
		updateConnectionState(s);
	}
	
	private void updateConnectionState(Object _cState)
	{
		CState s = (CState)_cState;
		if (connectionStateEdit != null)
		{
			
				String str = "Stan: " + s.name().toString();
				connectionStateEdit.setText(str);
			
			
		}
	}
	
	private void updateVolumeState(int _value)
	{
		if (volumeStateEdit != null)
		{
			volumeStateEdit.setText(String.format("G³oœnoœæ: {0}", new int[] { _value } ));
		}
	}
	
	private void handleError(Object obj)
	{
		if (obj != null)
		{
			Exception ex = (Exception)obj;
			Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
			
		}
	}
	private void handleMsg(Object _obj)
	{
		String _text = (String)_obj;
		Toast.makeText(mContext, _text, Toast.LENGTH_LONG).show();
	}

	
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case MSG_UP:
				break;
			case MSG_DOWN:
				break;
			case MSG_MUTE:
				break;
			case MSG_STATUS_CHANGE:
				updateConnectionState(msg.obj);
				break;
			case MSG_ERROR:
				handleError(msg.obj);
				break;
			case MSG_MESSAGE:
				handleMsg(msg.obj);
				break;
			default: break;				
			}
		}
	};
	
	private OnClickListener upListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			setVolumeUp();
		}
	};
	
	private OnClickListener downListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			setVolumeDown();			
		}
	};
	private OnClickListener muteListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			boolean pres = !(mMuteBtn.isPressed());					
			mMuteBtn.setPressed(pres);
			setVolumeMute(pres);			
		}
	};
	
	public static final int MSG_UP = 1;
	public static final int MSG_DOWN = 2;
	public static final int MSG_MUTE = 3;
	public static final int MSG_CHANGE = 4;
	public static final int MSG_STATUS_CHANGE = 5;
	public static final int MSG_INITIALIZE = 6;
	public static final int MSG_ERROR = 7;
	public static final int MSG_MESSAGE = 8;
	
	
	
}

