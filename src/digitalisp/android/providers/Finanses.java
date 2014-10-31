package digitalisp.android.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Finanses {
	public static final String AUTHORITY = "digitalisp.android.providers.Finanses";
	public static final String DATABASE_NAME = "finanses.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final class Categories implements BaseColumns {
		public static final String TABLE_NAME = "Categories";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

		public static final String DEFAULT_SORT_ORDER = "modified DESC";	
        
        public static final String NAME = "name";
		
		public static final String DESC = "description";
		
		public static final String PARENT_ID = "parentId";
        
        public static final String CREATED_DATE = "created";
        
        public static final String MODIFIED_DATE = "modified";  
        
        public static final String SYNCH = "sync";
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digitalisp.category";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digitalisp.category";
        
        public static final String[] PROJECTION = new String[] {
        	Categories._ID,
        	Categories.NAME,
        	Categories.DESC,
        	Categories.PARENT_ID
        };
        
        public static final int NAME_COLUMN_IDX = 1;
        public static final int DESC_COLUMN_IDX = 2;
        public static final int PARENTID_COLUMN_IDX = 3;
        
	}
}
