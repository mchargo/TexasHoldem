package com.chettergames.texasholdem;

public class Game {
	
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
	
	public static void main(String args[])
	{
		Deck deck = new Deck();
		deck.printDeckToConsole();
		
		Card card1 = deck.drawCard();
		Card card2 = deck.drawCard();
		Card card3 = deck.drawCard();
		
		System.out.println("==============");
		System.out.println("Card 1: " + card1);
		System.out.println("Card 2: " + card2);
		System.out.println("Card 3: " + card3);
		System.out.println("==============");
		
		deck.printDeckToConsole();
	}
}
