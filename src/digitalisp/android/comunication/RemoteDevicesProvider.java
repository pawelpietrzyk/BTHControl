package digitalisp.android.comunication;

import java.util.HashMap;


import digitalisp.android.comunication.settings.Settings;
import digitalisp.android.providers.Comunication;
import digitalisp.android.providers.Comunication.RemoteDevices;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RemoteDevicesProvider extends ContentProvider {

private static final String TAG = "RemoteDevicesProvider";
	
	private static HashMap<String, String> sProjectionMap;
	private static final UriMatcher sUriMatcher;
    private DatabaseHelper mOpenHelper;
    
    private static final int DIR = 1;
    private static final int ITEM = 2;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, Comunication.DATABASE_NAME, null, Comunication.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + RemoteDevices.TABLE_NAME + " ("
                    + RemoteDevices._ID + " INTEGER PRIMARY KEY,"                    
                    + RemoteDevices.DEVTYPE + " TEXT,"
                    + RemoteDevices.DEVNAME + " TEXT,"
                    + RemoteDevices.DEVADDR + " TEXT,"
                    + RemoteDevices.DEV_PORT + " INTEGER,"                    
                    + RemoteDevices.DEVSELECTED + " INTEGER,"
                    + RemoteDevices.CREATED_DATE + " INTEGER,"
                    + RemoteDevices.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
	
	
    @Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case DIR:
            count = db.delete(RemoteDevices.TABLE_NAME, where, whereArgs);
            break;

        case ITEM:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(RemoteDevices.TABLE_NAME, RemoteDevices._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) 
		 {
	        case DIR:        
	            return RemoteDevices.CONTENT_TYPE;

	        case ITEM:
	            return RemoteDevices.CONTENT_ITEM_TYPE;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	     }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != DIR) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) 
        {
            values = new ContentValues(initialValues);
        } else 
        {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        if (values.containsKey(RemoteDevices.CREATED_DATE) == false) {
            values.put(RemoteDevices.CREATED_DATE, now);
        }

        if (values.containsKey(RemoteDevices.MODIFIED_DATE) == false) {
            values.put(RemoteDevices.MODIFIED_DATE, now);
        }

        if (values.containsKey(RemoteDevices.DEVTYPE) == false) {            
            values.put(RemoteDevices.DEVTYPE, DType.UNKNOWN.name());
        }
        
        if (values.containsKey(RemoteDevices.DEVSELECTED) == false) {            
            values.put(RemoteDevices.DEVSELECTED, 0);
        }
        
        if (values.containsKey(RemoteDevices.DEVNAME) == false) {            
            values.put(RemoteDevices.DEVNAME, "Bez nazwy");
        }
        
        if (values.containsKey(RemoteDevices.DEV_PORT) == false) {            
            values.put(RemoteDevices.DEV_PORT, Settings.appDefPort);
        }
        

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(
        		RemoteDevices.TABLE_NAME, 
        		RemoteDevices.DEVADDR, 
        		values);
        
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(
            		RemoteDevices.CONTENT_URI, 
            		rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);		
	}

	@Override
	public boolean onCreate() {		
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(RemoteDevices.TABLE_NAME);
        
        switch (sUriMatcher.match(uri)) {
        case DIR:
            qb.setProjectionMap(sProjectionMap);
            break;

        case ITEM:
            qb.setProjectionMap(sProjectionMap);
            qb.appendWhere(RemoteDevices._ID + "=" + uri.getPathSegments().get(1));
            
            break;       

        default:
            //throw new IllegalArgumentException("Unknown URI " + uri);
        	return null;
        }
        //selection = "where dev_selected = 1";
        //qb.appendWhere(RemoteDevices.DEVSELECTED + "=" + "1");
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;		
	}
	
	

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case DIR:
            count = db.update(RemoteDevices.TABLE_NAME, values, where, whereArgs);
            break;

        case ITEM:
            String Id = uri.getPathSegments().get(1);
            count = db.update(RemoteDevices.TABLE_NAME, values, RemoteDevices._ID + "=" + Id
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
		
	}
	
	static 
	{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Comunication.AUTHORITY, "RemoteDevices", DIR);
        sUriMatcher.addURI(Comunication.AUTHORITY, RemoteDevices.TABLE_NAME + "/#", ITEM);       

        sProjectionMap = new HashMap<String, String>();
        sProjectionMap.put(RemoteDevices._ID, RemoteDevices._ID);
        sProjectionMap.put(RemoteDevices.DEVTYPE, RemoteDevices.DEVTYPE);
        sProjectionMap.put(RemoteDevices.DEVNAME, RemoteDevices.DEVNAME);
        sProjectionMap.put(RemoteDevices.DEVADDR, RemoteDevices.DEVADDR);
        sProjectionMap.put(RemoteDevices.DEV_PORT, RemoteDevices.DEV_PORT);
        sProjectionMap.put(RemoteDevices.DEVSELECTED, RemoteDevices.DEVSELECTED);
        sProjectionMap.put(RemoteDevices.MODIFIED_DATE, RemoteDevices.MODIFIED_DATE);
    }

}
