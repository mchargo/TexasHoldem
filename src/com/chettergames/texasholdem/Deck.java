package com.chettergames.texasholdem;

import java.util.Collections;
import java.util.LinkedList;

import com.chettergames.texasholdem.Card.Type;

public class Deck 
{
	public Deck()
	{
		cards = new LinkedList<Card>();
		
		for(int value = 2;value <= 14;value++)
		{
			Card spade = new Card(value, Type.SPADES);
			Card heart = new Card(value, Type.HEARTS);
			Card diamond = new Card(value, Type.DIAMONDS);
			Card club = new Card(value, Type.CLUBS);
			
			cards.add(spade);
			cards.add(heart);
			cards.add(diamond);
			cards.add(club);
		}
		
		Collections.shuffle(cards);
	}
	
	public Card drawCard()
	{
		return cards.remove();
	}
	
	public void printDeckToConsole()
	{
		for(int x = 0;x < cards.size();x++)
		{
			System.out.println(x + " : " + cards.get(x).toString());
		}
	}
	
	private LinkedList<Card> cards;
}
