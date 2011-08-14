package pvpworldserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import static pvpworldserver.NetworkProtocol.*;


public class Driver 
{
	static ServerSocketChannel serverChannel = null;
	static ArrayList<PlayerConnection> playerConnections = new ArrayList<PlayerConnection>();
	static ArrayList<PlayerConnection> attemptedConnections = new ArrayList<PlayerConnection>();
	static SocketChannel clientChannel = null;
	static ServerSocket ss = null;
	
	public static void main(String[]args)
	{
		setupConnections();
		serverLoop();
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
			checkCommands();
			try 
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
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
			}
		}
	}
	public static void checkCommands()
	{
		for(int i = 0; i<playerConnections.size();i++)
		{
			if(playerConnections.get(i).hasNextCommand())
			{
				processCommand(playerConnections.get(i).nextCommand(),playerConnections.get(i));
			}
		}
	}
	public static void processCommand(Command c,PlayerConnection pc)
	{
		byte commandType = c.getCommandType();
		
		if(commandType == GAME_INFO)
		{
			switch(c.getCommandSpecific())
			{
			case GAME_INFO_HEARTBEAT: GameInfo.processHeartbeat(c,pc);
			case GAME_INFO_LOGIN: GameInfo.processLogin(c,pc);
			case GAME_INFO_LOGOUT: GameInfo.processLogout(c,pc);
			}
		}
		if(commandType ==GAME_LOOKUP)
		{
			switch(c.getCommandSpecific())
			{
			case GAME_LOOKUP_PLAYER_ID: GameLookup.lookupPlayerID(c,pc);break;
			case GAME_LOOKUP_PLAYER_NAME: GameLookup.lookupPlayerName(c,pc);break;
			case GAME_LOOKUP_CHARACTER_ID: GameLookup.lookupCharacterID(c,pc);break;
			case GAME_LOOKUP_CHARACTER_NAME: GameLookup.lookupCharacterName(c,pc);break;
			case GAME_LOOKUP_MAP_ID: GameLookup.lookupMapID(c,pc);break;
			case GAME_LOOKUP_MAP_NAME: GameLookup.lookupMapName(c,pc);break;
			case GAME_LOOKUP_MAP_REQUEST: GameLookup.lookupMap(c,pc);break;
			}
		}
		if(commandType == GAME_UPDATE)
		{
			switch(c.getCommandSpecific())
			{
			case GAME_UPDATE_PHYSICS: GameUpdate.updatePhysics(c,pc);break;
			}
		}
		
	}
}
