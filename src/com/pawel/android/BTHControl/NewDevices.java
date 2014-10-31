package com.pawel.android.BTHControl;

import java.util.Set;

import com.pawel.android.BTHControl.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

public class NewDevices extends Activity {
	
	// Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
	
	private ListView lvPaired;
	private ListView lvNew;
	
	private Button scan;
	
	
	
	private BluetoothAdapter madapter;
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public static String EXTRA_DEVICE_NAME = "device_name";
	private ArrayAdapter<String> mArrayAdapter;
	private ArrayAdapter<String> mNewArrayAdapter;
	
	
	
	
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
        
        if (this.lvPaired == null) 
        {
    		MSGd("List View for Paired Devices is null");
    	} 
        else 
        {
    		this.lvPaired.setAdapter(mArrayAdapter);
            this.lvPaired.setOnItemClickListener(mDeviceClickListener);            
    	}
    	
    	if (this.lvNew == null) 
    	{
    		MSGd("List View for New devices is null");
    	} 
    	else 
    	{    		
            this.lvNew.setAdapter(mNewArrayAdapter);
            this.lvNew.setOnItemClickListener(mDeviceClickListener);            
    	}
    	
    	this.scan.setOnClickListener(mScanClickListener);
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    	
    	madapter = BluetoothAdapter.getDefaultAdapter();
    	
    	this.QueryBlue(madapter);
    	
    	
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
	    		String address = "";
	    		String nazwa = "";
	    		TextView textV = null;
	    		if (v != null) 
	    		{
	    			textV = ((TextView) v);
	    		} 
	    		else 
	    		{
	    			MSGd("v is null");	    		
	    		}    		
	    		if (textV != null) 
	    		{
					
	    			String sinfo = textV.getText().toString();
					
					if (sinfo.length() > 0) {
						address = sinfo.substring(sinfo.length() - 17);
						nazwa = sinfo.substring(0, sinfo.length() - 18);
						//MSGd(address);
					} else {
						MSGd("Info is empty or null");
					}
	        		
	    			//MSG(address);
				} else {
					MSGd("Text View is null");
				}
	    		
	    		if (madapter != null) {
	    			if (madapter.isDiscovering()) {
	    				if (madapter.cancelDiscovery()) {
	        				MSGd("Cancel Discovering successed");
	            		} else {
	            			MSGd("Some error during cancel Discovery");
	            		}        			
	    			} else {
	    				//MSGd("adapter is not in discovering mode");
	    			}
	    			Intent intent = new Intent();
		    		intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
		    		intent.putExtra(EXTRA_DEVICE_NAME, nazwa);
		    		setResult(Activity.RESULT_OK, intent);
	    			
	    			//remoteDevice = adapter.getRemoteDevice(address);
	    			
	    			//setContentView(R.layout.main);
		    		finish();        			
	    			//MSG("adapter is not null");
	    		} else {
	    			MSGd("Adapter is null so I can't do any operation on it");
	    		}
	    		
	    		
	    		
	    	}
	    };
	
	public void QueryBlue(BluetoothAdapter ada)
    {         
        if (ada == null) {
        	MSGd("Adapter is null");
        } else {
        	Set<BluetoothDevice> pairedDevices = ada.getBondedDevices();
        	if (pairedDevices.size() > 0) {
        		for (BluetoothDevice device : pairedDevices)
        		{
        			//MSG(device.getName() + "\n" + device.getAddress());
        			if (mArrayAdapter != null) {
        				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        			} else {
        				MSGd("mArrayAdapter is null");
        			}
        			
        		}
        	}    	
        }
    	//MSG("Wykonano QueryBlue");
    }
	public void MSGd(String txt)
    {
    	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    	
    }
	
	public void Discovery() {
		// Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (madapter.isDiscovering()) {
        	madapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        madapter.startDiscovery();
	}
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	mNewArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
