package pvpworldserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import static pvpworldserver.NetworkProtocol.*;

public class GameInfo 
{
	public static boolean processLogin(Command c,PlayerConnection pc)
	{
		try 
		{
			String accountName = new String(c.getParameter(1));
			Statement readLogin = ServerDriver.databaseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = readLogin.executeQuery("SELECT * from userpasswords WHERE userID = " + GameLookup.lookupAccountID(accountName) + ";");
			rs.next();
			byte[] retrievedPassword = rs.getBytes("encryptedpassword");
			for(int i = 0;i<retrievedPassword.length;i++)
			{
				if(retrievedPassword[i]==c.getParameter(0)[i])
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
		if(c.getParameter(0).length == 8)
		{
			//Parses for the long of the requested set
			long requestedSet = NetworkProtocol.bytesToLong(c.getParameter(0));
			//Directory containing the ImageSet files
			File directory = new File("C:/Documents and Settings/Connor/My Documents/ImageSets/");
			//List of files in the directory
			String[] fileList = directory.list();
			//Parses through list to find the correct ImageSet
			for(int i = 0;i<fileList.length;i++)
			{
				//If the file matches the requested Long
				if(fileList[i].equals(""+requestedSet))
				{
					//Create new scanner for the ImageSet file
					Scanner in;
					try {
						in = new Scanner(new File(directory.getAbsolutePath()+"\\" + fileList[i]));
					} catch (FileNotFoundException e) {
						in = null;
						e.printStackTrace();
					}
					//ArrayList of the IDs
					ArrayList<Long> imageIDs = new ArrayList<Long>();
					//Parse through the required images
					while(in.hasNextLine())
					{
						//a = the long represented by the next URL in the next line
						String a = in.nextLine();
						imageIDs.add(Long.parseLong(a));
						if(in.hasNextLine())
						{
							//b = the URL of the required image
							String b = in.nextLine();
							//output = ImageID + ImageURL
							UDPCommand output = new UDPCommand(GAME_INFO,GAME_INFO_IMAGE_LOCATION,longToBytes(Long.parseLong(a)),b.getBytes());
							//send the required image's URL
							pc.sendMessage(output);
						}
					}
					byte[][] outputLongs = new byte[imageIDs.size()][];
					for(int x = 0;x<imageIDs.size();x++)
						outputLongs[x] = longToBytes(imageIDs.get(x));
					UDPCommand output = new UDPCommand(GAME_INFO,GAME_INFO_IMAGESET_REQUEST,outputLongs);
					pc.sendMessage(output);
					break;
				}
			}
		}
		else
		{
			(new Exception()).printStackTrace();
			System.out.print("Imageset Command: ");
			for(int i = 0;i<c.getParameter(0).length;i++)
			{
				System.out.print(","+c.getParameter(0)[i]);
			}
			System.out.println();
		}
	}
}
