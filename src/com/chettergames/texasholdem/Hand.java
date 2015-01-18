package com.chettergames.texasholdem;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Hand 
{
	public Hand(Card card1, Card card2, Card tableCards[], Player owner)
	{
		cards = new Card[7];
		for(int x = 0;x < 5;x++)
			cards[x] = tableCards[x];
		cards[tableCards.length] = card1;
		cards[tableCards.length + 1] = card2;
		value = -1;
		type = -1;
		this.owner = owner;
	}
	
	public void calculateHand()
	{
		if(checkForRoyalFlush(cards))
			type = ROYAL_FLUSH;
		else if((value = checkForStraightFlush(cards)) != -1)
			type = STRAIGHT_FLUSH;
		else if((value = checkForFourOfAKind(cards)) != -1)
			type = FOUR_OF_A_KIND;
		else if((value = checkForFullHouse(cards)) != -1)
			type = FULL_HOUSE;
		else if((value = checkForFlush(cards)) != -1)
			type = FLUSH;
		else if((value = checkForStraight(cards)) != -1)
			type = STRAIGHT;
		else if((value = checkForThreeOfAKind(cards)) != -1)
			type = THREE_OF_A_KIND;
		else if((value = checkForTwoPair(cards)) != -1)
			type = TWO_PAIR;
		else if((value = checkForPair(cards)) != -1)
			type = PAIR;
		else if((value = checkForHighCard(cards)) != -1)
			type = HIGH_CARD;
		else type = NO_HAND;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getValue()
	{
		return value;
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
	
	public Player getOwner()
	{
		return owner;
	}
	
	private Card cards[];
	private int value;
	private int type;
	private Player owner;
	
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
}
