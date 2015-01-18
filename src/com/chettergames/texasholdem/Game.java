package com.chettergames.texasholdem;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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


	public Player findWinner()
	{	
		int hands[] = new int[players.length];
		Arrays.fill(hands, -1);
		
		for(int player = 0;player < players.length;player++)
		{
			Card hand[] = new Card[tableCards.length + 2];
			for(int x = 0;x < tableCards.length;x++)
				hand[x] = tableCards[x];
			hand[tableCards.length] = players[player].getCard1();
			hand[tableCards.length + 1] = players[player].getCard2();
			
			if(checkForRoyalFlush(hand))
				hands[player] = ROYAL_FLUSH;
			else if(checkForStraightFlush(hand) != -1)
				hands[player] = STRAIGHT_FLUSH;
			
			// continue here
		}

		return players[0];
	}
	
	public int checkForHighCard(Card cards[])
	{
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;
		for(int x = vals.length -1; x >= 0; x--)
			if(vals[x] > 0)
				return (x+2);
			
		return -1;
	}

	public int checkForPair(Card cards[])
	{
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;

		for(int x = vals.length - 1;x >= 0;x--)
			if(vals[x] > 2)
				return (x + 2);
		return -1;
	}

	public int checkForTwoPair(Card cards[])
	{
		int pairs = 0;
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;

		for(int x = 0;x <= vals.length-1;x++)
		{
			if(vals[x] > 1)
				pairs++;
			if(pairs>=2)
				return (x + 2);
		}

		return -1;
	}

	/**
	 * Return the highest value 3 of a kind in your hand.
	 * -1 for no three of a kind
	 * 
	 * @param cards
	 * @return The value of the highest 3 of a kind.
	 */
	public int checkForThreeOfAKind(Card cards[])
	{
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;

		for(int x = vals.length - 1;x >= 0;x--)
			if(vals[x] > 2)
				return (x + 2);

		return -1;
	}

	/**
	 * Check cards for a straight
	 * @param cards The cards to check
	 * @return The value of the highest card, -1 if no straight
	 */
	public int checkForStraight(Card cards[])
	{
		LinkedList<Integer> vals = new LinkedList<Integer>();
		for(Card c : cards)
			vals.add(c.getValue());
		Collections.sort(vals);

		// remove doubles
		for(int x = 0;x < vals.size() - 1;x++)
		{
			if(vals.get(x) == vals.get(x + 1))
			{
				vals.remove(x);
				x--;
			}
		}

		if(vals.size() < 5) return -1;
		int longestStreak = 0;
		int highestCard = 0;

		int currStreak = 0;
		int lastVal = 0;

		// ace can also be 1
		if(vals.getLast() == Card.VAL_ACE)
		{
			lastVal = 1;
			currStreak = 1;
		}

		Iterator<Integer> it = vals.iterator();
		while(it.hasNext())
		{
			int val = it.next();
			if(currStreak == 0)
			{
				currStreak++;
				lastVal = val;
			} else {
				if(val == lastVal + 1)
				{
					currStreak++;
					lastVal = val;

					if(currStreak > 4)
					{
						highestCard = val;
						longestStreak = currStreak;
					}
				} else {
					currStreak = 1;
					lastVal = val;
				}
			}
		}

		if(longestStreak > 4)
			return highestCard;
		return -1;
	}

	public int checkForFlush(Card cards[])
	{
		int suits[] = new int[4];
		for(Card c : cards)
			suits[Card.typeToVal(c.getType())]++;
		for(int x = 0;x < suits.length;x++)
			if(suits[x] > 4) return x; 
		return -1;
	}

	public int checkForFullHouse(Card cards[])
	{
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;

		boolean three = false;
		boolean pair = false;
		int val3 = 0;

		for(int x = vals.length - 1;x >= 0;x--)
			if(vals[x] > 2 && !three)
			{
				three = true;
				val3 = x + 2;
			} else {
				if(vals[x] > 1)
					pair = true;
			}

		if(three && pair)
			return val3;

		return -1;
	}

	public int checkForFourOfAKind(Card cards[])
	{
		int vals[] = new int[Card.CARDS_IN_SUIT];
		for(Card c : cards)
			vals[c.getValue() - 2]++;

		for(int x = vals.length - 1;x >= 0;x--)
			if(vals[x] > 3)
				return (x + 2);
		return -1;
	}

	public int checkForStraightFlush(Card cards[])
	{
		int suit = checkForFlush(cards);
		if(suit == -1) return -1;
		LinkedList<Card> remainingCards = new LinkedList<Card>();
		for(Card c : cards)
			if(Card.typeToVal(c.getType()) == suit)
				remainingCards.add(c);
		
		cards = new Card[remainingCards.size()];
		for(int x = 0;x < remainingCards.size();x++)
			cards[x] = remainingCards.get(x);
		
		return checkForStraight(cards);
	}

	public boolean checkForRoyalFlush(Card cards[])
	{
		if(checkForStraightFlush(cards)==14)
		{
			return true;
		}
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
			Player winner = findWinner(); // check for winner
			Output.gameln(winner.getName() + " is the winner!");
			winner.wonPot(pot);

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

	public static final int NO_HAND 		= -1;
	public static final int HIGH_CARD 		= 0;
	public static final int PAIR 			= 1;
	public static final int TWO_PAIR		= 2;
	public static final int THREE_OF_A_KIND = 3;
	public static final int STRAIGHT		= 4;
	public static final int FLUSH			= 5;
	public static final int FULL_HOUSE		= 6;
	public static final int FOUR_OF_A_KIND	= 7;
	public static final int STRAIGHT_FLUSH	= 8;
	public static final int ROYAL_FLUSH		= 9;

	public static void main(String args[])
	{
		Game game = new Game(6, 1000, 10);
		
		Card card1 = new Card(Card.VAL_2, Card.Type.SPADES);
		Card card2 = new Card(Card.VAL_JACK, Card.Type.HEARTS);
		Card card3 = new Card(Card.VAL_QUEEN, Card.Type.HEARTS);
		Card card4 = new Card(Card.VAL_KING, Card.Type.HEARTS);
		Card card5 = new Card(Card.VAL_8, Card.Type.CLUBS);
		Card card6 = new Card(Card.VAL_9, Card.Type.HEARTS);
		Card card7 = new Card(Card.VAL_3, Card.Type.CLUBS);
		
		Card cards[] = new Card[]{card1, card2, card3, card4, card5, card6, card7};
		int result = game.checkForHighCard(cards);
		if(result >= 0)
			System.out.println("highest card: " + result);
		else System.out.println("No hand.");
		System.exit(1);
		game.newTestGame();
		game.postPlayGame();
	}
}
