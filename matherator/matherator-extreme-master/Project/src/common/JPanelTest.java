package common;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import teacherapp.TeacherGUI;
import studentapp.MatheratorStudent;
import studentapp.kittenx.graphics.*;

/** Please let me know if this doesn't make sense...
 * 
 * @author TAZ
 *
 */
public class JPanelTest extends JPanel {
	/**
	 * You need this as to not instantiate a new MatheratorStudent..
	 */
	MatheratorStudent apc;
	
	/** Constructor must get MatheratorStudent arg to make it work.
	 * Must Call Super();
	 * Set your apc variable to your MatheratorStudent arg input.
	 * Putting in the UpdateView Method is the most efficient way to do this. 
	 * **This way when you make updates to your JPanel you can just call updateView And it repaints it.*/
	public JPanelTest(MatheratorStudent app){
		super();
		JButton b = new JButton("*Switch to Your JPanel*");
		b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Calls Method below to make call up to MatheratorStudent Parent... Look below to test yours.
            	updateView();
            }
        });
		
        add(b);
		apc = app;
        this.setBackground(Color.red);
        
        
	}
	
	public void updateView(){
		/**Call the constructor of your class here. Replace new BasicJPanel()...
		 * Make sure your class extends JPanel to make this work.
		 */
		apc.setView(new BasicJPanel());
		//apc.setView(new KittenX(apc));
	}
	public static void main(String[] args) {
		
	}
	
}
