package digitalisp.android.RemoteCotrol;

import java.nio.ByteBuffer;

import android.os.Handler;

import com.pawel.android.BTHControl.TOF;
import com.pawel.android.BTHControl.TOS;

import digitalisp.android.comunication.CState;
import digitalisp.android.comunication.ConnectionMgr;
import digitalisp.android.comunication.RemoteDevicesRecord;
import digitalisp.android.comunication.frame.Frame;

public class VolumeControlConnectionMgr extends ConnectionMgr
{
	private final String TAG = "VolumeControlConnectionMgr";
	private IVolumeControl volumeControlInterface;
	private VolumeControl volumeControlObj;
	private Handler handler;
	public int id;
	
	public VolumeControlConnectionMgr(RemoteDevicesRecord _record, IVolumeControl _volumeCtrl)
	{
		super(_record);
		this.volumeControlInterface = _volumeCtrl;
	}
	public VolumeControlConnectionMgr(RemoteDevicesRecord _record, Handler _hdl)
	{
		super(_record);
		this.handler = _hdl;
	}
	public void setHandler(Handler _hdl)
	{
		this.handler = _hdl;
	}
	public void Read(ByteBuffer _buffer)
	{
		super.Read(_buffer);
		if (_buffer != null)
		{
			String text = new String(_buffer.array());
			handler.obtainMessage(VolumeControlMgr.MSG_MESSAGE, text).sendToTarget();
		}
		
		//Frame frame = new Frame(_buffer.array());
		//volumeControlObj = VolumeControl.createFromFrame(frame);
		//this.dispatchVolumeControl();		
	}
	
	public void onStateChange(CState _state) 
	{
		super.onStateChange(_state);
		//this.volumeControlInterface.volumeStatusChange(_state);
		handler.obtainMessage(VolumeControlMgr.MSG_STATUS_CHANGE, _state).sendToTarget();
	}
	
	public void onError(Object _error) 
	{
		super.onError(_error);
		handler.obtainMessage(VolumeControlMgr.MSG_ERROR, _error).sendToTarget();
	}
	
	public void Send(ByteBuffer _buff)
	{
		this.Write(_buff);
	}
	
	
	
	private void dispatchVolumeControl()
	{
		if ((volumeControlObj != null))
		{
			switch (volumeControlObj.state)
			{
			case Initialize:
				handler.obtainMessage(VolumeControlMgr.MSG_INITIALIZE, volumeControlObj).sendToTarget();
				break;
			case Message:
				//volumeControlInterface.volumeChange(volumeControlObj);
				handler.obtainMessage(VolumeControlMgr.MSG_CHANGE, volumeControlObj).sendToTarget();
				break;
			case Action:
				switch (volumeControlObj.volumeAction)
				{
				case Up: 
					handler.obtainMessage(VolumeControlMgr.MSG_UP, volumeControlObj).sendToTarget();
					//volumeControlInterface.volumeUp();
					break;
				case Down:
					//volumeControlInterface.volumeDown();
					handler.obtainMessage(VolumeControlMgr.MSG_DOWN, volumeControlObj).sendToTarget();
					break;
				case Mute:
					//volumeControlInterface.mute(volumeControlObj.volumeMute);
					handler.obtainMessage(VolumeControlMgr.MSG_MUTE, volumeControlObj.volumeMute).sendToTarget();
					break;
				default: break;
				}
				break;
			}
			
		}
	}
}
