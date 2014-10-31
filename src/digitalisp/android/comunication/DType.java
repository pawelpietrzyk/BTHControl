package digitalisp.android.comunication;

public enum DType {
	BTH(1),
	NET(2),
	UNKNOWN(0);
	
	private final int id;
	DType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}	
}
