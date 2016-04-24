package serverapp.daemon;


import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import common.CrossMessage;
import common.Konstants;
import serverapp.doer.DBFetchDo;
import serverapp.doer.DBFetchList;
import serverapp.doer.DBUpdate;



/*
 * 
 * The flow of control looks something like
 * 
 *   (Class)                     
 *     main  ->  Constructor
 *           \                                                              ,->  other
 *            +->     ...       ->  serve  ->  handle  ->  handleMessage  -+-->  helper
 *   (Instance)                         ^  \                                '->  methods
 *                                     (    )
 *                                      \__/
 * 
 * 
 */



/**
 * The Matherator Daemon server
 * that manages student & teacher activities in a school.
 * 
 * @author Feckless Ellipses...
 *
 */
public class MatheratorD {
	
	
	Connection dattabazzz = null;
	
	private static final boolean BUGBUG = false;
	
	
	
	
	/**
	 * Make a Matherator Daemon
	 * 
	 * @param dbFilepath - the database file or identifier backing this server
	 * @throws IOException on file-not-readable or immediate database error
	 */
	public MatheratorD(String dbFilepath) throws IOException, ClassNotFoundException, SQLException {
		
		// Set up database connection...
		Class.forName("org.sqlite.JDBC");
		dattabazzz = DriverManager.getConnection("jdbc:sqlite:" + dbFilepath);
		
		// ...and make sure it's somewhat-gracefully torn down.
		Runtime.getRuntime().addShutdownHook( new Thread() {  public void run() {
			try {  dattabazzz.close();  } catch (Exception ex) {  }
		}} );
		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Handle a client connection.
	 * This is the main method in which the Daemon handles a connection.
	 * Extracts CrossMessages and dispatches each to handleMessage().
	 * 
	 */
	private void handle(Socket client) {
		InputStream streamFromClient = null;
		PrintWriter streamToClient = null;
		
		try {
			streamFromClient = client.getInputStream();
			streamToClient = new PrintWriter( client.getOutputStream(), true );  // The `true` is important!
			
			for (CrossMessage message : CrossMessage.messagesFrom(streamFromClient)) {
				// We must check explicitly for the end of messages first.
				// If we don't, we'll eventually get into an infinite loop where message == null:
				if (message == null)  break;
				
				try {
					
					
					handleMessage( message, client, streamToClient );
					
					
					
					
				} catch (SQLException sqe) {
					streamToClient.println(
							CrossMessage.generalError("The request could not be processed due to an internal error.")
							);
					System.err.println(sqe);
					
				}
				
			}
			
			
		} catch (IOException|IllegalArgumentException except) {
			System.err.println(except.toString());
            
		} finally {
			try { streamToClient.close(); streamFromClient.close(); }  catch (IOException|NullPointerException except) { }
			
		}
		
	}
	
	
	
	
	
	
	/**
	 * Handle and respond to a CrossMessage.
	 * This currently provides only a small subset of its future functionality.
	 * 
	 * @param message - the message from the client.
	 * @param client - the actual socket to the client---so that we can close it, if necessary, on syntax violation.
	 * @param responseStream - the stream on which we can print responses back to the client.
	 */
	private void handleMessage(CrossMessage message, Socket client, PrintWriter responseStream) throws IOException, SQLException {
		
		// I love how the switch is so much like a goto!
		
		String messageType = message.type();
		
		//
		// If there's no message type, trigger syntax violation..
		if (messageType == null)
			messageType = "Zoogbslarb";  // Will fall out to default.
		
		
		if (BUGBUG) {
			System.out.printf("\n\nReceived:\n");
			System.out.println(message);
		}
		
		
		switch (messageType) {
		
		case "request classes" :
			fetchClassList(message, responseStream);
			break;
			
			
		case "request students" :
			if (message.className() == null)  throw new IOException("Class name expected but not given.");
			fetchStudentList(message, responseStream);
			break;
			
		
		case "request game progression" :
			fetchGameProgression(message, responseStream);
			break;
			
			
		case "update game score" :
			updateGameProgression(message, responseStream);
			break;
			
			
		case "request teachers" :
			fetchTeacherList(message, responseStream);
			break;
			
			
			
			
		case "request teacher's class list" :
			fetchTeacherClassList(message, responseStream);
			break;
			
			
		case "request complete student information" :
			fetchCompleteStudentInformation(message, responseStream);
			break;
			
			
            //
			
			
		case "add or update student" :
            addOrUpdateStudent(message, responseStream);
			break;
			
			
		case "enroll student" :
			changeStudentEnrollment(false, message, responseStream);
			break;
			
			
		case "un-enroll student" :
            changeStudentEnrollment(true, message, responseStream);
			break;
			
			
		case "request teachers for class" :
			// This is roughly the inverse of 'request teacher's class list.'
			fetchTeachersForClass(message, responseStream);
			break;
			
			
		case "request available levels for class" :
			fetchAvailableLevels(message, responseStream);
			break;
			
			
		case "update available levels for class" :
			updateAvailableLevels(message, responseStream);
			break;
			
			
		case "boot teacher from class" :
			bootTeacherFromClass(message, responseStream);
			break;
			
			
		case "change class name" :
			classNameUpdate(message, responseStream);
			break;
			
			
		case "delete student" :
			deleteStudent(message, responseStream);
			break;
			
			
		case "create class" :
			createClass(message, responseStream);
			break;
			
			
		case "assign teacher to class" :
			assignTeacherToClass(message, responseStream);
			break;
			
			
		case "delete class" :
			deleteClass(message, responseStream);
			break;
			
			
		case "create teacher" :
			createTeacher(message, responseStream);
			break;
			
			
		case "delete teacher" :
			deleteTeacher(message, responseStream);
			break;
			
			
		case "update passphrase" :
			updatePassphrase(message, responseStream);
			break;
			
			
		case "request teacheratoriness" :
			fetchTeacheratoriness(message, responseStream);
			break;
			
			
		default :
			// Syntax/Protocol Violation!
			responseStream.println( CrossMessage.notUnderstood() );
			try { client.close(); } catch (IOException ioe) { }
			
		}
		
		
		
	}









	//
	// Response Helpers
	// 
	
	
	
	
	
	
	private void createTeacher(CrossMessage message, PrintWriter responseStream) {
		
		Long teacheratorID = Teacher.idForTeacherSpecifiedIn(message, true, true, dattabazzz);
		String teacherNameToAdd = message.name();
		
		if (teacheratorID == null  ||  !Teacher.isTeacherator(teacheratorID, dattabazzz)) {
			responseStream.println(CrossMessage.loginFail());
			return;
		}
		
		if (teacherNameToAdd == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		}
		
		
		boolean success = new DBUpdate(dattabazzz,
				" insert into  Teacher (id, name, passphrase) " +
				"      values  (null, ?, ?) ",  teacherNameToAdd, Konstants.DefaultPassphrase) {

			public void onError(SQLException sqex) {
				System.out.println("Hmmm...");  sqex.printStackTrace();
			}
		}.ex();
		
		
		if (!success) {
			responseStream.println(CrossMessage.generalError("The teacher could not be added."));
			return;
		}
		
		responseStream.println(CrossMessage.success());
		
	}
	
	
	
	
	
	
	
	private void deleteTeacher(CrossMessage message, PrintWriter responseStream) {
		
		Long teacheratorID = Teacher.idForTeacherSpecifiedIn(message, true, true, dattabazzz);
		Long teacherToDelete = Teacher.idForTeacherSpecifiedIn(message, false, false, dattabazzz);
		
		if (teacheratorID == null  ||  !Teacher.isTeacherator(teacheratorID, dattabazzz)) {
			responseStream.println(CrossMessage.loginFail());
			return;
		}
		
		if (teacherToDelete == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		}
		
		if (teacheratorID.equals(teacherToDelete)) {
			responseStream.println(CrossMessage.generalError("Teacherator cannot delete themself."));
			return;
		}
		
		
		boolean success = new DBUpdate(dattabazzz,
				" delete from  Teaching    " +
				"       where  teacher = ? ", teacherToDelete) {
			
			public void onError(SQLException sqex) { sqex.printStackTrace(); }
		}.ex();
		
		if (!success) {
			responseStream.println(CrossMessage.generalError("Unable to boot teacher from their classes before deleting."));
			return;
		}
		
		
		success = new DBUpdate(dattabazzz,
				" delete from  Teacher " +
				"       where  id = ?  ", teacherToDelete) {
			
			public void onError(SQLException sqex) { sqex.printStackTrace(); }
		}.ex();
		
		
		if (!success) {
			responseStream.println(CrossMessage.generalError("Unable to delete teacher."));
			return;
		}
		
		
		responseStream.println(CrossMessage.success());
		
		
	}
	
	
	
	
	
	private void fetchTeacheratoriness(CrossMessage message, PrintWriter responseStream) {
		
		Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);
		if (teacherID == null) {
			responseStream.println(CrossMessage.loginFail());
			return;
		}
		
		responseStream.println(CrossMessage.teacheratorinessResponse(
				Teacher.isTeacherator(teacherID, dattabazzz)
				));
		
	}
	
	
	
	
	
	
	private void updatePassphrase(CrossMessage message, PrintWriter responseStream) {
		
		Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);
		if (teacherID == null) {
			responseStream.println(CrossMessage.loginFail());
			return;
		}
		
		String newPassphrase = message.stringGet("passphrase");
		
		boolean success = new DBUpdate(dattabazzz,
				" update  Teacher        " +
				"    set  passphrase = ? " +
				"  where  id = ?         ", newPassphrase, teacherID) {
			
			public void onError(SQLException sqex) { sqex.printStackTrace(); }
		}.ex();
		
		
		responseStream.println(
				success ?
				CrossMessage.success()
			 :	CrossMessage.generalError("Must be something 'bout that trout farm next door went weird.")
		);
		
		
	}
	
	
	
	
	
	
	
	/**
	 * Fetch the list of classes and put it out to responseStream.
	 * @param message - the client's request message
	 * @param responseStream - the stream writer back to the client
	 * @throws SQLException
	 */
	private void fetchClassList(CrossMessage message, PrintWriter responseStream) throws SQLException {
		
		// Query the class names:
		PreparedStatement asker = dattabazzz.prepareStatement(
				" select name from Class; " );
		ResultSet classnameIter = asker.executeQuery();
		
		// Collect them:
		ArrayList<String> classNames = new ArrayList<String>();
		while (classnameIter.next()) {
			classNames.add( classnameIter.getString(1) );
			
		}
		
		// Hand them back:
		CrossMessage response = CrossMessage.classListResponse(classNames);
		responseStream.println(response);
		
	}
	
	
	
	/**
	 * Fetch the list of students enrolled in a particular class (specified within message)
	 * and put it out to responseStream.
	 * @param message - the client's request message, which is expected to contain a particular "class"
	 * @param responseStream - the stream back to the client over which the answer is transmitted
	 * @throws SQLException
	 */
	private void fetchStudentList(CrossMessage message, PrintWriter responseStream) throws SQLException {
		String className = message.className();
		
		PreparedStatement studentFinder = dattabazzz.prepareStatement(
				" select  Student.name from Student, Enrolled, Class " +
				" where   Student.id = Enrolled.student              " +
				"     and Enrolled.class = Class.id                  " +
				"     and Class.name = ?                            ;" );
		studentFinder.setString(1, className);
		
		ResultSet studentIter = studentFinder.executeQuery();
		List<String> names = new ArrayList<String>();
		while (studentIter.next()) {
			names.add(studentIter.getString(1));
		}
		
		responseStream.println( CrossMessage.studentListResponse(className, names) );
		
	}
	
	
	
	
	private void fetchTeacherList(CrossMessage message, PrintWriter responseStream) throws SQLException {
		
		PreparedStatement teachyFinder = dattabazzz.prepareStatement(
				" select  name from Teacher; " );
		
		ResultSet teachyIter = teachyFinder.executeQuery();
		List<String> names = new ArrayList<String>();
		while (teachyIter.next()) {
			names.add(teachyIter.getString(1));
		}
		
		responseStream.println( CrossMessage.teacherListResponse(names) );
		
	}
	
	
	
	
	/**
	 * Fetch the earned scores for every game the student has played
	 * and put the record out to responseStream.
	 * 
	 * Puts a dictionary, mapping level numbers to gameinfo,
	 * where gameinfo is a dictionary mapping game names to earned scores. Like:
	 *     1:
	 *       Kitten X: 13.0
	 *       Maze of Monty Halls: 12.2
	 *     2:
	 *       Kitten X: 12.9
	 *       Maze of Monty Halls: 13.0
	 *     ...
	 *     
	 * @param message - the client's request message, which is expected to contain student login info
	 * @param responseStream - the stream back to the client over which the result will be transmitted
	 * @throws SQLException
	 */
	private void fetchGameProgression(CrossMessage message, PrintWriter responseStream) throws SQLException {
        
        // TODO Fix this for when student credentials are checked:
		Long studentID = Student.idForStudentSpecifiedIn(message, false, dattabazzz);
		Long studentIDByLoggingIn = Student.idForStudentSpecifiedIn(message, true, dattabazzz);
        Long teacherID = Teacher.idForTeacherAlsoSpecifiedIn(message, dattabazzz);
		
		if (studentIDByLoggingIn == null && teacherID == null) {
			responseStream.println( CrossMessage.loginFail() );
			return;
		}
		
		if (studentID == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		}
		
		PreparedStatement scoreFinder;
		scoreFinder = dattabazzz.prepareStatement(
				" select  Game.name, Game.level, Played.score " +
				" from    Played, Game                        " +
				" where   Played.game = Game.id               " +
				"     and Played.student = ?                 ;" );
		scoreFinder.setLong(1, studentID);
		
		
		ResultSet scores = scoreFinder.executeQuery();
		Map<Integer, Map<String, Double>> progression = new HashMap<Integer, Map<String, Double>>();
		Map<String, Double> levelProg;
		
		while (scores.next()) {
			int level = scores.getInt(2);
			String game = scores.getString(1);
			double score = scores.getDouble(3);
			
			levelProg = progression.get(level);
			if (levelProg == null) {
				levelProg = new HashMap<String, Double>();
				progression.put(level, levelProg);
			}
			
			levelProg.put(game, score);
			
		}
		
		
		responseStream.println( CrossMessage.gameProgressionResponse(progression) );
		
	}
	
	
	
	
	private void updateGameProgression(CrossMessage message, PrintWriter responseStream) throws SQLException {
		Long studentID = Student.idForStudentSpecifiedIn(message, true, dattabazzz);
		
		if (studentID == null) {
			// Login fail.
			responseStream.println( CrossMessage.loginFail() );
			return;
		}
		
		int level = message.level();
		String gameName = message.gameName();
		double gameScore = message.gameScore();
		double oldScore = 0.0;
		
		if (gameName == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		} else if (level < 0 || level > 9) {
			responseStream.println(CrossMessage.generalError("Levels are 0..9."));
			return;
		} else if (gameScore < 0 || gameScore > 1) {
			responseStream.println(CrossMessage.generalError("Scores are 0..1."));
			return;
		}
		 /* query to get the old score of the player */
		PreparedStatement checkLevel = dattabazzz.prepareStatement(
				" select Played.score from Played, Game " +
				"    where Played.game = Game.id        " + 
				"        and Game.name = ?              " +
				"        and Game.level = ?             " +
				"        and Played.student = ?;        " );
		
		checkLevel.setString(1, gameName);
		checkLevel.setInt(2, level);
		checkLevel.setLong(3, studentID); 
		
		ResultSet result = checkLevel.executeQuery(); 
		
		if(result.next())
		{
			oldScore = result.getDouble(1); 
		}
		
		if( oldScore < gameScore )
		{
			PreparedStatement updater = dattabazzz.prepareStatement(
					" insert or replace into Played  (student, game, score) " +
					"     values (                                          " +
					"         ?,                                            " +
					"         (select Game.id from Game                     " +
					"             where Game.name = ? and Game.level = ?),  " +
					"         ?                                             " +
					"     );                                                " );
			updater.setLong(1, studentID);
			updater.setString(2, gameName);
			updater.setInt(3, level);
			updater.setDouble(4, gameScore);
			
			updater.executeUpdate();
		}
		
		responseStream.println(CrossMessage.success());
		
		
		
		
	}
    
    
    
    
	private void fetchTeacherClassList(CrossMessage message, PrintWriter responseStream) throws SQLException {
		Long teacherID = Teacher.idForTeacherSpecifiedIn(message, false, false, dattabazzz);
        if (teacherID == null) {
            responseStream.println( CrossMessage.loginFail() );
            return;
        }
        
		// Query the class names:
		PreparedStatement asker = dattabazzz.prepareStatement(
				" select  Class.name                " +
                " from    Class, Teaching           " +
                " where   Class.id = Teaching.class " +
                "     and Teaching.teacher = ? ;    " );
        asker.setLong(1, teacherID);
		ResultSet classnameIter = asker.executeQuery();
		
		// Collect them:
		ArrayList<String> classNames = new ArrayList<String>();
		while (classnameIter.next()) {
			classNames.add( classnameIter.getString(1) );
			
		}
		
		// Hand them back:
		CrossMessage response = CrossMessage.classListResponse(classNames);
		responseStream.println(response);
		
	}
	
	
	
	private void fetchTeachersForClass(CrossMessage message, PrintWriter responseStream) {
		
		// Extract the specified class
		String requestedClass = message.className();
		if (requestedClass == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		}
		
		List<String> teachers = new DBFetchList<String>(dattabazzz,
				" select  Teacher.name                  " +
				" from    Teacher, Teaching, Class      " +
				" where   Teacher.id = Teaching.teacher " +
				"     and Class.id = Teaching.class     " +
				"     and Class.name = ? ;              ",  requestedClass) {
			
			public String mapper(ResultSet gott) throws SQLException {
				return gott.getString(1);
			}
		}.ex();
		
		responseStream.println( CrossMessage.teacherListResponse(teachers) );
		
	}
    
    
    
    
    private void fetchCompleteStudentInformation(CrossMessage message, PrintWriter responseStream) {
        Long teacherID = Teacher.idForTeacherAlsoSpecifiedIn(message, dattabazzz);
        if (teacherID == null) {
            responseStream.println( CrossMessage.loginFail() );
            return;
        }
        
        String studentName = message.name();
        Long studentID = message.studentID();
        String className = message.className();
        
        String query = null;
        List<Object> prerams = new ArrayList<Object>(3);
        List<Long> applicableStudents = null;
        
        //
        // Decide which version the client intended.
        // 
        if (studentID != null) {
            // Get 0..1 student, by ID.
            applicableStudents = Arrays.asList(studentID);
            
            
        } else if (className != null) {
            // Get 0..n students, by class, and possibly by name.
            query = " select  distinct Student.id " +
                    " from    Student " +
                    "    join Enrolled on Student.id = Enrolled.student " +
                    "    join Class on Enrolled.class = Class.id " +
                    " where   Class.name = ? ";
            prerams.add(className);
            
            if (studentName != null) {
                // Add student name constraint, if given.
                query = query + " and Student.name = ? ";
                prerams.add(studentName);
            }
            
            
        } else if (studentName != null) {
            // Get 0..n students, by name only.
            query = " select  id       " +
                    " from    Student  " +
                    " where   name = ? ";
            prerams.add(studentName);
            
            
        } else {
            // Refuse to get all students, regardless.
            responseStream.println( CrossMessage.notUnderstood() );
            return;
            
        }
        
        
        // Run the query,
        // but only if the applicable students
        // have not already been decided upon:
        if (applicableStudents == null)
            applicableStudents = new DBFetchList<Long>(dattabazzz, query, prerams.toArray()) {
    			public Long mapper(ResultSet gott) throws SQLException {
    				return gott.getLong(1);
    			}
            }.ex();
        
        if (applicableStudents == null) {
            responseStream.println(CrossMessage.generalError("The database threw up. Please try that again."));
            return;
        }
        
        Map<Long, Map<String, Object>> applicableStudentRecords = new HashMap<Long, Map<String, Object>>();
        for (Long fetchedStuID : applicableStudents) {
            Map<String, Object> singleStudentRecord = singleCompleteStudentInformation(fetchedStuID);
            if (singleStudentRecord != null)
                applicableStudentRecords.put(fetchedStuID, singleStudentRecord);
        }
        
        
        responseStream.println( CrossMessage.completeStudentInformationResponse(applicableStudentRecords) );
        
        
    }
    
    
    
    
    
    private void addOrUpdateStudent(CrossMessage message, PrintWriter responseStream) {
    	
        //
        // Validate teacher credentials.
        // 
        Long teacherID = Teacher.idForTeacherAlsoSpecifiedIn(message, dattabazzz);
        if (teacherID == null) {
            responseStream.println(CrossMessage.loginFail());
            return;
        }
        
        //
        // Gather student information...
        // 
        Long studentID = message.studentID();
        String studentName = message.name();
        Konstants.StudentToken studentToken = message.token();
        
        // ... and validate student information...
        if (studentName == null) {
            responseStream.println(CrossMessage.notUnderstood());
            return;
        }
        
        // null student tokens indicate random selection is necessary.
        if (studentToken == null)
            studentToken = Konstants.StudentToken.random();
        
        
        //
        // Add, or update.
        // 
        if (studentID == null) {
            
            //
            // Add new student in.
            boolean addingSuccess = new DBUpdate(dattabazzz,
                    " insert into  Student  (id, name, token) " +
                    " values       (null, ?, ?);              ",
                    studentName, studentToken.toString()          ) {
            
                public void onError(SQLException sqex) { System.err.println("Hmm:\n" + sqex); }
            }.ex();
            
            // Check for success
            if (!addingSuccess) {
                responseStream.println( CrossMessage.generalError("Unable to add student. ...And no, I can't tell you why.") );
                return;
            }
            
            //
            // Fetch the student's new ID
            List<Long> addedIDs = new DBFetchList<Long>(dattabazzz,
                    " select last_insert_rowid(); ") {
            
                public Long mapper(ResultSet gott) throws SQLException {
                    return gott.getLong(1);
                }
            }.ex();
            if (addedIDs == null  ||  addedIDs.size() < 1 ) {
                responseStream.println(CrossMessage.generalError("Database not behaving. Please bugmit a sub report."));
                return;
            }
            
            studentID = addedIDs.get(0);
            
            
            
        } else {
            
            //
            // Update the student record.
            boolean updatingSuccess = new DBUpdate(dattabazzz,
                    " update  Student             " +
                    " set     name = ?, token = ? " +
                    " where   id = ?;             ",
                    studentName, studentToken.toString(), studentID ) {
            
                public void onError(SQLException sqex) { System.err.println("Hmmm:\n" + sqex); }
            }.ex();
            
            if (!updatingSuccess) {
                responseStream.println(CrossMessage.generalError("Unable to update student. Please make sure all datums are in order."));
                return;
            }
            
        }
        
        
        //
        // Now respond with complete student information.
        // 
        
        Map<Long, Map<String, Object>> csir = new HashMap<Long, Map<String, Object>>();
        Map<String, Object> single = singleCompleteStudentInformation(studentID);
        if (single == null) {
            responseStream.println( CrossMessage.generalError("The addition failed.") );
            return;
        }
        
        csir.put(studentID, single);
        responseStream.println( CrossMessage.completeStudentInformationResponse(csir) );
        
    
    }
    
    
    
    
    
    
    private void changeStudentEnrollment(boolean unEnroll, CrossMessage message, PrintWriter responseStream) {
        Long teacherID = Teacher.idForTeacherAlsoSpecifiedIn(message, dattabazzz);
        if (teacherID == null) {
            responseStream.println( CrossMessage.loginFail() );
            return;
        }
        
        
        Long studentID = message.studentID();
        String className = message.className();
        
        if (studentID == null  ||  className == null) {
            responseStream.println(CrossMessage.notUnderstood());
            return;
        }
        
        
        if (unEnroll) {
            boolean success = new DBUpdate(dattabazzz,
                    " delete  from Enrolled                         " +
                    " where   Enrolled.student = ?                  " +
                    "     and exists (                              " +
                    "             select  1 from Class              " +
                    "             where   Enrolled.class = Class.id " +
                    "                 and Class.name = ?            " +
                    "         );                                    ", studentID, className ) {
                
                public void onError(SQLException sqex) { System.err.println("Hmmm:\n" + sqex); }
            }.ex();
            
            if (!success) {
                responseStream.println( CrossMessage.generalError("The database didn't enjoy that.") );
                return;
            }
            
            
        } else {
            boolean success = new DBUpdate(dattabazzz,
                    " insert  into Enrolled (student, class) " +
                    " values  (   ?, (                       " +
                    "                 select  id from Class  " +
                    "                 where   name = ?       " +
                    "                 limit   1              " +
                    "             )                          " +
                    "         );                             ", studentID, className ) {
                
                public void onError(SQLException sqex) { System.err.println("Hmmm:\n" + sqex); }
            }.ex();
            
            if (!success) {
                responseStream.println( CrossMessage.generalError("Could not enroll student. They may already be enrolled in this class, or may have been removed from the server since your view was last refreshed.") );
                return;
            }
            
        }
        
        
        // All's well that ends well, that's what I say.
        responseStream.println( CrossMessage.success() );
        
    }
    
    
    private void deleteClass(CrossMessage message, PrintWriter responseStream) throws SQLException
    {
    	/* get the teacher's id */
    	Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);

        /* make sure we actually got the ID */
        if(teacherID == null) {
        	responseStream.println( CrossMessage.loginFail() );
        	return;
        }
        
        /* get some more information */
        String className = message.className(); 
        
        if(className == null)
        {
        	responseStream.println( CrossMessage.notUnderstood() );
        	return;
        }
        
        Long classID = null;
        
        PreparedStatement getClassID = dattabazzz.prepareStatement(
        		"select id from Class " + 
        		"	where name = ? " + 
        		"; "); 
        
        getClassID.setString(1, className);
        
        ResultSet classIDresult = getClassID.executeQuery();
        
        if(classIDresult.next())
        	classID = classIDresult.getLong(1);
        
        
        if (classID == null) {
            responseStream.println(CrossMessage.generalError("Specified class does not exist."));
            return;
        }

        
        /* checking if the teacher is a teacher of this class */
        boolean isTeacherOfClass = false; 
        Set<Long> teachers = new HashSet<Long>();
        
        /* query to receive all of the teacher ids of the given class by name */
        PreparedStatement isTeacher = dattabazzz.prepareStatement(
        		"select teacher from Teaching, Class " + 
        		"	where Class.id = Teaching.class  " + 
        		" 		and Class.name = ?           " +
        		";");
        
        isTeacher.setString(1, className);
        
        ResultSet result = isTeacher.executeQuery(); 
        
        /* add in all of the teacher ids returned by the query to a set */
        while(result.next())
        {
        	teachers.add(result.getLong(1));
        } 
        
        /* see if our teacher id matches any in the class */
        if(teachers.contains(teacherID))
        	isTeacherOfClass = true;
       
        /* delete class if it is a teacher of the class or a teacherator */
        if(!Teacher.isTeacherator(teacherID, dattabazzz) && !isTeacherOfClass)
        {
        	/* we have an error or a rogue teacher */ 
        	responseStream.println( CrossMessage.loginFail() );
        	return;
    	}
        
        //
    	// Un-enroll
    	boolean success = new DBUpdate(dattabazzz,
    			" delete from  Enrolled    " +
    			" where        class = ? ", classID) {
    		
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	if (!success) {
    		responseStream.println(CrossMessage.generalError("Student could not be un-enrolled."));
    		return;
    	}
    	
    	
    	//
    	// Delete from Teaching
    	new DBUpdate(dattabazzz,
    			" delete from  Teaching      " +
    			" where        class = ? ", classID) {
	
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	//
    	// Delete from Available
    	new DBUpdate(dattabazzz,
    			" delete from  Available      " +
    			" where        class = ? ", classID) {
	
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	//
    	// Delete Class
    	success = new DBUpdate(dattabazzz,
    			" delete from  Class " +
    			" where        id = ?  ", classID) {
	
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	if (!success) {
    		responseStream.println(CrossMessage.generalError("This class could not be deleterated."));
    		return;
    	}    	
    	
        
    	responseStream.println(CrossMessage.success());
    }
    
    
    private void deleteStudent(CrossMessage message, PrintWriter responseStream) {
    	
    	//
    	// Validate
    	Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);
    	Long studentID = message.studentID();
    	
    	if (studentID == null) {
    		responseStream.println(CrossMessage.notUnderstood());
    		return;
    	} else if (teacherID == null  ||  !Teacher.isTeacherator(teacherID, dattabazzz)) {
    		responseStream.println(CrossMessage.loginFail());
    		return;
    	}
    	
    	
    	//
    	// Un-enroll
    	boolean success = new DBUpdate(dattabazzz,
    			" delete from  Enrolled    " +
    			" where        student = ? ", studentID) {
    		
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	if (!success) {
    		responseStream.println(CrossMessage.generalError("Student could not be un-enrolled."));
    		return;
    	}
    	
    	
    	//
    	// Delete scores
    	new DBUpdate(dattabazzz,
    			" delete from  Played      " +
    			" where        student = ? ", studentID) {
	
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	
    	//
    	// Remove student
    	success = new DBUpdate(dattabazzz,
    			" delete from  Student " +
    			" where        id = ?  ", studentID) {
	
    		public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
    	}.ex();
    	
    	if (!success) {
    		responseStream.println(CrossMessage.generalError("This student could not be deleterated."));
    		return;
    	}
    	
    	
    	
    	
    	responseStream.println(CrossMessage.success());
    	
    }
    
    
    
    
    
    
    private Map<String, Object> singleCompleteStudentInformation(long stuID) {
        
        // Run the query,
        // map out the information;
        // add to resultant dictionary
        
        final Map<String, Object> studentRecord = new HashMap<String, Object>();
        
        // Fetch the basic student information.
        // Should only run once.
        Object oneRan = new DBFetchDo<Object>(dattabazzz,
                " select  name, token " +
                " from    Student     " +
                " where   id = ?     ;", stuID) {
            
            public Object mapper(ResultSet gott) throws SQLException {
                String stuName = gott.getString(1);
                String stuToken = gott.getString(2);
                studentRecord.put("name", stuName);
                studentRecord.put("credential", stuToken);
                
                return "1r";
            }
        }.ex();
        
        // On sql failure,
        if (oneRan == null)
            return null;
        
        // Fetch all the classes in which the student is enrolled.
        // Empty list indicates no enrollment.
        // Null return indicates SQL error.
        List<String> clases = new DBFetchList<String>(dattabazzz,
                " select  Class.name                " +
                " from    Class, Enrolled           " +
                " where   Class.id = Enrolled.class " +
                "     and Enrolled.student = ?     ;", stuID) {
            
            public String mapper(ResultSet gott) throws SQLException {
                return gott.getString(1);
            }
        }.ex();
        
        if (clases == null)
            return null;
        
        
        studentRecord.put("classes", clases);
        return studentRecord;
        
    }
	
	
	private void classNameUpdate(CrossMessage message, PrintWriter responseStream) throws SQLException{

		/* get teacher ID */
        Long teacherID = Teacher.idForTeacherAlsoSpecifiedIn(message, dattabazzz);

        /* make sure we actually got the ID */
        if(teacherID == null) {
        	responseStream.println( CrossMessage.loginFail() );
        	return;
        }
        
        /* get some more information */
        String newClassName = message.name();
        String oldClassName = message.className(); 
        
        if(newClassName == null  ||  oldClassName == null)
        {
        	responseStream.println( CrossMessage.notUnderstood() );
        	return;
        }
        
        /* checking if the teacher is a teacher of this class */
        boolean isTeacherOfClass = false; 
        Set<Double> teachers = new HashSet<Double>();
        
        /* query to receive all of the teacher ids of the given class by name */
        PreparedStatement isTeacher = dattabazzz.prepareStatement(
        		"select teacher from Teaching, Class " + 
        		"	where Class.id = class           " + 
        		" 		and Class.name = ?           " +
        		";");
        
        isTeacher.setString(1, oldClassName);
        
        ResultSet result = isTeacher.executeQuery(); 
        
        /* add in all of the teacher ids returned by the query to a set */
        while(result.next())
        {
        	teachers.add(result.getDouble(1)); //i
        } 
        
        /* see if our teacher id matches any in the class */
        if(teachers.contains(teacherID))
        	isTeacherOfClass = true;
        
        /* update class name if they are a teacher of the class or the teacherator */ 
        if(Teacher.isTeacherator(teacherID, dattabazzz) || isTeacherOfClass )
        {
	        PreparedStatement changeName = dattabazzz.prepareStatement(
	        		"update Class     " + 
	        		"   set name = ?  " + 
	        		"   where name = ?" +
	        		";"); 
	        
	        changeName.setString(1, newClassName);
	        changeName.setString(2, oldClassName);
	        
	        changeName.executeUpdate(); 
	        
			responseStream.println(CrossMessage.success());
        }
        else 
        { 
        	/* we have an error or a rogue teacher */ 
        	responseStream.println( CrossMessage.loginFail() );
        }
        
	} // end of classNameUpdate method 
	
	
	
	
	
	
	
	private void createClass(CrossMessage message, PrintWriter responseStream) {
		
		Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);
		if (teacherID == null) {
			responseStream.println(CrossMessage.loginFail());
			return;
		}
		
		String classNameToCreate = message.className();
        if (classNameToCreate == null) {
            responseStream.println(CrossMessage.notUnderstood());
            return;
        }
        
		boolean success = new DBUpdate(dattabazzz,
            " insert into  Class (id, name) " +
            " values       (null, ?)        ", classNameToCreate ) {
                
                public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
        }.ex();
		
        
        if (!success) {
            responseStream.println(CrossMessage.generalError("Could not create the class " + classNameToCreate));
            return;
        }
        
        
        List<Long> addedClassIDs = new DBFetchList<Long>(dattabazzz,
                " select last_insert_rowid(); ") {
        
            public Long mapper(ResultSet gott) throws SQLException {
                return gott.getLong(1);
            }
        }.ex();
        if (addedClassIDs == null  ||  addedClassIDs.size() < 1 ) {
            responseStream.println(CrossMessage.generalError("Database failed to accept class."));
            return;
        }
        
        Long classID = addedClassIDs.get(0);
        
        
        success = assignTeacherToClass(teacherID, classID);
        if (!success) {
            responseStream.println(CrossMessage.generalError("Added class, but unable to assign teacher to it."));
            return;
        }
        
        
        
        for (int levelAvail = Konstants.LevelsMin; levelAvail <= Konstants.LevelsMax; levelAvail++)
            new DBUpdate(dattabazzz,
                    " insert into  Available (class, level) " +
                    " values       (?, ?)                   ",  classID, levelAvail) {
                
                public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
            }.ex();
        
        
        responseStream.println(CrossMessage.success());
		
	}
	
	
	
	
	
	
	
	
	private void fetchAvailableLevels(CrossMessage message, PrintWriter responseStream) {
		
		String className = message.className();
		if (className == null) {
			responseStream.println(CrossMessage.notUnderstood());
			return;
		}
		
		Integer classID = new DBFetchDo<Integer>(dattabazzz,
				"select id from Class where name = ?", className) {
			
			public Integer mapper(ResultSet gott) throws SQLException {
				return gott.getInt(1);
			}
		}.ex();
		
		if (classID == null) {
			responseStream.println(CrossMessage.generalError("The class does not exist."));
			return;
		}
		
		
		//
		// Start out with all levels unavailable.
		final Map<Integer, Boolean> availableLevels = new HashMap<Integer, Boolean>();
		for (int level = Konstants.LevelsMin; level <= Konstants.LevelsMax; level++)
			availableLevels.put(level, false);
		
		Object oneIsAvailable =
		new DBFetchDo<Object>(dattabazzz,
				" select  level                                      " +
				"   from  Available                                  " +
				"           join Class on Class.id = Available.class " +
				"  where  Class.name = ?                             ",  className ) {
			
			public Object mapper(ResultSet gott) throws SQLException {
				availableLevels.put( gott.getInt(1), true );
				return "one ran";
			}
		}.ex();
		
		//
		// If none available, set all to available.
		if (oneIsAvailable == null) {
			for (int level = Konstants.LevelsMin; level <= Konstants.LevelsMax; level++)
				availableLevels.put(level, true);
		}
		
		
		responseStream.println(CrossMessage.availableLevelsResponse(className, availableLevels));
		
	}
	
	
	
	
	
	
	
	
	private void updateAvailableLevels(CrossMessage message, PrintWriter responseStream) {
		try {
            
    		Long teacherID = Teacher.idForTeacherSpecifiedIn(message, true, true, dattabazzz);
    		if (teacherID == null) {
    			responseStream.println(CrossMessage.loginFail());
    			return;
    		}
		
    		String className = message.className();
     		Map<Number, Boolean> sexytime = message.availableLevels();
     		
     		if (className == null || sexytime == null) {
    			responseStream.println(CrossMessage.notUnderstood());
    			return;
    		}
     		
     		
     		String makeAvailableUpdate = 	" insert or replace into  Available (class, level)  " +
     										"                 values  (                         " +
     										"                            ( select id from Class " +
     										"                              where name = ?       " +
     										"                              limit 1              " +
     										"                            ),                     " +
     										"                            ?                      " +
     										"                         )                         ";
     		
     		String makeUnavailableUpdate =	" delete from  Available                      " +
     										"       where  class = ( select id from Class " +
     										"                        where name = ?       " +
     										"                        limit 1              " +
     										"                      )                      " +
     										"         and  level = ?                      ";
     		
     		
     		
     		for (int level = Konstants.LevelsMin; level <= Konstants.LevelsMax; level++) {
     			
     			Boolean levelOK = sexytime.get(level);
     			if (levelOK == null)
     				continue;
     			
     			
     			
     			boolean thatWorked = new DBUpdate(dattabazzz, 
     					(levelOK ?
     							makeAvailableUpdate :
     							makeUnavailableUpdate), className, level ) {
     				
     				public void onError(SQLException sqex) { System.out.println("Hmm..."); sqex.printStackTrace(); }
     			}.ex();
     			
     			
     			if (!thatWorked) {
     				responseStream.println(CrossMessage.generalError("The level availability for this class could not be set."));
     				return;
     			}
     			
     		}
     		
     		
     		responseStream.println(CrossMessage.success());
     		
    		
    		// We have some unchecked casts, so let them be caught:
		} catch (Exception fooo) { fooo.printStackTrace(); responseStream.println(CrossMessage.notUnderstood()); }
	}
	
	
	
	
	
	
	
	
	
	/*
	 * This is an internal method, which should not be called directly
	 * from The Switch. It can, however, be called after authentication is done.
        
        THE SPECIFIED CLASS ID MUST EXIST.
        Undefined behaviour elsewise.
	 */
	private boolean assignTeacherToClass(long teacherID, long classID) {
        
        return
        new DBUpdate(dattabazzz,
                " insert into  Teaching (teacher, class) " +
                "      values  (?, ?)                    ",  teacherID, classID) {
            
            public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
        }.ex();
        
        
	}
    
    
	/*
	 * This is an internal method, which should not be called directly
	 * from The Switch. It can, however, be called after authentication is done.
        
        THE SPECIFIED CLASS NAME MUST EXIST.
        Undefined behaviour elsewise.
	 */
	private boolean assignTeacherToClass(long teacherID, String className) {
        
        return
        new DBUpdate(dattabazzz,
                " insert into  Teaching (teacher, class)     " +
                "      values  (?,                           " +
                "                  (   select  id from Class " +
                "                      where   name = ?      " +
                "                      limit   1             " +
                "                  )                         " +
                "              )                             ", teacherID, className) {
            
            public void onError(SQLException sqex) { System.err.println("Hmmm:\n"); sqex.printStackTrace(); }
        }.ex();
        
        
	}
    
    
    
    
    
    private void assignTeacherToClass(CrossMessage message, PrintWriter responseStream) {
        Long teacherDoingAdding = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz);
        Long teacherBeingAdded = Teacher.idForTeacherSpecifiedIn(message, false, true, dattabazzz);
        String className = message.className();
        
        if (teacherDoingAdding == null) {
            responseStream.println(CrossMessage.loginFail());
            return;
        } else if (teacherBeingAdded == null) {
            responseStream.println(CrossMessage.notUnderstood());
            return;
        }
        
        if (!Teacher.isTeacherator(teacherDoingAdding, dattabazzz)) {
            // 
            // Check that they're in charge of class
            List<Long> inChargeOf = new DBFetchList<Long>(dattabazzz,
                    " select  Class.id                  " +
                    " from    Teaching, Class           " +
                    " where   Teaching.class = Class.id " +
                    "     and Teaching.teacher = ?      " +
                    "     and Class.name = ?            ",  teacherDoingAdding, className) {
			
    			public Long mapper(ResultSet gott) throws SQLException {
    				return gott.getLong(1);
    			}
    		}.ex();
            
            if (inChargeOf == null || inChargeOf.size() == 0) {
                System.err.println("Teacher attempted to add someone to class when not in charge.");
                responseStream.println(CrossMessage.loginFail());
                return;
            }
            
            // The teacher is in charge of the specified class. Proceed.
        }
        
        
        
        boolean success = assignTeacherToClass(teacherBeingAdded, className);
        
        if (!success) {
            responseStream.println(CrossMessage.generalError("The teacher could not be added to the class."));
            return;
        }
        
        responseStream.println(CrossMessage.success());
        
    }
	
	



	
	private void bootTeacherFromClass(CrossMessage message, PrintWriter responseStream) throws SQLException{
		
		/* get teacher IDs */ 
        Long teacherBootingID = Teacher.idForTeacherSpecifiedIn(message, true, false, dattabazzz); 
        Long teacherBootedID  = Teacher.idForTeacherSpecifiedIn(message, false, true, dattabazzz);
        
        /* make sure we actually got the IDs */
        if (teacherBootingID == null) {
            responseStream.println( CrossMessage.loginFail() );
            return;
        }
        
        if (teacherBootedID == null) {
            responseStream.println( CrossMessage.loginFail() );
            return;
        }         
        
        /* let's get some more information */   
        String className = message.className();
		
        /* delete teacher from class if it is either themselves or a teacherator */
        if(teacherBootingID.equals(teacherBootedID) || Teacher.isTeacherator(teacherBootingID, dattabazzz))
        {
			PreparedStatement removeTeacher = dattabazzz.prepareStatement(
					"delete from Teaching                                        " +
					"    where teacher = ?                                       " + 
					"      and class = ( select Class.id from Class, Teaching    " +
					"					        where Teaching.class = Class.id  " +
					"                             and Class.name = ?    )        " + 
					";" );
			
	        removeTeacher.setLong(1, teacherBootedID); 
	        removeTeacher.setString(2, className);
	
			removeTeacher.executeUpdate(); 
			
			responseStream.println(CrossMessage.success());
        }
        else 
        { 
        	/* we have an error or a rogue teacher */ 
        	responseStream.println( CrossMessage.loginFail() );
        }
		
		
	} // end of bootTeacherFromClass method 
    
	
	
	

	
	
	
	
	/**
	 * Loopingly accept client connections
	 * and dispatch them to a new thread to be handled.
	 * 
	 */
	public void serve() {
		Executor exec = Executors.newCachedThreadPool();
		ServerSocket sock = null;
		
		try {
			sock = new ServerSocket(Konstants.MATH_PORT_EXTREME);
			System.out.printf("MatheratorD up on %d.\n", Konstants.MATH_PORT_EXTREME);
			
			
			
			//
			// --- HAPPY PATH ---
			// 
			while (true) {
				final Socket client = sock.accept();
				exec.execute( new Runnable() {  public void run() {
					handle(client);
				}} );
				
			}
			//
			// --- ---
			// 
			
			
			
		} catch (IOException ioe) {
			System.err.println("MatheratorD could not be started:\n" + ioe);
			
		} finally {
			try { sock.close(); }
			catch (NullPointerException npe) { } catch (IOException ioe) { }
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Gets things a-goin'.
	 * 
	 * Requires that the database backing be specified on the command line.
	 * 
	 */
	public static void main(String[] args) {
		// This is pretty standard server-side fare,
		// which I won't bother to comment right now.
		
		
		
		if (args.length < 1) {
			System.err.println("Please specify for me a database file to use.");
			return;
		}
		
		
		String dbfile = args[0];
		System.out.printf("Looking for database at “%s”...\n", dbfile);
		
		Runtime.getRuntime().addShutdownHook( new Thread() {  public void run() {
			System.out.println("MatheratorD going down.");
		}} );
		
		
		
		try {
			
			new MatheratorD( dbfile ).serve();
			
			
			
			
		} catch (IOException|ClassNotFoundException|SQLException except) {
			System.out.printf("Failed to matherate:\n%s\n", except.toString());
			return;
			
		}
		
	}

}
