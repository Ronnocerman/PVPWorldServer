package pvpworldserver;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import static pvpworldserver.NetworkProtocol.*;

public class GameInfo 
{
	public static boolean processLogin(Command c,PlayerConnection pc)
	{
		try 
		{
			byte[] accountNameBytes = new byte[c.getCommandBody().length-20];
			for(int i = 20;i<c.getCommandBody().length;i++)
			{
				accountNameBytes[i-20] = c.getCommandBody()[i];
			}
			String accountName = new String(accountNameBytes);
			Statement readLogin = ServerDriver.databaseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = readLogin.executeQuery("SELECT * from userpasswords WHERE userID = " + GameLookup.lookupAccountID(accountName) + ";");
			rs.next();
			byte[] retrievedPassword = rs.getBytes("encryptedpassword");
			for(int i = 0;i<retrievedPassword.length;i++)
			{
				if(retrievedPassword[i]==c.getCommandBody()[i])
				{
					continue;
				}
				else
				{
					System.out.println("REJECTED!");
					return false;
				}
			}
			System.out.println("Confirmed");
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	public static void processHeartbeat(Command c, PlayerConnection pc)
	{
		pc.heartBeat();
	}
	public static void processImageSetRequest(Command c,PlayerConnection pc)
	{
		System.out.println("7");
		if(c.getCommandBody().length == 8)
		{
			System.out.println("6");
			long requestedSet = NetworkProtocol.bytesToLong(c.getCommandBody());
			File directory = new File("C:/Documents and Settings/Conner/My Documents/ImageSets");
			String[] fileList = directory.list();
			for(int i = 0;i<fileList.length;i++)
			{
				System.out.println("5");
				if(fileList[i].equals(""+requestedSet))
				{
					System.out.println("4");
					Scanner in = new Scanner(directory.getAbsolutePath()+fileList[i]);
					while(in.hasNextLine())
					{
						System.out.println("3");
						String a = in.nextLine();
						if(in.hasNextLine())
						{
							System.out.println("2");
							String b = in.nextLine();
							byte[] output = new byte[10+b.getBytes().length];
							output[0] = GAME_INFO;
							output[1] = NetworkProtocol.GAME_INFO_IMAGE_LOCATION;
							for(int x = 0;x<8;x++)
							{
								output[x+2] = NetworkProtocol.longToBytes(Long.parseLong(a))[x];
							}
							for(int x = 0;x<b.getBytes().length;x++)
							{
								output[10+x] = b.getBytes()[x];
							}
							pc.sendUDPMessage(output);
							System.out.println("1");
						}
					}
					break;
				}
			}
		}
	}
}
