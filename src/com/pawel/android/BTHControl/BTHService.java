package com.pawel.android.BTHControl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BTHService extends Thread {
	private final BluetoothSocket mmSocket;
	private final Handler hdl;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public Boolean reading;
    private String typeOfService;
 
    public void SetType(String type) {
    	this.typeOfService = type;
    }
    public String GetType()
    {
    	return this.typeOfService;
    }
    
    public BTHService(BTHConnection cnt) {
    	mmSocket = cnt.getSocket();
    	hdl = cnt.getHandler();
    	    	
    	this.SetType("READ");
    	InputStream tmpIn = null;
    	OutputStream tmpOut = null;
    		
        // 	Get the input and output streams, using temp objects because
        // 	member streams are final
    	try {
    		tmpIn = mmSocket.getInputStream();
    		tmpOut = mmSocket.getOutputStream();
    	} catch (IOException e) { }
    		
    	mmInStream = tmpIn;
    	mmOutStream = tmpOut;
    	reading = true;
    	hdl.obtainMessage(MSG.SVC_INITIALIZED.getId(), this).sendToTarget();
    	
    }
    public BTHService(BTHConnection cnt, String svcType) {
    	mmSocket = cnt.getSocket();
    	hdl = cnt.getHandler();
    	this.SetType(svcType);
    	InputStream tmpIn = null;
    	OutputStream tmpOut = null;
    		
        // 	Get the input and output streams, using temp objects because
        // 	member streams are final
    	try {
    		tmpIn = mmSocket.getInputStream();
    		tmpOut = mmSocket.getOutputStream();
    	} catch (IOException e) { }
    		
    	mmInStream = tmpIn;
    	mmOutStream = tmpOut;
    	reading = false;
    	hdl.obtainMessage(MSG.SVC_INITIALIZED.getId(), this).sendToTarget();    	
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        reading = true;
        // Keep listening to the InputStream until an exception occurs    
        if (this.reading) {
        	hdl.obtainMessage(MSG.SVC_START_READING.getId()).sendToTarget();
        }        
        while (reading) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI Activity
                if (bytes == 0) {
                	hdl.obtainMessage(MSG.SVC_STOP_READING.getId()).sendToTarget();
                	this.reading = false;
                }
                hdl.obtainMessage(MSG.SVC_READ.getId(), bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
            	//String err = "Exception during launching";
            	//mHandler.obtainMessage(MESSAGE_READ, err.getBytes().length, -1, err.getBytes()).sendToTarget();
                //break;
            }
        }
        
    }
 
    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
        	//MSG(String.valueOf(bytes.length));
        	int size = bytes.length;
            mmOutStream.write(bytes);
            hdl.obtainMessage(MSG.SVC_WRITE.getId(), size, -1, bytes).sendToTarget();
            
        } catch (IOException e) { 
        	//MSG("Some problem during writing "+e.getMessage());
        }
    }
 
    /* Call this from the main Activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
