package digitalisp.android.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Comunication {
	public static final String AUTHORITY = "digitalisp.android.providers.Comunication";
	public static final String DATABASE_NAME = "comunication.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final class CreatedCommands implements BaseColumns
	{
		public static final String TABLE_NAME = "CreatedCommands";
	
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		
		public static final String DEFAULT_SORT_ORDER = "modified DESC";	
        
        public static final String CMDTYPE = "cmd_type";
        
        public static final String CMD = "cmd_cmd";
        
        public static final String CREATED_DATE = "created";
        
        public static final String MODIFIED_DATE = "modified";
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pawel.command";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pawel.command";
        
        public static final String[] PROJECTION = new String[] {
        	CreatedCommands._ID,
        	CreatedCommands.CMDTYPE,
        	CreatedCommands.CMD        	
        };
        
        public static final int CMDTYPE_COLUMN_IDX = 1;
        public static final int CMD_COLUMN_IDX = 2;		
	}
	
	
	public static final class RemoteDevices implements BaseColumns
	{
		public static final String TABLE_NAME = "RemoteDevices";
	
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		
		public static final String DEFAULT_SORT_ORDER = "modified DESC";	
        
        public static final String DEVTYPE = "dev_type";
        public static final String DEVADDR = "dev_address";	
        public static final String DEVSELECTED = "dev_selected";
        public static final String DEVNAME = "dev_name";
        public static final String DEV_PORT = "dev_port";
        
        
        public static final String CREATED_DATE = "created";
        
        public static final String MODIFIED_DATE = "modified";
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digitalisp.device";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digitalisp.device";
        
        public static final String[] PROJECTION = new String[] {
        	RemoteDevices._ID,
        	RemoteDevices.DEVNAME,
        	RemoteDevices.DEVTYPE,
        	RemoteDevices.DEVADDR,
        	RemoteDevices.DEV_PORT,
        	RemoteDevices.DEVSELECTED
        };
        
        public static final String[] PROJECTION_INSERT = new String[] {
        	RemoteDevices._ID,
        	RemoteDevices.DEVNAME,
        	RemoteDevices.DEVADDR        	
        };
        
        public static final int IDX_DEVSELECTED = 5;
        public static final int IDX_DEV_PORT = 4;
        public static final int IDX_DEV_NAME = 1;
        
        public static final int DEVTYPE_COLUMN_IDX = 2;
        public static final int DEV_COLUMN_IDX = 3;		
	}

}
