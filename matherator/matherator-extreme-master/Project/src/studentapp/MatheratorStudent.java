package studentapp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import studentapp.kittenx.graphics.KittenX;
import teacherapp.TeacherLogin;

/**
 * This Program is built to house all of the guis that will be stripped ... down ...
 * for now and updated later.
 * 
 * @author TAZ, The Feckless Ellipses!
 *
 */
public class MatheratorStudent extends JFrame{
	/**Constructor for MatheratorStudent*/
	public MatheratorStudent(){
	    //Don't create a new JFrame, you're already creating one!
	    
		this.setTitle("Matherator Extreme");
		
		//sets location on start up on screen.
		setLocation(200,30);
		
		//sets the size of the Jframe
	    setSize(1000, 700);
	    setResizable(false);
	    
	    //Needed so the X shuts down the program, not just the JFrame.
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    //This just starts up the JPanelTest Class which is the current default. Change in JPanel for you.
	    add(new StudentLogin(this));
	    //add(new KittenX(this, "","",1));
	    
	    /** Will set up other */
	    //Sets all contents visible.
	    setVisible(true);
	}
	/**Simply starts up the MatheratorStudent()*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable()
		  {
		      public void run()
		      {
		          new MatheratorStudent();
		      }
		  });
	}
	
	/** This class is used to update JPanels.
	 * If needed, will change. But UI's should fit.
	 * @param jc
	 */
	public void setView(JComponent jc){
		this.getContentPane().removeAll();
		this.getContentPane().add(jc, BorderLayout.CENTER);
		this.getContentPane().validate();
	}

}

