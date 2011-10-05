package pvpworldserver;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.ArrayList;

public class PlayerConnection 
{
	private int playerID;
	private Character loggedCharacter;
	private SocketChannel TCPConnection = null;
	private DatagramChannel UDPConnection = null;
	private Date timeCreated;
	private long timeSinceLastUDP;
	private byte[] incompleteSend = new byte[0];
	private ArrayList<Command> commands = new ArrayList<Command>();
	public PlayerConnection(SocketChannel TCPConnection)
	{
		this.TCPConnection = TCPConnection;
		try 
		{
			TCPConnection.configureBlocking(false);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		timeCreated = new Date(System.currentTimeMillis());
	}
	public PlayerConnection(DatagramChannel UDPConnection)
	{
		this.UDPConnection = UDPConnection;
		try 
		{
			UDPConnection.configureBlocking(false);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		timeCreated = new Date(System.currentTimeMillis());
	}
	public SocketChannel getTCPConnection()
	{
		return TCPConnection;
	}
	public void setTCPConnection(SocketChannel TCPConnection)
	{
		if(this.TCPConnection == null)
		this.TCPConnection = TCPConnection;
	}
	public void setUDPConnection(DatagramChannel UDPConnection)
	{
		if(this.UDPConnection == null)
		{
			if(UDPConnection.isBlocking())
			{
				try {
					UDPConnection.configureBlocking(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			this.UDPConnection = UDPConnection;
		}
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
		loggedCharacter.setID(id);
	}
	public int getCharacterID()
	{
		return loggedCharacter.getID();
	}
	public boolean finishConnect()
	{
		try 
		{
			return TCPConnection.finishConnect();
		} 
		catch (IOException e) 
		{
			return false;
		}
		catch (NullPointerException e)
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
		return TCPConnection.read(b);
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
		if(TCPConnection.isConnected())
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
		if(incompleteSend.length >= NetworkProtocol.bytesToInt(testSize))
		{
			byte[] commandData = new byte[NetworkProtocol.bytesToInt(testSize)];
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
			commands.add(new TCPCommand(commandData));
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
	public boolean hasTCPConnection()
	{
		if(TCPConnection!=null)
			return true;
		return false;
	}
	public boolean hasUDPConnection()
	{
		if(UDPConnection!=null)
			return true;
		return false;
	}
	/*public String getIP()
	{
		if(UDPConnection!=null)
		{
			return UDPConnection.
		}
	}*/
	public void heartBeat()
	{
		timeSinceLastUDP = (new Date()).getTime();
	}
	public void sendMessage(Command output)
	{
		if(output instanceof UDPCommand)
		{
			sendUDPMessage((UDPCommand) output);
		}
		else if(output instanceof TCPCommand)
		{
			sendTCPMessage((TCPCommand) output);
		}
	}
	
	public void sendUDPMessage(UDPCommand output)
	{
		if(hasUDPConnection())
		{
			try 
			{
				UDPConnection.write(ByteBuffer.wrap(output.toBytes()));
				System.out.println("UDP Message Sent");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void sendTCPMessage(TCPCommand output)
	{
		try {
			TCPConnection.write(ByteBuffer.wrap(output.toBytes()));
			System.out.println("TCP Message Sent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public SocketAddress receive(ByteBuffer b) throws IOException
	{
		SocketAddress output;
		if((output = UDPConnection.receive(b))!=null)
			heartBeat();
		return output;
	}
	public long lastUDP()
	{
		return timeSinceLastUDP;
	}
}
