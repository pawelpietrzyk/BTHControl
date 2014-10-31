package digitalisp.android.comunication;

import java.util.IllegalFormatException;

public class Protocol {
	public static final class Messages
	{
		public static final String MSG_BEGIN = "BEGIN;";		
		public static final String MSG_END = "END;";
		public static final String MSG_TYPE_SQL = "TYPE:SQL;";
		public static final String MSG_TYPE_INIT = "TYPE:INIT;";
		public static final String MSG_TYPE_OK = "TYPE:OK;";
		public static final String MSG_TYPE_STOP = "TYPE:STOP;";
		public static final String MSG_TYPE_ERROR = "TYPE:ERROR;";
		public static final String MSG_FORMAT = "|TYPE:%1.%2|";
		
		
		public static final String ENDLINE = ";";
	}
	
	public static boolean isOK(String _resp)
	{
		return _resp.contains(Messages.MSG_TYPE_OK);
	}
	public static byte[] msgSTOP()
	{
		return Messages.MSG_TYPE_STOP.getBytes();
	}
	
	/*
	public static byte[] packCommand(Command _cmd)
	{
		if (_cmd != null)
		{
			try
			{
				String str = "|"+ _cmd.getCmdType() + ";" + _cmd.getCmd() + "|";
				//return String.format("|%1;%2|", new Object[] { _cmd.getCmdType(), _cmd.getCmd() }).getBytes();
				return str.getBytes();
			}
			catch (Exception  ex)
			{
				return new String("BAD COMMAND").getBytes();
			}
			
		}
		return null;		
	}
	
	
	
	public static Command unpackCommand(byte[] _buff)
	{
		String scmd = String.valueOf(_buff);
		
		int max = Messages.MSG_FORMAT.length();
		int begin = scmd.indexOf("|");
		int end = scmd.indexOf("|", begin+1);
		
		String[] tab = scmd.substring(begin, end).split(";");
		if (tab.length == 2)
		{
			return new Command(tab[1], tab[2]);
		}
		
		return null;
	}
	
	public static byte[] packInitCommand()
	{
		return Protocol.packCommand(Command.initCommand());
	}
	
	private int numberOfObjects(String _format, char _mark)
	{
		int count = 0;
		for (int i = 1; i <= _format.length(); i++)
		{
			if (_format.charAt(i) == _mark)
			{
				count++;
			}
		}
			
		return count;
	}
	*/
	
	/*
	public static String packMsgString(String[] _msgs)
	{
		String msg;
		msg = Messages.MSG_BEGIN + Messages.MSG_TYPE_SQL;
		int i;
		if (_msgs != null)
		{
			for (i = 1; i < _msgs.length; i++)
			{
				msg = msg + _msgs[i] + Messages.ENDLINE;
			}			
		}
		msg = msg + Messages.MSG_END;
		return msg;
	}
	public static String unpackMsg(byte[] _buffer)
	{
		//int i;
		boolean in = false;
		String buf = String.valueOf(_buffer);
		String tb[] = buf.split(Messages.ENDLINE);
		String msg = "";
		
		for (int i = 1; i < tb.length; i++)
		{
			if (tb[i].equalsIgnoreCase(Messages.MSG_END))
			{
				in = false;
			}
			if (in)
			{
				msg = msg + tb[i];
			}
			if (tb[i].equalsIgnoreCase(Messages.MSG_BEGIN))
			{
				in = true;
			}
						
		}
		return msg;
	}
	
	public static byte[] packMsg(String[] _msgs)
	{
		return Protocol.packMsgString(_msgs).getBytes();
	}
	
	public static String MsgINIT()
	{		
		return Messages.MSG_BEGIN + Messages.MSG_TYPE_INIT + Messages.MSG_END;
	}
	public static byte[] MsgINITBuffer()
	{		
		return Protocol.MsgINIT().getBytes();
	}
	*/
	

}
