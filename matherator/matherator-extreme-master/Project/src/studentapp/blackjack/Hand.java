package studentapp.blackjack;

/* Keep track of the cards in the student's hand. 
 *  
 * @author Feckless Ellipses
 *
 * Hand.java
 */

import java.util.Vector;

public class Hand {

   private Vector<Card> hand;   // The cards in the hand. 
   boolean isThereAnAce = false; 
   int summ = 0;
   
   /* Initialize an empty hand of cards */ 
   public Hand() {
      hand = new Vector<Card>();
   }
   
   /* clear the hand of all cards */ 
   public void clear() {
	   
      hand.removeAllElements(); 
      
   }
   
   /* add a card to the back of the hand as long as it is not null */
   public void addCard(Card c) {

	   if (c != null) 
         hand.addElement(c); 
	   
   }
   
   /* How many cards are in the hand? */
   public int getCardCount() {
       
	   return hand.size(); 
	   
   }
   
   /* Get the sum of cards with ace = 1 */
   public int getSum()
   { 
	   int test = 0;
	   summ = 0;
	   
	   if(!hand.isEmpty())
	   {
		   for(int i = 0; i < getCardCount(); i ++)
		   { 
			   test = hand.get(i).getValue(); 
			   
			   if(test > 10) //make sure the jack, queen, and king only add 10 
				   summ += 10;
			   else
				   summ += test; 
			   
			   if(test == 1)
				   isThereAnAce = true;
			   		   
		   }
	   }
	   return summ;
   } 
   
   /* Get the card from the position or null otherwise */
   public Card getCard(int position) {

	   if (position >= 0 && position < hand.size())
	      return (Card)hand.elementAt(position);
	   else
	      return null; 
}
   
   /* Returns the boolean set in getSum() */ 
   public boolean isThereAnAce()
   {
	   return isThereAnAce;
   }

   /* Returns how many cards are in the hand */
   public int size() 
   {
	   return hand.size(); 
   }
   
} //end of class Hand 
