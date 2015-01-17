package com.chettergames.texasholdem;

import java.util.Arrays;

import com.chettergames.net.Output;

public class Game 
{
	public Game(int playerCount, int startingChips, int startingAnte)
	{
		players = new Player[playerCount];
		Arrays.fill(players, null);
		this.startChips = startingChips;
		this.startingAnte = startingAnte;
	}

	public int playerCount()
	{
		int count = 0;

		for(int index = 0; index<players.length; index++)
		{
			if(players[index]!=null)
			{
				count++;
			}
		}
		return count;
	}

	public synchronized boolean addPlayer(Player p)
	{
		int index;
		for(index = 0; index<players.length; index++)
		{
			if(players[index]==null)
			{
				players[index] = p;
				p.promptForName();
				return true;
			}

		}
		return false;
	}

	public synchronized void removePlayer(Player p)
	{
		/**
		 * Remove the given player from the
		 * array. In order to remove a player
		 * from the array, search for where
		 * the player is in the array, then
		 * set that position to null.
		 * 
		 * For example, to test position 3:
		 * 
		 * boolean equal = players[3] == p;
		 * 
		 * if(equal)
		 * {
		 *     ... 
		 * }
		 */


	}

	public void prepareForNewRound()
	{
		for (int index=0; index<players.length; index++)
		{
			if(players[index]!=null)
			{
				if(players[index].isPlaying())
				{
					players[index].newRound();
				}
			}
		}
		pot = 0;
		tableCards= new Card[5];
	}
	public void getAntes(int amount)
	{

		for(int index = 0; index<players.length; index++)
		{
			if(players[index]!=null)
			{
				if(!players[index].isFolded())
				{
					int ante = players[index].ante(amount);
					pot+=ante;
				}
			}
		}
	}

	public void dealStart(Deck deck)
	{
		for(int dealtCard =0; dealtCard<2; dealtCard++)
		{
			for(int index =0; index<players.length; index++)
			{
				if(players[index]!=null)
				{
					if(!players[index].isFolded())
					{
						players[index].recieveCard(deck.drawCard());
					}
				}
			}
		}
	}

	public void playRound()
	{
		Player lastRaise = null;
		int currentCall=0;
		for(int index =0; ;index++)
		{
			if(index==players.length)
			{
				index=0;
			}
			if(players[index]!=null)
			{
				if(!players[index].isFolded())
				{
					if(lastRaise==null)
					{
						lastRaise=players[index];
					}else if(lastRaise==players[index]){
						break;
					}
					int roundBet=players[index].getRoundBet();
					int bet = players[index].getBet(currentCall);
					int playerTotalBet= bet+roundBet;
					pot+=bet;
					if(playerTotalBet>currentCall)
					{
						lastRaise=players[index];
						currentCall=playerTotalBet;
					}

				}
			}
		}
	}
	public void dealFlop(Deck deck)
	{
		deck.drawCard();
		tableCards[0]=deck.drawCard();
		tableCards[1]=deck.drawCard();
		tableCards[2]=deck.drawCard();
	}

	public void dealTurn(Deck deck)
	{
		deck.drawCard();
		tableCards[3]=deck.drawCard();

	}

	public void dealRiver(Deck deck)
	{
		deck.drawCard();
		tableCards[4]=deck.drawCard();
	}


	public void checkForHighestHand()
	{
		
	}
	
	public boolean checkForPair(Card cards[])
	{
		int card1=cards[0].getValue();
		int card2=cards[1].getValue();
		int card3=cards[2].getValue();
		int card4=cards[3].getValue();
		int card5=cards[4].getValue();
		int card6=cards[5].getValue();
		int card7=cards[6].getValue();
		int cardValues[]= new int[13];
		
		//if()
		return false;
	}
	
	public boolean checkForTwoPair()
	{
		return false;
	}
	
	public boolean checkForThreeOfAKind()
	{
		return false;
	}
	
	public boolean checkForStraight()
	{
		return false;
	}
	
	public boolean checkForFlush()
	{
		return false;
	}
	
	public boolean checkForFullHouse()
	{
		return false;
	}
	
	public boolean checkForFourOfAKind()
	{
		return false;
	}
	
	public boolean checkForStraightFlush()
	{
		return false;
	}
	
	public boolean checkForRoyalFlush()
	{
		return false;
	}
	

	/**
	 * Play the game on a new Thread.
	 */
	public void postPlayGame()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				playGame();
			}
		}).start();
	}

	/**
	 * Game play loop.
	 */
	public void playGame()
	{	
		while(true)
		{
			if(playerCount() < 2)
			{
				try
				{
					Output.gameln("We must wait for more players to be ready...");
					Thread.sleep(5000);
					continue;
				}catch(Exception e){e.printStackTrace();}
			}

			System.out.println(players[0].isReady() + "  " + players[0].isPlaying());
			
			// check for people who need to join the game.
			for(Player p : players)
				if(p != null)
					if(p.isReady() && !p.isPlaying())
						p.joinGame(startChips);


			Deck deck = new Deck();

			prepareForNewRound(); // prepare for new round
			getAntes(startingAnte); // get ante from players
			dealStart(deck); // deal cards to players
			playRound(); // play a round of betting
			dealFlop(deck); // deal the flop
			playRound(); // play a round of betting
			dealTurn(deck); // deal the turn
			playRound(); // play a round of betting
			dealRiver(deck); // deal the river
			playRound(); // play the last round of betting

			checkForHighestHand(); // check for winner
		}
	}

	public void newTestGame()
	{
		for(int x = 0;x < 6;x++)
			addPlayer(new ComputerPlayer(x));
	}

	private Card[] tableCards;
	private Player[] players;
	private int pot;
	private int startChips;
	private int startingAnte;

	public static void main(String args[])
	{
		Game game = new Game(6, 1000, 10);
		game.newTestGame();
		game.postPlayGame();
	}
}
