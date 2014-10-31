package digitalisp.android.comunication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionPool extends HashMap<Integer, ConnectionMgr> 
{	 
		
	public ConnectionPool()
	{
		super();
	}
	public ConnectionMgr put(int key, RemoteDevicesRecord _device)
	{
		ConnectionMgr mgr = new ConnectionMgr(_device);
		if (mgr != null)
		{
			this.put(key, mgr);
		}
		return null;		
	}
	
	public ConnectionMgr get(int key)
	{
		return (ConnectionMgr)this.get(Integer.valueOf(key));
	}

	
}
