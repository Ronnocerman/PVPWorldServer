package pvpworldserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		if(c.getCommandBody().length == 8)
		{
			long requestedSet = NetworkProtocol.bytesToLong(c.getCommandBody());
			
		}
	}
}
