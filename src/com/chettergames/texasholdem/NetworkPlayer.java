package com.chettergames.texasholdem;

import com.chettergames.net.BufferBuilder;
import com.chettergames.net.NetworkListener;
import com.chettergames.net.NetworkManager;
import com.chettergames.net.Output;

/**
 * Whenever the chip count for the player changes,
 * we will send the amount that it changed by and
 * then we will send the total amount.
 * @author John Detter <jdetter@wisc.edu>
 */

public class NetworkPlayer extends Player implements NetworkListener
{
	public NetworkPlayer(int number, NetworkManager network) 
	{
		super(number);
		this.network = network;
		readyToPlay = false;
	}

	@Override
	public void messageReceived(BufferBuilder buffer) 
	{
		switch(buffer.pullFlag())
		{
		case RECEIVE_NAME:
			name = buffer.pullString();
			network.sendFlag(REQUEST_READY);
			break;
		case RECEIVE_READY:
			if(readyToPlay)
				Output.gameln("Network player " + number + " has readied more than once.");
			readyToPlay = true;
			break;
		}
	}

	public void newRound()
	{
		super.newRound();
		network.sendFlag(RECEIVE_NEW_ROUND);
	}

	public int ante(int ante)
	{
		ante = super.ante(ante);

		// tell client how much they anted.
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4);
		buffer.pushFlag(RECEIVE_ANTE);
		buffer.pushInt(ante);
		buffer.pushInt(chips);
		network.sendBuffer(buffer);

		return ante;
	}

	public void wonPot(int chipsWon)
	{
		super.wonPot(chipsWon);

		// tell client how much they won.
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4);
		buffer.pushFlag(RECEIVE_POT_WON);
		buffer.pushInt(chipsWon);
		buffer.pushInt(chips);
		network.sendBuffer(buffer);
	}

	public void newBettingRound()
	{
		super.newBettingRound();
		network.sendFlag(RECEIVE_NEW_BET_ROUND);
	}

	public void recieveCard(Card card)
	{
		super.recieveCard(card);

		if(card1 != null && card2 != null)
		{
			BufferBuilder buffer = new BufferBuilder(1 + card1.calculateSize() + card2.calculateSize());
			buffer.pushFlag(RECEIVE_HAND);
			card1.pushToBuffer(buffer);
			card2.pushToBuffer(buffer);
			network.sendBuffer(buffer);
		}
	}

	public void joinGame(int startChips)
	{
		super.joinGame(startChips);

		// tell the player that they are now playing poker.
		BufferBuilder buffer = new BufferBuilder(1 + 4);
		buffer.pushFlag(RECEIVE_NOW_PLAYING);
		buffer.pushInt(startChips);
		network.sendBuffer(buffer);
	}

	@Override
	public int getBet(int currentBet) 
	{
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4 + 4);
		buffer.pushFlag(REQUEST_BET);
		buffer.pushInt(currentBet);
		buffer.pushInt(myRoundBet);
		buffer.pushInt(chips);
		network.sendBuffer(buffer);

		while(true)
		{
			buffer = network.blockForBuffer(new byte[]{RECEIVE_BET});
			buffer.pullFlag();
			int bet = buffer.pullInt();

			if(bet > chips)
			{
				network.sendFlag(RECEIVE_BET_BAD);
				continue;
			} else if(bet + myRoundBet < currentBet)
			{
				// you have not put in enough chips!
				continue;
			}

			if(bet < 0)
			{
				// you folded.
				bet = -1;
			} else if(bet == chips)
			{
				// you're all in
			} else if(bet + myRoundBet == currentBet)
			{
				// you called or checked.
			} else if(bet + myRoundBet > currentBet)
			{
				// you are raising
			} 

			if(bet > 0)
				chips -= bet;

			// update chip count
			buffer = new BufferBuilder(1 + 4);
			buffer.pushFlag(RECEIVE_BET_GOOD);
			buffer.pushInt(chips);
			network.sendBuffer(buffer);

			return bet;
		}
	}

	public void playerJoined(Player player)
	{
		super.playerJoined(player);
		String name = player.getName();
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4 + name.length());
		buffer.pushFlag(RECEIVE_NEW_PLAYER_JOIN);
		buffer.pushInt(player.getNumber());
		buffer.pushString(name);
		network.sendBuffer(buffer);
	}

	@Override
	public void promptForName() 
	{
		BufferBuilder buffer = new BufferBuilder(1 + 4);
		buffer.pushFlag(REQUEST_NAME);
		buffer.pushInt(number);
		network.sendBuffer(buffer);
	}

	@Override
	public boolean isReady() {
		return readyToPlay;
	}
	
	public void playerAnted(int amount, Player p)
	{
		if(p.getNumber() == number) return;
		super.playerAnted(amount, p);
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4);
		buffer.pushFlag(RECEIVE_PLAYER_ANTE);
		buffer.pushInt(p.getNumber());
		buffer.pushInt(p.getChips());
		network.sendBuffer(buffer);
	}
	
	public void playerFolded(Player p)
	{
		if(p.getNumber() == number) return;
		super.playerFolded(p);
		BufferBuilder buffer = new BufferBuilder(1 + 4);
		buffer.pushFlag(RECEIVE_PLAYER_FOLD);
		buffer.pushInt(p.getNumber());
		network.sendBuffer(buffer);
	}
	
	public void playerCheck(Player p)
	{
		if(p.getNumber() == number) return;
		super.playerCheck(p);
		BufferBuilder buffer = new BufferBuilder(1 + 4);
		buffer.pushFlag(RECEIVE_PLAYER_CHECK);
		buffer.pushInt(p.getNumber());
		network.sendBuffer(buffer);
	}
	
	public void playerCalled(int tableBet, Player p)
	{
		if(p.getNumber() == number) return;
		super.playerCalled(tableBet, p);
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4 + 4);
		buffer.pushFlag(RECEIVE_PLAYER_CALLED);
		buffer.pushInt(p.getNumber());
		buffer.pushInt(p.getChips());
		buffer.pushInt(tableBet);
		network.sendBuffer(buffer);
	}
	
	public void playerRaised(int tableBet, Player p)
	{
		if(p.getNumber() == number) return;
		super.playerRaised(tableBet, p);
		BufferBuilder buffer = new BufferBuilder(1 + 4 + 4 + 4);
		buffer.pushFlag(RECEIVE_PLAYER_RAISED);
		buffer.pushInt(p.getNumber());
		buffer.pushInt(p.getChips());
		buffer.pushInt(tableBet);
		network.sendBuffer(buffer);
	}
	
	public void flopDealt(Card cards[])
	{
		super.flopDealt(cards);
		Card card1 = cards[0];
		Card card2 = cards[1];
		Card card3 = cards[2];
		
		BufferBuilder buffer = new BufferBuilder(1
				+ card1.calculateSize()
				+ card2.calculateSize()
				+ card3.calculateSize());
		buffer.pushFlag(RECEIVE_FLOP);
		card1.pushToBuffer(buffer);
		card2.pushToBuffer(buffer);
		card3.pushToBuffer(buffer);
		network.sendBuffer(buffer);
	}
	
	public void turnDealt(Card card)
	{
		super.turnDealt(card);
		BufferBuilder buffer = new BufferBuilder(1
				+ card.calculateSize());
		buffer.pushFlag(RECEIVE_TURN);
		card.pushToBuffer(buffer);
		network.sendBuffer(buffer);
	}
	public void riverDealt(Card card)
	{
		super.riverDealt(card);
		BufferBuilder buffer = new BufferBuilder(1
				+ card.calculateSize());
		buffer.pushFlag(RECEIVE_RIVER);
		card.pushToBuffer(buffer);
		network.sendBuffer(buffer);
	}
	
	private NetworkManager network;
	private boolean readyToPlay;

	// From Server to Client
	public static final byte REQUEST_NAME			 = 1;
	public static final byte REQUEST_READY			 = 2;
	public static final byte RECEIVE_NEW_ROUND		 = 3;
	public static final byte RECEIVE_ANTE			 = 4;
	public static final byte RECEIVE_POT_WON		 = 5;
	public static final byte RECEIVE_NEW_BET_ROUND	 = 6;
	public static final byte RECEIVE_HAND			 = 7;
	public static final byte RECEIVE_NOW_PLAYING	 = 8;
	public static final byte REQUEST_BET			 = 9;
	public static final byte RECEIVE_BET_GOOD		 = 10;
	public static final byte RECEIVE_BET_BAD		 = 11;
	public static final byte RECEIVE_NEW_PLAYER_JOIN = 12;
	
	public static final byte RECEIVE_PLAYER_ANTE	 = 13;
	public static final byte RECEIVE_PLAYER_FOLD	 = 14;
	public static final byte RECEIVE_PLAYER_CHECK	 = 15;
	public static final byte RECEIVE_PLAYER_CALLED	 = 16;
	public static final byte RECEIVE_PLAYER_RAISED	 = 17;
	public static final byte RECEIVE_FLOP 			 = 18;
	public static final byte RECEIVE_TURN 			 = 19;
	public static final byte RECEIVE_RIVER 			 = 20;


	// From Client to Server
	public static final byte RECEIVE_NAME			 = 1;
	public static final byte RECEIVE_READY			 = 2;
	public static final byte RECEIVE_BET			 = 3;
}
