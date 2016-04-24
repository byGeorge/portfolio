package studentapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.imageio.ImageIO;

import common.Img;
import common.Konstants.StudentToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class StudentLogin extends JPanel implements ActionListener{
	private JList<String>	classSelect;
	private JList<String>	studentSelect = new JList<String>();
	private JButton 		logIn = new JButton("Log in");
	private JButton[]		tokens	= new JButton[20];
	private String[]		table;
	private JLabel			message	= new JLabel("Please select your class, then your name.", JLabel.CENTER);
	private JLabel			header, cat;
	private MatheratorStudent 	con;
	private StudentClient	client	= new StudentClient();
	private String			teacher	= "teacher";
	private String			student	= "student";
	private String			token	= "token";
	
	private JPanel 			first, kitty, divided, t, s, second;
	private int 			state = 0;

	/**
	 * StudentLogin is a JPanel that will help the student log in. When login is complete
	 * the variable "ready" will return true so StudentGUI knows student is logged in.
	 */
	public StudentLogin(MatheratorStudent apc) {
		// setup all the things! Okay, some of the things. One of the things.
		con = apc;

		// Database query for class list
		classSelect = new JList<String>(client.getClassList());
		
		// format and add pictures to outermost layer
		setLayout(new BorderLayout());
		try{
			InputStream logo = Img.get("/studentapp/splash/ppe.png");
			header = new JLabel(new ImageIcon(ImageIO.read(logo)));
			this.add(header, BorderLayout.NORTH);
			InputStream kitten = Img.get("/studentapp/splash/kitten-sideways.png");
			cat = new JLabel(new ImageIcon(ImageIO.read(kitten)), JLabel.LEFT);
			kitty = new JPanel();
			this.add(kitty, BorderLayout.EAST);
			kitty.add(cat);
			kitty.setBorder(new EmptyBorder(150, 0, 0, 150));// top, left, bottom, right
		}
		catch(Exception e){
			System.out.println("Error: Unable to open picture file");
		}

		// Next layer in (named first because it's the first interior panel they see)
		first = new JPanel(new BorderLayout());
		this.add(first);
		first.add(message, BorderLayout.NORTH);
		message.setBorder(new EmptyBorder(0, 0, 10, 0)); // top, left, bottom, right
		JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		loginPanel.add(logIn);
		first.add(loginPanel, BorderLayout.SOUTH);
		first.setBorder(new EmptyBorder(100, 275, 175, 0)); // top, left, bottom, right
		
		// First screen's interior panel
		divided = new JPanel(new GridLayout(1, 3, 10, 10)); // rows, cols, hgap, vgap
		first.add(divided, BorderLayout.CENTER);
		
		// need two more JPanels to keep the space ... spacey
		t = new JPanel();
		s = new JPanel();
		divided.add(t);
		divided.add(s);
		t.add(classSelect);
		t.setBackground(Color.WHITE);
		s.setBackground(Color.WHITE);

		// action listener for Class select list
		classSelect.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()){
					teacher = (String) classSelect.getSelectedValue();
					if (s.getComponentCount() == 1){
						String[] none = new String[0]; 
						studentSelect.setListData(none);
						s.remove(studentSelect);
					}
					try {
						studentSelect.setListData(client.getStudentList(teacher));
						showStudentSelect();
					}
					catch (IOException e1) {
						System.out.println("Error: Could not show student list.");
					}
					studentSelect.setAlignmentX(CENTER_ALIGNMENT);
					message.setText("Please select your name");
					s.add(studentSelect);
				}
				repaint();
			}
		});

		// add action listener for login button
		logIn.addActionListener(this);
		setVisible(true);
	}

	private void showStudentSelect(){
		try{
			studentSelect = new JList<String>(client.getStudentList(teacher));
		}
		catch (IOException e){
			System.out.println("Error: Could not find student list");
		}
		studentSelect.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()){
					student = (String) studentSelect.getSelectedValue();
					message.setText("Click the Log in button!");
					state = 1;
				}
			}
		});
		setVisible(true);
	}
	
	private void showTokens(){
		this.remove(first);
		this.remove(kitty);
		second = new JPanel(new BorderLayout());
		JPanel secondCentre = new JPanel(new GridLayout(4, 5, 10, 10));
		this.add(second);
		second.add(secondCentre, BorderLayout.CENTER);
		JPanel messPan = new JPanel();
		messPan.setBorder(new EmptyBorder(0,0,20,0));
		JLabel newMess = new JLabel("Welcome " + student + "! Please click on your login token:", JLabel.CENTER);
		messPan.add(newMess);
		second.add(messPan, BorderLayout.NORTH);
		second.setBorder(new EmptyBorder(100, 325, 150, 325)); // top, left, bottom, right
		try {
			List<JButton> tokenButtons = new ArrayList<JButton>();
			table = new String[20];

			int i = 0;
			for (StudentToken token : StudentToken.values()) {
				String filename = String.format("/studentapp/tokens/%s.png", token.name());
				JButton newButton = new JButton(new ImageIcon(ImageIO.read(Img.get(filename))));
				tokenButtons.add(newButton);

				secondCentre.add(newButton);
				table[i] = token.name();

				i++;
			}

			tokens = new JButton[tokenButtons.size()];
			for (int h = 0; h < tokenButtons.size(); h++)
				tokens[h] = tokenButtons.get(h);

		}
		catch (Exception e) {
			System.out.println("Error: could not show tokens");
		}
		
		// add action listener for tokens
		for (int i = 0; i < tokens.length; i++){
			tokens[i].addActionListener(this);
		}
		this.validate();
	}

	/**
	 * Mouse listener for login button
	 */
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if (source == logIn && state == 1){
			showTokens();
			repaint();
		}
		else{
			for (int i = 0; i < tokens.length; i++){
				if (source == tokens[i]){
					token = table[i];
					passOrFailLogin();
				}
			}
		}
	}
	
	private void passOrFailLogin(){
		if (validateLogin())
			con.setView(new GameSelect(con, teacher, student, token));
		else{
			System.out.println("failed to log in");
			this.remove(second);
			JPanel fail = new JPanel();
			this.add(fail, BorderLayout.CENTER);
			fail.setBackground(Color.RED);
			JLabel warning = new JLabel("Login failed! Please contact your teacher!");
			fail.add(warning);
			this.validate();
		}
	}

	/**
	 * checks login button
	 */
	private boolean validateLogin() {
		if (!teacher.equals("teacher") && !student.equals("student") 
				&& !token.equals("token")){
			return client.verify(teacher, student, token);
		}
		return false;
	}
}
