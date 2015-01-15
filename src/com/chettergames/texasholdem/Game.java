package com.chettergames.texasholdem;

import java.util.Arrays;

public class Game 
{
	public Game(int playerCount)
	{
		players = new Player[playerCount];
		Arrays.fill(null, players);
	}
	
	public synchronized void addPlayer(Player p)
	{
		
	}
	
	public synchronized void removePlayer(Player p)
	{
		
	}
	
	public int playerCount()
	{
		return 0;
	}
	
	public void dealStart()
	{
		//deals starting hand to each player
	}
	public void dealFlop()
	{
		
	}
	
	public void dealTurn()
	{
		//deals the Turn after a round of betting
	}
	public void dealRiver()
	{
		//deals the River after a round of betting
	}
	
	public void playRound()
	{
		//each player completes a turn, either checking, betting, raising, or folding
	}
	public void checkForHighestHand()
	{
		//after the final playRound occurs, method checks for winner
	}
	
	private Player[] players;
	
	public static void main(String args[])
	{
		Game game = new Game(6);
	}
}
