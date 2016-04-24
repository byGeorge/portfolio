package studentapp.matheratorship;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;  //notice javax
import javax.swing.border.EmptyBorder;

import common.Img;

import studentapp.MatheratorStudent;
import studentapp.GameSelect;
import studentapp.QuestionatorExtreme;
import studentapp.StudentClient;

public class MatheratorshipGUI extends JPanel implements ActionListener{
	private String 			teacherName; 	// this should be obvious
	private String			studentName; 	// ... yeah, this too
	private MatheratorStudent 	con; 			// The container passed down from previous methods
	private int 			lvl; 			// the level of the game
	private String			tokenImage; 	// represents the login token
	private	StudentClient	client; 		// because it is now non-static
	private JButton			exit 			= new JButton("Exit");
	private Matheratorship 	game 			= new Matheratorship();
	private Image 			unknown, fire, hit, miss, water, lost, won;
	private QuestionatorExtreme question; 	// math question
	private JLabel 			problem; 		// math problems
	private JTextField 		answer			= new JTextField(10); // answer entry
	
	// confirmation button
	private JButton			check			= new JButton("Check answer!");
	
	// centre board for game
	private JPanel 			board	 		= new JPanel(new GridLayout(10, 10, 2, 2));
	private JPanel			fireAtWill		= new JPanel();
	
	// bottom board ...where math problems and messages go
	private JPanel			math 			= new JPanel();
	
	// top panel where the exit ... button ... goes ...
	private JPanel			top				= new JPanel();
	
	private JButton[][]		tiles 			= new JButton[10][10];
	private JLabel			message			= new JLabel("First, MATH: ");
	private Color			tan				= new Color(255, 255, 204);
	private Color			blue			= new Color(140, 170, 200);
	private int				count			= 0; // counts number of turns

	/* state codes: 
	 * 0 = Asking... a math question
	 * 1 = player is attacking computer
	 * 2 = computer is attacking player
	 * 3 = ...player has finished attacking
	 */
	private int				state			= 0;
	
	public MatheratorshipGUI(MatheratorStudent apc, String teacher, String student, int level, String token, StudentClient clnt){
		teacherName = teacher;
		studentName = student;
		con = apc;
		lvl = level;
		tokenImage = token;
		client = clnt;
		question = new QuestionatorExtreme(lvl);
		problem = new JLabel(question.getQuestion());
		
		// set up layouts
		this.setBackground(blue);
		this.setLayout(new BorderLayout(10,10));
		FlowLayout right = new FlowLayout();
		right.setAlignment(FlowLayout.RIGHT);
		top.setLayout(right);
		
		math.setBackground(blue);
		math.setLayout(new FlowLayout());
		fireAtWill.setBackground(blue);
		top.setBackground(blue);
		
		// add stuff

		math.add(message);
		math.add(problem);
		math.add(answer);
		math.add(check);
		top.add(exit);
		this.add(fireAtWill, BorderLayout.CENTER);
		this.add(math, BorderLayout.SOUTH);
		this.add(top, BorderLayout.NORTH);

		
		// add some listeners
		exit.addActionListener(this);
		check.addActionListener(this);
		answer.addActionListener(this);
		
		// graphics!
		try{
			unknown = ImageIO.read(Img.get("/studentapp/matheratorship/unknown.jpg"));
			hit = ImageIO.read(Img.get("/studentapp/matheratorship/hit.png"));
			water = ImageIO.read(Img.get("/studentapp/matheratorship/water.jpg"));
			lost = ImageIO.read(Img.get("/studentapp/matheratorship/lost.jpg"));
			won = ImageIO.read(Img.get("/studentapp/matheratorship/won.jpg"));
			miss = ImageIO.read(Img.get("/studentapp/matheratorship/miss.png"));
			fire = ImageIO.read(Img.get("/studentapp/matheratorship/fire.jpg"));
		}
		catch (Exception e){
			System.out.println("Error: could not find image file");
		}
		
		// initialise buttons and add listeners
		for (int i = 0; i<10; i++){
			for (int j = 0; j<10; j++){
				tiles[i][j] = new JButton();
				board.add(tiles[i][j]);
				tiles[i][j].addActionListener(this);
				tiles[i][j].setContentAreaFilled(false);
			}
		}
		
		// making images fit nicely
		board.setBorder(new EmptyBorder(10, 210, 10, 210));
		unknown = unknown.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		hit = hit.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		water = water.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		miss = miss.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		fireAtWill.add(new JLabel(new ImageIcon(fire)));
		repaint();
	}

	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		if (source == answer){
			// this makes... pressing "enter" work in the text field
			source = check;
		}
		if (source == exit) {
			// leaves the game
			con.setView(new GameSelect(con, teacherName, studentName, tokenImage));
		}
		else if (source == check){
			if (state == 0){
				int guess = -1;
				try {
					guess = Integer.parseInt(answer.getText());
				}
				catch (Exception e){
				message.setText("That wasn't a number!");
				}
				if (guess == question.getAnswer()){
					showComputerBoard();
				}
				else{
					showPlayerBoard();
				}
			}
			else if (state == 3){
				if (!game.gameWon()) 
					answerMathQuestion();
				else 
					gameEnd();
			}
			else if (state == 2){
				if (!game.gameLost())
					answerMathQuestion();
				else
					gameEnd();
			}
		}
		if (state == 1){
			for (int i = 0; i<10; i++){
				for (int j = 0; j<10; j++){
					if (source == tiles[i][j]){
						int pg = game.makePlayerGuess(i, j);
						if (pg>0){
							for (int a = 0; a<10; a++){
								for (int b = 0; b<10; b++){
									// redraw tiles
									if (game.compBoard[a][b] == 2)
										tiles[a][b].setIcon(new ImageIcon(water));
									else if (game.compBoard[a][b] == 3)
										tiles[a][b].setIcon(new ImageIcon(hit));
									else
										tiles[a][b].setIcon(new ImageIcon(unknown));
								}
							}
							// change messages and state to post-selection messages
							state = 3;
							if (pg == 2){
								message.setText("Sorry, you missed.      ");
								tiles[i][j].setIcon(new ImageIcon(miss));
							}
							else
								message.setText("It's a hit! "+game.checkComputerSunk()+"    ");
							this.repaint();
							math.add(message);
							math.remove(problem);
							check.setText("Continue");
							math.add(check);
							this.repaint();
						}
						else {
							message.setText("You've already attacked that square!");
						}
					}		
				}
			}
		}
	}
	
	
	private void showComputerBoard(){
		state = 1;
		for (int i = 0; i<10; i++){
			for (int j = 0; j<10; j++){
				if (game.compBoard[i][j] == 2)
					tiles[i][j].setIcon(new ImageIcon(water));
				else if (game.compBoard[i][j] == 3)
					tiles[i][j].setIcon(new ImageIcon(hit));
				else
					tiles[i][j].setIcon(new ImageIcon(unknown));
			}
		}
		this.remove(fireAtWill);
		this.add(board, BorderLayout.CENTER);
		math.remove(problem);
		math.remove(answer);
		check.setText("Continue");;
		message.setText("You got it right! Now choose a computer square above to attack!");
		this.repaint();
	}
	
	private void showPlayerBoard(){
		state = 2;
		int test = game.makeComputerGuess();
		for (int i = 0; i<10; i++){
			for (int j = 0; j<10; j++){
				if (game.playerBoard[i][j] == 2)
					tiles[i][j].setIcon(new ImageIcon(miss));
				else if (game.playerBoard[i][j] == 3)
					tiles[i][j].setIcon(new ImageIcon(hit));
				else
					tiles[i][j].setIcon(new ImageIcon(water));
			}
		}
		if (test == 2){
			message.setText("They missed!");
		}
		else if (test == 3){
			message.setText(game.checkPlayerSunk());
		}
		board.setBackground(tan);
		this.setBackground(tan);
		top.setBackground(tan);
		math.setBackground(tan);
		this.remove(fireAtWill);
		this.add(board, BorderLayout.CENTER);
		math.remove(answer);
		math.remove(problem);
		check.setText("Continue");
		this.repaint();
	}
	
	private void answerMathQuestion(){
		question.newQ(lvl);
		this.remove(board);
		this.add(fireAtWill);
		this.setBackground(blue);
		math.setBackground(blue);
		top.setBackground(blue);
		board.setBackground(blue);
		message.setText("Next Problem!");
		math.remove(check);
		math.remove(exit);
		problem.setText(question.getQuestion());
		math.add(problem);
		answer.setText("");
		math.add(answer);
		check.setText("Continue");
		math.add(check);
		this.repaint();
		count++;
		state = 0;
	}
	
	private void gameEnd(){
		// find high score
		double bestScore = (double) count;
		double previous = 0;
		try{
			previous = client.getScore("Matherator Ship", lvl);
		}
		catch (Exception e){
			System.out.println("Unable to retrieve previous score");
		}
		if ((200 - bestScore)/200 > previous){
			try {
				client.updateScore(lvl, "Matherator Ship", (200 - bestScore)/200);
			}
			catch (Exception e){
				System.out.println("Unable to update score");
			}
		}
		
		// now for the layout stuffs:
		JPanel endSplash = new JPanel(new BorderLayout());
		endSplash.setBackground(blue);
		
		// don't need previous JPanels
		this.remove(math);
		this.remove(board);
		
		// add components
		JPanel bottom = new JPanel();
		JLabel text = new JLabel("", JLabel.CENTER);
		JLabel picture;
		JButton playAgain = new JButton("Play again?");
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				con.setView(new GameSelect(con, teacherName, studentName, tokenImage, client));
			}
		});
		playAgain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				con.setView(new MatheratorshipGUI(con, teacherName, studentName, lvl, tokenImage, client));
			}
		});
		
		// logic for lost/won
		if (game.gameWon()){
			text.setText("Congratulations! You won in "+count+" moves! Your high score is "+bestScore);
			picture = new JLabel(new ImageIcon(won));
		}
		else{
			text.setText("Sorry, but the computer won this round... Better luck next time!");
			picture = new JLabel(new ImageIcon(lost));
		}
		
		// finish setting up layout
		bottom.add(playAgain);
		bottom.setBackground(blue);
		bottom.add(exit);
		picture.setBorder(new EmptyBorder(0,0,0,0));
		endSplash.add(picture, BorderLayout.CENTER);
		endSplash.add(text, BorderLayout.NORTH);
		endSplash.add(bottom, BorderLayout.SOUTH);
		con.setView(endSplash);
		
		// aaaaand redraw
		this.repaint();
	}
}
