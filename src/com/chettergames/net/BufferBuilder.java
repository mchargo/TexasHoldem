package com.chettergames.net;

public class BufferBuilder 
{
	public BufferBuilder(int size)
	{
		buffer = new byte[size];
		index = 0;
	}
	
	public BufferBuilder(byte[] buffer, int index)
	{
		this.buffer = buffer;
		this.index = index;
	}
	
	public int getIndex(){return index;}
	public byte[] getBuffer(){return buffer;}
	public void setIndex(int index){this.index = index;}
	public void addToIndex(int num){this.index += num;}
	
	public void pushInt(int i)
	{
		byte b1 = (byte)(i>>24);
		byte b2 = (byte)(i>>16);
		byte b3 = (byte)(i>>8);
		byte b4 = (byte)(i);
		buffer[index]= b1;
		buffer[index+1]= b2;
		buffer[index+2]= b3;
		buffer[index+3]= b4;
		index+=4;
	}
	
	public int pullInt()
	{
		int b1 = buffer[index];
		int b2 = buffer[index+1];
		int b3 = buffer[index+2];
		int b4 = buffer[index+3];
		
		if(b1<0) b1+=256;
		if(b2<0) b2+=256;
		if(b3<0) b3+=256;
		if(b4<0) b4+=256;
		
		int i1 = b1<<24;
		int i2 = b2<<16;
		int i3 = b3<<8;
		int i4 = b4;
		index+=4;
		
		return i1 | i2 | i3 | i4;
	}
	
	public void pushFlag(byte flag)
	{
		buffer[index]=flag;
		index++;
	}
	
	public byte pullFlag()
	{
		byte flag=buffer[index];
		index++;
		return flag;
	}
	
	public void pushByteArr(byte[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			buffer[index + x] = arr[x];
		index += arr.length;
	}
	
	public byte[] pullByteArr()
	{
		byte[] arr = new byte[pullInt()];
		
		for(int x = 0;x < arr.length;x++)
			arr[x] = buffer[index + x];
		index += arr.length;
		
		return arr;
	}
	
	public void pushString(String string)
	{
		pushByteArr(string.getBytes());
	}
	
	public String pullString()
	{
		return new String(pullByteArr());
	}
	
	public void pushStringArr(String arr[])
	{
		pushInt(arr.length);
		for(String s : arr) pushString(s);
	}
	
	public String[] pullStringArr()
	{
		String strings[] = new String[pullInt()];
		for(int x = 0;x < strings.length;x++)
			strings[x] = pullString();
		
		return strings;
	}
	
	private byte[] buffer;
	private int index;
}
