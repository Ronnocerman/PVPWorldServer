package pvpworldserver;
import static pvpworldserver.NetworkProtocol.*;
import java.util.ArrayList;

import pvpworldserver.NetworkProtocol;

public abstract class Command
{
	protected byte networkEventType;
	protected byte networkEventSpecific;
	protected byte[][] data;
	public Command(byte networkEventType,byte networkEventSpecific,byte[][] data)
	{
		this.networkEventType = networkEventType;
		this.networkEventSpecific = networkEventSpecific;
		this.data = data;
	}
	public Command(byte[] data)
	{
		this.networkEventType = data[0];
		this.networkEventSpecific = data[1];
		singleDimensionToDouble(NetworkProtocol.resizeByteArray(data,2,data.length-1));
	}
	private boolean singleDimensionToDouble(byte[] data)
	{
		ArrayList<byte[]> parameters = new ArrayList<byte[]>();
		if(data.length>=2)
		{
			for(int i = 0;i+1<data.length;)
			{
				byte[] byteLength = new byte[2];
				byteLength[0] = data[i];
				byteLength[1] = data[i+1];
				short paramLength = bytesToShort(byteLength);
				if(data.length-i >=paramLength)
				{
					byte[] parameter = new byte[paramLength];
					for(int x = 0;x<paramLength;x++)
					{
						parameter[x] = data[i+2+x];
					}
					parameters.add(parameter);
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
		this.data = new byte[parameters.size()][];
		for(int x = 0;x<this.data.length;x++)
		{
			this.data[x] = parameters.get(x);
		}
		return true;
	}
	public byte[][] addParameterLengths(byte[][] parameters)
	{
		byte[][] output;
		ArrayList<byte[]> adjusted = new ArrayList<byte[]>();
		for(int x = 0;x<parameters.length;x++)
		{
			if(parameters[x].length<=2)
				throw new IllegalArgumentException();
			adjusted.add(new byte[parameters[x].length+2]);
			adjusted.get(x)[0] = NetworkProtocol.shortToBytes((short)parameters[x].length)[0];
			adjusted.get(x)[1] = NetworkProtocol.shortToBytes((short)parameters[x].length)[1];
			for(int y = 0;y<parameters[x].length;y++)
			{
				adjusted.get(x)[y+2] = parameters[x][y];
			}
		}
		output = new byte[adjusted.size()][];
		for(int i = 0;i<adjusted.size();i++)
		{
			output[i] = adjusted.get(i);
		}
		return output;
	}
	public byte getCommandType()
	{
		return networkEventType;
	}
	public byte getCommandSpecific()
	{
		return networkEventSpecific;
	}
	public abstract byte[] toBytes();
	public byte[] getParameter(int i)
	{
		return data[i];
	}
	public int getParameterCount()
	{
		return data.length;
	}
}
