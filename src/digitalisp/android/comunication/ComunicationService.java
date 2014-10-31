package digitalisp.android.comunication;

import digitalisp.android.providers.Comunication.RemoteDevices;
import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public class ComunicationService extends Service {
	
	private final IBinder mBinder = new ComunicationBinder();
	public Connection connection;
	//private String mUser;
	//private String mAuth;
	//private String mCmd;
	private ConnectionPool connectionPool;
	private ConnectionMgr	mConnectionMgr;
	private RemoteDevicesTable mDevicesTable;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private HandlerThread mServiceThread;
	
	public class ComunicationBinder extends Binder
	{
		public ComunicationService getService()
		{
			return ComunicationService.this;
		}		
	}
	
	private final class ServiceHandler extends Handler 
	{		
		
		public ServiceHandler(Looper looper) 
		{
			super(looper);
		}
		
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case ACTION_INIT:
				actionInit();
				break;
				
			}
		}
		private void actionInit()
		{
			initDevices();
			initConnectionPool();
		}	
	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{		
		String u = intent.getStringExtra("USER");
		String p = intent.getStringExtra("AUTH");
		String cmd = intent.getStringExtra("CMD");
		User user = new User(null, u, p);
		mConnectionMgr.Connect(user);
		
		
		return 0;
	}
	
	
	public void onCreate()
	{
		initService();		
		sendToServiceHandler(ACTION_INIT);
	}
	
	public void Done()
	{
		this.stopSelf();
	}
	
	private void initConnectionPool()
	{
		connectionPool = new ConnectionPool();
		int count = 0;
		if (mDevicesTable != null) {
			count = mDevicesTable.count();
			for (int i = 0; i < count; i++)
			{
				//mConnectionMgr = connectionPool.add(mDevicesTable.get(i));				
			}
		}
			
	}
	
	private void createDevice()
	{
		
	}
	
	private void initDevices()
	{
		mDevicesTable = new RemoteDevicesTable();
		final ContentProviderClient client = this.getContentResolver().acquireContentProviderClient(RemoteDevices.CONTENT_URI);
		if (client != null)
		{
			ContentProvider provider = client.getLocalContentProvider();
			mDevicesTable.fill(provider);
		}
		//RemoteDevicesRecord record = new RemoteDevicesRecord();
		//record.mAddress = "10.0.2.2"; //TODO address
		//record.mPort = 12750;
		//record.mTimeout = 500;
		//record.mType = DType.NET;
		//mDevicesTable.add(record);
	}
	
	private void initService()
	{
		mServiceThread = new HandlerThread("ComunicationService", Process.THREAD_PRIORITY_BACKGROUND);
		mServiceThread.start();
		mServiceLooper = mServiceThread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
	private void sendToServiceHandler(int _action)
	{
		if (mServiceHandler != null)
		{
			mServiceHandler.obtainMessage(_action).sendToTarget();
		}				
	}
	
	
	
	private ConnectionReceiver connectionReceiver = new ConnectionReceiver() {
		public void onRead(Object _receivedObj)
		{
			
		}
		public void onMessage(String _msg)		
		{
			
		}
		public void onStateChange(CState _state)
		{
			
		}
		public void onError(Object _error)
		{
			
		}
	};
	
	
	
	
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	
	private final int ACTION_INIT = 0;
	private final int ACTION_CREATE = 1;
	private final int ACTION_BIND = 2;
	private final int ACTION_DESTROY = 10;
	private final int ACTION_CONNECT = 11;
	private final int ACTION_ENABLE = 12;

}
