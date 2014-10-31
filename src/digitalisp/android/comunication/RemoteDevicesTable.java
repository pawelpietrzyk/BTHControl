package digitalisp.android.comunication;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.database.Cursor;

import digitalisp.android.providers.Comunication.RemoteDevices;

public class RemoteDevicesTable extends ArrayList<RemoteDevicesRecord> 
{
	 public RemoteDevicesTable()
	 {
		 
	 }
	 
	 public void fill(ContentProvider _provider)
	 {
		 if (_provider != null)
		 {
			 Cursor cur = _provider.query(RemoteDevices.CONTENT_URI, RemoteDevices.PROJECTION, null, null, null);
			 if (cur != null)
			 {
				 while (cur.moveToNext())
				 {
					 RemoteDevicesRecord record = new RemoteDevicesRecord();
					 record.mID = cur.getInt(0);				 
					 record.mType = DType.valueOf(cur.getString(RemoteDevices.DEVTYPE_COLUMN_IDX));
					 record.mAddress = cur.getString(RemoteDevices.DEV_COLUMN_IDX);
					 record.mPort = cur.getInt(RemoteDevices.IDX_DEV_PORT);
					 this.add(record);					 
				 }				 				 
			 }
		 }
	 }
	 
	 public void fill(Cursor c)
	 {
		 if (c != null)
		 {
			 while (c.moveToNext())
			 {
				 RemoteDevicesRecord record = new RemoteDevicesRecord(c);				 
				 this.add(record);					 
			 }				 				 
		 }
		
	 }
	 
	 public int count()
	 {
		 return this.size();
	 }
}
