package com.pawel.android.BTHControl;

public enum TOD {
	UNKNOWN(0),
	BYTE(1),
	SHORT(2),
	INT(3),
	LONG(4),
	FLOAT(5),
	DOUBLE(6),
	BOOL(7),
	CHAR(8);
	private final int id;
	TOD(int id) {
		this.id = id;
	}
	public byte getId() {
		return (byte)this.id;
	}
}
