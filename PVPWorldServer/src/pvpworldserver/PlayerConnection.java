package pvpworldserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Date;
import java.util.ArrayList;

public class PlayerConnection 
{
	private int id;
	private SocketChannel connection;
	private Date timeCreated;
	private byte[] incompleteSend;
	ArrayList<Command> commands = new ArrayList<Command>();
	public PlayerConnection(SocketChannel connection)
	{
		this.connection = connection;
		timeCreated = new Date(System.currentTimeMillis());
	}
	public void setID(int id)
	{
		this.id = id;
	}
	public int getID()
	{
		return id;
	}
	public boolean finishConnect()
	{
		try 
		{
			return connection.finishConnect();
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
	public long getTimeCreated()
	{
		return timeCreated.getTime();
	}
	public void read(ByteBuffer b) throws IOException
	{
		connection.read(b);
	}
	public void addData(byte[] data)
	{
		byte[] temp = incompleteSend;
		incompleteSend = new byte[temp.length + data.length];
		for(int i = 0;i<incompleteSend.length;i++)
		{
			if(i<temp.length)
			{
				incompleteSend[i] = temp[i];
			}
			else
			{
				incompleteSend[i] = data[i-temp.length];
			}
		}
		testDataLength();
	}
	/*
	public byte[] getData()
	{
		if(incompleteSend.length<4)
		{
			return null;
		}
		else
		{
			byte[] testSize = new byte[2];
			testSize[0] = incompleteSend[2];
			testSize[1] = incompleteSend[3];
			if(incompleteSend.length)
			
		}
	}*/
	public void testDataLength()
	{
		if(incompleteSend.length<4)
			return;
		byte[] testSize = new byte[2];
		testSize[0] = incompleteSend[2];
		testSize[1] = incompleteSend[3];
		if(incompleteSend.length >= NetworkProtocol.twoBytesToInt(testSize))
		{
			byte[] commandData = new byte[NetworkProtocol.twoBytesToInt(testSize)];
			for(int i = 0;i<commandData.length;i++)
			{
				commandData[i] = incompleteSend[i];
			}
			byte[] tempData = incompleteSend;//Holds the data so that the command data can be clipped out
			incompleteSend = new byte[tempData.length-commandData.length];
			for(int i = 0;i<incompleteSend.length;i++)
			{
				incompleteSend[i] = tempData[commandData.length+i];
			}
			commands.add(new Command(commandData));
			testDataLength();
		}
	}
}
