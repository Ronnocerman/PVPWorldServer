package pvpworldserver;

import java.nio.ByteBuffer;

public class GameUpdate 
{
	public static void updatePhysics(Command c, PlayerConnection pc)
	{
		//Byte(0-1) Command
		//Byte(2-3) Command Length
		//Byte(4-7) Character ID
		//Byte(8-11) X Position Int
		//Byte(12-15) Y Position Int
		//Byte(16-19) X Velocity * 1000
		//Byte(20-23) Y Velocity * 1000
		//Byte(24-27) X Acceleration * 1000
		//Byte(28-31) Y Acceleration * 1000
		int xPosition;
		int yPosition;
		int xVelocity;
		int yVelocity;
		int xAcceleration;
		int yAcceleration;
		byte[] parseArray = c.getCommandBody();
		ByteBuffer b = ByteBuffer.allocate(4);
		b.put(parseArray[4]);
		b.put(parseArray[5]);
		b.put(parseArray[6]);
		b.put(parseArray[7]);
		ServerDriver.validatePlayerForConnection(b.getInt(), pc);
		b = ByteBuffer.allocate(4);
		b.put(parseArray[8]);
		b.put(parseArray[9]);
		b.put(parseArray[10]);
		b.put(parseArray[11]);
		xPosition = b.getInt();
		b = ByteBuffer.allocate(4);
		b.put(parseArray[12]);
		b.put(parseArray[13]);
		b.put(parseArray[14]);
		b.put(parseArray[15]);
		yPosition = b.getInt();
		b = ByteBuffer.allocate(4);
		b.put(parseArray[16]);
		b.put(parseArray[17]);
		b.put(parseArray[18]);
		b.put(parseArray[19]);
		xVelocity = b.getInt();
		b = ByteBuffer.allocate(4);
		b.put(parseArray[20]);
		b.put(parseArray[21]);
		b.put(parseArray[22]);
		b.put(parseArray[23]);
		yVelocity = b.getInt();
		b = ByteBuffer.allocate(4);
		b.put(parseArray[24]);
		b.put(parseArray[25]);
		b.put(parseArray[26]);
		b.put(parseArray[27]);
		xAcceleration = b.getInt();
		b = ByteBuffer.allocate(4);
		b.put(parseArray[28]);
		b.put(parseArray[29]);
		b.put(parseArray[30]);
		b.put(parseArray[31]);
		yAcceleration = b.getInt();
	}
}
