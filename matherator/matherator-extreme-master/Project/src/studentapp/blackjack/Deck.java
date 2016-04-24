package studentapp.blackjack;

/* Get our deck of cards. 
 *  
 * @author Feckless Ellipses
 *
 * Deck.java
 */

public class Deck { 
	
	Card[] deck; 
	int cardsUsed; 
	
	public Deck()
	{
		deck = new Card[52];
		
		int counter = 0;
		
		/* initialize the 52 cards in order */ 
		for(int suit = 0; suit < 4; suit++)
		{
			for(int value = 1; value < 14; value++)
			{
				deck[counter] = new Card(value, suit);
				counter++; 
			}
		}
		
		cardsUsed = 0;
		
	} //end of constructor 
	
	/* un-order the cards / switcheroo */
	public void shuffle()
	{
		for(int i = 51; i >= 0;  i--)
		{
	          int rand = (int)(Math.random()*(i+1));
	           Card temp = deck[i];
	           deck[i] = deck[rand];
	           deck[rand] = temp;
		}
		
		cardsUsed = 0;
	}

	/* deal out a card */
	public Card dealCard()
	{
      if (cardsUsed == 52)
         shuffle();
      cardsUsed++;
      return deck[cardsUsed - 1];
	}
	
	public static void main(String[] args)
	{
		for(int i = 51; i >= 0;  i--)
		{
	          int rand = (int)(Math.random()*(i+1));
	          System.out.println(i + " " + rand);
		}
	}
}


