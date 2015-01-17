package com.chettergames.texasholdem;

public abstract class Player 
{
	public Player(int number)
	{
		this.number = number;
		folded = true;
		playing = false;
	}

	/**
	 * newRound should be called at the
	 * begging of each new round of
	 * poker. This should be called
	 * before getAnte().
	 */
	public void newRound()
	{
		card1 = null;
		card2 = null;
		myRoundBet = 0;
		folded = false;

		if(chips == 0 && !isReady()) 
			folded = true;
	}

	/**
	 * This is called to get the ante
	 * from the player.
	 * 
	 * @param ante The ante amount
	 * @return How much the player can ante.
	 */
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

	/**
	 * Call this when the player has won
	 * the pot.
	 * @param chipsWon
	 */
	public void wonPot(int chipsWon)
	{
		chips += chipsWon;
	}

	/**
	 * Call this when a new round of
	 * betting has started.
	 */
	public void newBettingRound()
	{
		myRoundBet = 0;
	}

	/**
	 * Call this when the player is supposed
	 * to receive cards after the ante.
	 * 
	 * @param card1 The first card
	 * @param card2 The second card.
	 */
	public void recieveCards(Card card1, Card card2)
	{
		recieveCard(card1);
		recieveCard(card2);
	}

	/**
	 * Get dealt one card.
	 * @param card
	 */
	public void recieveCard(Card card)
	{
		if(card1 == null)
			card1 = card;
		else if(card2 == null)
			card2 = card;
	}

	/**
	 * Call this when the player has connected
	 * and is ready to play the game.
	 * 
	 * @param startChips Chips to start with.
	 */
	public void joinGame(int startChips)
	{
		playing = true;
		this.chips = startChips;
	}

	/**
	 * Get the bet during a round of betting.
	 * @param currentBet
	 * @return The player's bet. -1 if the player folds.
	 */
	public abstract int getBet(int currentBet);

	/**
	 * Tell the player to give us their name.
	 */
	public abstract void promptForName();

	/**
	 * Is the player ready?
	 * @return Whether or not the player is ready.
	 */
	public abstract boolean isReady();

	public Card getCard1(){return card1;}
	public Card getCard2(){return card2;}
	public int getRoundBet(){return myRoundBet;}
	public boolean isFolded(){return folded;}
	public String getName(){return name;}
	public int getNumber(){return number;}
	public boolean isPlaying(){return playing;}

	protected int chips;
	protected String name;
	protected int number;
	protected Card card1;
	protected Card card2;
	protected int myRoundBet;
	protected boolean folded;
	protected boolean playing;
}
