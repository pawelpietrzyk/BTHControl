package com.pawel.android.BTHControl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.pawel.android.BTHControl.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;





public class BTHControlActivity extends Activity {
	private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_EXIT = 0;
    public static final int MESSAGE_LOST = 6;
    public static final int MESSAGE_CONNECTED = 7;
    public static final int MESSAGE_CONNECT = 8;
    public static final int MESSAGE_CONNECT_FAILED = 9;
    public static final int MESSAGE_READ_INITIALIZED = 10;
    public static final int MESSAGE_WRITE_INITIALIZED = 11;
    public static final String SET_NAME = "Settings";
	   
	private BTHDevice bthDevice;
	private Settings settings;
  
	private BluetoothAdapter adapter;
	
	private ImageButton btnUP;
	private ImageButton btnDOWN;
	private ImageButton btnMUTE;
	private ImageButton btnREFRESH;
	
	private ImageButton btnRUN;
	
	private Button btnFind;
	private CheckBox boxAUTO;
	//private Button btnUP;
	//private Button btnDOWN;
	//private Button btnMUTE;
	private SeekBar skbProgress;
	private TextView txtV;
	private TextView txtVOL;
	private TextView txtDEVICE;
	private ListView lvMess;
	
	private boolean readingIsOn = true;
	private boolean writingIsOn = true;
	private boolean isConnected = false;
	private ArrayAdapter<String> arrMessage;
	
	
	public void onStop() {
		super.onStop();
		if (adapter != null) {
						
		}
		
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        
        InitializeComponents();
        //this.DisplaySettings();
        initApplication();
        Log("Uruchomiono aplikacjê, wersja: 2.1 test");
       
    	
    	//LaunchDevices();
    	
    }
    
    public BluetoothAdapter Adapter()
    {
    	if (adapter != null) {
    		return adapter;
    	} else {
    		adapter = BluetoothAdapter.getDefaultAdapter();
    		return adapter;
    	}
    }
    
    
    private void InitializeComponents()
    {
    	
    	//btnFind = ()this.findViewById(R.id.button1);
        btnUP = (ImageButton)this.findViewById(R.id.btnUP);
        btnDOWN = (ImageButton)this.findViewById(R.id.btnDOWN);
        btnMUTE = (ImageButton)this.findViewById(R.id.btnMUTE);
        
        btnREFRESH = (ImageButton)this.findViewById(R.id.btnCHANGE);        
        
        btnRUN = (ImageButton)this.findViewById(R.id.btnRUN);
    	skbProgress = (SeekBar)this.findViewById(R.id.seekBar1);
    	
    	//skbProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
    	//txtV = (TextView)this.findViewById(R.id.textView2);
        txtVOL = (TextView)this.findViewById(R.id.txtVOL);
    	txtDEVICE = (TextView)this.findViewById(R.id.txtDEVICE);
    	boxAUTO = (CheckBox)this.findViewById(R.id.checkBox1);
    	
    	arrMessage = new ArrayAdapter<String>(this, R.layout.message);
    	lvMess = (ListView)this.findViewById(R.id.listMSG);
    	lvMess.setAdapter(arrMessage);
    	    	
    	
    	
    	this.btnUP.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {    			
    			readingIsOn = false;
    			//SendFrame(VolumeUPMsg());
    			_volumeUP();
    			readingIsOn = true;
    		}    		
    	});    	
    	
    	this.btnDOWN.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			readingIsOn = false;
    			//SendFrame(VolumeDOWNMsg());
    			_volumeDOWN();
    			readingIsOn = true;
    		}    		
    	});
    	
    
    	this.btnMUTE.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {    			
    			//SendFrame(VolumeMUTEMsg());
    			_volumeMUTE();
    		}
    		
    	});    	
    	this.btnREFRESH.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			changeDevice();
    		}    		
    	});    	
    	this.btnRUN.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {    			
    			try {
    				if (bthDevice != null) {
        				
        				BTHDeviceState state = bthDevice.getState(); 
        				if (state == BTHDeviceState.Disconnected) {
        					bthDevice.Connect();
        				}
        				if (state == BTHDeviceState.Connected) {
        					bthDevice.Disconnect();
        				}
        				if (state == BTHDeviceState.Initialized) {
        					Log("device alreadt initialized");
        				}    				
        			} else {
        				InitDevice();
        			}
    			} catch (Exception ex) {
    				Log("onClick: "+ex.getMessage());
    			}
    		}    		
    	});
    	this.boxAUTO.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
    		{
    			if (settings != null) {
    				if (settings.setAutoInit(isChecked) != 0) {
        				Log("Nie zapisano");
        			}
    			}
    			
    		}
    	});
    	
    	
    	
    	   	    
    }
    
    private void _volumeUP() {
    	if (this.bthDevice != null) {
    		bthDevice.volumeUP();
    	}
    }
    private void _volumeDOWN() {
    	if (this.bthDevice != null) {
    		bthDevice.volumeDOWN();
    	}
    }
    private void _volumeMUTE() {
    	if (this.bthDevice != null) {
    		bthDevice.volumeMUTE();
    	}
    }
    private void _volumeCHANGE(int newValue) {
    	if (this.bthDevice != null) {
    		bthDevice.setVolumeLevel(newValue);
    	}
    }
    
    private OnVolumeChangeListener mOnVolumeChangeListener = new OnVolumeChangeListener() {
    	public void onVolumeChange(VolumeChangeEvent e) {
    		setVolumeLevel(e.Volume());    		
    	}
    };
    
    private onDeviceException mOnDeviceException = new onDeviceException() {
    	public void onException(Exception ex) {
    		Log(ex.getMessage());
    	}
    	public void onException(String msg) {
    		Log(msg);
    	}
    };
    
    private BTHDeviceStateListener mBTHDeviceStateList = new BTHDeviceStateListener() {
		public void bthDeviceStateReceived(DeviceStateEvents e) {
			if (e.State() == BTHDeviceState.Connected) {
				//btnRUN.setImageResource(R.drawable.stop);
		    	Log("Po³¹czono");
		    	//bthDevice.initialization();
			}
			if (e.State() == BTHDeviceState.Disconnected) {
				//btnRUN.setImageResource(R.drawable.run);
			}
			if (e.State() == BTHDeviceState.Connecting) {
				Log("£¹czenie");
			}
			if (e.State() == BTHDeviceState.Disconecting) {
				
			}			
		}
	};
    
    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    	{    		
    		if (fromUser) {
    			_volumeCHANGE(progress);
    		}
    		
    	}
    	
    	public void onStartTrackingTouch (SeekBar seekBar) {
    		readingIsOn = false;
    	}
    	
    	public void onStopTrackingTouch (SeekBar seekBar) {
    		readingIsOn = true;
    	}
    };
    
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(D) Log.d(TAG, "onActivityResult " + resultCode);    	
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(NewDevices.EXTRA_DEVICE_ADDRESS);
                String nazwa = data.getExtras().getString(NewDevices.EXTRA_DEVICE_NAME);
                SaveSettings(nazwa, address, false);
                InitDevice(address);
              
            }
            break;
        case REQUEST_ENABLE_BT:            
            if (resultCode == Activity.RESULT_OK) {
                MSG("Bluetooth w³¹czone");                
                if (settings.getAutoInit()) {
                	InitDevice(settings.GetName());
                }                
            } else {
                //Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
            	MSG("Blutooth nie wspierane");
                finish();
            }
        }
    }
    
    public void enableBTH()
    {
    	if (!adapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
    }
    public int changeDevice()
    {
    	try {
    		adapter = BluetoothAdapter.getDefaultAdapter();
        	if (adapter == null) {
        		MSG("Bluetooth nie jest dostêpny");
        		return 1;
        	}
        	if (!adapter.isEnabled()) {
        		enableBTH();	
        	} else {
        		Intent serverIntent = new Intent(this, NewDevices.class);    			
        	    this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);    		
        	}
    	} catch (Exception ex) {
    		Log(ex.getMessage());
    		return 1;
    	}    	   	
    	return 0;
    }
    public void InitSettings()
    {
    	this.settings = new Settings(this);
    	this.DisplaySettings();
    }
    public void SaveSettings(String nazwa, String adres, boolean auto)
    {
    	if (settings != null) {
    		settings.setAddress(adres);
    		settings.setName(nazwa);
    		settings.setAutoInit(auto);
    		DisplaySettings();
    	}
    }
    
    void initApplication() {
    	this.DisplaySettings();
    	if (settings != null) {
    		if (settings.getAutoInit()) {
    			String adr = settings.getAddress();
        		if ((adr != "NODEVICE") && (adr != "")) {
        			this.InitDevice(adr);
        		} else {   		
        			Log("Wybierz urz¹dzenie do po³¹czenia");
        		}
    		}
    		
    	} else {
    		InitSettings();
    		initApplication();
    	}
    	
    }    
    
    public void InitDevice() {
    	if (settings != null) {
    		InitDevice(settings.getAddress());
    	} else {
    		InitSettings();
    		InitDevice();
    	}
    }
    
    public void InitDevice(String address) {
    	if (bthDevice == null) {
    		bthDevice = new BTHDevice(address);
    		DeviceAddListeners(bthDevice);
    		
    		//bthDevice.Connect();
    		Log("Add Listeners");
    	}   	
    }
    public void DeviceAddListeners(BTHDevice d)
    {
    	if (d != null) {
    		d.addOnVolumeChangeListener(mOnVolumeChangeListener);
        	d.addStateListener(mBTHDeviceStateList);
        	d.addExceptionListener(mOnDeviceException);
    	}
    	
    }
    public void DeviceRemoveListeners(BTHDevice d) {
    	if (d != null) {
    		d.removeOnVolumeChangeListener(mOnVolumeChangeListener);
        	d.removeStateListener(mBTHDeviceStateList);
    	}    	
    }
    
    public void DisplaySettings()
    {
    	try {
    		if (settings != null) {
    			String address = settings.getAddress();
            	String nazwa = settings.GetName();
            	this.boxAUTO.setChecked(settings.getAutoInit());
            	this.txtDEVICE.setText(nazwa + "\n" + address);
    		} else {
    			InitSettings();
    		}
    		
    	} catch (Exception ex) {
    		Log(ex.getMessage());
    	}
    	
    }
    public void setVolumeLevel(int volume) {
    	try {
    		if (readingIsOn) {
        		this.skbProgress.setProgress(volume);
        		this.txtVOL.setText(String.valueOf(volume));
        	}
    	} catch (Exception ex) {
    		Log(ex.getMessage());
    	}
    }
    public void setVolumeRange(int min, int max) {
    	try {
    		this.skbProgress.setMax(max);
    	} catch (Exception ex) {
    		Log(ex.getMessage());
    	}    	
    }
    
/*   
    private byte[] CodeFrame(int value)
    {
    	char b = 'B';
    	char e = 'E';
    	//value = 0 - value;
    	
    	String stemp = b + String.valueOf(value) + e;
    	return stemp.getBytes(); 
    }
    private byte[] ExitMsg()
    {
    	char b = 'B';
    	char e = 'E';
    	String stemp = b + "4000" + e;
    	return stemp.getBytes();
    }
    private byte[] VolumeUPMsg()
    {
    	char b = 'B';
    	char e = 'E';
    	String stemp = b + "1000" + e;
    	return stemp.getBytes();
    }
    private byte[] VolumeDOWNMsg()
    {
    	char b = 'B';
    	char e = 'E';
    	String stemp = b + "2000" + e;
    	return stemp.getBytes();
    }
    private byte[] VolumeMUTEMsg()
    {
    	char b = 'B';
    	char e = 'E';
    	String stemp = b + "3000" + e;
    	return stemp.getBytes();
    }
    private byte[] INITMsg()
    {
    	char b = 'B';
    	char e = 'E';
    	String stemp = b + "5000" + e;
    	return stemp.getBytes();
    }
*/  
    
    public void MSG(String txt)
    {
    	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    	
    }
    public void Log(String txt) {
    	if (this.arrMessage != null) {
    		arrMessage.insert(txt, 0);
    	}
    }   
}
