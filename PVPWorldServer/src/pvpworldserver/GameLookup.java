package pvpworldserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameLookup 
{
	public static int lookupAccountID(String accountName)
	{
		Statement readID;
		try {
			readID = ServerDriver.databaseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = readID.executeQuery("SELECT * from username WHERE userName = '" + accountName + "';");
			rs.next();
			return rs.getInt("userID");
		} catch (SQLException e) {
			System.out.print("Exception");
			e.printStackTrace();
		}
		System.out.println("Wrong Value");
		return -1;
	}
}
