package teacherapp;

import java.awt.event.*;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;  //notice javax
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import common.CrossMessage;
import common.Konstants;

@SuppressWarnings("serial")
public class TeacherLogin extends JPanel 
{
	private Color betterPink   = new Color(255, 130, 171);
	private int width; 
	private int height; 
	
	//Added.
	private MatheratorTeacher tapc;
	private String teacherName;
	private String teacherPass="";
	//private JTextField usernameField;
	private JPasswordField passwordField;
	
	CrossMessage cm;
	Socket extremeServer;
	PrintWriter toServer;
	static Iterator<CrossMessage> 	responses;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TeacherLogin(MatheratorTeacher matheratorTeacher) 
	{ 
		try{
			extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
			toServer = new PrintWriter( extremeServer.getOutputStream(), true );
		}catch(IOException ioe){}
		
		tapc = matheratorTeacher;
		
		width = tapc.getSize().width;
		height = tapc.getBounds().height;
		
		
		setBackground(betterPink);
		setLayout(null);
		
		/** Getting teacher list section*/
		cm = CrossMessage.teacherListRequest();
		toServer.println(cm);
		try {responses = CrossMessage.messagesFrom(extremeServer.getInputStream()).iterator();} catch (IOException e1) {}
		CrossMessage mesg = responses.next();
		List<String> mes = mesg.teacherNames();
		
		//Replace with clickable list of teachers.
		DefaultListModel dlm = new DefaultListModel();
		for(int i = 0; i< mes.size(); i++)
			dlm.addElement(mes.get(i));
		final JList<String> clickableTeachers = new JList<String>(dlm);
		clickableTeachers.addListSelectionListener(new ListSelectionListener()
				{
					@Override
					public void valueChanged(ListSelectionEvent lse) {
						if (lse.getValueIsAdjusting() == false) {
							teacherName = clickableTeachers.getSelectedValue();
						}
					}
				});
		clickableTeachers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clickableTeachers.setBounds( width/2 - 80, height/2 - 140, 150, 100 );
		clickableTeachers.setVisible(true);
		add(clickableTeachers);
		
		JLabel password = new JLabel( "Password:" ); 
		password.setBounds( width/2 - 80, height/2-40, 150, 20 );
		add( password);

		passwordField = new JPasswordField();
		passwordField.setBounds( width/2 - 80, height/2 - 20, 150, 20 );
		add( passwordField);
		
		passwordField.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER){
                	
                	char[] test = passwordField.getPassword();
                	
                	//Temporary.
                	for(int i = 0; i < test.length; i++){
                		teacherPass += test[i];
                	}
                	
                	
                	//Send all null short credential. Check type to see if login fail. (next.type.)
                	cm = CrossMessage.completeStudentInformationRequest(null, null, null, teacherName, teacherPass);
                	
                	//Check type to make if login fail by seeing if login type is failed Login.
                	toServer.println(cm);
            		try {responses = CrossMessage.messagesFrom(extremeServer.getInputStream()).iterator();} catch (IOException e1) {}
            		CrossMessage mesg = responses.next();
            		
            		if (mesg.type().equals(CrossMessage.Type.login_fail.toString())){
            			teacherPass = "";
            			passwordField.setText("");
            			JOptionPane.showMessageDialog(tapc,
            				    "Login Failed, Please Try Again.",
            				    "I D Ten T Error",
            				    JOptionPane.ERROR_MESSAGE);
            		}else{
            			updateView(new TeacherGUI(tapc, teacherName, teacherPass));
                	}
                }
            }
			@Override
			public void keyPressed(KeyEvent arg0) {
				// Auto-generated method stub
				
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				// Auto-generated method stub
				
			}
		});	
	}
	
	public void updateView(JComponent jc){
		tapc.setView(jc);
	}
	
	 
} //end of TeacherGUI class 
