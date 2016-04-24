package studentapp.blackjack;

/* A Card whose value may not actually be its number value. 
 *  
 * @author Feckless Ellipses
 *
 * Card.java
 */

public class Card {
	
	int suit;
	int value;
	
	int HEART    = 0,
		SPADES   = 1,
		DIAMONDS = 2,
		CLUBS    = 3; 
	
	int ACE      = 1,
		JACK     = 11,
		QUEEN    = 12,
		KING     = 13;
	
	private char charsuit; 
	private String stringvalue;
	
	public Card(int valueOftheCard, int suitOftheCard)
	{
		value = valueOftheCard;
		suit = suitOftheCard;
	}
	
	public int getSuit()
	{
		return suit;
	}
	
	public char getCharSuit()
	{
		if( getSuit() == 0)
			charsuit = 'h';
		else if ( getSuit() == 1)
			charsuit = 's';
		else if ( getSuit() == 2)
			charsuit = 'd';
		else 
			charsuit = 'c';
		
		return charsuit;
	}
	
	public int getValue()
	{
		if(value > 10)
			return 10;
		else
			return value;
	} 
	
	public String getStringValue()
	{
		if( getValue() == 13 )
			stringvalue = "k";
		else if ( getValue() == 12)
			stringvalue = "q";
		else if ( getValue() == 11)
			stringvalue = "j";
		else if ( getValue() == 1)
			stringvalue = "a";
		else
			stringvalue = Integer.toString( getValue() ); 
		
		return stringvalue;
	}
	
	public String toString()
	{
		String card = "";
		if(value == ACE)
			card += "n Ace "; 
		else if(value == JACK)
			card += " Jack";
		else if(value == QUEEN)
			card += " Queen";
		else if (value == KING)
			card += " King";
		else if(value == 8)
			card += "n " + value;
		else
			card += " " + value;
		
		if(suit == HEART)
			card += " of Hearts";
		else if(suit == SPADES)
			card += " of Spades";
		else if(suit == DIAMONDS)
			card += " of Diamonds";
		else
			card += " of Clubs";
			
			
		return card;
	}
}
