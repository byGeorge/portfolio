package common;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.Random;




/**
 * 
 * 
 * Project-wide constants go in this file.
 * 
 *
 */

public class Konstants {
	
	public static final String DefaultPassphrase = "hamburger";

	public static final int MATH_PORT_EXTREME = 8080;
	public static final Random RandomatorExtreme = new Random();
	
	public static final Rectangle ExitButtonBounds = new Rectangle(925, 0, 60, 30);
	
	public static final long AnimationFrameDelayMS = Math.round(1d / 30d * 1000);
	
	public static final int LevelsMin = 0;
	public static final int LevelsMax = 9;
	
	public enum StudentToken {
		flwr,  // flower
		eifl,  // eiffel tower
		pnda,  // panda
		doge,  // dog
		hamr,  // hammer
		casl,  // castle
		fdra,  // hat
		fish,  // fish
		apln,  // airplane
		yzru,  // skater
		cryn,  // crayon box
		cake,  // slice of cake
		cmra,  // camera
		choo,  // train
		bird,  // bird
		eyee,  // eye
		wind,  // windmill
		tele,  // rotary telephone
		appl,  // apple
		orng;  // orange
		
		private static final List<StudentToken> tarry = Arrays.asList( values() );
		private static final Random rando = new Random();
		
		public static StudentToken random() {
			return tarry.get( rando.nextInt(tarry.size()) );
		}
		
	}
	
	public static String resolveToName(String resolvee){
		switch (resolvee) {
			case "flwr":
				return "Flower";
			case "eifl":
				return "Eiffel Tower";
			case "pnda":
				return "Panda";
			case "doge":
				return "Dog";
			case "hamr":
				return "Hammer";
			case "casl":
				return "Castle";
			case "fdra":
				return "Hat";
			case "fish":
				return "Fish";
			case "apln":
				return "Airplane";
			case "yzru":
				return "Ice Skater";
			case "cryn":
				return "Crayons";
			case "cake":
				return "Cake";
			case "cmra":
				return "Camera";
			case "choo":
				return "Train";
			case "bird":
				return "Bird";
			case "eyee":
				return "Eye";
			case "wind":
				return "Windmill";
			case "tele":
				return "Telephone";
			case "appl":
				return "Apple";
			case "orng":
				return "Orange";
			default:
				return "Not Made";
		}
	}
	
}
