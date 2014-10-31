package com.pawel.android.BTHControl;

import java.util.EventObject;

public class DeviceStateEvents extends EventObject {
	private BTHDeviceState _state;
	
	public DeviceStateEvents(Object source, BTHDeviceState state) {
		super(source);
		_state = state;
	}
	public BTHDeviceState State()
	{
		return _state;
	}
}
