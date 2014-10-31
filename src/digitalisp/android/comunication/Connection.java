package digitalisp.android.comunication;

import java.nio.ByteBuffer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import digitalisp.android.comunication.bth.BTHConnection;
import digitalisp.android.comunication.net.NETConnection;



public abstract class Connection extends Thread 
{
	//private List<ConnectionReceiver> mReceiver = new ArrayList<ConnectionReceiver>();
	//private List<ServiceReceiver> mSvcReceiver = new ArrayList<ServiceReceiver>();
	private final String TAG = "Connection";
	private Handler mReceiver;
	private Object 		mAddress;
	private CState 		mCState;
	private ByteBuffer	mBufferReceived;
	private ByteBuffer	mBufferSent;
	private User 		mUser;
	private boolean		mKeepConnect = true;
	protected ComService svcCom;	
	protected ComService svcWrite;
	
	
	
	
	
	public Connection(Object _address, Handler _receiver)
	{
		
		this.setAddress(_address);
		this.mReceiver = _receiver;
		this.mKeepConnect = true;
		
	}
	public Connection(Object _address, User _user, Handler _receiver)
	{
		
		this.setAddress(_address);
		this.mReceiver = _receiver;
		this.mUser = _user;
		this.mKeepConnect = true;
	}
	
	public ConnectionInfo getConnectionInfo()
	{
		return new ConnectionInfo(this);
	}
	public void Connect(User _u)
	{
		try
		{
			mUser = _u;
			this.mKeepConnect = true;			
		}
		catch (Exception ex)
		{
			Log.d(TAG, ex.getMessage());
		}
		
	}
	public void Disconnect()
	{
		this.mKeepConnect = false;
		//this.svcCom.interrupt();
		//this.svcCom.cancel();
		//this.svcCom.stopNow();
		//this.write(new byte[] { 0 });
		this.cancel();
	}
	public int write(byte[] _buff) 
	{
		final byte[] buf = _buff;		
		Thread runner = new Thread() 
		{
			public void run() 
			{
				if (svcWrite != null)
				{
					svcWrite.write(buf);
				}		
			}
		};
		runner.setName("Runner Write");
		runner.start();
		/*
		try {
			//runner.join();
		} catch (InterruptedException e) {
			
		}
		*/
				
			
		/*if (svcCom != null)
		{
			return svcCom.write(_buff);
		}
		*/
		return 0;
	}
	public void run()
	{
		boolean ret;
		
		ret = validateStart();
		if (!ret)
		{
			return;
		}
		if (ret) {
			ret = OnInit(mAddress);
			
		}		
		
		if (ret) {
			setState(CState.Disconnected);
			ret = OnConnect(mAddress);			
		}
		if (ret) {
			
			OnServiceRun(serviceHandler);
			//setState(CState.);
			if (svcCom != null)
			{
				try 
				{
					svcCom.join();
				} 
				catch (InterruptedException e) { }
				svcCom = null;
				setState(CState.Disconnected);
				if (mKeepConnect)
				{
					this.run();
				}
			}
		}	
		else
		{
			if (mKeepConnect)
			{
				this.run();
			}
		}
		
		
	}
	public void cancel() 
	{
		this.OnStop();
		
		//this.svcRead.cancel();
		//this.svcWrite.cancel();
		//try {
		//	if (this.svcRead.isAlive())
		//	{
				//this.svcRead.interrupt();
		//		this.svcRead.stop();
		//	}
		//	this.svcRead.join();
			//this.svcWrite.join();
		//} catch (InterruptedException e) {
		//	Log.e(TAG, e.getMessage());
		//}		
		//setState(CState.Disconnected);
    }
	public boolean OnHandshake(User _user)
	{
		setState(CState.Handshake);
		//int res = this.write(_user.pack().toString().getBytes());	
		
		return true;
	}
	
	
	public abstract boolean OnInit(Object _address);	
	public abstract boolean OnConnect(Object _address);
	public abstract void OnServiceRun(Handler _hdl);
	public abstract void OnStop();
	
	
	
	public void setAddress(Object _address)
	{
		this.mAddress = _address;
	}
	public Object getAddress()
	{
		return this.mAddress;
	}
	public void setState(CState _state)
	{
		mCState = _state;
		onStateChangeExt(mCState);
	}
	public boolean isConnected()
	{
		return (mCState == CState.Connected);
	}
	public CState getConnectionState()
	{
		return mCState;
	}
	public User getUser()
	{
		return mUser;
	}
	public boolean getKeepConnect()
	{
		return this.mKeepConnect;
	}
	
	public boolean getIsAlive()
	{
		return this.isAlive();
	}
	
		
	private Handler serviceHandler = new Handler()
	{
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
				this.onStateChange(msg.obj);
			}			
		}
		
		public void onRead(Object _obj)
		{
			onReadExt(_obj);
			/*
			if (mCState == CState.Handshake)
			{
				onReadHandshake(_obj);
			}
			else
			{
				onReadExt(_obj);
			}
			*/
			
		}
		
		public void onMessage(Object _msg)
		{
			if (_msg != null)
			{
				String str = (String)_msg;
				onMessageExt(str);
			}			
		}
		public void onError(Object _obj)
		{
			onErrorExt(_obj);
		}
		public void onStateChange(Object _obj)
		{
			if (_obj != null)
			{
				SState state = (SState)_obj;
				switch (state)
				{
				case Error:
				case Lost:					
					setState(CState.Disconnected);
					break;
				case Reading:
					setState(CState.Connected);
					break;
				case Stop:
					setState(CState.Disconnected);
					break;
				}
				
			}
			
		}
		
		
	};
	
	private void keepConnect()
	{
		setState(CState.Disconnected);
		try {
			this.svcCom.join();
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}
		this.run();
	}
	
	private void onReadHandshake(Object _obj)
	{
		ByteBuffer bytes = (ByteBuffer)_obj;
		
		String resp = new String(bytes.array());
		
		if (Protocol.isOK(resp))
		{
			setState(CState.Connected);
		}
		else
		{
			setState(CState.Disconnected);
		}
		
	}
	
	private void onReadExt(Object _obj) 
	{
		if (mReceiver != null)
		{
			mReceiver.obtainMessage(MSG.ON_READ.getId(), _obj).sendToTarget();			
		}				
	}
	
	protected void onErrorExt(Object _obj) 
	{
		if (mReceiver != null)
		{
			mReceiver.obtainMessage(MSG.ON_ERROR.getId(), _obj).sendToTarget();			
		}				
	}
	
	private void onMessageExt(String _msg) 
	{
		if (mReceiver != null)
		{
			mReceiver.obtainMessage(MSG.ON_MESSAGE.getId(), _msg).sendToTarget();			
		}				
	}
	
	private void onStateChangeExt(CState _state) 
	{
		if (mReceiver != null)
		{
			mReceiver.obtainMessage(MSG.ON_STATE_CHANGE.getId(), _state).sendToTarget();			
		}				
	}
	
	private boolean validateStart()
	{
		boolean ret = true;
		if (svcCom != null)
		{
			onMessageExt("Po³¹czenie ju¿ nawi¹zano");
			ret = false;
		}
		if (mAddress == null)
		{
			onMessageExt("Nie podano adresu");
			ret = false;
		}
		if (mReceiver == null)
		{
			onMessageExt("Interfejs nie zosta³ zainicjowany");
			ret = false;
		}
		
		return ret;
	}
	
	
	
	
	
	public final static String BROADCAST_INITIALIZED = "digitalisp.android.comunication.INITIALIZED";
	public final static String BROADCAST_CONNECTED = "digitalisp.android.comunication.CONNECTED";
	public final static String BROADCAST_DISCONNECTED = "digitalisp.android.comunication.DISCONNECTED";
	
	public static Connection construct(DType _type, String _address, int _timeout, Handler _receiver)
	{
		if (_type == DType.BTH) {
			return new BTHConnection(_address, _receiver);			
		}
		if (_type == DType.NET) {
			return new NETConnection(_address, _timeout, _receiver);
		}
		return null;
	}
	public static Connection construct(RemoteDevicesRecord _remoteDevice, Handler _receiver)
	{
		if (_remoteDevice != null)
		{
			if (_remoteDevice.mType == DType.BTH) {
				return new BTHConnection(_remoteDevice.mAddress, _receiver);
			}
			if (_remoteDevice.mType == DType.NET) {
				return new NETConnection(_remoteDevice.mAddress, _remoteDevice.mPort, _receiver);				
			}			
		}
		return null;
	}
	public static Connection construct(RemoteDevicesRecord _remoteDevice, User u, Handler _receiver)
	{
		if (_remoteDevice != null)
		{
			if (_remoteDevice.mType == DType.BTH) {
				return new BTHConnection(_remoteDevice.mAddress, _receiver);
			}
			if (_remoteDevice.mType == DType.NET) {
				return new NETConnection(_remoteDevice.mAddress, _remoteDevice.mPort, u, _receiver);				
			}			
		}
		return null;
	}
	
}
