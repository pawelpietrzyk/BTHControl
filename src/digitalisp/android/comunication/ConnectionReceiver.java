package digitalisp.android.comunication;

public interface ConnectionReceiver 
{
	public void onRead(Object _receivedObj);
	public void onMessage(String _msg);
	public void onStateChange(CState _state);
	public void onError(Object _error);
	
}
