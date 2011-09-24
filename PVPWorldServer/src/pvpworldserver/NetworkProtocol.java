package pvpworldserver;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class NetworkProtocol 
{
public static final byte GAME_INFO = 1;
	public static final byte GAME_INFO_HEARTBEAT = 0;//UDP 
	public static final byte GAME_INFO_LOGIN = 1;//TCP VAR LENGTH
	public static final byte GAME_INFO_LOGOUT = 2;//UDP
	public static final byte GAME_INFO_MAP_REQUEST = 3;//TCP
	public static final byte GAME_INFO_IMAGE = 4;
	public static final byte GAME_INFO_IMAGESET_REQUEST = 5;
	public static final byte GAME_INFO_IMAGE_LOCATION = 6;

public static final byte GAME_LOOKUP = 2;
	public static final byte GAME_LOOKUP_PLAYER_ID = 0;//UDP
	public static final byte GAME_LOOKUP_PLAYER_NAME = 1;//UDP
	public static final byte GAME_LOOKUP_CHARACTER_ID = 2;//UDP
	public static final byte GAME_LOOKUP_CHARACTER_NAME = 3;//UDP
	public static final byte GAME_LOOKUP_MAP_ID = 4;//UDP
	public static final byte GAME_LOOKUP_MAP_NAME = 5;//UDP

public static final byte GAME_UPDATE = 3;
	public static final byte GAME_UPDATE_PHYSICS = 0;//UDP
	public static final byte GAME_UPDATE_BASE_STATS = 1;//UDP
	public static final byte GAME_UPDATE_DERIVED_STATS = 2;//UDP
	public static final byte GAME_UPDATE_PLAYER_INFO = 3;//UDP
	public static final byte GAME_UPDATE_QUEST = 4;//TCP
	public static final byte GAME_UPDATE_CREATURE = 5;//UDP
	public static final byte GAME_UPDATE_SCRIPT = 6;//UDP
	public static final byte GAME_UPDATE_BUFFS = 7;//UDP

public static final byte GAME_ITEM = 4;
	public static final byte GAME_ITEM_USED = 0;//TCP
	public static final byte GAME_ITEM_CONSUMED = 1;//TCP
	public static final byte GAME_ITEM_DROPPED = 2;//TCP
	public static final byte GAME_ITEM_CREATED = 3;//TCP
	public static final byte GAME_ITEM_DESTROYED = 4;//TCP
	public static final byte GAME_ITEM_EQUIPPED = 5;
	public static final byte GAME_ITEM_UNEQUIPED = 6;

public static final byte GAME_TRADE = 5;
	public static final byte GAME_TRADE_OFFERED = 0;
	public static final byte GAME_TRADE_JOINED = 1;
	public static final byte GAME_TRADE_CONFIRMED = 2;
	public static final byte GAME_TRADE_ACCEPTED = 3;
	public static final byte GAME_TRADE_ITEM_OFFERED = 4;
	public static final byte GAME_TRADE_ITEM_REMOVED = 5;
	
public static final byte GAME_COMMUNITY = 6;
	public static final byte GAME_PARTY_INVITE_SENT = 0;
	public static final byte GAME_PARTY_INVITE_DECLINED = 1;
	public static final byte GAME_PARTY_INVITE_ACCEPTED = 2;
	public static final byte GAME_PARTY_LEFT = 3;
	public static final byte GAME_PARTY_LEADERSHIP = 4;
	
	public static final byte GAME_FRIEND_INVITE_SENT = 10;
	public static final byte GAME_FRIEND_INVITE_DECLINED = 11;
	public static final byte GAME_FRIEND_INVITE_ACCEPTED = 12;
	public static final byte GAME_FRIEND_DELETED = 13;
	
	public static final byte GAME_GUILD_INVITE_SENT = 20;
	public static final byte GAME_GUILD_INVITE_DECLINED = 21;
	public static final byte GAME_GUILD_INVITE_ACCEPTED = 22;
	public static final byte GAME_GUILD_LEFT = 23;
	public static final byte GAME_GUILD_RANK = 24;
	
	public static final byte GAME_RAID_INVITE_SENT = 30;
	public static final byte GAME_RAID_INVITE_DECLINED = 31;
	public static final byte GAME_RAID_INVITE_ACCEPTED = 32;
	public static final byte GAME_RAID_LEFT = 33;

public static final byte GAME_CHAT = 7;
	public static final byte GAME_CHAT_MESSAGE_SENT = 0;
	public static final byte GAME_CHAT_IGNORE_PLAYER = 1;

public static final byte GAME_COMBAT = 8;
	public static final byte GAME_COMBAT_ABILITY_USED = 0;

public static final byte GAME_REPORT = 9;
	public static final byte GAME_REPORT_BUG = 0;
	public static final byte GAME_REPORT_CHARACTER = 1;

	public static short signedIntToUnsignedShort(int input)
	{
		if(input>=65536)
		{
			input -= 65536;
		}
		if(input<0)
		{
			input += 65536;
		}
		if(input<32768&&input>=0)
		{
			return (short)input;
		}
		else
		{
			return (short)(input*(0-1));
		}
	}
	public static int unsignedShortToSignedInt(short input)
	{
		if(input<0)
		{
			return (int)(input+65536);
		}
		else
		{
			return (int)input;
		}
	}
	public static int twoBytesToInt(byte[] input)
	{
		ByteBuffer b = ByteBuffer.allocate(2);
		b.put(input[0]);
		b.put(input[1]);
		return unsignedShortToSignedInt(b.getShort(0));
	}
	public static byte[] shortToTwoBytes(short input)
	{
		byte[] output = new byte[2];
		output[0] = (byte) ((input-(input%256))/256);
		output[1] = (byte) (input%256);
		return output;
	}
	public static byte[] longToBytes(long input)
	{
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putLong(input);
		return b.array();
	}
	public static long bytesToLong(byte[] input)
	{
		if(input.length == 8)
		{
			ByteBuffer b =ByteBuffer.wrap(input);
			return b.getLong();
		}
		return 0;
	}
	public static boolean IPsEqual(SocketAddress a,SocketAddress b)
	{
		String a1 = a.toString();
		String b1 = b.toString();
		for(int i = 0;i<a1.length();i++)
		{
			if(a1.substring(i,i+1).equals(":"))
				a1 = a1.substring(0,i);
		}
		for(int i = 0;i<b1.length();i++)
		{
			if(b1.substring(i,i+1).equals(":"))
				b1 = b1.substring(0,i);
		}
		if(a1.equals(b1))
		{
			return true;
		}
		return false;
	}
}
