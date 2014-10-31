package com.pawel.android.BTHControl;

public enum TOS {
	UNK(0),
	UP(85),
	DOWN(68),
	MUTE(77),
	EXIT(81);
	private final int id;
	TOS(int id) {
		this.id = id;
	}
	public int getId()
	{
		return this.id;
	}
	public byte getByte()
	{
		return (byte)this.id;
	}
}
