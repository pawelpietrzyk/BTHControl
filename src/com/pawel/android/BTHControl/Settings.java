package com.pawel.android.BTHControl;

import java.util.UUID;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Settings {
	public static final UUID mUUID = UUID.fromString("E82F3B8B-8DC0-4669-9CE4-C0BBC1B80DED");
	public static final String SET_NAME = "Settings";
	private final Activity source;
	private final SharedPreferences settings;
	private final SharedPreferences.Editor editor;
	
	public Settings(Activity srcAct)
	{
		this.source = srcAct;
		this.settings = source.getSharedPreferences(SET_NAME, 0);
		this.editor = this.settings.edit();
	}
	
	public int setAddress(String address)
    {    	
        try {
        	editor.putString("deviceAddress", address);       
            editor.commit();
        } catch (Exception ex) {
        	return 1;
        }		
        //Log("Zapisano urz¹dzenie: \n" + address);
        Toast.makeText(source, "Zapisano urz¹dzenie: " + address, Toast.LENGTH_LONG).show();
        return 0;
    }
    
	public String getAddress()
    {
		try {
			return this.settings.getString("deviceAddress", "NODEVICE");
		} catch (Exception ex) {
			return "ERROR";
		}                
    }
	
	
	public int setName(String name)
    {    	
		try {
			editor.putString("deviceName", name);     
	        editor.commit();
		} catch (Exception ex) {
			return 1;
		}        
        return 0;
    }
	
	public String GetName()
    {
		try {
			return settings.getString("deviceName", "NODEVICE");
		} catch (Exception ex) {
			return "ERROR";
		}                
    }
    public int setAutoInit(Boolean stan)
    {
    	try {
    		editor.putBoolean("autoInit", stan);
            editor.commit();    
    	} catch (Exception ex) {
    		return 1;
    	}            
        return 0;
    }
    
    
    
    public Boolean getAutoInit()
    {
    	try {
    		return settings.getBoolean("autoInit", false);
    	} catch (Exception ex) {
    		Toast.makeText(source, ex.getMessage(), Toast.LENGTH_LONG);
    		return false;
    	}
    	
                
    }
}
