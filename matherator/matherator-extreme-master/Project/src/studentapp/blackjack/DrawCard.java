package studentapp.blackjack;

import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

import common.Img;

/**
 * This class will return an image with the get(num, suit) command
 * Thanks to http://byronknoll.blogspot.com/2011/03/vector-playing-cards.html for
 * making his grahics public access.
 * @author Aprillemae
 *
 */
public class DrawCard {
	
	/**
	 * Will return an image with the proper card on it
	 * @param num This is the number of the card (as in ace, two, three, four... etc). The 
	 * files are in the lower case, but that doesn't seem to matter. NAMED CARDS USE THE FIRST
	 * LETTER ONLY (a for ace, q for queen, k for king, and j for jack)! 
	 * @param suit c = clubs, s = spades, d = diamonds, h = hearts
	 * @return an Image with the correct card
	 */
	public Image get(String num, char suit){
		Image toReturn = null;
		try{
			toReturn = ImageIO.read(Img.get("/studentapp/blackjack/CardImages/" + num + suit + ".png"));
		}
		catch (Exception e){
			System.out.println("Image not found");
		}
		return toReturn;
	}
}
