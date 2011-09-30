package pvpworldserver;

import pvpworldserver.NetworkProtocol;

public class UDPCommand extends Command
{
	public UDPCommand(byte[] data)
	{
		super(data);
	}
	public UDPCommand(byte commandType,byte commandSpecific,byte[]... commandBody)
	{
		super(commandType,commandSpecific,commandBody);
	}
	public byte[] toBytes()
	{
		byte[] command = {networkEventType,networkEventSpecific};
		byte[] data = NetworkProtocol.joinByteArrays(addParameterLengths(this.data));
		return NetworkProtocol.joinByteArrays(command,data);
	}
}
