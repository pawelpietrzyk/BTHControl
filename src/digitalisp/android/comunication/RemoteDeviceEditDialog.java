package digitalisp.android.comunication;

import com.pawel.android.BTHControl.R;

import digitalisp.android.RemoteControl.settings.Settings;
import digitalisp.android.providers.Comunication.RemoteDevices;
import digitalisp.android.providers.Finanses.Categories;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RemoteDeviceEditDialog extends Activity {
	private static final String TAG = "RemoteDeviceEditDialog";
	
	EditText deviceAddress;
	EditText deviceName;
	EditText devicePort;
	public final int STATE_EDIT = 0;
	public final int STATE_INSERT = 1;
	
	private static final String ORIGINAL_CONTENT = "origContent";
	private String mOriginalContent;
	
	
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	
	

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
					
		setContentView(R.layout.add_device_dialog);
		
		deviceAddress = (EditText)this.findViewById(R.id.device_addr_edit);
		deviceName = (EditText)this.findViewById(R.id.device_name_edit);
		devicePort = (EditText)this.findViewById(R.id.device_port_edit);
		
		Intent intent = getIntent();
		final String action = intent.getAction();
		if (Intent.ACTION_INSERT.equals(action))
		{
			mState = STATE_INSERT;
			mUri = this.getContentResolver().insert(intent.getData(), null);
			if (mUri == null) {
                Log.e(TAG, "Failed to insert new device into " + getIntent().getData());
                finish();
                return;
            }
		}
		else if (Intent.ACTION_EDIT.equals(action))
		{
			mState = STATE_EDIT;
			mUri = intent.getData();
		}
		else
		{
			Log.e(TAG, "Action not defined");
			finish();
			return;
		}
		
		mCursor = managedQuery(mUri, RemoteDevices.PROJECTION, null, null, null);

        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
		
	}
	
	public void onResume()
	{
		super.onResume();
		
		if (mCursor != null)
		{
			mCursor.moveToFirst();
			int idxAddr = mCursor.getColumnIndex(RemoteDevices.DEVADDR);
			int idxName = mCursor.getColumnIndex(RemoteDevices.DEVNAME);
			int idxPort = mCursor.getColumnIndex(RemoteDevices.DEV_PORT);
			
			String addr = mCursor.getString(idxAddr);			
			String name = mCursor.getString(idxName);
			int port = mCursor.getInt(idxPort);
			
			deviceAddress.setText(addr);
			deviceName.setText(name);
			devicePort.setText(String.valueOf(port));
			
			if (mOriginalContent == null) {
                mOriginalContent = addr;
            }
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
    }
	
	protected void onPause() {
        super.onPause();
        
        if (mCursor != null) {
            String text = deviceAddress.getText().toString();
            String name = deviceName.getText().toString();
            String port = devicePort.getText().toString();
            int iPort = 0;
            try
            {
            	iPort = Integer.parseInt(port);
            }
            catch (NumberFormatException ex)
            {
            	iPort = Settings.appDefPort;
            }
        
            int length = text.length();
            
            if (isFinishing() && (length == 0)) 
            {
                setResult(RESULT_CANCELED);
                deleteDevice();            
            } 
            else 
            {
                ContentValues values = new ContentValues();
                values.put(RemoteDevices.DEVADDR, text);
                values.put(RemoteDevices.DEVNAME, name);
                values.put(RemoteDevices.DEV_PORT, iPort);
                values.put(RemoteDevices.MODIFIED_DATE, System.currentTimeMillis());
                
                if (mState == STATE_INSERT)
                {
                	values.put(RemoteDevices.DEVTYPE, "NET");                	
                }

                getContentResolver().update(mUri, values, null, null);
            }
        }
    }	
	
	public void handleSave(View _view)
	{
		this.setResult(RESULT_OK);
		this.finish();
		
	}
	
	public void handleCancel(View _view)
	{
		this.cancelDevice();
	}
	
	private final void cancelDevice() {
        if (mCursor != null) 
        {
            if (mState == STATE_EDIT) 
            {                
                mCursor.close();
                mCursor = null;
                ContentValues values = new ContentValues();
                values.put(RemoteDevices.DEVADDR, mOriginalContent);
                getContentResolver().update(mUri, values, null, null);
            } 
            else if (mState == STATE_INSERT) 
            {            
                deleteDevice();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    
    private final void deleteDevice() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            deviceAddress.setText("");
        }
    }

}
