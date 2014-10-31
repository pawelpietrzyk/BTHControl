package digitalisp.android.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Sync {
	public static final String AUTHORITY = "digitalisp.android.providers.Sync";
	public static final String DATABASE_NAME = "sync.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final class Commands implements BaseColumns
	{
		public static final String TABLE_NAME = "Commands";
	
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		
		public static final String DEFAULT_SORT_ORDER = "modified DESC";	
        
        public static final String CMDTYPE = "cmd_type";
        
        public static final String CMD = "cmd_cmd";
        
        public static final String CREATED_DATE = "created";
        
        public static final String MODIFIED_DATE = "modified";
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digitalisp.command";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digitalisp.command";
        
        public static final String[] PROJECTION = new String[] {
        	Commands._ID,
        	Commands.CMDTYPE,
        	Commands.CMD        	
        };
        
        public static final int CMDTYPE_COLUMN_IDX = 1;
        public static final int CMD_COLUMN_IDX = 2;
        
	}
}
