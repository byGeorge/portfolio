package studentapp;
import java.net.*;
import java.util.*;
import java.io.*;

import common.*;



/**
 * 
 * This is the client for the student side :)
 * 
 * 
 * 
 * @author The Feckless Ellipses
 *
 */
public class StudentClient {
	Socket 						extremeServer;
	PrintWriter 				toServer;
	Iterator<CrossMessage> 		responses;
	String						tn = "teacher";
	String						sn = "Student";
	String 						to = "token";
	
	public StudentClient(){
		try {
			connectToDatabase();
		} catch (IOException e) {
			System.out.println("Error: Unable to connect to database");
		}
	}
	
	public StudentClient(String teacher, String student, String token){
		try {
			connectToDatabase();
			tn = teacher;
			sn = student;
			to = token;
		} catch (IOException e) {
			System.out.println("Error: Unable to connect to database");
		}
	}
	
	/** 
	 * Method to get list of classes from database
	 * @return a String[] of classes
	 */
	public String[] getClassList(){
		String[] toReturn = null;
		toServer.println( CrossMessage.classListRequest() );
		List <String> classNames = responses.next().classNames();
		toReturn = classNames.toArray(new String[0]);
		return toReturn;
	}
	
	/**
	 * Will return a list of student names from database
	 * @param teacher: a String containing the teacher's name
	 * @return a String[] containing student names from the specified teacher
	 * @throws IOException
	 */
	public String[] getStudentList(String teacher) throws IOException{
		String[] toReturn = null;
		toServer.println( CrossMessage.studentListRequest(teacher) );
		List<String> studentNames = responses.next().studentNames();
		toReturn = studentNames.toArray(new String[0]);
		return toReturn;
	}
	
	public boolean[] getAvailableLevels(String teacher){
		toServer.println( CrossMessage.availableLevelsRequest(teacher) );
		boolean[] toReturn = new boolean[10];
		CrossMessage levels = responses.next();
		for (int i = 0; i < 10; i++){
			toReturn[i] = levels.availableLevels().get(i);
		}
		return toReturn;
	}
	
	public boolean verify(String teacher, String student, String token){
		toServer.println( CrossMessage.gameProgressionRequest(teacher, student, token) );
		CrossMessage response = responses.next();
		if (response.isErrorific()) 
			return false;
		else{
			tn = teacher;
			sn = student;
			to = token;
			return true;
		}
	}
	
	public double[] getGamesCompleted(String game){
		double[] toReturn = new double[10];
		toServer.println( CrossMessage.gameProgressionRequest(tn, sn, to) );
		System.out.println(CrossMessage.gameProgressionRequest(tn, sn, to));
		CrossMessage mesg = responses.next();  // read the response from the server.
		System.out.println(mesg.isErrorific());
		Map<Integer, Map<String, Double>> progression = mesg.gameProgression();
		// if we can't get game progression, then all levels are 0
		if (progression == null) {
			for (int i = 0; i<10; i++)
				toReturn[i] = 0;
			return toReturn;
		}
		//This for is at fault... changed 1 to 0, 10 to 9, toReturn[level-1] to just level and below also.
		for (int level = 0; level <= 9; level++) {
			Map<String, Double> levelScores = progression.get(level);
			// if the level doesn't have a score yet, that level is 0
			if (levelScores == null) {
				toReturn[level] = 0;
			}
			else { // otherwise, give it the score from the database
				Double kxScoreObj = levelScores.get(game);
				double kxScore =  kxScoreObj == null  ?  0  :  kxScoreObj;
				toReturn[level] = kxScore;
			}
		}
		return toReturn;
	}
	
	public double getScore(String game, int level) throws IOException{
		double toReturn = 0;
		toServer.println( CrossMessage.gameProgressionRequest(tn, sn, to) );
		CrossMessage mesg = responses.next();  // read the response from the server.
		Map<Integer, Map<String, Double>> progression = mesg.gameProgression();
		// if we can't get game progression, then all levels are 0
		if (progression == null) {
			return toReturn;
		}
		for (int i = 1; i <= 10; i++) {
			Map<String, Double> levelScores = progression.get(i);
			// if the level doesn't have a score yet, that level is 0
			if (levelScores == null) {
				return toReturn;
			}
			else { // otherwise, give it the score from the database
				Double kxScoreObj = levelScores.get(game);
				double kxScore =  kxScoreObj == null  ?  0  :  kxScoreObj;
				toReturn = kxScore;
			}
		}
		return toReturn;
	}
	
	public void updateScore(int level, String game, double score) throws IOException{
		toServer.println( CrossMessage.gameScoreUpdate(sn, tn, level, game , score, to) );
	}
	
	private void connectToDatabase() throws IOException{
		extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
		toServer = new PrintWriter( extremeServer.getOutputStream(), true );
		responses = CrossMessage.messagesFrom(
				extremeServer.getInputStream()
			).iterator();
	}
	
	public void close(){
		try {
			extremeServer.close();
		} catch (IOException e) {
			System.out.println("Error: Unable to close server");
		}
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
		//
		// Connect to the server (here, localhost)
		Socket extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
		
		//
		// Establish a PrintWriter to the server so that we can println to it.
		// NOTE the argument `true` which auto-flushes on print. This is important!
		PrintWriter toServer = new PrintWriter( extremeServer.getOutputStream(), true );
		
		//
		// Grab an iterator over the responses from the server.
		// MessagesFrom() returns an iterable, which can also be used in foreach loops;
		// we, however, explicitly want its iterator() instead.
		Iterator<CrossMessage> responses = CrossMessage.messagesFrom(
					extremeServer.getInputStream()
				).iterator();
		
		//
		// Formulate the request message...
		CrossMessage gameRequest = CrossMessage.gameProgressionRequest(
				"Matsuzaki/Truffle's 1st Graders",
				"Tiny Terrorist",
				"");
		
		//
		// ...and send it to the server.
		toServer.println(gameRequest);
		
		//System.out.println("Sending:");
		//System.out.println(gameRequest);
		
		//
		// Wait for the server's first response (.next()) and print it out.
		// For now, I'll put the raw source of the message right out.
		//System.out.println("Waiting...");
		System.out.println( responses.next() );
		
		
		
		//
		// Now let's ask for the list of classes,
		// this time iterating over the responses ourself.
		toServer.println( CrossMessage.classListRequest() );
		List<String> classNames = responses.next().classNames();
		
		System.out.println("Classes are:");
		for (String clas : classNames) {
			System.out.printf("%s, ", clas);
		}
		System.out.println();
		
		
		
		
		//
		// Suppose, after some hemming and hawing, we've decided we're interested
		// in the second class. Let's get the list of students associated with that class.
		if (classNames.size() < 2)  return;
		String classWereInterestedIn = classNames.get(1);
		
		toServer.println( CrossMessage.studentListRequest(classWereInterestedIn) );
		List<String> studentNames = responses.next().studentNames();
		System.out.printf("Students in %s are:\n", classWereInterestedIn);
		for (String student : studentNames) {
			System.out.printf("%s, ", student);
		}
		System.out.println();
		
		
		
		
		// Note that if you use messagesFrom() in a foreach loop---i.e.
		//     for (CrossMessage thisMessage : CrossMessage.messagesFrom(inStream)) {...
		// 
		//---you MUST include in the loop the line
		//         if (thisMessage == null)  break;
		// 
		// because the iterator can't know, in advance, when to stop.
		// See MatheratorD#handle() for an example of this.
		
		
		extremeServer.close();

	}

}
