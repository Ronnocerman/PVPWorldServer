package pvpworldserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Driver 
{
	public static void main(String[]args)
	{
		ServerSocketChannel serverChannel = null;
		SocketChannel clientChannel = null;
		ServerSocket ss = null;
		try 
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			ss = serverChannel.socket();
			ss.bind(new InetSocketAddress(5472));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		while(true)
		{
			ByteBuffer b = ByteBuffer.allocate(65536);
			b.clear();
			if(clientChannel!=null)
				try {
					if(clientChannel.read(b) != -1)
					{
						for(int i = 0; i<b.array().length;i++)
						{
							if(b.array()[i]!=0)
							System.out.println(b.array()[i]);
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(clientChannel==null)
			try 
			{
				clientChannel = serverChannel.accept();
				if(clientChannel != null)
				{
					System.out.println("Client Accepted");
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
