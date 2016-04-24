package studentapp;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;  //notice javax
import javax.swing.border.EmptyBorder;

import common.Img;
import studentapp.blackjack.BlackJack;
import studentapp.kittenx.graphics.KittenX;
import studentapp.matheratorship.MatheratorshipGUI;
import studentapp.monty.MazeOfMontyHalls;

public class GameSelect extends JPanel implements ActionListener{
	private JButton			add, subtract, places, extreme, back, forward, 
		one, two, three, bj, kx, mh, ms;
	private JLabel 			header, message, title, bjcomp, kxcomp, mhcomp, mscomp;
	private JPanel			main, mainHeader, levelType, levelList, 
		fordnback, onetwothree, gameSelect, unavailable;
	private int				level = -1;
	private boolean[]		availableLevels;
	private double[] 		kittenLevelsCompleted;
	private double[]		matheratorLevelsCompleted; 
	private double[]		blackjackLevelsCompleted;
	private double[]		montyHallsCompleted;
	
	// passed variables
	private String 			teacherName = "student";
	private String			studentName = "teacher";
	private String			tokenImage	= "token";
	private MatheratorStudent 	con;
	private StudentClient	client;
	
	private int				asx = 0;
	private GridLayout		griddle = new GridLayout(2, 3, 10, 10); // rows, cols, hgap, vgap

	public GameSelect(MatheratorStudent apc, String teacher, String student, String token){
		setUp(apc, teacher, student, token, new StudentClient(teacher, student, token));
	}
	
	public GameSelect(MatheratorStudent apc, String teacher, String student, String token, StudentClient clnt){
		setUp(apc, teacher, student, token, clnt);
	}
	
	public void setUp(MatheratorStudent apc, String teacher, String student, String token, StudentClient clnt){
		// initialise ALL THE THINGS!
		con = apc;
		teacherName = teacher;
		studentName = student;
		tokenImage = token;
		client = clnt;
		availableLevels = client.getAvailableLevels(teacherName);
		
		// set up GameSelect background
		this.setLayout(new BorderLayout());
		try{
			InputStream logo = Img.get("/studentapp/splash/ppe.png");
			header = new JLabel(new ImageIcon(ImageIO.read(logo)));
			this.add(header, BorderLayout.NORTH);
		}
		catch(Exception e){
			System.out.println("Error: Unable to open header file");
		}
		
		// set up main panel
		main = new JPanel(new BorderLayout());
		main.setBorder(new EmptyBorder(100, 100, 100, 100)); // top, left, bottom, right
		this.add(main, BorderLayout.CENTER);
		mainHeader = new JPanel();
		mainHeader.setBorder(new EmptyBorder(0, 0, 15, 0));// top, left, bottom, right
		mainHeader.setLayout(new GridLayout(2, 1, 0, 10)); // rows, cols, hgap, vgap
		main.add(mainHeader, BorderLayout.NORTH);
		title = new JLabel(studentName + ", welcome to Matherator.", JLabel.CENTER);
		title.setFont(new Font("default", Font.BOLD, 20)); // name, style, pixels
		mainHeader.add(title);
		message = new JLabel("Please choose place values, addition, subtraction, or EXTREME!", JLabel.CENTER);
		mainHeader.add(message);

		// set up choice
		levelType = new JPanel(new GridLayout(1, 4, 10, 10)); // rows, cols, hgap, vgap
		levelType.setBorder(new EmptyBorder(10, 50, 200, 50)); // top, left, bottom, right
		main.add(levelType, BorderLayout.CENTER);
		places = new JButton("Place Values");
		places.setFont(new Font("default", Font.BOLD, 20)); // name, style, pixels
		places.addActionListener(this);
		levelType.add(places);
		add = new JButton("Addition");
		add.setFont(new Font("default", Font.BOLD, 20)); // name, style, pixels
		add.addActionListener(this);
		levelType.add(add);
		subtract = new JButton("Subtraction");
		subtract.setFont(new Font("default", Font.BOLD, 20)); // name, style, pixels
		subtract.addActionListener(this);
		levelType.add(subtract);
		extreme = new JButton("EXTREME!");
		extreme.setFont(new Font("default", Font.BOLD, 20)); // name, style, pixels
		levelType.add(extreme);
		extreme.addActionListener(this);
			
		
		// set up gameSelect panel (although we're not using it yet)
		gameSelect = new JPanel(griddle); // rows, cols, hgap, vgap
		try{
			InputStream assign = Img.get("/studentapp/splash/blackjack-button.png");
			bj = new JButton(new ImageIcon(ImageIO.read(assign)));
			bj.addActionListener(this);
			assign = Img.get("/studentapp/splash/kittenx-button.png");
			kx = new JButton(new ImageIcon(ImageIO.read(assign)));
			gameSelect.add(kx);
			kx.addActionListener(this);
			assign = Img.get("/studentapp/splash/matheratorship-button.png");
			ms = new JButton(new ImageIcon(ImageIO.read(assign)));
			gameSelect.add(ms);
			ms.addActionListener(this);
			assign = Img.get("/studentapp/splash/monty-button.png");
			mh = new JButton(new ImageIcon(ImageIO.read(assign)));
			gameSelect.add(mh);
			mh.addActionListener(this);			
		}
		catch (Exception e){
			System.out.println("Error: Unable to find game image file");
		}
		kittenLevelsCompleted = client.getGamesCompleted("Kitten X");
		matheratorLevelsCompleted = client.getGamesCompleted("Matherator Ship"); 
		blackjackLevelsCompleted = client.getGamesCompleted("BlackJack");
		montyHallsCompleted = client.getGamesCompleted("Maze of Monty Halls");
		bjcomp = new JLabel();
		kxcomp = new JLabel();
		mhcomp = new JLabel();
		mscomp = new JLabel();
		gameSelect.add(kxcomp);
		gameSelect.add(mscomp);
		gameSelect.add(mhcomp);
		
		// forward and back buttons
		levelList = new JPanel(new BorderLayout());
		fordnback = new JPanel(new BorderLayout());
		forward = new JButton("  ");
		forward.addActionListener(this);
		fordnback.add(forward, BorderLayout.EAST);
		back = new JButton("  ");
		back.addActionListener(this);
		fordnback.add(back, BorderLayout.WEST);
		levelList.add(mainHeader, BorderLayout.NORTH);
		levelList.setBorder(new EmptyBorder(100, 200, 10, 200)); // top, left, bottom, right
		
		// level select panel
		onetwothree = new JPanel(new GridLayout(1, 3, 10, 10)); // rows, cols, hgap, vgap
		onetwothree.setBorder(new EmptyBorder(50, 0, 150, 0)); // top, left, bottom, right
		one = new JButton("1");
		two = new JButton("2");
		three = new JButton("3");
		one.setFont(new Font("default", Font.BOLD, 90)); // name, style, pixels
		two.setFont(new Font("default", Font.BOLD, 90)); // name, style, pixels
		three.setFont(new Font("default", Font.BOLD, 90)); // name, style, pixels
		one.addActionListener(this);
		two.addActionListener(this);
		three.addActionListener(this);
		onetwothree.add(one);
		onetwothree.add(two);
		onetwothree.add(three);
		
		// unavailable panel
		unavailable = new JPanel(new BorderLayout());
		JLabel msg = new JLabel("Try another level. This one is unavailable.", JLabel.CENTER);
		unavailable.add(msg, BorderLayout.CENTER);
		
		setVisible(true); // display this frame
		validate();
	}
 
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		if (source == places){
			this.add(levelList, BorderLayout.CENTER);
			levelList.add(fordnback, BorderLayout.SOUTH);
			this.remove(main);
			if (availableLevels[0] == true){
				System.out.println("Available");
				level = 0;
				asx = 0;
				levelList.add(gameSelect, BorderLayout.CENTER);
				forward.setText("Go to: Addition Level 1");
				back.setText("No previous levels!");
				title.setText("Ones, Tens, and Hundreds Level");
				message.setText("Choose a game to begin!");
				setChecks();
				validate();
			}
			else{
				levelList.add(unavailable, BorderLayout.CENTER);
				forward.setText("Go to: Addition Level 1");
				back.setText("No previous levels!");
				title.setText("Ones, Tens, and Hundreds Level");
				message.setText("Choose a game to begin!");
				level = 0;
				asx = 0;
			}
		}
		else if (source == add){
			this.remove(main);
			asx = 1;
			forward.setText("Back");
			this.add(levelList, BorderLayout.CENTER);
			levelList.add(onetwothree, BorderLayout.CENTER);
			title.setText("Addition");
			message.setText("Choose a level");
			validate();
		}
		else if (source == subtract){
			this.remove(main);
			forward.setText("Back");
			asx = 4;
			this.add(levelList, BorderLayout.CENTER);
			levelList.add(onetwothree, BorderLayout.CENTER);
			title.setText("Subtraction");
			message.setText("Choose a level");
			validate();
		}
		else if (source == extreme){
			this.remove(main);
			asx = 7;
			forward.setText("Back");
			this.add(levelList, BorderLayout.CENTER);
			levelList.add(onetwothree, BorderLayout.CENTER);
			title.setText("EXTREME!!!");
			message.setText("Choose a level");
			validate();
		}
		else if (source == one){
			levelList.remove(onetwothree);
			levelList.add(fordnback, BorderLayout.SOUTH);
			level = asx;
			if (level >= 1 && level < 4)
				title.setText("Addition Level " + level);
			else if (level >= 4 && level < 7)
				title.setText("Subtraction Level " + (level-3));
			else
				title.setText("EXTREME! Level "+ (level-6));
			message.setText("Choose a game to begin!");
			forward.setText(nextSuggestedLevel());
			back.setText(previousSuggestedLevel());
			if (availableLevels[level]){ 
				levelList.add(gameSelect, BorderLayout.CENTER);
				setChecks();
			}
			else{ 
				levelList.add(unavailable, BorderLayout.CENTER);
			}
			validate();
		}
		else if (source == two){
			levelList.remove(onetwothree);
			levelList.add(fordnback, BorderLayout.SOUTH);
			level = asx + 1;
			if (level >= 1 && level < 4)
				title.setText("Addition Level " + level);
			else if (level >= 4 && level < 7)
				title.setText("Subtraction Level " + (level-3));
			else
				title.setText("EXTREME! Level "+ (level-6));
			message.setText("Choose a game to begin!");
			forward.setText(nextSuggestedLevel());
			back.setText(previousSuggestedLevel());
			if (availableLevels[level]){ 
				levelList.add(gameSelect, BorderLayout.CENTER);
				setChecks();
			} 
			else{
				levelList.add(unavailable, BorderLayout.CENTER);
			}
			validate();
		}
		else if (source == three){
			levelList.remove(onetwothree);
			level = asx + 2;
			levelList.add(gameSelect, BorderLayout.CENTER);
			levelList.add(fordnback, BorderLayout.SOUTH);
			this.add(levelList, BorderLayout.CENTER);
			if (level >= 1 && level < 4)
				title.setText("Addition Level " + level);
			else if (level >= 4 && level < 7)
				title.setText("Subtraction Level " + (level-3));
			else
				title.setText("EXTREME! Level "+ (level-6));
			message.setText("Choose a game to begin!");
			forward.setText(nextSuggestedLevel());
			back.setText(previousSuggestedLevel());
			if (availableLevels[level]){
				levelList.add(gameSelect, BorderLayout.CENTER);
				griddle.setColumns(4);
				gameSelect.add(bj);
				// make sure all components are in the right place
				gameSelect.remove(kxcomp);
				gameSelect.remove(mscomp);
				gameSelect.remove(mhcomp);
				gameSelect.add(kxcomp);
				gameSelect.add(mhcomp);
				gameSelect.add(mscomp);
				gameSelect.add(bjcomp);
				setChecks();
			}
			else{
				levelList.add(unavailable, BorderLayout.CENTER);
			}
			validate();
		}
		else if (source == bj){
			client.close();
			con.setView(new BlackJack(con, studentName, teacherName, level, tokenImage));
		}
		else if (source == kx){
			client.close();
			con.setView(new KittenX(con, studentName, teacherName, level, tokenImage));
		}
		else if (source == ms){
			con.setView(new MatheratorshipGUI(con, teacherName, studentName, level, tokenImage, client));
		}
		else if (source == mh){
			client.close();
			con.setView(new MazeOfMontyHalls(con, studentName, teacherName, level, tokenImage));
		}
		else if (source == forward){
			level = chooseNext();
			if (level >= 1 && level < 4)
				title.setText("Addition Level " + level);
			else if (level >= 4 && level < 7)
				title.setText("Subtraction Level " + (level-3));
			else
				title.setText("EXTREME! Level "+ (level-6));
			forward.setText(nextSuggestedLevel());
			back.setText(previousSuggestedLevel());
			if (availableLevels[level]){
				levelList.remove(unavailable);
				levelList.add(gameSelect, BorderLayout.CENTER);
			}
			else {
				levelList.remove(gameSelect);
				levelList.add(unavailable, BorderLayout.CENTER);
			}
			if ((availableLevels[level] && level == 3 )|| (availableLevels[level] && level == 6) 
					|| (availableLevels[level] && level == 9)){
				if (griddle.getColumns() == 3){
					griddle.setColumns(4);
					gameSelect.add(bj);
					// make sure all components are in the right place
					gameSelect.remove(kxcomp);
					gameSelect.remove(mscomp);
					gameSelect.remove(mhcomp);
					gameSelect.add(kxcomp);
					gameSelect.add(mhcomp);
					gameSelect.add(mscomp);
					gameSelect.add(bjcomp);
				}
			}
			else {
				if (griddle.getColumns() == 4){
					gameSelect.remove(bj);
					gameSelect.remove(bjcomp);
					griddle.setColumns(3);
				}
			}
			setChecks();
			validate();
			repaint();
		}
		else if (source == back){
			level = choosePrevious();
			if (level == 0){
				title.setText("Ones, Tens, and Hundreds Level");
				asx = 0;
			}
			else if (level >= 1 && level < 4){
				asx = 1;
				title.setText("Addition Level " + level);
			}
			else if (level >= 4 && level < 7){
				asx = 4;
				title.setText("Subtraction Level " + (level-3));
			}
			else{
				title.setText("EXTREME! Level "+ (level-6));
				asx = 7;
			}
			forward.setText(nextSuggestedLevel());
			back.setText(previousSuggestedLevel());
			if (availableLevels[level]){
				levelList.remove(unavailable);
				levelList.add(gameSelect, BorderLayout.CENTER);
			}
			else {
				levelList.remove(gameSelect);
				levelList.add(unavailable, BorderLayout.CENTER);
			}
			if ((availableLevels[level] && level == 3 )|| (availableLevels[level] && level == 8) 
					|| (availableLevels[level] && level == 9)){
				if (griddle.getColumns() == 3){
					griddle.setColumns(4);
					gameSelect.add(bj);
					// make sure all components are in the right place
					gameSelect.remove(kxcomp);
					gameSelect.remove(mscomp);
					gameSelect.remove(mhcomp);
					gameSelect.add(kxcomp);
					gameSelect.add(mhcomp);
					gameSelect.add(mscomp);
					gameSelect.add(bjcomp);
				}
			}
			else {
				if (griddle.getColumns() == 4){
					gameSelect.remove(bj);
					gameSelect.remove(bjcomp);
					griddle.setColumns(3);
				}
			}
			setChecks();
			validate();
			repaint();
		}
	}
	
	private String nextSuggestedLevel(){
		if (level >= 1 && level < 4)
			return("Go to: Subtraction Level " + (level));
		else if (level >= 4 && level < 7)
			return("Go to: EXTREME level " + (level - 3));
		else if (level >= 7 && level < 9)
			return("Go to: Addition Level " + (level - 5));
		else if (level == 0)
			return("Go to: Addition Level 1");
		else {
			return("No more levels!");
		}
	}
	
	private String previousSuggestedLevel(){
		if (level >= 2 && level < 4)
			return("Go to: EXTREME Level " + (level - 1));
		else if (level >= 4 && level < 7)
			return("Go to: Addition level " + (level - 3));
		else if (level >= 7 && level <= 9)
			return("Go to: Subtraction Level " + (level - 6));
		else if (level == 1)
			return("Go to: Ones, Tens, and Hundreds Level");
		else {
			return("No previous levels!");
		}
	}
	
	private int chooseNext(){
		if (level == 0)
			return 1;
		if (level >= 1 && level < 7)
			return(level + 3);
		else if (level >= 7 && level < 9)
			return (level - 5);
		else {
			return 9;
		}
	}
	
	private int choosePrevious(){
		if (level >= 4)
			return (level - 3);
		else if (level > 1 && level < 4)
			return (level + 5);
		else
			return 0;
	}
	
	private void setChecks(){
		kxcomp.setText("");
		kxcomp.setHorizontalAlignment( SwingConstants.CENTER ); 
		mscomp.setText("");
		mscomp.setHorizontalAlignment( SwingConstants.CENTER ); 
		bjcomp.setText("");
		bjcomp.setHorizontalAlignment( SwingConstants.CENTER ); 
		mhcomp.setText("");
		mhcomp.setHorizontalAlignment( SwingConstants.CENTER ); 
		for (int i = 0; i < kittenLevelsCompleted.length; i++){
			if (kittenLevelsCompleted[level] > 0)
				kxcomp.setText("X");
		}
		for (int i = 0; i < matheratorLevelsCompleted.length; i++){
			if (matheratorLevelsCompleted[level] > 0)
				mscomp.setText("X");
		}
		for (int i = 0; i < blackjackLevelsCompleted.length; i++){
			if (blackjackLevelsCompleted[level] > 0)
				bjcomp.setText("X");
		}
		for (int i = 0; i < montyHallsCompleted.length; i++){
			if (montyHallsCompleted[level] > 0)
				mhcomp.setText("X");
		}
	}
}

