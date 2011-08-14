package pvpworldserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Date;
import java.util.ArrayList;

public class PlayerConnection 
{
	private int playerID;
	private Character loggedCharacter;
	private SocketChannel connection;
	private Date timeCreated;
	private byte[] incompleteSend = new byte[0];
	private ArrayList<Command> commands = new ArrayList<Command>();
	public PlayerConnection(SocketChannel connection)
	{
		this.connection = connection;
		try {
			connection.configureBlocking(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timeCreated = new Date(System.currentTimeMillis());
	}
	public void setPlayerID(int id)
	{
		this.playerID = id;
	}
	public int getPlayerID()
	{
		return playerID;
	}
	public void setCharacterID(int id)
	{
		this.characterID = id;
	}
	public int getCharacterID()
	{
		return characterID;
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
	public int read(ByteBuffer b) throws IOException
	{
		return connection.read(b);
	}
	public void addData(byte[] data)
	{
		byte[] temp = incompleteSend;
		assert temp!= null;
		assert data !=null;
		
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
		if(connection.isConnected())
		System.out.println("Length: " + incompleteSend.length);
		else
		{
			System.out.println("Not Connected");
		}
		int index;
		for(index = 0; index<incompleteSend.length;index++)
		{
			if(incompleteSend[index]==0)
			{
				continue;
			}
			else
			{
				break;
			}
		}
		if(index==incompleteSend.length)
		{
			incompleteSend = new byte[0];
			return;
		}
		else
		{
			byte[] temp = new byte[incompleteSend.length-index];
			for(int i = index;index<incompleteSend.length;index++)
			{
				temp[index-i] = incompleteSend[index];
			}
		}
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
	public boolean hasNextCommand()
	{
		if(commands.size()>0)
		{
			return true;
		}
		return false;
	}
	public Command nextCommand()
	{
		if(commands.size()>0)
		{
			return commands.remove(0);
		}
		return null;
	}
}
