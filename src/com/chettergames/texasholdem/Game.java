package com.chettergames.texasholdem;

import java.io.IOException;
import java.util.Arrays;

import com.chettergames.net.Output;

public class Game 
{
	public Game(int playerCount, int startingChips)
	{
		players = new Player[playerCount];
		Arrays.fill(players, null);
		this.startChips = startingChips;
	}

	public int playerCount()
	{
		/**
		 * Implement this first.
		 * 
		 * Count the players in the 'players' array.
		 * 
		 * How do I do this?
		 * 
		 * For each element in the players array,
		 * check if it is equal to null. If it is
		 * not equal to null, there is a player
		 * in that position. If the element is
		 * equal to null, that position is open
		 * for another player to join. The length
		 * of the players array can be found using:
		 * 
		 * int playersLength = players.length;
		 * 
		 * NOTE: only count a player if they are ready
		 * to play.
		 */

		return 0;
	}

	public synchronized boolean addPlayer(Player p)
	{
		/**
		 * Add a player to the game.
		 * 
		 * How do I do this?
		 *
		 * Find a position in the array where
		 * the element is equal to null. If
		 * there are no such positions, then
		 * the game is full and we should
		 * return false. If we are able to
		 * add a player to the array, return
		 * true.
		 * 
		 * make sure you call promptForName()
		 * on each player when they are added.
		 */

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
		/**
		 * Tell each player that a new round
		 * of poker is about to start.
		 * 
		 * How do I do this?
		 * 
		 * For each element in players,
		 * if the element is not equal to null
		 * we know there is a valid player
		 * there. Tell that player to prepare
		 * for a new round by calling:
		 * 
		 * player.newRound();
		 * 
		 * Also you should clear all of the
		 * cards on the table and reset the pot.
		 */


	}

	public void getAntes()
	{
		/**
		 * Get the antes from all of the players
		 * and add the result to the pot.
		 * 
		 * How do I do this?
		 * 
		 * Ask each player for their ante using:
		 * 
		 * int bet = player.getAnte();
		 * 
		 * If the player is folded, do not ask
		 * them for their ante.
		 */
	}

	public void dealStart(Deck deck)
	{
		/**
		 * Deal 2 cards to each player like
		 * you would in poker.
		 * 
		 * How do I do this?
		 * 
		 * In poker, you deal one card to each
		 * player at a time in a circle, so
		 * for each player that is playing
		 * we should deal one card to them.
		 * Once we are done with that, we should
		 * deal them all another card.
		 * 
		 * 
		 */
	}

	public void playRound()
	{
		/**
		 * Do a round of betting in poker.
		 */
	}



	public void dealFlop(Deck deck)
	{
		/**
		 * Put the flop onto the table
		 */
	}

	public void dealTurn(Deck deck)
	{

	}

	public void dealRiver(Deck deck)
	{

	}


	public void checkForHighestHand()
	{
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
				if(p.isReady() && !p.isPlaying())
					p.joinGame(startChips);


			Deck deck = new Deck();

			prepareForNewRound(); // prepare for new round
			getAntes(); // get ante from players
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
		for(int x = 0;x < players.length;x++)
			addPlayer(new ComputerPlayer(x));
	}

	private Card[] tableCards;
	private Player[] players;
	private int pot;
	private int startChips;

	public static void main(String args[])
	{
		Game game = new Game(6, 1000);
		game.newTestGame();
		game.postPlayGame();
	}
}
