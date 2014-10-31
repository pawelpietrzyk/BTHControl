package digitalisp.android.comunication;

import java.nio.ByteBuffer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectionMgr extends Handler {
	private final String TAG = "ConnectionMgr";
	
	public Connection connection;
	//private volatile Connection connection;
	private volatile Thread startThread;
	private volatile int mConnectionId;
	private User mUser;
	private String mCmd;
	private boolean writing;
	private int writingError;
	private int mDeviceId;
	private RemoteDevicesRecord mDevice;
	
	
	public ConnectionMgr()
	{
		mDevice = null;
	}
	public ConnectionMgr(RemoteDevicesRecord _device) 
	{
		if (_device != null)
		{		
			mDevice = _device;
			
		}		
	}
	
	public boolean isReady()
	{
		return (mDevice != null);
	}
	
	public void run()
	{
		Connect(mUser);
		Thread thisThread = Thread.currentThread();
		while (connection == thisThread)
		{
			
		}
	}
	public void setConnectionId(int _id)
	{
		this.mConnectionId = _id;
	}
	public int getConnectionId()
	{
		return this.mConnectionId;
	}
	public void setCmd(String _cmd)
	{
		this.mCmd = _cmd;		
	}
	public void setUser(User _user)
	{
		this.mUser = _user;
	}
	public CState getCState()
	{
		if (connection != null)
		{
			return connection.getConnectionState();
		}
		return CState.Disconnected;
	}
	public boolean isActive()
	{
		if (connection != null)
		{
			return connection.isAlive();
		}
		return false;
	}
	public boolean isConnected()
	{
		if (connection != null)
		{
			return connection.isConnected();
		}
		return false;
		
	}
	public ConnectionInfo getConnectionInfo()
	{
		if (connection != null)
		{
			return connection.getConnectionInfo();			
		}
		return null;
	}
	public void Connect(User _user)
	{		
		mUser = _user;
		if (connection == null)
		{
			connection = Connection.construct(mDevice, mUser, this);
		}
		
		
		if (connection != null)
		{
			try
			{
				//connection.Connect(mUser);
				connection.start();
			}
			catch (Exception ex)
			{
				Log.d(TAG, ex.getMessage());
			}
			
		}
	}
	public void Disconnect()
	{
		if (connection != null)
		{
			connection.Disconnect();
			try {
				connection.join();
			} catch (InterruptedException e) {
				Log.d(TAG, e.getMessage());
			}
			connection = null;
		}
	}
	
	public void Post(String _msg)
	{
		if (connection != null)
		{
			writing = true;
			connection.write(_msg.getBytes());
		}
	}
	public void Write(ByteBuffer _buffer)
	{
		if (connection != null)
		{
			writing = true;
			connection.write(_buffer.array());
		}
	}
	public void Receive(String _msg)
	{
		
	}
	public void Read(ByteBuffer _buffer)
	{
		
	}

	public void onRead(Object _receivedObj) 
	{
		//byte[] bytes = (byte[])_receivedObj;
		ByteBuffer buf = (ByteBuffer)_receivedObj;
		
		this.Read(buf);
		
		//String resp = new String(bytes);
		//this.Receive(resp);		
		
	}

	public void onMessage(Object _msg) {
		// TODO Auto-generated method stub

	}

	public void onStateChange(CState _state) 
	{
		
	}

	public void onError(Object _error) {
		

	}
	
	public void handleMessage(Message msg)
	{
		if (msg.what == MSG.ON_READ.getId())
		{
			this.onRead(msg.obj);
		}
		if (msg.what == MSG.ON_ERROR.getId())
		{
			this.onError(msg.obj);
		}
		if (msg.what == MSG.ON_MESSAGE.getId())
		{				
			this.onMessage(msg.obj);
		}
		if (msg.what == MSG.ON_STATE_CHANGE.getId())
		{	
			if (msg.obj != null)
			{
				CState st = (CState)msg.obj;
				this.onStateChange(st);
			}
			
		}			
	}

}
