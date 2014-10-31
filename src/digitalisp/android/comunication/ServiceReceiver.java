package digitalisp.android.comunication;

public interface ServiceReceiver {
	public void onRead(Object _obj);
	public void onMessage(String _obj);
	public void onError(Object ex);
	public void onStateChange(SState _state);
}
