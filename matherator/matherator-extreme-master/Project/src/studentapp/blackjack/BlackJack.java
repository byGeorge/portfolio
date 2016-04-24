package studentapp.blackjack;

/* BlackJack - the child version -  
 * Given 10 chips, they must double them to win. 
 * 
 * The dealer only gets dealt two cards. And aces are only worth 1. 
 * 
 * @author Feckless Ellipses
 *
 * BlackJack.java
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import common.CrossMessage;
import common.Img;
import common.Konstants;
import studentapp.MatheratorStudent;
import studentapp.GameSelect;

public class BlackJack extends JPanel{
	
	MatheratorStudent appcontainer;  
	String studentName;
	String className;
	int level; 
	String studentToken; 
	Iterator<CrossMessage> serverResponses;
	
	int numberOfChips = 10; 
	int chipsBet = 0; 
	int numberOfAnswers = 0;
	int numberOfCorrectAnswers = 0; 
	boolean subtraction = false; 
	boolean passedSubQ = false;
	
	/* answers of math question */ 
	int playerAnswer;
	int correctAnswer;
	int subCorrectAnswer;
	Hand playersHand; //of cards 
	Hand dealersHand;
	Deck deck;
	
	boolean notPassed = true;  //this gets set to true when they have reached 20 chips and will not change if they keep playing and lose everything. 
	boolean wantsToPlay = true; //so they can keep playing even after reaching 20 chips 
	
	JTextPane message; //what is used to display messages to the screen 
	JTextField answerbox; //where the player enters their answer 
	JLabel question; //where the math question is displayed 
	JLabel chips; //displays current number of chips
	JButton enter; //enter the answer 
	JButton exit; //exit the game 
	JButton bet; //bet some chips 
	JButton okay; //get passed the welcome message 
	JButton hit; //add another card to the total 
	JButton freeze; //freeze total 
	JButton restart; //allows player to restart the game after they have lost all of their chips 
	JButton yes; //let's player continue playing the game even after earning 20 chips 
	JButton advance; //continues play
	JButton subEnter; //enter for subtraction problems
	JButton subContinue; //continues play after asking subtraction problem 
	
	CrossMessage crossMessage;
	Socket extremeServer;
	PrintWriter toServer; 
	
	Color pink = new Color(255, 130, 171); 
	Color pokergreen = new Color(51, 128, 0);
	Font font = new Font("Verdana", Font.BOLD, 18); 
	
	DrawCard draw = new DrawCard(); 
	ImageIcon[] playercards = new ImageIcon[6];  
	JLabel[] playercardpictures;
	
	ImageIcon[] dealercards = new ImageIcon[2];
	JLabel[] dealercardpictures;
	
	BufferedImage background; 
	JLabel bg; 

	public BlackJack(MatheratorStudent app, String student, String classs, int lvl, String token)
	{
		/* put away the parameters */
		appcontainer = app;
		studentName = student;
		className = classs; 
		level = lvl; 
		studentToken = token;
		if(level > 4)
			subtraction = true; 
		
		/* make it green */ 
		try{      
			background = ImageIO.read(Img.get("/studentapp/blackjack/bg.png"));
		}
		catch (Exception e){
			System.out.println("Image not found"); 
		}
		this.setBackground(pokergreen);	
		ImageIcon bagd = new ImageIcon(background);
		appcontainer.setTitle("BlackJack"); 
		this.setLayout(null); 
		
		/* connecting to the server */
		try{
		extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
		toServer = new PrintWriter( extremeServer.getOutputStream(), true ); 
		serverResponses = CrossMessage.messagesFrom( extremeServer.getInputStream() ).iterator();
		}catch(IOException ioe){ ioe.printStackTrace(); }
		
		/* answer text field */ 
		answerbox = new JTextField();
		answerbox.setBorder(BorderFactory.createLineBorder(Color.WHITE)); 
		answerbox.setBounds(390, 250, 100, 40);
		answerbox.setText("        ");
		answerbox.setBorder(BorderFactory.createLineBorder(Color.black));
		answerbox.setFont(font);
		this.add(answerbox); 
		answerbox.setVisible(false); 
		
		/* enter button used to get the player's answer */ 
		enter = new JButton("Enter"); 
		enter.setBounds(510, 250, 100, 40);
		enter.addActionListener(enterButtonActionListener()); 
		enter.setFont(font);
		this.add(enter);
		enter.setVisible(false); 
		
		/* enter button used to get the player's answer to subtraction problem */
		subEnter = new JButton("Enter");
		subEnter.setBounds(510, 250, 100, 40);
		subEnter.addActionListener(subEnterButtonActionListener());
		subEnter.setFont(font);
		subEnter.setVisible(false);
		this.add(subEnter);
		
		/* bet button used to get the player's answer */ 
		bet = new JButton("Bet");
		bet.setBounds(510, 250, 100, 40);
		bet.addActionListener(betButtonActionListener());
		bet.setFont(font);
		this.add(bet);
		bet.setVisible(false); 
		
		/* okay button used to get passed the welcome message */
		okay = new JButton("Okay");
		okay.setBounds(450, 250, 100, 40);
		okay.addActionListener(okayButtonActionListener()); 
		okay.setFont(font);
		this.add(okay);
		
		/* hit button for those trying to get closer to 21 */
		hit = new JButton("Hit Me!");
		hit.setBounds(390, 250, 100, 40);
		hit.addActionListener(hitButtonActionListener()); 
		hit.setFont(font);
		this.add(hit);
		hit.setVisible(false);
		
		/* freeze button for those not so daring */
		freeze = new JButton("Freeze");
		freeze.setBounds(510, 250, 100, 40);
		freeze.addActionListener(freezeButtonActionListener()); 
		freeze.setFont(font);
		this.add(freeze);
		freeze.setVisible(false);
		
		/* restart button for when the player loses */
		restart = new JButton("Restart");
		restart.setBounds(450, 250, 100, 40); 
		restart.addActionListener(restartButtonActionListner());
		restart.setFont(font);
		this.add(restart);
		restart.setVisible(false); 
		
		/* yes button pressed if they want to keep playing */
		yes = new JButton("Yes!");
		yes.setBounds(450, 250, 100, 40);
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				wantsToPlay = true;
				chips.setText("Chips: " + numberOfChips); 
				placeBets();
			}
		}); 
		yes.setFont(font);
		this.add(yes);
		yes.setVisible(false); 
		
		/* continues the play allowing the text to be seen */
		advance = new JButton("Continue");
		advance.setBounds(400, 250, 200, 40); 
		advance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				chips.setText("Chips: " + numberOfChips); 
				checkChips(); 
				for(int i = 0; i < dealercardpictures.length; i++)
					dealercardpictures[i].setVisible(false);
			}
		}); 
		advance.setVisible(false);
		advance.setFont(font);
		this.add(advance); 
		
		/* continues the play allowing subtraction text to be seen */
		subContinue = new JButton("Continue");
		subContinue.setBounds(400, 250, 200, 40); 
		subContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				passedSubQ = true;
				subContinue.setVisible(false);
				hitOrfreeze();
			}
		});
		subContinue.setVisible(false);
		subContinue.setFont(font);
		this.add(subContinue); 
		
		/* exit button sends player back to game selection  */
		exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
        		endGame();
            }
        });
		exit.setBounds(875, 25, 100, 40);
		exit.setFont(font);
		this.add(exit);
		
		/* this is what will display messages to the player */
	    SimpleAttributeSet attribs = new SimpleAttributeSet();  
	    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);  
	    StyleConstants.setFontFamily(attribs, "Verdana");
	    StyleConstants.setFontSize(attribs, 18); 
	    StyleConstants.setBold(attribs, true); 
	    StyleConstants.setForeground(attribs, Color.WHITE);
		message = new JTextPane(); 
		message.setBounds(25, 75, 950, 100); 
		message.setParagraphAttributes(attribs, true);
		message.setEditable(false);
		message.setOpaque(false);
		this.add(message); 
		message.setVisible(false);		

		/* this is what will display the math questions */
		question = new JLabel(); 
		question.setBounds(25, 135, 950, 100);  
		question.setHorizontalAlignment(SwingConstants.CENTER);
		question.setFont(font); 
		question.setForeground(Color.WHITE);
		question.setOpaque(false);
		this.add(question); 
		question.setVisible(false); 
		
		/* Displays the total number of chips the player has */
		chips = new JLabel(); 
		chips.setBounds(25, 25, 500, 25); 
		chips.setOpaque(false);
		chips.setText("Chips: " + numberOfChips); 
		chips.setFont(font);
		chips.setForeground(Color.WHITE);
		this.add(chips); 
		chips.setVisible(false); 
		
		/* Initializing the pictures of the cards */ 
		playercardpictures = new JLabel[6]; 
		int j = 185;
		for(int i = 0; i < playercardpictures.length; i++)
		{
			playercardpictures[i] = new JLabel();
			
			if(i == 0)
				playercardpictures[i].setBounds(25, 350, 146, 213); 
			else if( i == playercardpictures.length - 1)
				playercardpictures[i].setBounds(829, 350, 146, 213);
			else
			{
				playercardpictures[i].setBounds(j, 350, 146, 213); 
				j += 161; 
			}
 
			playercardpictures[i].setOpaque(false); 
			playercardpictures[i].setVisible(false);
			this.add(playercardpictures[i]); 
		}
		
		/* Initializing the dealers pictures of the cards */ 
		dealercardpictures = new JLabel[2];
		dealercardpictures[0] = new JLabel();
		dealercardpictures[1] = new JLabel();
		dealercardpictures[0].setBounds(820, 140, 73, 107);
		dealercardpictures[1].setBounds(910, 140, 73, 107);
		dealercardpictures[0].setVisible(false);
		dealercardpictures[1].setVisible(false);
		this.add(dealercardpictures[0]);
		this.add(dealercardpictures[1]); 
		
		bg = new JLabel();
		bg.setBounds(0, 0, 1000, 700);
		bg.setBackground(pokergreen);
		bg.setVisible(true);  
		bg.setIcon(bagd);		
		this.add(bg);
		
		/* let's display the rules and start the game */
		displayWelcome(); 
		
	}
	
	private ActionListener subEnterButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	String given = answerbox.getText(); //get player's answer
            	given = given.replaceAll("\\s+",""); //get only the digits 
            	
            	try{
            		playerAnswer = Integer.parseInt(given); 
        			answerbox.setText("        "); 
            		subCheckAnswer();
            	} catch (NumberFormatException nfe){ }
            }
        };
		return actionlistener;
	}

	private ActionListener restartButtonActionListner() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	try { 
            		//send information of losing game first 
            		double score = (double)numberOfCorrectAnswers/(double)numberOfAnswers;
            		crossMessage = CrossMessage.gameScoreUpdate(studentName, className, level, "Blackjack", score , studentToken);
            		toServer.println(crossMessage); 
            	}
            	catch (Exception exception) { } 
            	
            		//restart the game
            		numberOfChips = 10;
            		chipsBet = 0;
            		playersHand.clear();
            		numberOfAnswers = 0;
            		numberOfCorrectAnswers = 0;
            		restart.setVisible(false); 
            		chips.setText("Chips: " + numberOfChips);
            		placeBets();

            }
        };
		return actionlistener;
	}

	/* action listener for the freeze button 
	 * sends control to startDealersHand()
	 */
	private ActionListener freezeButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	try {
            		freeze.setVisible(false);
            		hit.setVisible(false);
            		startDealersHand(); 
            	}
            	catch (Exception exception) { }
            }
        };
		return actionlistener;
	}

	/* action listener for the hit button 
	 * sends control to hitMe() 
	 */
	private ActionListener hitButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	try {
            		freeze.setVisible(false);
            		hit.setVisible(false); 
            		hitMe();
            	}
            	catch (Exception exception) { }
            }
        };
		return actionlistener;
	}

	/* action listener for the enter button which is for the player's answer submission  
	 * sends control to checkAnswer() after saving the player's answer 
	 */
	private ActionListener enterButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	String given = answerbox.getText(); //get player's answer
            	given = given.replaceAll("\\s+",""); //get only the digits 
            	
            	try{
            		playerAnswer = Integer.parseInt(given); 
        			answerbox.setText("        "); 
            		checkAnswer();
            	} catch (NumberFormatException nfe){ }
            }
        };
		return actionlistener;
	}

	/* action listener for the bet button which is for the player's bet submission
	 * sends control to displayQuestion() if it is a valid bet 
	 */
	private ActionListener betButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	String given = answerbox.getText(); //get player's answer
            	given = given.replaceAll("\\s+",""); //get only the digits 
            	
            	try{
            		chipsBet = Integer.parseInt(given); 
            		if( chipsBet < 1 || chipsBet > numberOfChips ) //make sure it is a valid bet 
            		{
            			message.setText("You must bet at least one chip but not more than what you have. Try Again. \r\nCurrent Chips: " + numberOfChips);
            			answerbox.setText("        "); 
            		}
            		else
            		{
            			answerbox.setText("        "); 
            			bet.setVisible(false);
            			message.setVisible(false);
            			enter.setVisible(true);
            			question.setVisible(true); 
            			displayQuestion();
            		}
            		
            	} catch (NumberFormatException nfe){ }
            }
        };
		return actionlistener;
	}
	
	/* action listener for the okay button 
	 * sends control to placeBets() 
	 */
	private ActionListener okayButtonActionListener() {
		ActionListener actionlistener = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				try {
					
					placeBets();
					okay.setVisible(false); 
					for(int i = 0; i < playercardpictures.length; i++)
						playercardpictures[i].setVisible(false);
					
				} catch (Exception exception) { }
			}
		};
		return actionlistener; 
	}
	 
	/* Tells player how many chips they have and asks for bet until they have a valid bet */
	public void placeBets()
	{
		question.setVisible(false);
		message.setVisible(true);
		bet.setVisible(true);
		yes.setVisible(false);
		if(notPassed)
			message.setText("You currently have " + numberOfChips + " chips and you need to 20 to win. How many chips would you like to bet? "); 
		else
			message.setText("You currently have " + numberOfChips + " chips. How many chips would you like to bet? ");
		answerbox.setVisible(true);	
		chips.setVisible(true); 
		
		for(int i = 0; i < playercardpictures.length; i++)
			playercardpictures[i].setVisible(false);
	}
	
	/* where they questions will be asked */ 
	public void displayQuestion()
	{
		message.setVisible(true);
		question.setVisible(true);
		answerbox.setVisible(true);
		enter.setVisible(true);
		
		/* Create the deck of cards */
		deck = new Deck();
		deck.shuffle();
		
		/* Create the players hand deal the first two cards */ 
		playersHand = new Hand(); 
		playersHand.clear(); 
		playersHand.addCard(deck.dealCard()); 
		playersHand.addCard(deck.dealCard());  
		
		message.setText("You were dealt a" + playersHand.getCard(0).toString() + " and a" + playersHand.getCard(1).toString() + ". ");
		question.setText("What is " + playersHand.getCard(0).getValue() + " + " + playersHand.getCard(1).getValue() + " ?"); 
		
		correctAnswer = playersHand.getSum(); //this is with ace = 1 
		
		/* displaying cards */ 
		playercardpictures[0].setVisible(true);
		playercards[0] = new ImageIcon( draw.get( playersHand.getCard(0).getStringValue(), playersHand.getCard(0).getCharSuit() ) ); 
		playercardpictures[0].setIcon(playercards[0]); 
		
		playercardpictures[1].setVisible(true);
		playercards[1] = new ImageIcon( draw.get( playersHand.getCard(1).getStringValue(), playersHand.getCard(1).getCharSuit() ) ); 
		playercardpictures[1].setIcon(playercards[1]);

	}
	
	/* check the player's answer correctness */
	public void checkAnswer()
	{
		question.setVisible(false);
		enter.setVisible(false);
		answerbox.setVisible(false);
		
		numberOfAnswers++;
		
		if(correctAnswer == playerAnswer)
		{
			numberOfCorrectAnswers++;
			displayCorrect();
		}
		else
		{
			displayWrong();
		}
	}
	
	/* check the player's answer correctness for subtraction questions */
	public void subCheckAnswer()
	{
		question.setVisible(false);
		subEnter.setVisible(false);
		answerbox.setVisible(false);
		subContinue.setVisible(true);
		
		numberOfAnswers++;
		
		if(subCorrectAnswer == playerAnswer)
		{
			numberOfCorrectAnswers++; 
			message.setText("Correct! Now let's move on! ");
			
		}
		else
		{
			message.setText("Bummer! You would actually want a card with a value of " + subCorrectAnswer + ". \r\n"
					+ "Now let's move on! (don't worry, you didn't lose any chips for this) ");
		}
		
	}
	
	/* if there is an ace, ask if they want it to be 1 or 11 */
	public void askforprefferedvalueoface()
	{
		//TODO 
		hitOrfreeze();
	}
	
	/* says bummer */ 
	public void displayWrong()
	{
		message.setVisible(true);
		message.setText("Bummer! The correct answer is " + correctAnswer + ". You just lost " + chipsBet + " chips! "); 
		numberOfChips -= chipsBet;
		chipsBet = 0; 
		
		chips.setText("Chips: " + numberOfChips);
		
		advance.setVisible(true);
	}
	
	/* says correct and sends control to the right place according to the sum of the hand */
	public void displayCorrect()
	{
		message.setVisible(true); 
		question.setVisible(false);

		if( correctAnswer > 21)
		{
			message.setText("Correct! \r\n\r\nBummer! You went over 21! You lose " + chipsBet + " chips!");
			numberOfChips -= chipsBet;
			chipsBet = 0;
			chips.setText("Chips: " + numberOfChips);
			advance.setVisible(true);
		}
		else if( correctAnswer == 21)
		{
			message.setText("Correct! AND you got 21! You win " + chipsBet + " chips!"); 
			numberOfChips += chipsBet;
			chipsBet = 0;
			chips.setText("Chips: " + numberOfChips); 
			advance.setVisible(true);
		}
		else
		{
			if(!playersHand.isThereAnAce())
			{
				hitOrfreeze();
			}
			else
			{
				askforprefferedvalueoface();
			}
		}
		
	}
	
	/* checks if they have passed the game or lost or neither */
	public void checkChips()
	{ 
		message.setVisible(true);
		advance.setVisible(false); 
		question.setVisible(false);
		
		if(numberOfChips == 0)
		{
			message.setText("Bummer! You have lost all of your chips! You have officially lost the game! \r\nGame Over. "); 
			restart.setVisible(true); 
			
			for(int i = 0; i < playercardpictures.length; i++)
				playercardpictures[i].setVisible(false);
		}
		else if(numberOfChips >= 20 && notPassed) 
		{
			message.setText("Congratulations! You have won the game! Would you like to keep playing? \r\nExit if not."); 
			notPassed = false; 
			yes.setVisible(true); 
			
			for(int i = 0; i < playercardpictures.length; i++)
				playercardpictures[i].setVisible(false);
		}
		else
		{
			bet.setVisible(true);
			answerbox.setVisible(true);
			placeBets();
		}
	}
	
	/* ask them if they want to hit or freeze */
	public void hitOrfreeze()
	{
		if( subtraction && !passedSubQ )
		{
			question.setVisible(true);
			message.setVisible(true); 
			answerbox.setVisible(true);
			subEnter.setVisible(true);
			
			message.setText("Correct! Now, you want to get a total of 21. \r\nYou have a current total of " + playersHand.getSum() + ". \r\n"
					+ "What is the value of the best card you can get? "); 
			
			question.setText("What is 21 - " + playersHand.getSum() + " ? "); 
			
			subCorrectAnswer = 21 - playersHand.getSum(); 
		}
		else if(passedSubQ)
		{
			question.setVisible(true);
			message.setVisible(true); 
			question.setText("Your hand total is " + playersHand.getSum() + ". ");
			message.setText("Would you like to hit and try to get closer to 21 or freeze and see if the dealer beats you? ");
			hit.setVisible(true);
			freeze.setVisible(true); 
			passedSubQ = false;
		}
		else
		{
			question.setVisible(true);
			message.setVisible(true); 
			question.setText("Your hand total is " + playersHand.getSum() + ". ");
			message.setText("Correct! \r\nWould you like to hit and try to get closer to 21 or freeze and see if the dealer beats you? ");
			hit.setVisible(true);
			freeze.setVisible(true); 
			passedSubQ = false;
		}
		
	}
	
	/* Welcome the player to the game with some basic rules */ 
	public void displayWelcome() 
	{
		if(level == 3 || level == 8 || level == 9)
		{
			String intro = "Welcome to BlackJack! \r\nStarting with 10 chips, you must bet chips and get the answers correct to get 20 chips and pass the game. "
					+ "Answer the math questions and try to get the cards to add to a total of 21. Don't go over 21 or you will lose! " ; 
			message.setText(intro); 
			message.setVisible(true);
			okay.setVisible(true); 
			
			/* first testing out pictures, but kinda liked it so imma keep it */
			for(int i = 0; i < playercardpictures.length; i++)
			{
				playercardpictures[i].setVisible(true);
				
				playercards[i] = new ImageIcon( draw.get("a", 's') ); 
				playercardpictures[i].setIcon(playercards[i]); 			
			}	
			
			/* practicing dealer cards 
			for(int j = 0; j < dealercardpictures.length; j++)
			{
				dealercardpictures[j].setVisible(true);
				
				dealercards[j] = new ImageIcon ( draw.get("a", 'h')); 
				Image image = dealercards[j].getImage().getScaledInstance(73, 107, Image.SCALE_DEFAULT);
				dealercards[j] = new ImageIcon( image );
				dealercardpictures[j].setIcon(dealercards[j]); 
			}
			*/
		}
		else
		{
			message.setText("BlackJack is not available for the level you selected. Please exit and try another level. ");
			message.setVisible(true);
			okay.setVisible(false);
		}
	}
	
	/* this begins the dealers hand which will only deal two cards regardless of totaling 17 or higher */
	public void startDealersHand()
	{
		message.setVisible(true);
		question.setVisible(true);
		answerbox.setVisible(false);
		enter.setVisible(false);
		hit.setVisible(false);
		freeze.setVisible(false);
		
		dealersHand = new Hand();
		dealersHand.clear();
		dealersHand.addCard(deck.dealCard());
		dealersHand.addCard(deck.dealCard()); 
		
		message.setText("The dealer got a" + dealersHand.getCard(0).toString() + " \r\nand a" + dealersHand.getCard(1).toString() 
						 + " \r\ngiving them a total of " + dealersHand.getSum() + ". "); 
		
		advance.setVisible(true); 
		
		for(int j = 0; j < dealercardpictures.length; j++)
		{
			dealercardpictures[j].setVisible(true);
			
			dealercards[j] = new ImageIcon ( draw.get( dealersHand.getCard(j).getStringValue(), dealersHand.getCard(j).getCharSuit() ) ); 
			Image image = dealercards[j].getImage().getScaledInstance(73, 107, Image.SCALE_DEFAULT);
			dealercards[j] = new ImageIcon( image );
			dealercardpictures[j].setIcon(dealercards[j]); 
		}	
		
		if(playersHand.getSum() > dealersHand.getSum())
		{
			question.setText("You were closer to 21, so you win!");
			numberOfChips += chipsBet;
			chipsBet = 0; 
			chips.setText("Chips: " + numberOfChips); 
		}
		else if(playersHand.getSum() < dealersHand.getSum() )
		{
			question.setText("Bummer! The dealer was closer to 21 than you, so you lose!");
			numberOfChips -= chipsBet;
			chipsBet = 0;
			chips.setText("Chips: " + numberOfChips); 
		}
		else //they tied
		{
			question.setText("You tied with the dealer, so you win!"); 
			numberOfChips += chipsBet;
			chipsBet = 0; 
			chips.setText("Chips: " + numberOfChips); 
		}
	} 
	
	/* asks the player if what the addition of their previous total plus a new card is */
	public void hitMe()
	{ 
		question.setVisible(true);
		hit.setVisible(false);
		freeze.setVisible(false);
		
		int sumOfhandBefore = playersHand.getSum(); 
		int numberOfCardsinHandBefore = playersHand.size(); 
		
		playersHand.addCard(deck.dealCard());
		
		message.setText("Your hand total is " + sumOfhandBefore + " \r\nHope you don't go over 21! \r\nYou were dealt a"
							+ playersHand.getCard(numberOfCardsinHandBefore).toString() + ". " ); 
		
		question.setText("What is " + sumOfhandBefore + " + " + playersHand.getCard(numberOfCardsinHandBefore).getValue() + " ?"); 
		
		correctAnswer = playersHand.getSum(); 
		
		answerbox.setVisible(true);
		enter.setVisible(true); 
		
		/* display next card */ 
		if(numberOfCardsinHandBefore <= playercardpictures.length)
		{
			playercardpictures[numberOfCardsinHandBefore].setVisible(true);
			playercards[numberOfCardsinHandBefore] = new ImageIcon( draw.get( playersHand.getCard(numberOfCardsinHandBefore).getStringValue(), playersHand.getCard(numberOfCardsinHandBefore).getCharSuit() ) ); 
			playercardpictures[numberOfCardsinHandBefore].setIcon(playercards[numberOfCardsinHandBefore]);
		}

	}
	
	/* send score and return to game select screen */
	public void endGame()
	{
		double score = (double)numberOfCorrectAnswers/(double)numberOfAnswers;
		crossMessage = CrossMessage.gameScoreUpdate(studentName, className, level, "Blackjack", score , studentToken);
		toServer.println(crossMessage); 
		
		CrossMessage result = serverResponses.next();
		if (result.isErrorific()) {
			System.err.println("Oopsies:");
			System.err.println(result);
			
		} else {		}
  
		appcontainer.setTitle("Matherator Extreme : Pretty Princess Edition" );
		appcontainer.setView(new GameSelect(appcontainer, className, studentName, studentToken));
	}
	
} //end of BlackJack 
