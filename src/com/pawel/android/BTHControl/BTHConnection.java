package com.pawel.android.BTHControl;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

public class BTHConnection extends Thread {
	
	private final BluetoothSocket bthSocket;
    private final BluetoothDevice bthDevice;
    private final BluetoothAdapter bthAdapter;
    private final Handler mHandler;
    private BTHService svcWrite;
    private BTHService svcRead;
    
    public BluetoothSocket getSocket() {
    	return this.bthSocket;
    }
    public Handler getHandler() {
    	return this.mHandler;
    }
    public BTHService getWrite() {
    	return this.svcWrite;
    }
    public BTHService getRead() {
    	return this.svcRead;
    }
    
    public int InitServices() {
    	
    	this.svcRead = new BTHService(this, "READ");
    	this.svcWrite = new BTHService(this, "WRITE");
    	return 0;
    }
    public int StartReading() {
    	if (this.svcRead != null) {
    		this.svcRead.start();
    	}
    	return 0;
    }
    public int StopReading() {
    	if (this.svcRead != null) {
    		this.svcRead.reading = false;
    		this.svcRead.cancel();    		
    	}
    	return 0;
    }
    public int WriteData(byte[] data)
    {
    	if (svcWrite != null) {
    		this.svcWrite.write(data);
    	}    	
    	return 0;
    }
    
   public BTHConnection(String remoteAddress, UUID mUUID, Handler mHdl) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        
	   BluetoothSocket tmp = null;
        this.mHandler = mHdl;
        this.bthAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if (this.bthAdapter != null) {
        	this.bthDevice = this.bthAdapter.getRemoteDevice(remoteAddress);
        	//Toast.makeText(null, "Remote device: " + bthDevice.getName(), Toast.LENGTH_LONG);
        } else {
        	this.bthDevice = null;
        	mHandler.obtainMessage(MSG.CNT_NO_BTH.getId()).sendToTarget();
        }
        
       
        if (bthDevice != null) {
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        	try {
        		// MY_UUID is the app's UUID string, also used by the server code
        		tmp = bthDevice.createRfcommSocketToServiceRecord(mUUID);
        		
        	} catch (IOException e) { }
        }
        bthSocket = tmp;
        
    }
 
    public void run() {
    	//MSG("Thread run");
        // Cancel discovery because it will slow down the connection
    	if (bthAdapter != null) {
    		if (bthAdapter.isDiscovering()) {
        		bthAdapter.cancelDiscovery();
        	}
    	}
    	
        try {
            // Connect the device through the socket. This will block
            // it succeeds or throws an exception 
        	bthSocket.connect();
            mHandler.obtainMessage(MSG.CNT_SUCCESSED.getId(), bthDevice).sendToTarget();
        } catch (IOException connectException) {
        	//connectException.printStackTrace();
        	mHandler.obtainMessage(MSG.CNT_FAILED.getId(), bthDevice).sendToTarget();
            	
            try {
            	bthSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
        //MSG("Device connected");
        
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {            	
        	bthSocket.close();
        } catch (IOException e) { }
    }
    
    
    
    
}


