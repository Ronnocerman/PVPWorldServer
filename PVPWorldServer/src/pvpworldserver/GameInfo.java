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
			ResultSet rs = readLogin.executeQuery("SELECT * from userpasswords ORDER BY userID WHERE userID = " + GameLookup.lookupAccountID(accountName));
			byte[] retrievedPassword = rs.getBytes("encryptedpassword");
			for(int i = 0;i<retrievedPassword.length;i++)
			{
				if(retrievedPassword[i]==c.getCommandBody()[i])
				{
					continue;
				}
				else
				{
					return false;
				}
			}
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
}
