package com.chettergames.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class NetworkManager 
{
	/**
	 * Use NetworkManager to connect to a server.
	 * @param address The address to connect to.
	 * @param port The port to connect to.
	 */
	public NetworkManager(String address, int port)
	{
		this.address = address;
		this.port = port;
		connected = false;
		blocking = false;
	}
	
	/**
	 * Create a NetworkManager with an already
	 * existing socket
	 * @param socket The socket to manage.
	 */
	public NetworkManager(Socket socket)
	{
		this.socket = socket;
		connected = false;
		blocking = false;
	}

	public void setNetworkListener(NetworkListener listener)
	{
		this.listener = listener;
	}

	public void connect()
	{
		try
		{
			if(socket == null)
			{
				Output.netVer("Connecting to host...\t\t");
				socket = new Socket(address, port);
				Output.netok();
			}

			Output.netVer("Initilizing Streams...\t\t");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			Output.netok();
			connected = true;

			listen();
		}catch (IOException e)
		{
			Output.netfail();
			Output.netln("Connection Failed");
			Output.neterr(e);
			connected = false;
		}
	}

	private void listen()
	{
		Output.net("Starting listen thread...\t\t");
		listenThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(connected)
				{
					try
					{
						Output.netln("Waiting for message...");
						int length = in.readInt();

						Output.netln("Message received!!");
						Output.netln("Message length: " + length);

						byte[] buffer = new byte[length];
						int bytesRead = 0;

						Output.net("Reading message...\t\t");

						while(bytesRead != length)
						{
							bytesRead += in.read(buffer, bytesRead, length);

							if(length == bytesRead) break;
							Thread.sleep(100);
						}

						Output.netok();

						if(Output.NET_DEBUG_VER)
						{
							Output.netVerrln("Packet bytes: ");
							for(int x = 0;x < buffer.length;x++)
								Output.netVerrln(x + "  :  " + buffer[x] +  "  :  " + (char)buffer[x]);
						}

						if(blocking)
						{
							Output.netln("Blocker got message");
							blockData = buffer;
							latch.countDown();
							
							while(blockData != null)
							{
								Thread.sleep(100);
								Output.netln("Waiting for block to end...");
							}
						}else{
							if(listener != null)
							{
								Output.netln("NetworkListener got message");
								postMessageToListener(buffer);
							} else {
								Output.netln("WARNING: Message fell through");
							}
						}
					}catch(Exception e)
					{
						Output.netln("Connection Lost");
						Output.neterr(e);
						connected = false;
					}
				}
			}
		});

		listenThread.start();
		Output.netok();
	}

	private void postMessageToListener(final byte[] message)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				listener.messageReceived(message);
			}
		}).start();
	}

	/** 
	 * Send a message across the stream.
	 * @param buffer The buffer to send.
	 * @return Whether or not the message was sent.
	 */
	public boolean sendData(byte[] buffer)
	{
		Output.net("Buffer length: " + buffer.length);
		Output.net("Sending buffer...\t\t\t");
		if(!connected) 
		{
			Output.netfail();
			Output.netln("Not connected to host.");
			return false;
		}

		try
		{
			if(Output.NET_DEBUG_VER)
			{
				Output.netVerrln("Packet bytes: ");
				for(int x = 0;x < buffer.length;x++)
					Output.netVerrln(x + "  :  " + buffer[x] +  "  :  " + (char)buffer[x]);
			}

			out.writeInt(buffer.length);
			out.write(buffer);
			out.flush();
			Output.netok();
		}catch(IOException e)
		{
			Output.netfail();
			Output.netln("Broken Connection");
			e.printStackTrace();
			connected = false;

			return false;
		}

		return true;
	}

	public void messageNotUsed(byte[] message)
	{
		if(listener != null)
			postMessageToListener(message);
	}

	public byte[] blockForFlags(byte[] flags)
	{
		while(true)
		{
			byte[] buffer = blockForMessage();

			for(byte b : flags)
			{
				if(buffer[0] == b)
				{
					System.out.println("We were looking for " + buffer[0] + ", we found " + b);
					return buffer;
				}
			}

			System.out.println("Passed to listener.");
			messageNotUsed(buffer);
		}
	}

	private synchronized byte[] blockForMessage()
	{
		while(blocking) 
		{
			try
			{
				Thread.sleep(100);
				Output.netln("ALREADY BLOCKING!!");
			}catch(Exception e){}
		}

		blocking = true;

		Output.net("Blocking for message...\t\t");

		try
		{
			latch = new CountDownLatch(1);
			latch.await();
			Output.netok();
		}catch(Exception e)
		{
			Output.netfail();
			Output.neterr(e);
		}

		
		byte[] data = blockData;
		blockData = null;
		blocking = false;

		return data;
	}

	private NetworkListener listener;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;

	private String address;
	private int port;

	private Thread listenThread;
	private boolean connected;

	private CountDownLatch latch;
	private boolean blocking;
	private byte[] blockData;
}
