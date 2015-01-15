    package com.chettergames.texasholdem;

public abstract class Player 
{
	public Player(int number)
	{
		this.number = number;
		folded = true;
	}

	public int ante(int ante)
	{
		if(chips == 0)
		{
			folded = true;
			ante = 0;
		} else if(chips >= ante)
		{
			folded = false;
			chips -= ante;
		}else {
			folded = false;
			ante = chips;
			chips = 0;
		}

		return ante;
	}

	public void wonPot(int chipsWon)
	{
		chips += chipsWon;
	}

	public void newBettingRound()
	{
		myRoundBet = 0;
	}

	public void getCards(Card card1, Card card2)
	{
		this.card1 = card1;
		this.card2 = card2;
	}

	public void joinGame(int startChips)
	{
		this.chips = startChips;
	}

	// -1 = fold
	public abstract int getBet(int currentBet);
	public abstract void promptForName();
	public abstract boolean isReady();
	
	public Card getCard1(){return card1;}
	public Card getCard2(){return card2;}
	public int getRoundBet(){return myRoundBet;}
	public boolean isFolded(){return folded;}
	public String getName(){return name;}
	public int getNumber(){return number;}

	private int chips;
	private String name;
	private int number;
	private Card card1;
	private Card card2;
	private int myRoundBet;
	private boolean folded;
}
