package digitalisp.android.comunication.bth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import digitalisp.android.comunication.ComService;
import digitalisp.android.comunication.Connection;
import digitalisp.android.comunication.User;
import digitalisp.android.comunication.settings.Settings;

public class BTHConnection extends Connection 
{
	private final String tag = "BTHConnection";
	private BluetoothSocket bthSocket;
    private BluetoothDevice bthDevice;
    private BluetoothAdapter bthAdapter;
    private BTHBroadcastReceiver bthBroadcastReceiver;
    
        
    
	public BTHConnection(String _remoteAddress, Handler _receiver)
	{
		super(_remoteAddress, _receiver);
	}
	

	@Override
	public boolean OnInit(Object _address) 
	{
		boolean ret;
		ret = this.enableDevice();
		if (!ret)
		{
			return false;
		}
		bthBroadcastReceiver = new BTHBroadcastReceiver();
		BluetoothSocket tmp = null;
		String addr = _address.toString();
		this.bthAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (this.bthAdapter != null) 
	    {
	    	this.bthDevice = this.bthAdapter.getRemoteDevice(addr);        	
	    } 
	    else 
	    {
	    	this.bthDevice = null;	    	    	   
	    }  
	      
	    if (bthDevice != null) 
	    {
	    	try 
	    	{
	    		tmp = bthDevice.createRfcommSocketToServiceRecord(Settings.appUUID);
	    	} 
	    	catch (IOException e)
	    	{	    		   
	    		return false;
	    	}  
	    }
	    else
	    {	    	   
	    	return false;
	    }
	    bthSocket = tmp;    
	       
	    return true;		
	}

	@Override
	public boolean OnConnect(Object _address) 
	{
		
		if (bthAdapter.isDiscovering()) 
		{
    		bthAdapter.cancelDiscovery();
    	}         
 
        try 
        {        
        	bthSocket.connect();
        	return true;
        } 
        catch (IOException connectException) 
        {
        	//connectException.printStackTrace();
        	//mHandler.obtainMessage(BTHMSG.CNT_FAILED.getId(), bthDevice).sendToTarget();
            	
            try 
            {
            	bthSocket.close();
            	
            } catch (IOException closeException) 
            { 
            	
            }            
        }
		return false;
	}

	@Override
	public void OnServiceRun(Handler _hdl) {
		svcCom = new BTHService(bthSocket, _hdl);
		//svcWrite = new BTHService(bthSocket, _hdl);
		svcCom.start();
	}
		
	
	@Override
	public void OnStop()
	{
		try {
			bthSocket.close();
			Log.d(tag, "BTH Socket closed");
		} catch (IOException e) {
			Log.d(tag, e.getMessage());
		}		
	}
	
	public boolean enableDevice()
	{
		if (bthAdapter != null)
		{
			if (bthAdapter.isEnabled())
			{								
				return true;
			}
			else
			{
				bthAdapter.enable();
			}			
		}
		return false;
	}
	
	public BroadcastReceiver getBroadcastRecevier()
	{
		return this.bthBroadcastReceiver;
	}
	private class BTHBroadcastReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			{
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
				if (state == BluetoothAdapter.STATE_ON)
				{
					OnInit(getAddress());
				}
			}
		}
	};
	
	public class BTHService extends ComService
	{
		private static final String TAG = "BTHServiceRead";
		
		private BluetoothSocket mSocket;		
		
		public BTHService(BluetoothSocket _socket, Handler _hdl) 
		{
			super(_hdl);
			mSocket = _socket;
		}
		
		public InputStream getInputStream()
		{
			if (mSocket != null)
			{
				try 
				{
					return mSocket.getInputStream();
				} 
				catch (IOException e) 
				{	
					Log.e(TAG, e.getMessage());					
				}
			}
			return null;			
		}
		
		public OutputStream getOutputStream()
		{
			if (mSocket != null)
			{
				try 
				{
					return mSocket.getOutputStream();
				} 
				catch (IOException e) 
				{
					Log.e(TAG, e.getMessage());					
				}
			}
			return null;
		}		
	}	
}
