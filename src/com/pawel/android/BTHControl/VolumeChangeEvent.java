package com.pawel.android.BTHControl;

import java.util.EventObject;

public class VolumeChangeEvent extends EventObject {
	private int _vol;
	private int _min;
	private int _max;
	private boolean _mute;
	
	public VolumeChangeEvent(Object source, int volumeLevel, int min, int max, boolean mute) {
		super(source);
		this._vol = volumeLevel;
		this._mute = mute;
		this._min = min;
		this._max = max;
	}
	public int Volume() {
		return _vol;
	}
	public int Min() {
		return _min;
	}
	public int Max() {
		return _max;
	}
	public boolean Mute() {
		return _mute;
	}
}
