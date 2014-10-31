package digitalisp.android.RemoteCotrol;

import digitalisp.android.comunication.CState;

public interface IVolumeControl {
	public void volumeChange(VolumeControl _volume);
	public void volumeUp();
	public void volumeDown();
	public void mute(boolean _mute);
	public void volumeInitialize(VolumeControl _volume);
	public void volumeStatusChange(CState s);
}
