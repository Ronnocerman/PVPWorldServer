package pvpworldserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class Driver 
{
	static ServerSocketChannel serverChannel = null;
	static ArrayList<PlayerConnection> playerConnections = new ArrayList<PlayerConnection>();
	static ArrayList<PlayerConnection> attemptedConnections = new ArrayList<PlayerConnection>();
	static SocketChannel clientChannel = null;
	static ServerSocket ss = null;
	
	public static void main(String[]args)
	{
	}
	public static void setupConnections()
	{
		try 
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			ss = serverChannel.socket();
			ss.bind(new InetSocketAddress(5472));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static void serverLoop()
	{
		while(true)
		{
			checkForNewConnections();
			//checkUDPConnections();
			checkTCPConnections();
			//sendUDPResponses();
			//sendTCPResponses();
			ByteBuffer b = ByteBuffer.allocate(65536);
			b.clear();
			if(clientChannel!=null)
				try {
					if(clientChannel.read(b) != -1)
					{
						for(int i = 0; i<b.array().length;i++)
						{
							if(b.array()[i]!=0)
							System.out.println(b.array()[i]);
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(clientChannel==null)
			try 
			{
				clientChannel = serverChannel.accept();
				if(clientChannel != null)
				{
					System.out.println("Client Accepted");
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public static void checkForNewConnections()
	{
		
		//Checks for new connections
		for(boolean finished = false;!finished;)
		{
			SocketChannel attemptedConnection = null;
			try {
				attemptedConnection = serverChannel.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(attemptedConnection!=null)
			{
				attemptedConnections.add(new PlayerConnection(attemptedConnection));
				continue;
			}
			else
			{
				finished = true;
			}
		}
		
		//Finalizes found connections

		for(int i = 0;i<attemptedConnections.size();i++)
		{
			if(attemptedConnections.get(i).finishConnect())
			{
				playerConnections.add(attemptedConnections.remove(i));
				--i;
			}
			else
			{
				if(System.currentTimeMillis()-attemptedConnections.get(i).getTimeCreated() > 5000)
				{
					attemptedConnections.remove(i);
					--i;
					continue;
				}
			}
		}
	}
	public static void checkTCPConnections()
	{
		for(int i = 0;i<playerConnections.size();i++)
		{
			//Create ByteBuffer
			ByteBuffer b = ByteBuffer.allocate(65536);
			b.clear();
			
			//Read from Player Connection, if error, remove connection
			try
			{
				playerConnections.get(i).read(b);
			}
			catch(IOException e)
			{
				playerConnections.remove(i);
				--i;
				continue;
			}
			if(b.array().length > 0)
			{
				playerConnections.get(i).addData(b.array());
				completeTCPCommand(playerConnections.addCommand());
			}
		}
	}
}
