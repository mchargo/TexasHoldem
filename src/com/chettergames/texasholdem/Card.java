package com.chettergames.texasholdem;

public class Card 
{
	public Card(int value, Type type)
	{
		this.value = value;
		this.type = type;
	}
	
	public String toString()
	{
		String printValue = "";
		if(value > 1 && value < 11)
			printValue = "" + value;
		else if(value == 11)
			printValue = "Jack";
		else if(value == 12)
			printValue = "Queen";
		else if(value == 13)
			printValue = "King";
		else if(value == 14)
			printValue = "Ace";
		
		String suit = "";
		if(type == Type.CLUBS)
			suit = " of Clubs";
		else if(type == Type.DIAMONDS)
			suit = " of Diamonds";
		else if(type == Type.HEARTS)
			suit = " of Hearts";
		else if(type == Type.SPADES)
			suit = " of Spades";
		
		return printValue + suit;
	}
	
	public int getValue(){return value;}
	public Type getType(){return type;}
	
	// 2 - 10, Jack = 11, Queen = 12, King = 13, Ace = 14
	private int value;
	private Type type;
	
	public enum Type{HEARTS, DIAMONDS, SPADES, CLUBS}
}
