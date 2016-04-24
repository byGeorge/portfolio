package teacherapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;  //notice javax
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import common.CrossMessage;
import common.Konstants;

@SuppressWarnings("serial")
public class TeacherGUI extends JPanel
{
	
	/**
	 * Tabbed pane with teachers classes as tabs. 
	 * 
	 * Inside That is the Students, Teachers and Class Management
	 * 
	 * Sign in as different teacher button, Change passphrase and new Class button along bottom.
	 * 
	 * 
	 */
	private JTabbedPane outerTabbedPane;
	private JTabbedPane innerTabbedPane;
	private JTabbedPane innerTabbedPane2;
	//private Color		betterPink   = new Color(255, 130, 171);
	private Color		betterPink   = new Color(102, 178, 255);
	private JTextArea statusPane;
	
	JPanel outerTabbedPaneContainer;
	
	String[] columnNamesForStuTab1 = {"Students in this Class","Login Token"};
	private String[] students;
	
	//Awesome buttons...
	JButton sIAADTButton = new JButton("Sign in as a Different Teacher");
	JButton cPassphraseButton = new JButton("Change Passphrase...");
	JButton newClassButton = new JButton("New Class...");
	JButton addStudentButton = new JButton("Add Student");
	JButton removeStudentButton = new JButton("Remove Student");
	
	private MatheratorTeacher tapc;
	private String teacherName;
	private String teacherPass;
	private String className;
	
	private String studentToUnenroll;
	private int rowOfStudentToUnenroll;
	
	CrossMessage cm;
	Socket extremeServer;
	PrintWriter toServer;
	static Iterator<CrossMessage> 	responses;
	
	Boolean isTeacheratoriness = false;
	
	public TeacherGUI(MatheratorTeacher app, String tn, String tp) 
	{ 
		try{
			extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
			toServer = new PrintWriter( extremeServer.getOutputStream(), true );
			responses = CrossMessage.messagesFrom(extremeServer.getInputStream()).iterator();
		}catch(IOException ioe){}
		
		tapc = app;
		
		teacherName = tn;
		teacherPass = tp;
		//Query server
		setBackground(betterPink);
		this.setLayout(new GridLayout());
		
		//Contains Class
		outerTabbedPaneContainer = new JPanel(new BorderLayout());
		add(outerTabbedPaneContainer,BorderLayout.CENTER);
		outerTabbedPane = new JTabbedPane();
		
		outerTabbedPaneContainer.setBackground(betterPink);
		
		outerTabbedPaneContainer.add(outerTabbedPane, BorderLayout.CENTER);
	
		/** Adding to JPanel outer pane******************************************************/
		JPanel southOuterButtonsPanel = new JPanel();
		southOuterButtonsPanel.setBackground(betterPink);
		addSignInAsDifferentTeacherButton(southOuterButtonsPanel);
		addChangePassPhraseButton(southOuterButtonsPanel);
		addNewClassButton(southOuterButtonsPanel);
		outerTabbedPaneContainer.add(southOuterButtonsPanel, BorderLayout.SOUTH);
		/** End of adding to JPanel outer Pane.*/
		

		//Get classes Teachers has.
		cm = CrossMessage.teacherClassListRequest(teacherName);
		toServer.println(cm);
		CrossMessage mesg = responses.next();
		buildOuterClassesTabs(mesg);
	}

	public void addSignInAsDifferentTeacherButton(JPanel jp){
		/**Sign in as different teacher button*/
		jp.add(sIAADTButton, BorderLayout.SOUTH);
		/** I think just call teacher login, this should overwriteeverything*/
		sIAADTButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tapc.setView(new TeacherLogin(tapc));
			}
		});
		/**END sign in as different teacher button. */
	}
	
	public void addChangePassPhraseButton(JPanel jp){
		/** Change the Passphrase of the teacher. */
		jp.add(cPassphraseButton, BorderLayout.SOUTH);
		cPassphraseButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField oldP = new JTextField(10);
			      JTextField newP = new JTextField(10);
			      JTextField conP = new JTextField(10);
			      JPanel changePassPanel = new JPanel(new GridLayout(0,2));
			      changePassPanel.add(new JLabel("    Old Password:"));
			      changePassPanel.add(oldP);
			      changePassPanel.add(new JLabel("    New Password:"));
			      changePassPanel.add(newP);
			      changePassPanel.add(new JLabel("Confirm Password:"));
			      changePassPanel.add(conP);
			      int result = JOptionPane.showConfirmDialog(null, changePassPanel, 
			               "Password Changerator", JOptionPane.OK_CANCEL_OPTION);
			      
			      /** Check if new and con are same. If not same, pop error and close. 
			       * If they are same, check if oldP == teacherPass
			       * If that works, send request and verify if it updated.
			       * */
			      if (result == JOptionPane.OK_OPTION) {
			    	  if(newP.getText().equals(conP.getText())){
			    		  if(oldP.getText().equals(teacherPass)){

			    			  cm = CrossMessage.updatePassphraseRequest(teacherName, teacherPass, newP.getText());
			    			  toServer.println(cm);
			    			  CrossMessage mesg = responses.next();
			    			  if(!mesg.isErrorific())
			    				  teacherPass = newP.getText();
			    			  else
			    				  JOptionPane.showMessageDialog(null, " Password Changed Successfully! ");
			    		  } else
			    			  JOptionPane.showMessageDialog(null, " Your old password was incorrect. ");
			    		  
			    	  } else
			    		  JOptionPane.showMessageDialog(null, " The new password and new confirmation password did not match. ");
			      }
			}
		});
		/** End Changing the passphrase*/
	}
	public void addNewClassButton(JPanel jp){
		/** Create a new class Button */
		jp.add(newClassButton, BorderLayout.SOUTH);
		newClassButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField newClassName = new JTextField(20);
				JPanel changePassPanel = new JPanel(new GridLayout(0,1));
				changePassPanel.add(new JLabel("Enter name for the new class: "));
				changePassPanel.add(newClassName);
				changePassPanel.add(new JLabel("You will be added as the only teacher for this class.\n"));
				changePassPanel.add(new JLabel("If you are making this class listing for another teacher\n"));
				changePassPanel.add(new JLabel("to use, you can add them to this class after you create \n"));
				changePassPanel.add(new JLabel("it, and then recuse yourself from it."));


				int result = JOptionPane.showConfirmDialog(null, changePassPanel, 
						"New Classerator", JOptionPane.OK_CANCEL_OPTION);

				//Need to do the communication with server here.

				/** Check if new and con are same. If not same, pop error and close. 
				 * If they are same, check if oldP == teacherPass
				 * If that works, send request and verify if it updated.
				 * */
				if (result == JOptionPane.OK_OPTION) {
					cm = CrossMessage.createClassRequest(newClassName.getText(), teacherName, teacherPass);
					toServer.println(cm);
					CrossMessage mesg = responses.next();
					

					outerTabbedPane.add(innerTabbedPane2, newClassName.getText());
					buildInnerClassInformation(innerTabbedPane2, newClassName.getText());
					
					if(!mesg.isErrorific())
						JOptionPane.showMessageDialog(null, " The new class by the name of: " + newClassName.getText() + " has been successfully created.");
				}
			}

		});
		/** End new class button*/
	}

	
	public void buildOuterClassesTabs(CrossMessage mesg){
		//Start of inner tabbed pane which needs to be fixed.
				innerTabbedPane = new JTabbedPane();
				innerTabbedPane2 = new JTabbedPane();
				
				
				/**Build Outer Tabs  */
				if(mesg.classNames().size() > 1){
					String[] classNames = new String[mesg.classNames().size()];
					Iterator<String> cNames = mesg.classNames().iterator();
					for(int i = 0; i < classNames.length; i ++){
						classNames[i] = cNames.next();
					}
					
					for(int i = 0; i< classNames.length; i++){
						//Populate proper information on the jPanel. Adding JTabbedPane.
						outerTabbedPane.add(innerTabbedPane, classNames[i]);
					}
					outerTabbedPane.add(innerTabbedPane, classNames[0]);
					buildInnerClassInformation(innerTabbedPane, classNames[0]);
					outerTabbedPane.add(innerTabbedPane2, classNames[1]);
					buildInnerClassInformation(innerTabbedPane2, classNames[1]);
					
					//className = classNames[0];
				}
				else{
					className = mesg.classNames().get(0);
					outerTabbedPane.add(innerTabbedPane, className);
					buildInnerClassInformation(innerTabbedPane, className);
				}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildInnerClassInformation(JComponent jc, final String cn){
		CrossMessage mesg;
		JPanel Students;
		JPanel Teachers;
		JPanel ClassManagement = new JPanel();
		final JTable stuTab1;
		final JTextArea stuTab2 = new JTextArea();
		JScrollPane scrollPane2;
		DefaultListModel tITCLM = new DefaultListModel();
		JLabel teachersInThisClassHeader = new JLabel("Teachers In This Class");
		JList teachersInThisClass = new JList(tITCLM);
		DefaultListModel aOTLM = new DefaultListModel();
		JLabel allOtherTeachersHeader = new JLabel("All Other Teachers");
		JList allOtherTeachers = new JList(aOTLM);
		
		
		/************Building inner Tabs***************************************/
		cm = CrossMessage.studentListRequest(cn);
		toServer.println(cm);
		mesg = responses.next();
		List<String> mes = mesg.studentNames();
		students = Arrays.copyOf(mes.toArray(), mes.size(), String[].class);
		
		
		/***************************************************/
		
		/***************************************************/
		/**Building the Students Tab.*/
		/***************************************************/
		Students = new JPanel(); 
		Students.setLayout(new GridLayout(0,2));
		Students.setBackground(betterPink);
		/**************************************************/
		
		/** Building first JTable in Students Tab*/
		Object[][] data = new Object[students.length][2];
		
		//Build data for Table 1.
		for(int i = 0; i < students.length; i++){
			cm = CrossMessage.completeStudentInformationRequest(null, students[i].toString(), cn, teacherName, teacherPass);
			toServer.println(cm);
			mesg = responses.next();
			
			data[i][0] = students[i].toString();
			data[i][1] = Konstants.resolveToName(mesg.studentInformation().get(mesg.studentInformation().keySet().iterator().next()).get("credential").toString());
		}
		
		final studentTableModel stm = new studentTableModel(data, columnNamesForStuTab1);
		stuTab1 = new JTable(stm);
		final JScrollPane scrollPane = new JScrollPane(stuTab1);
		stuTab1.setFillsViewportHeight(true);
		
		stuTab1.addMouseListener(new MouseListener(){
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mouseClicked(MouseEvent e) {
				if(stuTab1.getSelectedRow() != -1){
					String s = stuTab1.getValueAt(stuTab1.getSelectedRow(), 0)+"";
					if(s != null){
						cm = CrossMessage.gameProgressionRequest(cn, s, teacherName, teacherPass);
			        	toServer.println(cm);
			        	CrossMessage mesg = responses.next();
			        	Map<String, Object> mes = mesg.gameProgression();

			        	/** Figure out the way to show this.*/
			        	
			        	DecimalFormat df = new DecimalFormat("#.##");
			        	String output = "";
			        	String gameName = "";
			        	String tempStr = "";
			        	String[] fix;
			        	String[] subFix;
			        	for(int i = 0; i < 10; i++){
			        		//Do parse.
			        		if(mes.get(i) != null){
			        			tempStr = mes.get(i).toString();

			        			tempStr = tempStr.substring(1,tempStr.length()-1);

			        			if(tempStr.contains(", "));
			        				fix = tempStr.split(", ");
			        				
			        			for(int j = 0; j < fix.length; j++){
			        				subFix = fix[j].split("=");
			        				gameName += subFix[0] + " ";
		
			        				gameName += df.format((Double.parseDouble(subFix[1])*100)) + "%\n"; 
			        			}
			        		}
			        		else{
			        			gameName = "Student hasn't played this level yet.\n";
			        		}
			        		
			        		output+="Level "+ i + ": " +
			        				"\n********************\n"
			        				+gameName+
			        				"********************\n";
			        		gameName = "";
			        		fix = null;
			        		subFix = null;
			        		tempStr = "";
			    		}
			        	stuTab2.setText(output);
					}
				}
			}
		});
		
		JButton addStudent = new JButton("Add Student");
		addStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {	
            	String newStudentName = JOptionPane.showInputDialog("Please input new Students Name: ");
            	Konstants.StudentToken s = Konstants.StudentToken.random();
            	cm = CrossMessage.addStudent(newStudentName, s, teacherName, teacherPass);
        		toServer.println(cm);
        		CrossMessage mesg = responses.next();
        		if(mesg.isErrorific()){
        			//System.out.println(mesg);
        		}
        		else{
        			Long studentID = null;
        			
        			Map studentInfo = mesg.studentInformation();
        			for(Object k : studentInfo.keySet()){
        				Number id = (Number)k;
        				studentID = id.longValue();
        			}

        			cm = CrossMessage.enrollmentUpdate(studentID, cn, false, teacherName, teacherPass);
        			toServer.println(cm);
        			mesg = responses.next();
        			stuTab2.setText("|*************************|\n"
        					+ "| Student Name: " + newStudentName
        					+ "\n|*************************|\n"
        					+ "| Student Pass: " + s.toString()
        					+ "\n|*************************|\n"
        					+ "|*Successfully Created*|");
        			
        			String[] d = {newStudentName, Konstants.resolveToName(s.toString()).toString()}; 
        			stm.addRow(d);
        			stm.fireTableDataChanged();
        		}
        	}
        });
		JButton deleteStudent = new JButton("Unenroll Student");
		deleteStudent.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				cm = CrossMessage.studentListRequest(cn);
				toServer.println(cm);
				CrossMessage mesg = responses.next();
				List<String> mes = mesg.studentNames();
				
				 
			      
			      DefaultListModel dlm = new DefaultListModel();
					for(int i = 0; i< mes.size(); i++)
						dlm.addElement(mes.get(i));
			      final JList<String> clickableDelete = new JList<String>(dlm);
					clickableDelete.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					clickableDelete.addListSelectionListener(new ListSelectionListener()
					{
						@Override
						public void valueChanged(ListSelectionEvent lse) {
							if (lse.getValueIsAdjusting() == false) {
								studentToUnenroll = clickableDelete.getSelectedValue();
								rowOfStudentToUnenroll = clickableDelete.getMinSelectionIndex();
							}
						}
					});
			      JPanel changePassPanel = new JPanel(new GridLayout(0,1));
			      changePassPanel.add(clickableDelete);
			      int result = JOptionPane.showConfirmDialog(null, changePassPanel, 
			               "Student Unenrollerator", JOptionPane.OK_CANCEL_OPTION);
			      
			      /** Check if new and con are same. If not same, pop error and close. 
			       * If they are same, check if oldP == teacherPass
			       * If that works, send request and verify if it updated.
			       * */
			      if (result == JOptionPane.OK_OPTION) {
			    	  cm = CrossMessage.completeStudentInformationRequest(null, studentToUnenroll, cn, teacherName, teacherPass);
						toServer.println(cm);
						mesg = responses.next();
						
						Long studentID = (long) 0;
				    	  Map studentInfo = mesg.studentInformation();
		        			for(Object k : studentInfo.keySet()){
		        				Number id = (Number)k;
		        				studentID = id.longValue();
		        			}
		        			
						cm = CrossMessage.enrollmentUpdate(studentID, cn, true, teacherName, teacherPass);
						toServer.println(cm);
						mesg = responses.next();
						if(mesg.isErrorific()){
							JOptionPane.showMessageDialog(null, "Ran Into an Issue, try again.");
						}
						else{
					    	  studentToUnenroll = "";
					    	  stm.removeRow(rowOfStudentToUnenroll);
					    	  rowOfStudentToUnenroll = 0;
							JOptionPane.showMessageDialog(null, "Student Unenrolled Successfully");
						}
			      }
			}
		});
		JPanel leftSideAddDeleteScrollPane = new JPanel(new BorderLayout());
		JPanel southAddDeleteButtonPanel = new JPanel();
		
		southAddDeleteButtonPanel.add(addStudent);
		southAddDeleteButtonPanel.add(deleteStudent);
		leftSideAddDeleteScrollPane.add(southAddDeleteButtonPanel, BorderLayout.SOUTH);
		leftSideAddDeleteScrollPane.add(scrollPane, BorderLayout.CENTER);
		Students.add(leftSideAddDeleteScrollPane);
		/**End Students Table 1*/
 
		/**Start Students TextArea 2*/
		scrollPane2 = new JScrollPane(stuTab2,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Students.add(scrollPane2);
		
		
		/**End Students Textarea 2*/
		
		
		
		
		/** Teacher Tab */
		
		/** Easiest way would be to use Jpanel teachers with grid layout. Then for left use new JPanel with BorderLayout, then do scroll
		 * Pane in center and the add and minus buttons to SOUTH.
		 */
		Teachers = new JPanel(null);
		
		teachersInThisClassHeader.setLocation(200, 60);
		teachersInThisClassHeader.setSize(200,20);
		Teachers.add(teachersInThisClassHeader);
		
		allOtherTeachersHeader.setLocation(500, 60);
		allOtherTeachersHeader.setSize(200,20);
		Teachers.add(allOtherTeachersHeader);
		
		
		/************************/
		
		
		
		
		teachersInThisClass.setLocation(200, 80);
		teachersInThisClass.setSize(200,200);
		Teachers.add(teachersInThisClass);
		teachersInThisClass.setDragEnabled(true);
		teachersInThisClass.setDropMode(DropMode.INSERT);
		teachersInThisClass.setTransferHandler(new ListTransferHandler());
		cm = CrossMessage.classTeacherListRequest(cn);
		toServer.println(cm);
		mesg = responses.next();
		Iterator tNames = mesg.teacherNames().iterator();
		//Add Names that are currently in class to list.
		while(tNames.hasNext()){
			tITCLM.addElement(tNames.next());
		}
		
		allOtherTeachers.setDragEnabled(true);
		allOtherTeachers.setDropMode(DropMode.INSERT);
		allOtherTeachers.setTransferHandler(new ListTransferHandler());
		allOtherTeachers.setLocation(500,80);
		allOtherTeachers.setSize(200,200);
		Teachers.add(allOtherTeachers);
		

		

		cm = CrossMessage.teacherListRequest();
		toServer.println(cm);
		mesg = responses.next();
		Iterator tNames2 = mesg.teacherNames().iterator();
		
		//Add names that aren't currently in class.
		Object test;
		while(tNames2.hasNext()){
			if(!tITCLM.contains((test = tNames2.next())))
					aOTLM.addElement(test);
		}
		
		
		//tITCLM and aOTLM are my list models....
		tITCLM.addListDataListener(new ListDataListener(){
		    public void contentsChanged(ListDataEvent e) {/*do nothing*/}
		    public void intervalAdded(ListDataEvent e) {
		    	/** Get interval entered into, then get name from the getSource().
		    	 * 
		    	 * assignTeacherToClassRequest(String cn, String teacherNameToAdd, String teacherDoingTheAdding, String credential)
		    	 */
		    	cm = CrossMessage.assignTeacherToClassRequest(cn, ((DefaultListModel)e.getSource()).elementAt(e.getIndex0()).toString(), teacherName, teacherPass);
		    	toServer.println(cm);
		    	CrossMessage mesg = responses.next();
		    }
		    public void intervalRemoved(ListDataEvent e) {/*do nothing*/}
		});
		aOTLM.addListDataListener(new ListDataListener(){
		    public void contentsChanged(ListDataEvent e) {/*do nothing*/}
		    public void intervalAdded(ListDataEvent e) {
		    	cm = CrossMessage.bootTeacherFromClassRequest(cn, ((DefaultListModel)e.getSource()).elementAt(e.getIndex0()).toString(), teacherName, teacherPass);
		    	toServer.println(cm);
		    	CrossMessage mesg = responses.next();
		    }
		    public void intervalRemoved(ListDataEvent e) {/*do nothing*/}
		});
		/**Teacher Tab End! Yay!*****/
		
		
		
		/** Class Management Tab*/
		ClassManagement = new JPanel(new BorderLayout());
		ClassManagement.add(new JLabel("Available Classes!"), BorderLayout.NORTH);
		ClassManagement.add(new JLabel("The checked levels are available for all students in class to play."), BorderLayout.SOUTH);
		
		cm = CrossMessage.availableLevelsRequest(cn);
		toServer.println(cm);
		mesg = responses.next();
		
		mesg.availableLevels();
		
		String[] coN = {"Level", "Boolean"};
        Object[][] dat = {        };
		final teacherTableModel dtm = new teacherTableModel(dat, coN);
		
		
		if(mesg.availableLevels().get(0))
			dtm.addRow(new Object[]{"Place Level Identifying", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Place Level Identifying", new Boolean(false)});
		
		if(mesg.availableLevels().get(1))
			dtm.addRow(new Object[]{"Addition Level 1", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition Level 1", new Boolean(false)});
		
		if(mesg.availableLevels().get(2))
			dtm.addRow(new Object[]{"Addition Level 2", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition Level 2", new Boolean(false)});
		
		if(mesg.availableLevels().get(3))
			dtm.addRow(new Object[]{"Addition Level 3", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition Level 3", new Boolean(false)});
		
		if(mesg.availableLevels().get(4))
			dtm.addRow(new Object[]{"Subtract Level 1", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Subtract Level 1", new Boolean(false)});
		
		if(mesg.availableLevels().get(5))
			dtm.addRow(new Object[]{"Subtract Level 2", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Subtract Level 2", new Boolean(false)});
		
		if(mesg.availableLevels().get(6))
			dtm.addRow(new Object[]{"Subtract Level 3", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Subtract Level 3", new Boolean(false)});
		
		if(mesg.availableLevels().get(7))
			dtm.addRow(new Object[]{"Addition and Subtraction Level 1", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition and Subtraction Level 1", new Boolean(false)});
		
		if(mesg.availableLevels().get(8))
			dtm.addRow(new Object[]{"Addition and Subtraction Level 2", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition and Subtraction Level 2", new Boolean(false)});
		
		if(mesg.availableLevels().get(9))
			dtm.addRow(new Object[]{"Addition and Subtraction Level 3", new Boolean(true)});
		else
			dtm.addRow(new Object[]{"Addition and Subtraction Level 3", new Boolean(false)});
		
		JTable avaCla = new JTable(dtm);
		avaCla.setEditingColumn(1);
		avaCla.setModel(dtm);
		dtm.addTableModelListener((new TableModelListener(){

			@Override
			public void tableChanged(final TableModelEvent arg0) {
				Map<Integer, Boolean> pam = new HashMap<Integer,Boolean>() {
					 {
						    put(arg0.getFirstRow(), (Boolean) dtm.getValueAt(arg0.getFirstRow(), 1));
					 }
				};
				cm = CrossMessage.availableLevelsUpdate(cn, pam, teacherName, teacherPass);
				toServer.println(cm);
				CrossMessage mesg = responses.next();
			}
			
		}));
		
		ClassManagement.add(avaCla,BorderLayout.CENTER);
		
		/**End Class Management Tab*/
		jc.add(Students, "Students");
		jc.add(Teachers, "Teachers");
		jc.add(ClassManagement, "Class Management");
	}
} //end of TeacherGUI class
