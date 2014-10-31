package digitalisp.android.comunication.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;
import digitalisp.android.comunication.ComService;
import digitalisp.android.comunication.Connection;
import digitalisp.android.comunication.User;
import digitalisp.android.comunication.settings.Settings;

public class NETConnection extends Connection {
	
	private static final String tag = "NETConnection"; 
	private Socket mSocket;
	private InetSocketAddress mNetAddress;
	private static int mId;
	
	
	public NETConnection(String _host, int _iport, Handler _receiver)
	{		
		super(new InetSocketAddress(_host, _iport), _receiver);
		mId++;
		this.setName("NETConnection " + String.valueOf(mId));
	}
	public NETConnection(String _host, int _iport, User u, Handler _receiver)
	{		
		super(new InetSocketAddress(_host, _iport), u, _receiver);
		mId++;
		this.setName("NETConnection " + String.valueOf(mId));
	}



	@Override
	public boolean OnInit(Object _address) {
		
		if (InetSocketAddress.class == _address.getClass())
		{
			mNetAddress = (InetSocketAddress)_address;
			mSocket = new Socket(); 
			return true;
		}
		return false;
	}

	

	@Override
	public boolean OnConnect(Object _address) {
		
		if (mNetAddress != null && mSocket != null)
		{
			try {
				mSocket.connect(mNetAddress, Settings.appDefTimeout);
				return true;
			} catch (IOException e) {
				onErrorExt(e);
			}
		}		
		
		return false;
	}

	@Override
	public void OnServiceRun(Handler _hdl) 
	{
		svcCom = new NETService(mSocket, _hdl);
		svcWrite = new NETService(mSocket, _hdl);
		
		svcCom.start();
	}

	@Override
	public void OnStop() {
		try {
			mSocket.shutdownInput();
			Log.d(tag, "Input Stream shutdown call");
		} catch (IOException e) {
			Log.d(tag, e.getMessage());
		}
		
		try {
			mSocket.shutdownOutput();
			Log.d(tag, "Output Stream shutdown call");
		} catch (IOException e) {
			Log.d(tag, e.getMessage());			
		}
	}
	
	
	public class NETService extends ComService
	{
		private static final String TAG = "NETServiceRead";
		
		private Socket mSocket;		
		
		public NETService(Socket _socket, Handler _hdl) 
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
				onError(e);
			}
		}
	}	
	

}
