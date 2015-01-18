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
	
	public int typeToVal(Type type)
	{
		switch(type)
		{
		case SPADES: return 3;
		case CLUBS: return 0;
		case DIAMONDS: return 2;
		case HEARTS: return 1;
		}
		
		return 0;
	}
	
	public int getValue(){return value;}
	public Type getType(){return type;}
	
	// 2 - 10, Jack = 11, Queen = 12, King = 13, Ace = 14
	private int value;
	private Type type;
	
	public static final int VAL_2 = 2;
	public static final int VAL_3 = 3;
	public static final int VAL_4 = 4;
	public static final int VAL_5 = 5;
	public static final int VAL_6 = 6;
	public static final int VAL_7 = 7;
	public static final int VAL_8 = 8;
	public static final int VAL_9 = 9;
	public static final int VAL_10 = 10;
	public static final int VAL_JACK = 11;
	public static final int VAL_QUEEN = 12;
	public static final int VAL_KING = 13;
	public static final int VAL_ACE = 14;
	
	public static final int CARDS_IN_SUIT = 13;
	
	public enum Type{HEARTS, DIAMONDS, SPADES, CLUBS}
}
