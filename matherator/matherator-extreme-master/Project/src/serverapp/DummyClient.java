package serverapp;
import java.net.*;
import java.util.*;
import java.io.*;

import common.*;



/**
 * 
 * A class which demonstrates some techniques
 * for interacting with the server.
 * 
 * 
 * 
 * @author The Feckless Ellipses
 *
 */
public class DummyClient {
	
	
	
	
	
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
		
		
		
		
		
		if (System.currentTimeMillis() > 1) {
			toServer.println(CrossMessage.availableLevelsRequest("Mrs. Rene's 2nd Graders"));
			System.out.println(responses.next());
			
			return;
			
		}
		
		
		
		
		
		// What follows is a sample procedure for using cross messages
		// to communicate with matheratord. It has been obliviated by
		// the above return, for testing purposes.
		// As is, only code above this point will run; but the rest is
		// left for educational purposes.
		
		
		
		
		
		
		//
		// Formulate the request message...
		CrossMessage gameRequest = CrossMessage.gameProgressionRequest(
				"Matsuzaki/Truffle's 1st Graders",
				"Tiny Terrorist",
				"");
		
		//
		// ...and send it to the server.
		toServer.println(gameRequest);
		
		
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
	
	
	
	
	
	
	
	
	/**
	 * Talus is testing some things here.
	 * @param toServer
	 * @param responses
	 */
	private static void doTests(PrintWriter toServer, Iterator<CrossMessage> responses) {
//		toServer.println( CrossMessage.addStudent("Jimmy Wicket", null, "Mrs. Rene", "hamburger") );
//		responses.next();
//		toServer.println( CrossMessage.enrollmentUpdate(21, "Mr. Beam's 1st Graders", false, "Jim Beam", "hamburger") );
//		System.out.println(responses.next());
//		toServer.println( CrossMessage.enrollmentUpdate(21, "Matsuzaki/Truffle's 1st Graders", false, "Jim Beam", "hamburger") );
//		System.out.println(responses.next());
//		toServer.println( CrossMessage.completeStudentInformationRequest(21l, null, null, "Jim Beam", "hamburger") );
//		System.out.println(responses.next());
//		toServer.println( CrossMessage. );
		
//		CrossMessage cm = CrossMessage.addStudent("Johnny Rocket", Konstants.StudentToken.random(), "Mrs. Rene", "hamburger");
//	    toServer.println(cm);
//	    System.out.println(cm);
//	    System.out.println(responses.next());
		

	}
	
	
	

}
