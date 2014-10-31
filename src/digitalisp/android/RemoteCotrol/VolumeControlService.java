package digitalisp.android.RemoteCotrol;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.pawel.android.BTHControl.R;

import digitalisp.android.comunication.ConnectionMgr;
import digitalisp.android.comunication.ConnectionPool;
import digitalisp.android.comunication.IConnection;
import digitalisp.android.comunication.RemoteDevicesRecord;
import digitalisp.android.comunication.User;

/**
 * @author Pawel
 * Us³uga obs³uguj¹ca ¿adania od interfejsu klienta i przekazuj¹ca je do urz¹dzenia zdalnego przez ConnectionMgr
 */
public class VolumeControlService extends Service 
{
	private final String tag = "VolumeControlService";
	private IConnection connectionInterface;
	private int connectionCounter;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private HandlerThread mServiceThread;	
	private boolean isForeground;
	private final int notifyID = 1;
	private int mStartId;

	private final IBinder mBinder = new VolumeControlBinder();
	
	
		
	
			
	private final class ServiceHandler extends Handler 
	{			
				
		private ConnectionPool connectionPool;
		private int lastId;
		private volatile int mCurrentId;
		private Handler mServiceHdl;
		private HashMap<Integer, Integer> mDevicesConnectionMap = new HashMap<Integer, Integer>();
		
		public synchronized boolean isAnyConnected()
		{			 
			if (connectionPool != null)
			{				
				Iterator<ConnectionMgr> it = connectionPool.values().iterator();
				while (it.hasNext())
				{					
					if (it.next().isConnected())
					{
						return true;
					}						
				}				
			}
			return false;
		}
		
		public synchronized int getLastId()
		{
			return lastId;
		}
		public synchronized void setLastId(int _id)
		{
			this.lastId = _id;
		}
		public synchronized int getCurrentId()
		{
			return mCurrentId;
		}
		public synchronized void serCurrentId(int _id)
		{
			mCurrentId = _id;
		}
		
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
			case ACTION_INIT_CON:
				initConnection(msg.obj);
				break;
			case ACTION_CONNECT:
				connect(msg.obj);
				break;
			case ACTION_DISCONNECT:
				disconnect(msg.arg1);
				break;
			case ACTION_SEND:
				send(msg.arg1, msg.obj);
				break;
			case ACTION_DISCONNECT_ALL:
				disconnectAll();
				break;
			}
		}
		
			
		private void actionInit()
		{
			connectionCounter = 0;
			initConnectionPool();			
		}
		
		
		public synchronized int initConnection(Object iObj)
		{
			
			boolean initNew = false;
			
			try
			{
			if (iObj != null)
			{
				VolumeControlConnectionMgr mgr = null;
				
				VcBox obj = (VcBox)iObj;
				RemoteDevicesRecord _record = obj.mRecord;
				Handler _volumeCtrl = obj.mHandler;
				mServiceHdl = obj.mServiceHandler;
				VolumeControlMgr vcMgr = obj.mVolumeMgr;
				int id = obj.mId;
				int lastConnectionId = -1;
				int recId = -1;
				
				if (_record != null)
				{
					recId = _record.mID;
					if (mDevicesConnectionMap.containsKey(Integer.valueOf(recId)))
					{
						lastConnectionId = mDevicesConnectionMap.get(Integer.valueOf(recId)).intValue();
					}					
				}
				
				
				if (lastConnectionId > -1)
				{
					if (connectionPool.containsKey(Integer.valueOf(lastConnectionId)))
					{
						mgr = (VolumeControlConnectionMgr)this.connectionPool.get(lastConnectionId);
					}
				}
				if (mgr != null)
				{
					if (mgr.isActive())
					{
						mgr.setHandler(_volumeCtrl);
						if (mgr.getConnectionId() != lastConnectionId)
						{
							Log.d(tag, "Connection Id's does not match");
							mgr.setConnectionId(lastConnectionId);
						}
						vcMgr.setConnectionId(lastConnectionId);
						//setLastId(lastConnectionId);
						//mServiceHdl.obtainMessage(INIT_SUCCESED, mgr.getConnectionId(), mgr.getCState().ordinal(), mgr.getConnectionInfo()).sendToTarget();
						vcMgr.getHandler().obtainMessage(VolumeControlMgr.MSG_STATUS_CHANGE, mgr.getCState()).sendToTarget();
						return mgr.getConnectionId();
					}					
				}
				mCurrentId++;
				mgr = new VolumeControlConnectionMgr(_record, _volumeCtrl);
				
				
				if (this.connectionPool != null)
				{
					mgr.setConnectionId(mCurrentId);
					
					mDevicesConnectionMap.put(Integer.valueOf(_record.mID), Integer.valueOf(mgr.getConnectionId()));
					this.connectionPool.put(mgr.getConnectionId(), mgr);			
					//setLastId(mgr.getConnectionId());
					vcMgr.setConnectionId(mgr.getConnectionId());
					vcMgr.getHandler().obtainMessage(VolumeControlMgr.MSG_STATUS_CHANGE, mgr.getCState()).sendToTarget();
					//mServiceHdl.obtainMessage(INIT_SUCCESED, mgr.getConnectionId(), 0, mgr.getConnectionInfo()).sendToTarget();
					return mgr.getConnectionId();
				}
			}		
			setLastId(-1);
			return -1;
			}
			finally
			{
				//this.notifyAll();
			}
			
			
		}
		
		private void connect(Object obj)
		{
			if (obj != null)
			{
				ConnectBox box = (ConnectBox)obj;
				User _user = box.mUser;
				int id = box.mId;
				if (this.connectionPool != null)
				{
					VolumeControlConnectionMgr mgr = (VolumeControlConnectionMgr)this.connectionPool.get(id);
					if (mgr != null)
					{
						mgr.Connect(_user);
					}
					
				}
			}					 
		}
		private void disconnect(int id)
		{
			
			if (this.connectionPool != null)
			{
				VolumeControlConnectionMgr mgr = (VolumeControlConnectionMgr)this.connectionPool.get(id);
				if (mgr != null)
				{
					mgr.Disconnect();
					mServiceHdl.obtainMessage(DISCONNECT_SUCCESED, id, 0, mgr.getConnectionInfo()).sendToTarget();
					//this.connectionPool.remove(Integer.valueOf(id));
					
				}			
			}			
		}
		private void disconnectAll()
		{
			if (connectionPool != null)
			{				
				Iterator<ConnectionMgr> it = connectionPool.values().iterator();
				while (it.hasNext())
				{		
					ConnectionMgr mgr = it.next(); 
					if (mgr.isConnected())
					{
						mgr.Disconnect();						
					}						
				}
				mServiceHdl.obtainMessage(DISCONNECT_ALL_SUCCESED).sendToTarget();
			}					
		}
		private void send(int id, Object obj)
		{
			if (obj != null)
			{
				ByteBuffer box = (ByteBuffer)obj;				
				
				if (this.connectionPool != null)
				{
					VolumeControlConnectionMgr mgr = (VolumeControlConnectionMgr)this.connectionPool.get(id);
					if (mgr != null)
					{
						mgr.Send(box);
					}
				}
			}		
		}		
		
		private void initConnectionPool()
		{
			connectionPool = new ConnectionPool();			
		}
	
	}	
	
	
	private Handler serviceReceiver = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case INIT_SUCCESED:
				if (connectionInterface != null)
				{
					connectionInterface.onInit(msg.arg1, msg.arg2);
				}
				break;
			case DISCONNECT_ALL_SUCCESED:
				afterDisconnectAll();
				break;
				
			default: break;
				
			}
		}
	};
	
	private void afterDisconnectAll()
	{
		mServiceLooper.quit();
		try {
			mServiceThread.join();
		} catch (InterruptedException e) {
			Log.d(tag, e.getMessage());
		}
		
		stopSelf(mStartId);
	}
	
	public void setConnectionInterface(IConnection _interface)
	{
		this.connectionInterface = _interface;
	}
	
	public synchronized int InitConnection(RemoteDevicesRecord _record, Handler _hdl, VolumeControlMgr _mgr)
	{
		if (mServiceHandler != null)
		{
			this.connectionCounter++;
			mServiceHandler.obtainMessage(ACTION_INIT_CON, new VcBox(connectionCounter, _record, _hdl, serviceReceiver, _mgr)).sendToTarget();
			
			//int id = mServiceHandler.getLastId();
			return 0;
			//return mServiceHandler.initConnection(new VcBox(connectionCounter, _record, _hdl));			 
						
		}	
		return -1;
	}
	
	public synchronized void Connect(int id, User _user)
	{
		this.mServiceHandler.obtainMessage(ACTION_CONNECT, new ConnectBox(id,_user)).sendToTarget();
	}
	
	public synchronized void Done()
	{
		sendToServiceHandler(ACTION_DESTROY);
		this.stopSelf();
	}	
	public synchronized void Send(int id, ByteBuffer buff)
	{
		this.mServiceHandler.obtainMessage(ACTION_SEND, id, 0, buff).sendToTarget();
	}
	public synchronized void Disconnect(int id)
	{
		this.mServiceHandler.obtainMessage(ACTION_DISCONNECT, id, 0).sendToTarget();
		//this.mServiceHandler.disconnect(id);
	}
	public synchronized void DisconnectAll()
	{
		this.mServiceHandler.obtainMessage(ACTION_DISCONNECT_ALL).sendToTarget();		
	}
	
	
	public synchronized void Close()
	{		
		this.DisconnectAll();		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{		
		mStartId = startId;
		return START_STICKY;
	}
	
	@Override
	public void onCreate()
	{		
		initService();		
		sendToServiceHandler(ACTION_INIT);
	}
	
	public void onDestroy()
	{
		
	}
	
	
	
	@Override
	public boolean onUnbind(Intent intent) {				
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{			
		return mBinder;
	}	
		
	public class VolumeControlBinder extends Binder
	{
		public VolumeControlService getService()
		{
			return VolumeControlService.this;
		}		
	}
		
	private void initService()
	{
		mServiceThread = new HandlerThread("ComunicationService", Process.THREAD_PRIORITY_BACKGROUND);
		mServiceThread.start();
		mServiceLooper = mServiceThread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	private void showNotification()
	{
		String ns = this.NOTIFICATION_SERVICE;
		NotificationManager nmgr = (NotificationManager)getSystemService(ns);
		
		int icon = R.drawable.volume_blue;
		CharSequence tickerText = "Us³uga dzia³a";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Volume Control Manager";
		CharSequence contentText = "Treœæ wiadomoœci";
		Intent notificationIntent = new Intent(this, VolumeControlActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);				

		nmgr.notify(1, notification);
	}
	
	
	public void beginForeground()
	{
		if (mServiceHandler != null)
		{
			//if (mServiceHandler.isAnyConnected())
			{
				Notification notification = new Notification(
						R.drawable.volume_blue, 
						"Volume",
				        System.currentTimeMillis());
				
				Intent notificationIntent = new Intent(this, VolumeControlActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
				notification.setLatestEventInfo(
						this, 
						"Volume Control Manager",
				        "PrzejdŸ do us³ugi", 
				        pendingIntent);
				startForeground(notifyID, notification);
				isForeground = true;
			}
		}		
	}
	public void endForeground()
	{		
		if (isForeground)
		{
			this.stopForeground(true);
			//String ns = Context.NOTIFICATION_SERVICE;
			//NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			//mNotificationManager.cancel(notifyID);			
		}
		isForeground = false;		
	}
		
	private void sendToServiceHandler(int _action)
	{
		if (mServiceHandler != null)
		{
			mServiceHandler.obtainMessage(_action).sendToTarget();
		}				
	}
	
	private final int ACTION_INIT = 0;
	private final int ACTION_CREATE = 1;
	private final int ACTION_BIND = 2;
	private final int ACTION_DESTROY = 10;
	private final int ACTION_CONNECT = 11;
	private final int ACTION_DISCONNECT = 16;
	private final int ACTION_DISCONNECT_ALL = 17;
	
	private final int ACTION_ENABLE = 12;
	private final int ACTION_COMMAND = 13;
	private final int ACTION_SEND = 14;
	private final int ACTION_INIT_CON = 15;
	public final int INIT_SUCCESED = 100;
	public final int DISCONNECT_SUCCESED = 101;
	public final int DISCONNECT_ALL_SUCCESED = 102;
	
	
	
	public class ConnectBox
	{
		public int mId;
		public User mUser;		
		
		public ConnectBox(int _id, User _user)
		{
			mId = _id;
			mUser = _user;
		}
	}
	public class SendBox
	{
		public int mId;
		public VolumeControl mVc;
		public SendBox(int _id, VolumeControl _vc)
		{
			mId = _id;
			mVc = _vc;
		}
	}
	public class VcBox
	{
		public int mId;
		public RemoteDevicesRecord mRecord;
		public Handler mHandler;
		public Handler mServiceHandler;
		public VolumeControlMgr mVolumeMgr;
		
		public VcBox(int _id, RemoteDevicesRecord _record, Handler _volumeCtrl, Handler _svcHdl, VolumeControlMgr _vcMgr)
		{
			mId = _id;
			mRecord = _record;
			mHandler = _volumeCtrl;
			mServiceHandler = _svcHdl;
			mVolumeMgr = _vcMgr;
		}
	}

}
