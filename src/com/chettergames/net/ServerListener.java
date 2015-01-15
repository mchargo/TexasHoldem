package com.chettergames.net;

import java.net.Socket;

public interface ServerListener 
{
	void clientAccepted(Socket socket);
}
