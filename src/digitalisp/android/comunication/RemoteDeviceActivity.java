package digitalisp.android.comunication;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pawel.android.BTHControl.R;

import digitalisp.android.providers.Comunication.RemoteDevices;

public class RemoteDeviceActivity extends ListActivity 
{
	private static final String TAG = "RemoteDeviceActivity";
	
	
	public final String DIALOG_COM_TYPE_BTH = "Bluetooth";
	public final String DIALOG_COM_TYPE_NET = "Internet";
	
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
    public static final int MENU_ITEM_EDIT = Menu.FIRST + 2;
    
    public static final int PICK_NET_DEVICE_REQUEST = 1;
    
    public class DeviceCursorAdapter extends SimpleCursorAdapter
    {
    	private String[] mFrom;
    	private int[] mTo;
    	CheckBox checkBox;

		public DeviceCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			mFrom = from;
			mTo = to;
		}
		
		@Override
		public void bindView (View view, Context context, Cursor cursor)
		{
			if (cursor != null)
			{
				Long id = cursor.getLong(0);
				view.setTag(id);
				int selected = cursor.getInt(RemoteDevices.IDX_DEVSELECTED);
				
				
				checkBox = (CheckBox)view.findViewById(mTo[0]);
				if (checkBox != null)
				{
					checkBox.setChecked((selected > 0));
				}
			}			
			super.bindView(view, context, cursor);			
			
		}
    	
    }
	
	
	protected void onCreate(Bundle _bundle)
	{
		super.onCreate(_bundle);
				
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(RemoteDevices.CONTENT_URI);
        }
        
        getListView().setOnCreateContextMenuListener(this);
        
        
        Cursor cursor = managedQuery(getIntent().getData(), RemoteDevices.PROJECTION, null, null, null);
        
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(        		
        		this, 
        		android.R.layout.simple_list_item_multiple_choice, 
        		cursor,
                new String[] { RemoteDevices.DEVADDR, RemoteDevices.DEVTYPE }, 
                new int[] { android.R.id.text1, android.R.id.text2 });
        */    
        
        DeviceCursorAdapter adapter = new DeviceCursorAdapter(
        		this.getListView().getContext(), 
        		R.layout.add_device_item, 
        		cursor,
                new String[] { RemoteDevices.DEVSELECTED, RemoteDevices.DEVNAME, RemoteDevices.DEVADDR, RemoteDevices.DEVTYPE }, 
                new int[] { R.id.checkBoxSelected, R.id.txtName, R.id.txtAddress, R.id.txtType });
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.add_device_item, 
        		cursor,
                new String[] { RemoteDevices.DEVNAME, RemoteDevices.DEVADDR }, 
                new int[] { R.id.txtName, R.id.txtAddress });
        */
        setListAdapter(adapter);
		
	}
	
	public void bindView(View view, Context context, Cursor cursor) 
	{
		
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, RemoteDeviceActivity.class), null, intent, 0, null);

        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
        	showInsertDialog();
            
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
	{
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        
        Cursor c = this.getContentResolver().query(uri, RemoteDevices.PROJECTION, null, null, null);
        if (c != null)
        {
        	c.moveToFirst();
        	int idx = c.getColumnIndex(RemoteDevices.DEVTYPE);
        	String type = c.getString(idx);
        	if (type.equalsIgnoreCase(DEVICE_TYPE_BTH))
        	{
        		startBTHDeviceEdit(uri);
        	}
        	if (type.equalsIgnoreCase(DEVICE_TYPE_NET))
        	{
        		startNETDeviceEdit(uri);
        	}
        }        
    }
	
	public void onCheckBoxClick(View _view)
	{
		
	}
	public void onDevListItemClick(View _view)
	{
		Object tag = _view.getTag();
		
		if (tag != null)
		{
			Long Id = (Long)tag;
			Uri uri = ContentUris.withAppendedId(getIntent().getData(), Id.longValue());
			ContentValues values = new ContentValues();
			
			CheckBox cb = (CheckBox)_view.findViewById(R.id.checkBoxSelected);
			if (cb.isChecked())
			{
				cb.setChecked(false);
				values.put(RemoteDevices.DEVSELECTED, 0);
			}
			else
			{
				cb.setChecked(true);
				values.put(RemoteDevices.DEVSELECTED, 1);
			}			
			getContentResolver().update(uri, values, null, null);			
		}
		
		TextView txtAddr = (TextView)_view.findViewById(R.id.txtAddress);
		
		Toast.makeText(this, String.valueOf(_view.getId()) +"; "+txtAddr.getText().toString(), Toast.LENGTH_LONG).show();
	}
	
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try 
        {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } 
        catch (ClassCastException e) 
        {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) 
        {        
            return;
        }
        menu.setHeaderTitle(cursor.getString(RemoteDevices.DEV_COLUMN_IDX));
        menu.add(0, MENU_ITEM_EDIT, 0, R.string.menu_edit);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }
	public boolean onContextItemSelected(MenuItem item) 
	{
        AdapterView.AdapterContextMenuInfo info;
        try 
        {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } 
        catch (ClassCastException e) 
        {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) 
        {
            case MENU_ITEM_DELETE: 
            {
                Uri uri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                getContentResolver().delete(uri, null, null);
                return true;
            }
            case MENU_ITEM_EDIT:
            	editDevice(info.id);
            	return true;
        }
        return false;
    }
	
	private void editDevice(long _id)
	{
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), _id);
        
        Cursor c = this.getContentResolver().query(uri, RemoteDevices.PROJECTION, null, null, null);
        if (c != null)
        {
        	c.moveToFirst();
        	int idx = c.getColumnIndex(RemoteDevices.DEVTYPE);
        	String type = c.getString(idx);
        	if (type.equalsIgnoreCase(DEVICE_TYPE_BTH))
        	{
        		startBTHDeviceEdit(uri);
        	}
        	if (type.equalsIgnoreCase(DEVICE_TYPE_NET))
        	{
        		startNETDeviceEdit(uri);
        	}
        }
	}
	
	private void showInsertDialog()
	{
		final CharSequence[] items = {DIALOG_COM_TYPE_BTH, DIALOG_COM_TYPE_NET};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_device_type_label);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	if (DIALOG_COM_TYPE_BTH.equalsIgnoreCase(items[item].toString()))
		    	{
		    		startBTHDevicePicker();
		    	}
		    	if (DIALOG_COM_TYPE_NET.equalsIgnoreCase(items[item].toString()))
		    	{
		    		startNETDevicePicker();
		    	}		        
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void startBTHDevicePicker()
	{
		Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
		intent.addCategory(CATEGORY_BTH);		
		startActivity(intent);
	}
	private void startBTHDeviceEdit(Uri _uri)
	{
		Intent intent = new Intent(Intent.ACTION_EDIT, _uri);
		intent.addCategory(CATEGORY_BTH);		
		startActivity(intent);
	}
	private void startNETDevicePicker()
	{
		Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
		intent.addCategory(CATEGORY_NET);		
		startActivity(intent);		
	}
	private void startNETDeviceEdit(Uri _uri)
	{
		Intent intent = new Intent(Intent.ACTION_EDIT, _uri);
		intent.addCategory(CATEGORY_NET);		
		startActivity(intent);		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        switch (requestCode)
        { 
        case PICK_NET_DEVICE_REQUEST:
        	break;
        
        }
		if (requestCode == PICK_NET_DEVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                
            }
        }
    }
	
	public static final String CATEGORY_BTH = "digitalisp.android.comunication.bth.CATEGORY_BTH";
	public static final String CATEGORY_NET = "digitalisp.android.comunication.bth.CATEGORY_NET";
	
	public static final String DEVICE_TYPE_NET = "NET";
	public static final String DEVICE_TYPE_BTH = "BTH";
	
	
}
