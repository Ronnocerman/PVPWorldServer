package pvpworldserver;

public class Command 
{
	private byte commandType;
	private byte commandSpecific;
	private byte[] body;
	public Command(byte[] data)
	{
		commandType = data[0];
		commandSpecific = data[1];
		body = new byte[data.length-4];
		for(int i = 4;i<data.length;i++)
		{
			body[i-4] = data[i];
		}
	}
	public byte getCommandType()
	{
		return commandType;
	}
	public byte getCommandSpecific()
	{
		return commandSpecific;
	}
}
