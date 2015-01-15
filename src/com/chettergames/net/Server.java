package com.chettergames.net;

import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
	public Server(int port)
	{
		this.port = port;
		running = false;
	}
	
	public void setServerListener(ServerListener listener)
	{
		this.listener = listener;
		running = false;
	}
	
	public void stop()
	{
		Output.net("Stopping server...\t\t");
		running = false;
		
		try{thread.interrupt();}catch(Exception e){}
		thread = null;
		Output.netok();
	}
	
	public void start()
	{
		Output.net("Starting server...\t\t");
		try
		{
			server = new ServerSocket(port);
			running = true;
			listen();
			Output.netok();
		}catch(Exception e)
		{
			Output.netfail();
			running = false;
			Output.netln("Server port already in use.");
		}
	}
	
	private void listen()
	{
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				while(running)
				{
					Output.netln("Waiting for client...");
					try
					{
						Socket client = server.accept();
						Output.netln("Client accepted");
						if(listener != null)
							listener.clientAccepted(client);
					}catch(Exception e)
					{
						Output.neterr(e);
					}
				}
			}
		});
		thread.start();
	}
	
	private Thread thread;
	private boolean running;
	private int port;
	
	private ServerListener listener;
	private ServerSocket server;
}
