package pvpworldserver;

import pvpworldserver.NetworkProtocol;

public class TCPCommand extends Command
{
	public TCPCommand(byte[] data)
	{
		super(NetworkProtocol.joinByteArrays(NetworkProtocol.resizeByteArray(data, 0, 1),NetworkProtocol.resizeByteArray(data, 4, data.length-1)));
	}
	public TCPCommand(byte commandType,byte commandSpecific,byte[]... commandBody)
	{
		super(commandType,commandSpecific,commandBody);
	}
	public byte[] toBytes()
	{
		byte[] command = {networkEventType,networkEventSpecific};
		short shortLength = (short)(data.length*2);
		for(int i = 0;i<this.data.length;i++)
		{
			shortLength+=data[i].length;
		}
		byte[] length = NetworkProtocol.shortToBytes(shortLength);
		byte[] data = NetworkProtocol.joinByteArrays(addParameterLengths(this.data));
		return NetworkProtocol.joinByteArrays(command,length,data);
	}
}
