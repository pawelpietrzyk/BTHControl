package digitalisp.android.comunication.settings;

import java.util.UUID;

public class Settings 
{
	public static final UUID appUUID = UUID.fromString("E82F3B8B-8DC0-4669-9CE4-C0BBC1B80DED");
	public static final int appDefBufSize = 1024;
	public static final int appDefPort = 8082;
	public static final int appDefTimeout = 10000;
	
	public static final String deviceAddressFormatHttp = "http://{0}:{1}/";
	//public static final String deviceAddressFormatNet = "{0}:{1}/";
	
}
