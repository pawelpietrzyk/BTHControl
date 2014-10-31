package digitalisp.android.RemoteCotrol;

import java.util.HashMap;

import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.pawel.android.BTHControl.R;

import digitalisp.android.RemoteCotrol.VolumeControlService.VolumeControlBinder;
import digitalisp.android.comunication.CState;
import digitalisp.android.comunication.RemoteDeviceActivity;
import digitalisp.android.providers.Comunication.RemoteDevices;

public class VolumeControlActivity extends ListActivity 
{
	private static final String tag = "VolumeControlActivity";
	public static final int MENU_ITEM_CONNECT = Menu.FIRST;
	public static final int MENU_ITEM_DISCONNECT = Menu.FIRST + 1;
	public static final int MENU_ITEM_CONFIGURE = Menu.FIRST + 2;	
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 3;
    public static final int MENU_ITEM_EDIT = Menu.FIRST + 4;
    
    private VolumeControlService mConnectionService;
    private boolean mServiceBound;
    
    private ServiceConnection serviceConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			VolumeControlBinder binder =  (VolumeControlBinder)service;
			mConnectionService = binder.getService();
			mServiceBound = true;
			mConnectionService.endForeground();
			loadActivity();
			//mConnectionService.setConnectionInterface(connectionInterface);
			//initConnection();						
		}
		
		public void onServiceDisconnected(ComponentName arg0)
		{
			mServiceBound = false;
		}
	};
    
    public class VolumeCursorAdapter extends SimpleCursorAdapter
    {
    	private String[] mFrom;
    	private int[] mTo;
    	CheckBox checkBox;
    	VolumeControlService mService;
    	private HashMap<Integer, VolumeControlMgr> vcMap = new HashMap<Integer, VolumeControlMgr>();
    	
    	

		public VolumeCursorAdapter(
				VolumeControlService service,
				Context context, 
				int layout, 
				Cursor c,
				String[] from, 
				int[] to) 
		{
			super(context, layout, c, from, to);
			mFrom = from;
			mTo = to;			
			mService = service;
		}
		
		@Override
		public void bindView (View view, Context context, Cursor cursor)
		{						
			super.bindView(view, context, cursor);
			VolumeControlMgr vcMgr = new VolumeControlMgr(mService, context, view, cursor);
			vcMap.put(cursor.getPosition(), vcMgr);
			vcMgr.initCommunication();
			
			view.setTag(vcMgr);
			
						
			//vcMap.put(view.hashCode(), vcMgr);
			//vcMgr.initCommunication();
			
				
			
		}		
    }
    
    protected void onCreate(Bundle _bundle)
	{
		super.onCreate(_bundle);
				
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(RemoteDevices.CONTENT_URI);
        }        
        getListView().setOnCreateContextMenuListener(this);
        getListView().setOnItemClickListener(mOnItemClickListener);
        Intent it = new Intent(this, VolumeControlService.class);
    	this.startService(it);
        startConnectionService();
		
	}
    public void onResume()
    {
    	super.onResume();
    }
    public void onStop()
    {
    	super.onStop(); 
    	
    	  	
    }
    public void onDestroy()
    {
    	super.onDestroy();
    	this.stopConnectionService();
    }
    private void loadActivity()
    {
    	String selection = RemoteDevices.DEVSELECTED + " = '1'";
    	
    	Cursor cursor = managedQuery(
        		getIntent().getData(), 
        		RemoteDevices.PROJECTION, 
        		selection, 
        		null, 
        		null);
    	
    	/*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(        		
        		this, 
        		android.R.layout.simple_list_item_multiple_choice, 
        		cursor,
                new String[] { RemoteDevices.DEVADDR, RemoteDevices.DEVTYPE }, 
                new int[] { android.R.id.text1, android.R.id.text2 });
        */    
        /*
        ListView lv = this.getListView();
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.volume_control_device, 
        		cursor,
                new String[] { RemoteDevices.DEVNAME }, 
                new int[] { R.id.txtDeviceName });
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        */
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.add_device_item, 
        		cursor,
                new String[] { RemoteDevices.DEVNAME, RemoteDevices.DEVADDR }, 
                new int[] { R.id.txtName, R.id.txtAddress });
        */
    	
    	VolumeCursorAdapter adapter = new VolumeCursorAdapter(
        		this.mConnectionService,
        		this, 
        		R.layout.volume_control_device, 
        		cursor,
                new String[] { RemoteDevices.DEVNAME }, 
                new int[] { R.id.txtDeviceName });
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        setListAdapter(adapter);
    }
    private void startConnectionService()
    {
    	
    	Intent it = new Intent(this, VolumeControlService.class);
    	//this.startService(it);
		
    	if (!mServiceBound)
		{
			this.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE);			
		}
		
    }
    private void stopConnectionService()
    {
    	if (mServiceBound)
		{
    		mConnectionService.beginForeground();
    		//mConnectionService.Close();
			this.unbindService(serviceConnection);
			mServiceBound = false;
			
		}
    	//Intent it = new Intent(this, VolumeControlService.class);
    	//this.stopService(it);
    }
    public boolean onCreateOptionsMenu(Menu menu) 
	{
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ITEM_CONFIGURE, 0, R.string.menu_config)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_ITEM_DISCONNECT, 0, R.string.menu_close)
        .setShortcut('3', 'a')
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, RemoteDeviceActivity.class), null, intent, 0, null);

        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_CONFIGURE:
        	showConfig();            
            return true;
        case MENU_ITEM_DISCONNECT:
        	closeApp();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try 
        {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } 
        catch (ClassCastException e) 
        {
            //Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) 
        {        
            return;
        }
        menu.setHeaderTitle(cursor.getString(RemoteDevices.DEV_COLUMN_IDX));
        menu.add(0, MENU_ITEM_CONNECT, 0, R.string.menu_connect);
        
        menu.add(0, MENU_ITEM_DISCONNECT, 0, R.string.menu_disconnect);
        //menu.add(0, MENU_ITEM_CONFIGURE, 0, R.string.menu_config);
        //menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }
    
    public VolumeControlMgr getMgr(View _view)
    {
    	if (_view != null)
    	{
    		View root = _view.getRootView();
    		
    		if (root != null)
    		{
    			Object obj = root.getTag();
        		if (obj != null)
        		{
        			return (VolumeControlMgr)obj;
        		}
    		}
    		
    	}
    	return null;
    }
    
	public boolean onContextItemSelected(MenuItem item) 
	{
        AdapterView.AdapterContextMenuInfo info;
        try 
        {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
             
             //VolumeCursorAdapter ada = (VolumeCursorAdapter)getListView().getItemAtPosition(info.position);
             VolumeCursorAdapter ada = (VolumeCursorAdapter)getListAdapter();
             if (ada != null)
             {
            	 //VolumeControlMgr mgr = (VolumeControlMgr)ada.vcMap.get(info.targetView.getTag());
            	 VolumeControlMgr mgr = (VolumeControlMgr)info.targetView.getTag();
            	 //VolumeControlMgr mgr = getMgr(info.targetView);
            	 if (mgr != null)
                 {
            		 switch (item.getItemId()) 
            	     {
            	     	case MENU_ITEM_CONNECT:            	        
            	        	mgr.startCommunication();
            	            return true;            	        
            	        case MENU_ITEM_DISCONNECT:
            	        	mgr.stopComunication();
            	        	return true;
            	        default: break;
            	     }
                 }
             }
             
             
             
             
        } 
        catch (ClassCastException e) 
        {
            //Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        
        switch (item.getItemId()) 
        {
            case MENU_ITEM_CONNECT: 
            {
                //Uri uri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                //getContentResolver().delete(uri, null, null);
                return true;
            }
            case MENU_ITEM_DISCONNECT:
            	//editDevice(info.id);
            	return true;
        }
        return false;
    }
    
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView view, View v, int position, long id)
		{
			int idd = 0;
		}
	};
    
    public void handleUpClick(View v)
    {
    	VolumeControlMgr mgr = this.getMgr(v);
    	if (mgr != null)
    	{    		
    		mgr.setVolumeUp();
    	}
    }
    public void handleDownClick(View v)
    {
    	VolumeControlMgr mgr = this.getMgr(v);
    	if (mgr != null)
    	{
    		mgr.setVolumeDown();
    	}
    }
    
    public void handleMuteClick(View v)
    {
    	VolumeControlMgr mgr = this.getMgr(v);
    	if (mgr != null)
    	{
    		
    	}
    }
    
    public void handleConnectClick(View v)
    {
    	VolumeControlMgr mgr = this.getMgr(v);
    	if (mgr != null)
    	{
    		if (mgr.connectionState == CState.Connected)
    		{
    			mgr.stopComunication();
    		}
    		if (mgr.connectionState == CState.Disconnected)
    		{
    			
    			mgr.initCommunication();
    		}
    		
    	}
    }
    
    public void showConfig()
    {
    	Intent intent = new Intent();
    	intent.setData(getIntent().getData());
    	intent.setAction(Settings.ACTION_LOCALE_SETTINGS);
    	this.startActivity(intent);
    	
    }
    public void closeApp()
    {
    	if (mConnectionService != null)
    	{    		
    		mConnectionService.Close();    		
    	}
    	this.finish();
    }
}
