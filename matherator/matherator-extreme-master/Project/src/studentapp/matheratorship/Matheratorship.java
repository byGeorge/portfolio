package studentapp.matheratorship;

import studentapp.MatheratorStudent;


/**
 * Student will solve math problems to try to sink opponent's battleships
 * @author Feckless Ellipses
 *
 */
public class Matheratorship{
	/* 
	 * These variables store arrays representing player and computer boards.
	 * 0 = empty water, no hits, misses, or battleships
	 * 1 = undiscovered ship
	 * 2 = miss
	 * 3 = hit
	 */
	public int [][]		playerBoard 	= new int[10][10];
	public int [][] 	compBoard	 	= new int[10][10];
	
	/**
	 * "Fleet" refers to all of the the ships owned by either the player 
	 * or the computer
	 * fleet[0] = aircraft carrier
	 * fleet[1] = battleship
	 * fleet[2] = submarine
	 * fleet[3] = cruiser
	 * fleet[4] = destroyer
	 */
	private boolean[]	playerFleet		= new boolean[5];
	private boolean[]	compFleet		= new boolean[5];
	
	/* 
	 * These variables store the location of the ships to 
	 * reference later shipName[0] = x position, 
	 * shipName[1] = y position lengths are shamelessly 
	 * borrowed from classic battleship game.
	 */
	private int[][] 	aircraftCarrierC = new int[2][5]; // length: 5
	private int[][] 	battleshipC		= new int[2][4]; // length: 4
	private int[][] 	submarineC		= new int[2][3]; // length 3
	private int[][] 	cruiserC		= new int[2][3]; // length 3
	private int[][] 	destroyerC		= new int[2][3]; // length 2
	private int[][] 	aircraftCarrierS = new int[2][5]; // length: 5
	private int[][] 	battleshipS		= new int[2][4]; // length: 4
	private int[][] 	submarineS		= new int[2][3]; // length 3
	private int[][] 	cruiserS		= new int[2][3]; // length 3
	private int[][] 	destroyerS		= new int[2][3]; // length 2

	
	public Matheratorship(){
		
		// initialising board
		for (int i = 0; i< 10; i++) {
			for (int j = 0; j<10; j++){
				playerBoard[i][i] = 0;
				compBoard[i][i] = 0;
			}
		}
		placeShips();
		
		// initialising fleets
		for (int i = 0; i<5; i++){
			playerFleet[i] = false;
			compFleet[i] = false;
		}
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeShips(){
		placeAircraftCarrier();
		placeBattleship();
		placeCruiser();
		placeSubmarine();
		placeDestroyer();
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeAircraftCarrier(){
		int putX = (int) (Math.random() * 10);
		int putY = (int) (Math.random() * 10);
		boolean vertical = true;
		if (Math.random() > 0.5)
			vertical = false;
		if (vertical) {
			for (int j = 0; j<5; j++){
				if (putY > 4){
					putY = putY - 5;
				}
				compBoard[putX][putY + j] = 1;
				aircraftCarrierC[0][j] = putX;
				aircraftCarrierC[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<5; i++){
				if (putX > 4){
					putX = putX - 5;
				}
				compBoard[putX+i][putY] = 1;
				aircraftCarrierC[0][i] = putX + i;
				aircraftCarrierC[1][i] = putY;
			}
		}
		putX = (int) (Math.random() * 10);
		putY = (int) (Math.random() * 10);
		vertical = true;
		if (Math.random() > 0.5)
			vertical = false;
		if (vertical) {
			for (int j = 0; j<5; j++){
				if (putY > 4){
					putY = putY - 5;
				}
				playerBoard[putX][putY + j] = 1;
				aircraftCarrierS[0][j] = putX;
				aircraftCarrierS[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<5; i++){
				if (putX > 4){
					putX = putX - 5;
				}
				playerBoard[putX+i][putY] = 1;
				aircraftCarrierS[0][i] = putX + i;
				aircraftCarrierS[1][i] = putY;
			}
		}
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeBattleship(){
		int putX = 0;
		int putY = 0;
		boolean vertical = true;
		boolean valid = false;
		// makes sure new ship does not conflict with existing ships
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 6)
				valid = false;
			else if (!vertical && putX > 6)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<4; j++){
						if (compBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<4; i++){
						if (compBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<4; j++){
				compBoard[putX][putY + j] = 1;
				battleshipC[0][j] = putX;
				battleshipC[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<4; i++){
				compBoard[putX + i][putY] = 1;
				battleshipC[0][i] = putX + i;
				battleshipC[1][i] = putY;
			}
		}
		valid = false;
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 6)
				valid = false;
			else if (!vertical && putX > 6)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<4; j++){
						if (playerBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<4; i++){
						if (playerBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<4; j++){
				playerBoard[putX][putY + j] = 1;
				battleshipS[0][j] = putX;
				battleshipS[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<4; i++){
				playerBoard[putX + i][putY] = 1;
				battleshipS[0][i] = putX + i;
				battleshipS[1][i] = putY;
			}
		}
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeCruiser(){
		int putX = 0;
		int putY = 0;
		boolean vertical = true;
		boolean valid = false;
		// makes sure new ship does not conflict with existing ships
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 7)
				valid = false;
			else if (!vertical && putX > 7)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<3; j++){
						if (compBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<3; i++){
						if (compBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<3; j++){
				compBoard[putX][putY + j] = 1;
				cruiserC[0][j] = putX;
				cruiserC[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<3; i++){
				compBoard[putX + i][putY] = 1;
				cruiserC[0][i] = putX + i;
				cruiserC[1][i] = putY;
			}
		}
		valid = false;
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 7)
				valid = false;
			else if (!vertical && putX > 7)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<3; j++){
						if (playerBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<3; i++){
						if (playerBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<3; j++){
				playerBoard[putX][putY + j] = 1;
				cruiserS[0][j] = putX;
				cruiserS[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<3; i++){
				playerBoard[putX + i][putY] = 1;
				cruiserS[0][i] = putX + i;
				cruiserS[1][i] = putY;
			}
		}
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeSubmarine(){
		int putX = 0;
		int putY = 0;
		boolean vertical = true;
		boolean valid = false;
		// makes sure new ship does not conflict with existing ships
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 7)
				valid = false;
			else if (!vertical && putX > 7)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<3; j++){
						if (compBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<3; i++){
						if (compBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<3; j++){
				compBoard[putX][putY + j] = 1;
				submarineC[0][j] = putX;
				submarineC[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<3; i++){
				compBoard[putX + i][putY] = 1;
				submarineC[0][i] = putX + i;
				submarineC[1][i] = putY;
			}
		}
		valid = false;
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 7)
				valid = false;
			else if (!vertical && putX > 7)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<3; j++){
						if (playerBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<3; i++){
						if (playerBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<3; j++){
				playerBoard[putX][putY + j] = 1;
				submarineS[0][j] = putX;
				submarineS[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<3; i++){
				playerBoard[putX + i][putY] = 1;
				submarineS[0][i] = putX + i;
				submarineS[1][i] = putY;
			}
		}
	}
	
	/**
	 * places computer and player ships into their respective boards
	 */
	private void placeDestroyer(){
		int putX = 0;
		int putY = 0;
		boolean vertical = true;
		boolean valid = false;
		// makes sure new ship does not conflict with existing ships
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 8)
				valid = false;
			else if (!vertical && putX > 8)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<2; j++){
						if (compBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<2; i++){
						if (compBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<2; j++){
				compBoard[putX][putY + j] = 1;
				destroyerC[0][j] = putX;
				destroyerC[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<2; i++){
				compBoard[putX + i][putY] = 1;
				destroyerC[0][i] = putX + i;
				destroyerC[1][i] = putY;
			}
		}
		valid = false;
		while (!valid){
			putX = (int) (Math.random() * 10);
			putY = (int) (Math.random() * 10);
			if (Math.random() > 0.5)
				vertical = false;
			if (vertical && putY > 8)
				valid = false;
			else if (!vertical && putX > 8)
				valid = false;
			else{
				// check for overlapping ships
				boolean allOkay = true;
				if (vertical){
					for (int j = 0; j<2; j++){
						if (playerBoard[putX][putY+j] != 0){
							allOkay = false;
						}
					}
				}
				else{
					for (int i = 0; i<2; i++){
						if (playerBoard[putX+i][putY] != 0){
							allOkay = false;
						}
					}
				}
				valid = allOkay;
			}
		}
		if (vertical){
			for (int j = 0; j<2; j++){
				playerBoard[putX][putY + j] = 1;
				destroyerS[0][j] = putX;
				destroyerS[1][j] = putY + j;
			}
		}
		else {
			for (int i = 0; i<2; i++){
				playerBoard[putX + i][putY] = 1;
				destroyerS[0][i] = putX + i;
				destroyerS[1][i] = putY;
			}
		}
	}
	
	/**
	 * Checks the square to see if the guess hits or misses a ship
	 * @param x the X coordinate of the board
	 * @param y the Y coordinate of the board
	 * @return 2 if miss, 3 if hit, -1 if invalid
	 */
	public int makePlayerGuess(int x, int y){
		int toReturn = -1;
		if (compBoard[x][y] == 0) {
			toReturn = 2;
			compBoard[x][y] = 2;
		}
		else if (compBoard[x][y] == 1) {
			toReturn = 3;
			compBoard[x][y] = 3;
		}
		return toReturn;
	}

	/**
	 * Checks the square to see if the guess hits or misses a ship
	 * @param x the X coordinate of the board
	 * @param y the Y coordinate of the board
	 * @return 2 if miss, 3 if hit, -1 if invalid
	 */
	public int makeComputerGuess(){
		boolean valid = false;
		int x=-1;
		int y=-1;
		while (!valid){
			x = (int) (Math.random()*10);
			y = (int) (Math.random()*10);
			if (playerBoard[x][y] == 0) {
				valid = true;
				playerBoard[x][y] = 2;
				return 2;
			}
			else if (playerBoard[x][y] == 1) {
				valid = true;
				playerBoard[x][y] = 3;
				return 3;
			}
		}
		return -1;
	}
	
	/**
	 * checks to see if the player's ship has sunk
	 * @return A string message telling the player which, 
	 * if any, ship has been hit.
	 */
	public String checkPlayerSunk(){
		String toReturn = "Captain we're hit!";
		if (shipSunk(aircraftCarrierS, 5, playerBoard)){
			if (playerFleet[0] == false){
				playerFleet[0] = true;
				return "The computer sunk your Aircraft Carrier!";
			}
		}
		if (shipSunk(battleshipS, 4, playerBoard)){
			if (playerFleet[1] == false){
				playerFleet[1] = true;
				return "The computer sunk your Battleship!";
			}
		}
		if (shipSunk(submarineS, 3, playerBoard)){
			if (playerFleet[2] == false){
				playerFleet[2] = true;
				return "The computer sunk your Submarine!";
			}
		}
		if (shipSunk(cruiserS, 3, playerBoard)){
			if (playerFleet[3] == false){
				playerFleet[3] = true;
				return "The computer sunk your Cruiser!";
			}
		}
		if (shipSunk(destroyerS, 2, playerBoard)){
			if (playerFleet[4] == false){
				playerFleet[4] = true;
				return "The computer sunk your Destroyer!";
			}
		}
		return toReturn;
	}
	
	/**
	 * checks to see if the computer's ship has sunk
	 * @return A string message telling the player which, 
	 * if any, ship has been hit.
	 */
	public String checkComputerSunk() {
		String toReturn = "We got them, Captain!";
		if (shipSunk(aircraftCarrierC, 5, compBoard)){
			if (compFleet[0] == false){
				compFleet[0] = true;
				return "You sunk the computer's Aircraft Carrier!";
			}
		}
		if (shipSunk(battleshipC, 4, compBoard)){
			if (compFleet[1] == false){
				compFleet[1] = true;
				return "You sunk the computer's Battleship!";
			}
		}
		if (shipSunk(submarineC, 3, compBoard)){
			if (compFleet[2] == false){
				compFleet[2] = true;
				return "You sunk the computer's Submarine!";
			}
		}
		if (shipSunk(cruiserC, 3, compBoard)){
			if (compFleet[3] == false){
				compFleet[3] = true;
				return "You sunk the computer's Cruiser!";
			}
		}
		if (shipSunk(destroyerC, 2, compBoard)){
			if (compFleet[4] == false){
				compFleet[4] = true;
				return "You sunk the computer's Destroyer!";
			}
		}
		return toReturn;
	}
	
	/**
	 * Tells the player whether or not they have won the game
	 * @return true if won, false if not
	 */
	public boolean gameWon(){
		boolean toReturn = true;
		for (int i=0; i<compFleet.length; i++){
			if (compFleet[i] == false)
				toReturn = false;
		}
		return toReturn;
	}
	
	/**
	 * tells the player whether or not they have lost the game
	 * @return true if lost, false if not
	 */
	public boolean gameLost(){
		boolean toReturn = true;
		for (int i=0; i<playerFleet.length; i++){
			if (playerFleet[i] == false)
				toReturn = false;
		}
		return toReturn;
	}
	
	/**
	 * Checks to see if the ship has been sunk
	 * @param ship specify the ship in question
	 * @param board player or computer board
	 * @return true if sunk, false if still afloat
	 */
	private boolean shipSunk(int[][] ship, int size, int[][] board){
		boolean checking = true;
		int x;
		int y;
		for (int i=0; i<size; i++){
			x = ship[0][i];
			y = ship[1][i];
			if (board[x][y] < 3) {
				checking = false;
			}
		}
		return checking;
	}
	
	public static void main (String[]args){
		Matheratorship bs = new Matheratorship();
		System.out.println("Computer sunk? " + bs.checkComputerSunk());
		System.out.println("Player sunk? " + bs.checkPlayerSunk());
		System.out.println("Should be false: " + bs.gameWon());
		System.out.println("Should be false: " + bs.gameLost());
		
		for (int i = 0; i<10; i++){
			for (int j = 0; j<10; j++){
				bs.makePlayerGuess(i, j);
				bs.makeComputerGuess();
			}
		}
		for (int i = 0; i<5; i++){
			System.out.println("Computer sunk? " + bs.checkComputerSunk());
			System.out.println("Player sunk? " + bs.checkPlayerSunk());
		}
		System.out.println("Should be true: " + bs.gameWon());
		System.out.println("Should be true: " + bs.gameLost());
	}
}
