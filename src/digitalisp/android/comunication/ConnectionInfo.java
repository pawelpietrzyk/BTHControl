package digitalisp.android.comunication;

import java.nio.ByteBuffer;

import android.os.Handler;

public class ConnectionInfo {
	
	private final String TAG = "ConnectionInfo";	
	public final Object 		mAddress;
	public final CState 		mCState;	
	public final User	 		mUser;
	public final boolean		mKeepConnect;
	public final boolean		mIsAlive;
	
	public ConnectionInfo(Connection c)
	{
		mAddress = c.getAddress();
		mCState = c.getConnectionState();
		mUser = c.getUser();
		mKeepConnect = c.getKeepConnect();
		mIsAlive = c.getIsAlive();		
	}
		
}
