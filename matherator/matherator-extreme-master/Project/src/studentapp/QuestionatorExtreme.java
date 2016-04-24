package studentapp;

import java.util.Arrays;

public class QuestionatorExtreme {
	
	private int firstNumber; // first number in the math equation
	private int secondNumber; // second number in the math equation
	private int answer; // stores the answer to the math question
	private boolean plus;
	private int lvl; 
	
	/**
	 * This will generate an equation for use in the Matherator Extreme 
	 * game series.
	 * 
	 * @param level refers to the level of difficulty. 
	 * level 0 is differentiating between ones, tens, and hundreds places
     * level 1 is addition for the numbers 1-9.
     * level 2 is addition for double digit equations with no carrying.
     * level 3 is addition for double digit equations that include carrying.
     * level 4 is single digit subtraction
     * level 5 is double digit subtraction with no borrowing.
     * level 6 is double digit subtraction with borrowing.
     * level 7 combines levels 1 and 4 (simple subtraction)
     * level 8 combines levels 2 and 5 (simple subtraction in double digits)
     * level 9 combines levels 3 and 6 (not-so-simple subtraction)
     * 
     * HOWEVER!
     * These are the INTERNAL numbers. They
     * should not be presented to the user; only the names.
     * They should default to the Standard Progression:
     *  1 -> 2 -> 3 -> 4 -> 7 -> 5 -> 8 -> 6 -> 9
     *  
	 */
	public QuestionatorExtreme(int level) {
		lvl = level;
		if (level == 0){
			generatePlacesProblem();
		}
		else if (level >= 1 && level <= 3) {
			generateAdditionProblem(level);
		}
		else if (level >=4 && level <=6) {
			generateSubtractionProblem(level);
		}
		else {
			if (Math.random() > 0.5f) {
				generateAdditionProblem(level-6);
			}
			else {
				generateSubtractionProblem(level-3);
			}
		}
	}
	
	public void generatePlacesProblem(){
		firstNumber = (int) (Math.random() * 999);
		boolean valid = false;
		while (!valid) {
			if (firstNumber < 123) {
				firstNumber = (int) (Math.random() * 999);
			}
			else if (firstNumber%10 == (firstNumber/10)%10 
					|| firstNumber%10 == firstNumber/100 
					|| (firstNumber/10)%10 == firstNumber/100) {
				// making sure numbers are not repeated
				firstNumber = (int) (Math.random() * 999);
			}
			else
				valid = true;
		}
		int place = (int) (Math.random() * 3);
		if (place == 0) {
			secondNumber = firstNumber%10;
			answer = 1;
		}
		else if (place == 1) {
			secondNumber = (firstNumber/10)%10;
			answer = 10;
		}
		else {
			secondNumber = firstNumber/100;
			answer = 100;
		}
	}
		
	public void generateAdditionProblem(int level) {
		plus = true;
		if (level == 1 ) {
			firstNumber = (int) (Math.random() * 10);
			secondNumber = (int) (Math.random() * 10);
		}
		else if (level == 2) {
			boolean valid = false;
			while (!valid) {
				int first = (int) (Math.random() * 98);
				int second = (int) (Math.random() * 98);
				if (first + second < 100) {
					if (first % 10 + second % 10 < 10) {
						valid = true;
						firstNumber = first;
						secondNumber = second;
					}
				}
			}
		}
		else {
			boolean valid = false;
			while (!valid) {
				int first = (int) (Math.random() * 98);
				int second = (int) (Math.random() * 98);
				if (first + second < 100) {
					valid = true;
					firstNumber = first;
					secondNumber = second;
				}
			}
		}
		answer = firstNumber + secondNumber;
	}

	public void generateSubtractionProblem(int level) {
		plus = false;
		if (level == 4) {
			answer = (int) (Math.random() * 10);
			secondNumber = (int) (Math.random() * 10);
		}
		else if (level == 5) {
			boolean valid = false;
			while (!valid) {
				int ans = (int) (Math.random() * 98);
				int second = (int) (Math.random() * 98);
				if (ans + second < 100) {
					if (ans % 10 + second % 10 < 10) {
						valid = true;
						answer = ans;
						secondNumber = second;
					}
				}
			}
		}
		else {
			boolean valid = false;
			while (!valid) {
				int ans = (int) (Math.random() * 98);
				int second = (int) (Math.random() * 98);
				if (ans + second < 100) {
					valid = true;
					answer = ans;
					secondNumber = second;
				}
			}
		}
		firstNumber = answer + secondNumber;
	}
	
	/**
	 * Creates a "math sentence" using randomly generated numbers
	 * 
	 * @return a String in the following formats where n and m are 
	 * randomly generated numbers:
	 *    addition: n + m = 
	 *    subtraction: n - m =
	 * with no space at the end.
	 */
	public String simpleMathQ() {
		if (lvl == 0){
			String toReturn = "What place does the number " + secondNumber + " have in " + firstNumber + "? 1, 10, or 100";
			return toReturn;
		}
		return String.format("%d %c %d =",
				firstNumber,
				getOperator(),
				secondNumber);
	}
	
	/**
	 * @return the answer to the problem
	 */
	public int getAnswer() {
		return answer;
	}
	
	/**
	 * @return the first number in the problem
	 */
	public int getFirstNumber() {
		return firstNumber;
	}
	
	/**
	 * @return the second number in the problem
	 */
	public int getSecondNumber() {
		return secondNumber;
	}
	
	/**
	 * "reasonable" means:
	 * lvl 0 - no more than two wrong answers available
	 * lvl 1, 4, and 7 - no more than 18 wrong answers available
	 * all other levels - no more than 100 wrong answers available
	 * because... YOU WON'T NEED THAT MANY WRONG ANSWERS!!!!
	 * 
	 * returns sorted answers
	 * 
	 * @return a number reasonable for the level
	 */
	public int[] getWrongNumbers(int answerCount) {
		int toReturn[] = new int[answerCount];
		if (answerCount < 1){
			System.out.println("Why did you even call this method?");
			return toReturn;
		}
		if (lvl == 0){
			toReturn = wrong0Answers(answerCount);
		}
		else if (lvl == 1 || lvl == 4 || lvl == 7){
			toReturn = wrong1Answers(answerCount);
		}
		else {
			toReturn = wrong2Answers(answerCount);
		}
		return toReturn;
	}
	
	/**
	 * Wrong answers for problem level 0
	 * @param answerNumber (number of wrong answers requested
	 * @return int array of wrong answers
	 */ 
	private int[] wrong0Answers(int answerNumber){
		int[] toReturn = new int[answerNumber];
		if (answerNumber > 2){
			System.out.println("Can have no more than 2 wrong answers for this level");
			return toReturn;
		}
		double rnd = Math.random();
		if (answer == 1){
			if (answerNumber == 1 && rnd > 0.5)
				toReturn[0] = 10;
			else if (answerNumber == 1 && rnd < 0.5)
				toReturn[0] = 100;
			else { // answer count has to be 2 at this point
				toReturn[0] = 10;
				toReturn[1] = 100;
			}
		}
		else if (answer == 10){
			if (answerNumber == 1 && rnd > 0.5)
				toReturn[0] = 1;
			else if (answerNumber == 1 && rnd < 0.5)
				toReturn[0] = 100;
			else { // answer count has to be 2 at this point
				toReturn[0] = 1;
				toReturn[1] = 100;
			}
		}
		else { // answer must be 100
			if (answerNumber == 1 && rnd > 0.5)
				toReturn[0] = 1;
			else if (answerNumber == 1 && rnd < 0.5)
				toReturn[0] = 10;
			else { // answer count has to be 2 at this point
				toReturn[0] = 1;
				toReturn[1] = 10;
			}
		}
		return toReturn;
	}
	
	private int[] wrong1Answers(int answerNumber){
		int[] toReturn = new int[answerNumber];
		if (answerNumber > 18)
			System.out.println("Not enough appropriate wrong answers");
		else{
			int rnd = 0;
			for (int i = 0; i < answerNumber; i++){
				boolean valid = false; // will be true if you can add it to the array
				while (!valid) {
					// pick a number! any nu...mber!
					rnd = (int) (Math.random() * 19);
					// now check to see if it matches the answer
					if (rnd != answer){
						// now check to see if it matches any previous wrong answers found
						if (!contains(toReturn, rnd, i))
							// heyyy it works! ... now stop looping, valid is true
							valid = true;
						}
					}
				// add it to the array, now that you have a valid number
				toReturn[i]= rnd;
				}
			}
		Arrays.sort(toReturn);
		return toReturn;
	}

	private boolean contains(int[] array, int number, int count){
		for (int i = 0; i<count; i++){
			if (array[i] == number)
				return true;
		}
		return false;
	}
	
	private int[] wrong2Answers(int answerNumber){
		int[] toReturn = new int[answerNumber];
		if (answerNumber > 100)
			System.out.println("Not enough appropriate wrong answers");
		else{
			int rnd = 0;
			for (int i = 0; i < answerNumber; i++){
				boolean valid = false; // will be true if you can add it to the array
				while (!valid) {
					// pick a number! any nu...mber!
					rnd = (int) (Math.random() * 999);
					// now check to see if it matches the answer
					if (rnd != answer){
						// now check to see if it matches any previous wrong answers found
						if (!contains(toReturn, rnd, i))
							// heyyy it works! ... now stop looping, valid is true
							valid = true;
						}
					}
				// add it to the array, now that you have a valid number
				toReturn[i]= rnd;
				}
			}
		Arrays.sort(toReturn);
		return toReturn;
	}
	
	/**
	 * @return the operator, '+' or '-'.
	 */
	public char getOperator() {
		return isAddition() ? '+' : '-';
	}
	
	/**
	 * @return true if the problem is an addition problem, false if not
	 */
	public boolean isAddition() {
		return plus;
	}
	
	/**
	 * @return Returns a simpleMathQ math sentence, plus a space at the beginning and end.
	 */
	public String getQuestion() {
		return " " + simpleMathQ() + " ";
	}
	
	/**
	 * creates a new question for Matherator Extreme
	 * @param level refers to the level of difficulty. 
     * level 1 is addition for the numbers 1-9.
     * level 2 is addition for double digit equations with no carrying.
     * level 3 is addition for double digit equations that include carrying.
     * level 4 is single digit subtraction
     * level 5 is double digit subtraction with no borrowing.
     * level 6 is double digit subtraction with borrowing.
     * level 7 combines levels 1 and 4 (simple subtraction)
     * level 8 combines levels 2 and 5 (simple subtraction in double digits)
     * level 9 combines levels 3 and 6 (not-so-simple subtraction)
	 */
	public void newQ(int level) {
		lvl = level;
		if (level == 0) {
			generatePlacesProblem();
		}
		else if (level >= 1 && level <= 3) {
			generateAdditionProblem(level);
		}
		else if (level >=4 && level <=6) {
			generateSubtractionProblem(level);
		}
		else {
			if (Math.random() > 0.5f) {
				generateAdditionProblem(level-6);
			}
			else {
				generateSubtractionProblem(level-3);
			}
		}
	}
	
	public static void main(String[] args) {
		QuestionatorExtreme askMe = new QuestionatorExtreme(2);
		System.out.println(askMe.simpleMathQ() + " " + askMe.getAnswer());
//		askMe.newQ(9);
//		System.out.println(askMe.simpleMathQ() + " " + askMe.getAnswer());
//		System.out.println(askMe.getFirstNumber());
//		System.out.println(askMe.getSecondNumber());
//		System.out.println(askMe.isAddition());
		int[] test;
		test = askMe.getWrongNumbers(0);
		System.out.println("Testing 0");
		for (int i = 0; i< test.length; i++)
			System.out.println(test[i]);
		test = askMe.getWrongNumbers(1);
		System.out.println("Testing 1");
		for (int i = 0; i< test.length; i++)
			System.out.println(test[i]);
		test = askMe.getWrongNumbers(17);
		System.out.println("Testing 17");
		for (int i = 0; i< test.length; i++)
			System.out.println(test[i]);
		test = askMe.getWrongNumbers(100);
		System.out.println("Testing 999");
		for (int i = 0; i< test.length; i++)
			System.out.println(test[i]);
		test = askMe.getWrongNumbers(101);
	}
}
