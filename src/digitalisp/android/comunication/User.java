package digitalisp.android.comunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private String mAccType;
	private String mAccName;
	private String mToken;
	
	public User(String _type, String _name, String _token)
	{
		mAccType = _type;
		mAccName = _name;
		mToken = _token;
	}
	
	public String accType()
	{
		return this.mAccType;
	}
	public String accName()
	{
		return this.mAccName;
	}
	public String accToken()
	{
		return this.mToken;
	}
	
	public JSONObject pack()
	{		
		JSONObject obj = new JSONObject();
		try {
			obj.put("t", mAccType);
			obj.put("n", mAccName);
			obj.put("p", mToken);
		} catch (JSONException e) {
			return null;
		}				
		return obj;		
	}

}
