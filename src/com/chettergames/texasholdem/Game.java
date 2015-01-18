package com.chettergames.texasholdem;

import java.util.Arrays;
import java.util.LinkedList;

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

		for(Player p : players)
			if(p != null) 
				if(p.isReady()) count++;

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
		for(int x = 0;x < players.length;x++)
			if(players[x] == p)
				players[x] = null;
	}

	public void prepareForNewRound()
	{
		for (int index=0; index<players.length; index++)
			if(players[index]!=null)
				if(players[index].isPlaying())
					players[index].newRound();

		pot = 0;
		tableCards= new Card[5];
	}
	public void getAntes(int amount)
	{
		for(int index = 0; index<players.length; index++)
			if(players[index]!=null)
				if(!players[index].isFolded())
				{
					int ante = players[index].ante(amount);
					pot += ante;

					for(Player p : players)
						if(p != null)p.playerAnted(ante, p);
				}
	}

	public void dealStart(Deck deck)
	{
		for(int dealtCard = 0;dealtCard < 2;dealtCard++)
			for(int index = 0;index < players.length;index++)
				if(players[index] != null)
					if(!players[index].isFolded())
						players[index].recieveCard(deck.drawCard());
	}

	public void playRound()
	{
		Player lastRaise = null;
		int currentCall=0;
		for(int index = 0; ;index++)
		{
			index %= players.length;

			if(players[index] != null)
				if(!players[index].isFolded())
				{
					if(lastRaise == null)
					{
						lastRaise = players[index];
					}else if(lastRaise == players[index]){
						break;
					}

					int roundBet = players[index].getRoundBet();
					int bet = players[index].getBet(currentCall);

					// they folded.
					if(bet < 0) 
					{
						// let the other players know a player folded.
						for(Player p : players)
							if(p != null)
								p.playerFolded(players[index]);
						continue;
					}

					int playerTotalBet = bet + roundBet;
					pot += bet;

					if(playerTotalBet > currentCall)
					{
						lastRaise=players[index];
						currentCall=playerTotalBet;

						// let the other players know a player
						// has raised
						for(Player p : players)
							if(p != null)
								p.playerRaised(bet, players[index]);
					}else{
						// player has called.
						for(Player p : players)
							if(p != null)
								p.playerCalled(players[index]);
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

		// notify players that the flop was dealt

		for(Player p : players)
			if(p != null)
				p.flopDealt(tableCards);
	}

	public void dealTurn(Deck deck)
	{
		deck.drawCard();
		tableCards[3]=deck.drawCard();

		// notify players that the turn was dealt
		for(Player p : players)
			if(p != null)
				p.turnDealt(tableCards);
	}

	public void dealRiver(Deck deck)
	{
		deck.drawCard();
		tableCards[4]=deck.drawCard();

		// notify players that the river was dealt
		for(Player p : players)
			if(p != null)
				p.riverDealt(tableCards);
	}


	public Player[] findWinners()
	{	
		Hand[] hands = new Hand[players.length];
		for(int player = 0;player < players.length;player++)
		{
			if(players[player] != null)
			{
				if(!players[player].isFolded())
				{
					hands[player] = new Hand(players[player].getCard1(),
							players[player].getCard2(),
							tableCards, players[player]);
					hands[player].calculateHand();
				}
			}
		}
		
		LinkedList<Player> winners = new LinkedList<Player>();
		
		int highestType = -1;
		int highestValue = -1;
		
		for(Hand hand : hands)
		{
			if(hand == null) continue;
			int type = hand.getType();
			int val = hand.getValue();
			
			if(type == highestType)
			{
				if(val == highestValue)
					winners.add(hand.getOwner()); // same hand
				else if(val > highestValue)
				{
					// better hand
					winners = new LinkedList<Player>();
					winners.add(hand.getOwner());
					
					highestValue = val;
				} else continue; // you lost
			} else if(type > highestType)
			{
				// better hand
				winners = new LinkedList<Player>();
				winners.add(hand.getOwner());
				
				highestType = type;
				highestValue = val;
			}else continue; // you lost!
		}

		if(winners.size() == 1) return new Player[]{winners.getFirst()};
		
		Player[] result = new Player[winners.size()];
		for(int x = 0;x < result.length;x++)
			result[x] = winners.get(x);
		
		return result;
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

			// check for people who need to join the game.
			for(Player p : players)
				if(p != null)
					if(p.isReady() && !p.isPlaying())
					{
						p.joinGame(startChips);

						// tell all players that p joined the game.
						for(Player play : players)
							if(play != null)
								play.playerJoined(p);
					}


			Deck deck = new Deck();
			Output.gameln("Deck has been shuffled.");

			Output.gameln("Preparing for new round...");
			prepareForNewRound(); // prepare for new round
			Output.gameln("Getting antes...");
			getAntes(startingAnte); // get ante from players
			Output.gameln("Dealing cards...");
			dealStart(deck); // deal cards to players
			Output.gameln("Lets do a round of betting!");
			playRound(); // play a round of betting
			Output.gameln("Dealing the flop:");
			dealFlop(deck); // deal the flop
			for(int x = 0;x < 3;x++)
				Output.gameln((x + 1) + ": " + tableCards[x]);
			Output.gameln("Lets do a round of betting!");
			playRound(); // play a round of betting
			Output.gameln("Dealing the turn:");
			dealTurn(deck); // deal the turn
			Output.gameln(3 + ": " + tableCards[3]);
			Output.gameln("Lets do a round of betting!");
			playRound(); // play a round of betting
			Output.gameln("Dealing the river:");
			dealRiver(deck); // deal the river
			Output.gameln(4 + ": " + tableCards[4]);
			Output.gameln("Lets do the final round of betting!");
			playRound(); // play the last round of betting
			Player winners[] = findWinners(); // check for winner
			//Output.gameln(winner.getName() + " is the winner!");
			//winner.wonPot(pot);

			try{Thread.sleep(10000);}catch(Exception e){}
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

		Card card1 = new Card(Card.VAL_KING, Card.Type.DIAMONDS);
		Card card2 = new Card(Card.VAL_JACK, Card.Type.HEARTS);
		Card card3 = new Card(Card.VAL_QUEEN, Card.Type.HEARTS);
		Card card4 = new Card(Card.VAL_2, Card.Type.HEARTS);
		Card card5 = new Card(Card.VAL_9, Card.Type.CLUBS);
		Card card6 = new Card(Card.VAL_9, Card.Type.HEARTS);
		Card card7 = new Card(Card.VAL_3, Card.Type.CLUBS);

		Card cards[] = new Card[]{card1, card2, card3, card4, card5, card6, card7};
		Hand hand = new Hand(cards[5], cards[6], cards, null);
		hand.calculateHand();
		System.out.println(hand);
		
		System.exit(1);
		game.newTestGame();
		game.postPlayGame();
	}
}
