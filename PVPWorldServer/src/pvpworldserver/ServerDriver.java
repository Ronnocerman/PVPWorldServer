package pvpworldserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static pvpworldserver.NetworkProtocol.*;


public class ServerDriver
{
	static ServerSocketChannel serverChannel = null;
	static ArrayList<PlayerConnection> playerConnections = new ArrayList<PlayerConnection>();
	static ArrayList<PlayerConnection> attemptedConnections = new ArrayList<PlayerConnection>();
	static SocketChannel clientChannel = null;
	static ServerSocket ss = null;
	static Connection databaseConnection;
	
	public static void main(String[]args)
	{
		setupConnections();
		serverLoop();
	}
	public static void setupConnections()
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			String databaseURL = "jdbc:mysql://localhost:3306/PVPWorld";
			//Statement stmnt;
			databaseConnection =DriverManager.getConnection(databaseURL,"PVPWorldServer", "WarePhant8");
			 //stmnt = con.createStatement(); 
			 //stmnt.executeUpdate("CREATE TABLE myTable(test_id int,test_val char(15) not null)");
			 //stmnt.executeUpdate("INSERT INTO myTable(test_id, test_val) VALUES(1,'One')");
			 //stmnt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			 //ResultSet rs = stmnt.executeQuery("SELECT * from myTable ORDER BY test_id");
			 //System.out.println("Display all results:");
			 //while(rs.next()){
			 //int theInt= rs.getInt("test_id");
			 //String str = rs.getString("test_val");
			 //System.out.println("\ttest_id= " + theInt
			 //+ "\tstr = " + str);
			 //}//end while loop
		}
		catch (ClassNotFoundException e1)
		{
			e1.printStackTrace();
			System.exit(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
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
			System.exit(1);
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
			try
			{
				attemptedConnection = serverChannel.accept();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if(attemptedConnection!=null)
			{
				System.out.println("Attempted Connection");
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
				System.out.println("Connection Accepted");
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
				int bytesRead = playerConnections.get(i).read(b);
				if(bytesRead == 0)
					continue;
				if(bytesRead==-1)
				{
					//Removes player from connections if disconnected.
					playerConnections.remove(i);
					--i;
					continue;
				}
				if(b.array().length > 0)
				{
					//Trims info to the bytes which are actually read.
					byte[] info = new byte[bytesRead];
					for(int x = 0;x<bytesRead;x++)
					{
						info[x] = b.array()[x];
					}
					playerConnections.get(i).addData(info);
				}
			}
			catch(IOException e)
			{
				playerConnections.remove(i);
				--i;
				continue;
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
		System.out.println("Command Type: " + commandType);
		if(commandType == GAME_INFO)
		{
			switch(c.getCommandSpecific())
			{
			//case GAME_INFO_HEARTBEAT: GameInfo.processHeartbeat(c,pc);
			case GAME_INFO_LOGIN: GameInfo.processLogin(c,pc);
			//case GAME_INFO_LOGOUT: GameInfo.processLogout(c,pc);
			}
		}
		if(commandType ==GAME_LOOKUP)
		{
			switch(c.getCommandSpecific())
			{
			//case GAME_LOOKUP_PLAYER_ID: GameLookup.lookupPlayerID(c,pc);break;
			//case GAME_LOOKUP_PLAYER_NAME: GameLookup.lookupPlayerName(c,pc);break;
			//case GAME_LOOKUP_CHARACTER_ID: GameLookup.lookupCharacterID(c,pc);break;
			//case GAME_LOOKUP_CHARACTER_NAME: GameLookup.lookupCharacterName(c,pc);break;
			//case GAME_LOOKUP_MAP_ID: GameLookup.lookupMapID(c,pc);break;
			//case GAME_LOOKUP_MAP_NAME: GameLookup.lookupMapName(c,pc);break;
			//case GAME_LOOKUP_MAP_REQUEST: GameLookup.lookupMap(c,pc);break;
			}
		}
		if(commandType == GAME_UPDATE)
		{
			switch(c.getCommandSpecific())
			{
			case GAME_UPDATE_PHYSICS: GameUpdate.updatePhysics(c,pc);break;
			//case GAME_UPDATE_BASE_STATS: GameUpdate.updateBaseStats(c,pc);break;
			//case GAME_UPDATE_DERIVED_STATS: GameUpdate.updateDerivedStats(c,pc);break;
			//case GAME_UPDATE_PLAYER_INFO: GameUpdate.updatePlayerInfo(c,pc);break;
			//case GAME_UPDATE_QUEST: GameUpdate.updateQuest(c,pc);
			//case GAME_UPDATE_CREATURE: GameUpdate.updateCreature(c,pc);
			//case GAME_UPDATE_SCRIPT: GameUpdate.updateScript(c,pc);
			//case GAME_UPDATE_BUFFS: GameUpdate.updateBuffs(c,pc);
			}
		}
		if(commandType == GAME_ITEM)
		{
			switch(c.getCommandSpecific())
			{
			
			}
		}
		
	}
	public static boolean validatePlayerForConnection(int characterID,PlayerConnection pc)
	{
		if(pc.getCharacterID()==characterID)
		{
			return true;
		}
		return false;
	}
}
