package digitalisp.android.comunication.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

import digitalisp.android.comunication.ComService;

public class NETComService extends ComService
{
	private static final String TAG = "NETComService";
	
	private Socket mSocket;		
	
	public NETComService(Socket _socket, Handler _hdl) 
	{
		super(_hdl);
		mSocket = _socket;
	}
	
	public InputStream getInputStream()
	{
		if (mSocket != null)
		{
			try 
			{
				return mSocket.getInputStream();
			} 
			catch (IOException e) 
			{	
				Log.e(TAG, e.getMessage());					
			}
		}
		return null;			
	}
	
	public OutputStream getOutputStream()
	{
		if (mSocket != null)
		{
			try 
			{
				return mSocket.getOutputStream();
			} 
			catch (IOException e) 
			{
				Log.e(TAG, e.getMessage());					
			}
		}
		return null;
	}		

	public boolean validateRun()
	{
		boolean ret = super.validateRun();
		ret = ret && (this.mSocket != null);
		return ret;
	}
	
	public void cancel()
	{
		super.cancel();
		try 
		{
			mSocket.close();
		} 
		catch (IOException e) 
		{
			Log.e(TAG, e.getMessage());
			onError(e);
		}
	}
}	
