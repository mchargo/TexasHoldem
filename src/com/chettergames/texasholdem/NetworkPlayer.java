package com.chettergames.texasholdem;

public class NetworkPlayer extends Player
{
	public NetworkPlayer(int number) 
	{
		super(number);
	}

	@Override
	public int getBet(int currentBet) {
		return 0;
	}

	@Override
	public void promptForName() {
	}

	@Override
	public boolean isReady() {
		return false;
	}
	
	
	// From Server to Client
	public static final int REQUEST_NAME			= 1;
	public static final int REQUEST_READY			= 2;
	public static final int REQUEST_ANTE			= 3;
	public static final int REQUEST_BET				= 4;
	public static final int NEW_BET_ROUND			= 5;
	public static final int STARTING_CHIPS			= 6;
	public static final int RECEIVE_CURRENT_CHIPS	= 7;
	public static final int RECEIVE_HAND			= 8;
	public static final int RECEIVE_FLOP			= 9;
	public static final int RECEIVE_TURN			= 10;
	public static final int RECEIVE_RIVER			= 11;
	public static final int CURRENT_BET				= 12;
	public static final int YOU_WON_POT				= 13;
	public static final int YOU_LOST_POT			= 14;
	public static final int REQUEST_PLAY_AGAIN		= 15;
	
	// From Client to Server
	public static final int RECIEVE_NAME			= 1;
	public static final int RECIEVE_READY			= 2;
	public static final int RECEIVE_ANTE			= 3;
	public static final int RECIEVE_BET				= 4;
	public static final int RECIEVE_PLAY_AGAIN		= 5;
	public static final int RECIEVE_DONT_PLAY_AGAIN = 6;
}
