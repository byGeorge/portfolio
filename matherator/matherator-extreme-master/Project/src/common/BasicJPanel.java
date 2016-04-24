package common;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BasicJPanel extends JPanel{
    private JButton b;
    private JTextField t;
	
	public BasicJPanel(){
        b = new JButton("bla");
        t = new JTextField("blaadda");
        add(b);
        add(t);
        this.setBackground(Color.black);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
