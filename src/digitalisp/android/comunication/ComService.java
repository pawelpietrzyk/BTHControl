package digitalisp.android.comunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.os.Handler;
import android.util.Log;
import digitalisp.android.comunication.settings.Settings;

public abstract class ComService extends Thread 
{
	private final String tag = "ComService";
	private Handler mHdl;	
	private SState mState;
	private InputStream mInStream;
	private OutputStream mOutStream;
		
	public abstract InputStream getInputStream();
	public abstract OutputStream getOutputStream();
	
	
	
	public ComService(Handler _hdl)
	{		
		mHdl = _hdl;				
		this.setName("ComService");
		Log.i(tag, "ComService Thread Initialized");
	}
	
	
	public synchronized void run()
	{
		this.init();
		if (validateRun())
		{			
			this.read();
			//this.cancel();
		}
		else
		{
			Log.i(tag, "Walidacja urochomienia ComService nie powiod³a siê");
		}
	}
	private void init()
	{
		mInStream = this.getInputStream();
		mOutStream = this.getOutputStream();
	}
		
	public int read()
	{		
		
		byte buffer[] = new byte[Settings.appDefBufSize];
		int bytes = 0;	
		
		if (mInStream != null)
		{					
			setState(SState.Reading);
			Log.i(tag, "ComService wesz³a w stan odczytu");
				
			while (mState == SState.Reading)
			{
				try
				{
					try
					{
						bytes = mInStream.read(buffer);
						
					}
					catch (SocketException sex1)
					{
						bytes = -1;
					}					
					
					if (bytes == 0)
					{
						this.cancel();
						return -1;
					}
					if (bytes < 0)
					{
						this.cancel();
						return -2;
					}
					if (bytes == 1)
					{
						if (buffer[0] == 0)
						{
							setState(SState.Stop);
							return -2;
						}
					}
					onRead(buffer, bytes);
				}
				catch (Exception ex)
				{
					setState(SState.Error);
					onError(ex);
					Log.e(tag, ex.getMessage());
				}
			}						
		}
		else
		{
			Log.i(tag, "Input Stream nie zosta³ zainicjowany");
		}
		return 0;
	}
		
	public int write(byte[] _buff) 
	{		
		int lenght;		
		if (_buff != null)
		{
			mOutStream = this.getOutputStream();
			if (mOutStream != null)
			{
				lenght = _buff.length;
				try 
				{
					mOutStream.write(_buff);
					Log.d(tag, "Write: " + new String(_buff));
				} 
				catch (IOException e) 
				{
					onError(e);
					Log.e(tag, e.getMessage());
				}				
				return lenght;
			}
			else
			{
				Log.i(tag, "Output Stream nie zosta³ zainicjowany");
			}
		}		
		return 0;
	}
		
	
	public void cancel()
	{
		//setState(SState.Lost);
		
		try 
		{
			mInStream.close();
			mOutStream.close();
		} 
		catch (IOException e) 
		{
			onError(e);
			Log.e(tag, e.getMessage());
		}			
	}
	
		
	public synchronized boolean validateRun()
	{
		boolean ret = true;
		
		if ((mInStream == null))
		{
			ret = false;
			Log.i(tag, "Input Stream nie zosta³ zainicjowany");
		}
		if ((mOutStream == null))
		{
			ret = false;
			Log.i(tag, "Output Stream nie zosta³ zainicjowany");			
		}
			
		return ret;			
	}
	protected synchronized void setState(SState _state)
	{
		mState = _state;
		onStateChange(mState);
	}
	protected synchronized void onRead(byte[] _obj, int _bytes)
	{
		if (mHdl != null)
		{
			ByteBuffer buff = ByteBuffer.wrap(_obj, 0, _bytes);
			mHdl.obtainMessage(MSG.ON_READ.getId(), buff).sendToTarget();
			Log.d(tag, "Wys³ano sygna³ MSG.ON_READ");
		}
	}
	
	protected synchronized void onStateChange(SState _state)
	{
		if (mHdl != null)
		{
			mHdl.obtainMessage(MSG.ON_STATE_CHANGE.getId(), _state).sendToTarget();				
		}
	}
	protected void onError(Object _err)
	{
		if (mHdl != null)
		{
			mHdl.obtainMessage(MSG.ON_ERROR.getId(), _err).sendToTarget();				
		}
	}		
	

}
