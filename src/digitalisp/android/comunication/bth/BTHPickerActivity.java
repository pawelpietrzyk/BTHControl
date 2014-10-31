package digitalisp.android.comunication.bth;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pawel.android.BTHControl.R;

import digitalisp.android.providers.Comunication.RemoteDevices;

public class BTHPickerActivity extends Activity {	
	
    private static final String TAG = "BTHPickerActivity";
    private static final boolean D = true;
    
    private final int STATE_EDIT = 0;
	private final int STATE_INSERT = 1;

	private ListView lvPaired;
	private ListView lvNew;
	
	private Button scan;
	
	private BluetoothAdapter madapter;
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public static String EXTRA_DEVICE_NAME = "device_name";
	private ArrayAdapter<String> mArrayAdapter;
	private ArrayAdapter<String> mNewArrayAdapter;
	private Uri mUri;
	private Cursor mCursor;
	private int mState;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        
        setResult(Activity.RESULT_CANCELED);       
        
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);    	
        mNewArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        this.lvPaired = (ListView)this.findViewById(R.id.paired_devices);
        this.lvNew = (ListView)this.findViewById(R.id.new_devices);
        this.scan = (Button)this.findViewById(R.id.button_scan);
        
        this.lvPaired.setAdapter(mArrayAdapter);
        this.lvPaired.setOnItemClickListener(mDeviceClickListener);            
    	    	
    	this.lvNew.setAdapter(mNewArrayAdapter);
        this.lvNew.setOnItemClickListener(mDeviceClickListener);            
    	    	
    	this.scan.setOnClickListener(mScanClickListener);
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
                
        this.initFromCallerIntent(getIntent());
    	
    	madapter = BluetoothAdapter.getDefaultAdapter();
    	
    	this.QueryBlue(madapter);
    	
    	
	}
	
	private boolean initFromCallerIntent(Intent _intent)
	{
		boolean ret = true;
		
		if (_intent != null)
		{
			String action = _intent.getAction();
			if (Intent.ACTION_INSERT.equals(action))
			{
				mState = STATE_INSERT;
				mUri = this.getContentResolver().insert(_intent.getData(), null);
				if (mUri == null) 
				{
	                Log.e(TAG, "Failed to insert new device into " + getIntent().getData());
	                finish();
	                return false;
	            }
				
			}
			else if (Intent.ACTION_EDIT.equals(action)) 
			{
				mState = STATE_EDIT;
				mUri = _intent.getData();
			}
			else
			{
				Log.e(TAG, "Action not defined");
				return false;
			}
			
			//mCursor = this.managedQuery(mUri, RemoteDevices.PROJECTION, null, null, null);
			
		}
		return ret;
	}
	
	private OnClickListener mScanClickListener = new OnClickListener()
	{
		public void onClick(View v)
    	{    		
			Discovery();
    	}
	};
	
	 private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	    {
	    	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
	    	{
	    		handleSave(v);
	    	}
	    };
	
	public void QueryBlue(BluetoothAdapter ada)
    {         
        if (ada == null) 
        {
        	MSGd("Bluetooth Adapter doesn't exists");
        } 
        else 
        {
        	Set<BluetoothDevice> pairedDevices = ada.getBondedDevices();
        	if (pairedDevices.size() > 0) {
        		for (BluetoothDevice device : pairedDevices)
        		{        
        			if (mArrayAdapter != null) 
        			{
        				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        			}        			       			
        		}
        	}    	
        }
    }
	public void MSGd(String txt)
    {
    	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    	
    }
	
	public void Discovery() 
	{
	
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
    
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (madapter.isDiscovering()) {
        	madapter.cancelDiscovery();
        }

        madapter.startDiscovery();
	}
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() 
	{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
            {            
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
             
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
                {
                	mNewArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }            
            } 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewArrayAdapter.getCount() == 0) 
                {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewArrayAdapter.add(noDevices);
                }
            }
        }
    };
    
    public void handleSave(View _view)
	{
    	String address = "";
		String nazwa = "";
		TextView textV = (TextView)_view;
		
		if (textV != null) 
		{					
			String sinfo = textV.getText().toString();					
			if (sinfo.length() > 0) 
			{
				address = sinfo.substring(sinfo.length() - 17);
				nazwa = sinfo.substring(0, sinfo.length() - 18);
			}
		} 
		
		if ((madapter != null)
				&& (address != null)) 
		{
			if (madapter.isDiscovering()) 
			{
				madapter.cancelDiscovery();	    				
			}
				
			ContentValues values = new ContentValues();
			values.put(RemoteDevices.DEVADDR, address);
			values.put(RemoteDevices.DEVTYPE, "BTH");	
			this.getContentResolver().update(mUri, values, null, null);
						
    		setResult(Activity.RESULT_OK);    		
		}    		
		else
		{
			this.setResult(Activity.RESULT_CANCELED);
		}
		
		this.finish();
		
	}
	
	public void handleCancel(View _view)
	{
		this.cancelDevice();
	}
	
	private final void cancelDevice() {
        if (mCursor != null) 
        {
        	deleteDevice();            
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    
    private final void deleteDevice() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);            
        }
    }
    
    public static final String BTH_PICKER_ACTION_DISCOVERY = "digitalisp.android.comunication.bth.DISCOVERY";

}
