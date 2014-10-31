package digitalisp.android.comunication;

import android.database.Cursor;
import android.net.Uri;
import digitalisp.android.comunication.settings.Settings;
import digitalisp.android.providers.Comunication.RemoteDevices;

public class RemoteDevicesRecord {
	public int 	mID;
	public DType 	mType;
	public String	mAddress;
	public int		mPort;
	public int		mTimeout;
	public String	mName;
	
	public RemoteDevicesRecord()
	{
		mID = 0;
		mType = DType.UNKNOWN;
		mAddress = "";
		mPort = Settings.appDefPort;
		mTimeout = Settings.appDefTimeout;
	}
	
	public RemoteDevicesRecord(Cursor c)
	{
		if (c != null)
		{			
			
			mID = c.getInt(0);
			mAddress = c.getString(RemoteDevices.DEV_COLUMN_IDX);
			mPort = c.getInt(RemoteDevices.IDX_DEV_PORT);
			String type = c.getString(RemoteDevices.DEVTYPE_COLUMN_IDX);
			mName = c.getString(RemoteDevices.IDX_DEV_NAME);
			
			if (type.equalsIgnoreCase(RemoteDeviceActivity.DEVICE_TYPE_BTH))
			{
				mType = DType.BTH;
			}
			else if (type.equalsIgnoreCase(RemoteDeviceActivity.DEVICE_TYPE_NET))
			{
				mType = DType.NET;
			}
			else
			{
				mType = DType.UNKNOWN;
			}
		}
	}
}
