package com.chettergames.net;

public class Output 
{
	public static final boolean NET_DEBUG = true;
	public static final boolean NET_DEBUG_VER = true;
	public static final boolean NET_ERR = true;
	
	public static final void netVer(String message)
	{
		if(NET_DEBUG && NET_DEBUG_VER)
			System.out.print(message);
	}
	
	public static final void netVerrln(String message)
	{
		if(NET_DEBUG && NET_DEBUG_VER)
			System.out.println(message);
	}
	
	public static final void net(String message)
	{
		if(NET_DEBUG)
			System.out.print(message);
	}
	
	public static final void netln(String message)
	{
		if(NET_DEBUG)
			System.out.println(message);
	}
	
	public static final void netok()
	{
		if(NET_ERR)
			System.out.println("[ OK ]");
	}	
	
	public static final void netfail()
	{
		if(NET_ERR)
			System.out.println("[FAIL]");
	}
	
	public static final void neterr(Exception e)
	{
		if(NET_ERR)
			e.printStackTrace();
	}
}
