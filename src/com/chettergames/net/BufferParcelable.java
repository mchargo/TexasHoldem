package com.chettergames.net;

public interface BufferParcelable 
{
	void pushToBuffer(BufferBuilder builder);
	int calculateSize();
}
