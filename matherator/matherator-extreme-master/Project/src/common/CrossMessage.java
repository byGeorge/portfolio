package common;

import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.*;

import common.Konstants.StudentToken;

import java.util.*;
import java.io.*;


public class CrossMessage {
	
	
	public enum Type {
		// Deprecated; not comprehensive; never will be, dammit.
		request_classes("request classes"),
		request_students("request students"),
		request_teachers("request teachers"),
		request_teacher_class_list("request teacher's class list"),
        request_complete_student_information("request complete student information"),
		request_game_progression("request game progression"),
		update_game_score("update game score"),
		response("response"),
		error("error"),
		login_fail("login fail"),
		not_understood("not understood");
		
		private final String text;
		private Type(final String typetype) { text = typetype; }
		public String toString() { return text; }
	}
	
	
	
	//
	// Class-wide utilities
	// 
	private static final Yaml safeYaml = new Yaml( new SafeConstructor() );
	private static final Yaml multiYaml;
	static {
		DumperOptions multiDump = new DumperOptions();
		multiDump.setExplicitStart(true);
		multiDump.setExplicitEnd(true);
		multiDump.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		multiDump.setLineBreak(DumperOptions.LineBreak.UNIX);
		multiYaml = new Yaml(multiDump);
	}
	
	
	//
	// The backing of a CrossMessage
	// 
	private Map<String, Object> content = null;
	
	
	
	
	
	//
	// CrossMessage Making
	// 
	
	
	
	
	/**
	 * Constructor for empty, blank message.
	 * Not sure if this would be useful publicly.
	 */
	public CrossMessage() {
		this(null);
		
	}
	
	
	/**
	 * Construct a message from the information provided by contents.
	 * @param contents - a dictionary of the information to send
	 */
	public CrossMessage(Map<String, Object> contents) {
		if (contents == null)
			content = new HashMap<String, Object>();
		else
			content = new HashMap<String, Object>(contents);
		
	}
	
	
	/**
	 * Read a number of cross messages from the input stream.
	 * @param iost - The stream across which messages are being provided.
	 * @return an iterator of CrossMessages, read and interpreted as they are accessed.
	 */
	public static Iterable<CrossMessage> messagesFrom(InputStream iost) {
		return new CrossMessageStreamer( iost );
		
	}
	
	
	
	
	
	//
	// Specialized Making
	// 
	
	
	
	
	
	
	/**
	 * All the specialized factory functions make a CrossMessage expressing
	 * some sort of intention, and accompanying details. The recipient of this
	 * message can call methods on it to gather the intention and details.
	 * 
	 * All messages can be asked for their type(), which indicates what they want to say.
	 * Based on the type (expressed in the documentation for the factory functions),
	 * particular query methods are available for the supporting information. A factory
	 * might list something like:
	 *  >  Queryable for name(), credential(), gameProgression().
	 * which indicates that the returned message may be asked, in addition to its type(),
	 * for its name via the name() method, its credential via the credential() method,
	 * and so on. The expected response type is also listed. If you send an availableLevelsRequest(),
	 * for instance, the documentation says
	 *  >  The response message is an availableLevelsResponse(), or error.
	 * which means that, to find out the methods you can call on the response message, you'd
	 * look to the availableLevelsResponse() method, which states
	 *  >  Queryable for availableLevels().
	 * as expected. If the format of the data you're looking for is not listed in the
	 * factory function area, look at the data accessor methods for guidance.
	 * 
	 * BEAR IN MIND that any of these methods, including type(), may return null or a
	 * nonsensical value, indicating the requested information was not provided. This can
	 * be normal, as in the case of a new-student token which should be auto-generated.
	 * Or it can indicate that the client sent a bad message, or that you're dealing with
	 * a malicious client; or that you're asking for information not in the protocol.
	 * THUS, anytime you use these methods, CHECK the return value; don't assume it's ok.
	 * 
	 * Messages can be divided into these categories:
	 *  -  Requests
	 *  -  Responses
	 *      -  error messages
	 * If any party receives a request, it is REQUIRED by protocol to answer with another
	 * message (or just close the connection, if it's feeling snotty). This means that
	 * the sender of a request is very strongly encouraged to read the response back from the
	 * receiver. This response will either
	 *  -  contain the requested information, if any;
	 *  -  be a CrossMessage.success(); or
	 *  -  be an error.
	 * The known error types are CrossMessage.generalError(), notUnderstood(), and
	 * loginFail(). You can check if a response is an error via its isErrorific() method.
	 * General errors carry a human-readable message describing what went wrong.
	 * isErrorific() is true also when no message type is given.
	 * 
	 * If you want to receive any meaningful information over a network socket to the server,
	 * you MUST read back exactly one response (or error) for every request you send.
	 * This way, you know that what you're reading corresponds to the request you made.
	 * 
	 */
	
	
	
	
	
	/**
	 * Make a request for the list of classes known to the server.
	 * 
	 * The response message is a classListResponse(), or error.
	 * 
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage classListRequest() {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.request_classes.toString());
		
		return mesg;
		
	}
	
	
	/**
	 * Make a response indicating the list of classes known to the server.
	 * 
	 * Queryable for classes().
	 * Does not require a response message, being itself a response.
	 * 
	 * @param list - the known class names
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage classListResponse(List<String> list) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.response.toString());
		mesg.put("classes", list);
		
		return mesg;
		
	}
	
	
	/**
	 * Make a request for the list of students known to be in the named class.
	 * 
	 * Queryable for className().
	 * The response message is a studentListResponse(), or error.
	 * 
	 * @param className - the full name of the requested class (as returned in the class list)
	 * @return a request suitable for writing directly to the network.
	 */
	public static CrossMessage studentListRequest(String className) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.request_students.toString());
		mesg.put("class", className);
		
		return mesg;
		
	}
	
	
	/**
	 * Make a response indicating the list of student names in the named class.
	 * 
	 * Queryable for studentNames(), className().
	 * Does not require a response from the receiver, being itself a response.
	 * 
	 * @param className - the name of the class which this list identifies
	 * @param list - the list of student names
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage studentListResponse(String className, List<String> list) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.response.toString());
		mesg.put("students", list);
		mesg.put("class", className);
		
		return mesg;
		
	}
	
	
	/**
	 * Make a request for the entire game progress information structure
	 * relative to a student in a class.
	 * 
	 * Queryable for className(), name() of student, credential().
	 * The response message is a gameProgressionResponse(), or error.
	 * 
	 * @param className - the name of the relevant class
	 * @param student - the name of the student
	 * @param credential - something qualifying the student to access their information.
	 * @return a request suitable for writing directly to the network.
	 */
	public static CrossMessage gameProgressionRequest(String className, String student, String studentCredential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.request_game_progression.toString());
		mesg.put("class", className);
		mesg.put("name", student);
		mesg.put("credential", studentCredential);
		
		return mesg;
		
	}
    
    
    public static CrossMessage gameProgressionRequest(String className, String student, String teacherName, String teacherCredential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.request_game_progression.toString());
		mesg.put("class", className);
		mesg.put("name", student);
        mesg.put("teacher name", teacherName);
		mesg.put("credential", teacherCredential);
		
		return mesg;
		
    }
	
	
	/**
	 * Make a response containing the entire game progress information structure.
	 * Student and class info is implied by the stream over which the response is made.
	 * 
	 * Queryable for gameProgression().
	 * Does not require a response from the receiver, being itself a response.
	 * 
	 * @param responseInfo - an object containing the response structure
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage gameProgressionResponse(Object responseInfo) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.response.toString());
		mesg.put("game progression", responseInfo);
		
		return mesg;
		
	}
	
	
	/**
	 * Make a request for the server to update a game score.
	 * This one's a bit of a doozy.
	 * 
	 * Queryable for className(), name(), credential(), level(), gameName(), gameScore().
	 * The response message is a success(), or error.
	 * 
	 * @param studentName - the name of Mz. Stu.
	 * @param className   - the name of the class
	 * @param level       - the level or unit of the game
	 * @param gameName    - the name of the game
	 * @param score       - the earned score
	 * @param credential  - yeah, yeah, we know what this is.
	 * @return a request suitable for writing directly to the network.
	 */
	public static CrossMessage gameScoreUpdate(String studentName, String className, int level, String gameName, double score, String credential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.update_game_score.toString());
		mesg.put("class", className);
		mesg.put("name", studentName);
		mesg.put("credential", credential);
		
		mesg.put("level", level);
		mesg.put("game", gameName);
		mesg.put("score", score);
		
		return mesg;
	}
	
	
	
	
	
	
	
	/**
	 * Request a list of all teachers known to the server.
	 * 
	 * The response message is a teacherListResponse(), or error.
	 * 
	 * @return like all cross message factories, a missive suitable for writing directly to the network.
	 */
    public static CrossMessage teacherListRequest() {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", Type.request_teachers.toString());
    	
    	return mesg;
    	
    }
    
    
    /**
     * Respond with a list of teachers, for various reasons.
     * 
     * Queryable for teacherNames().
     * Does not require a response from the receiver, being itself a response.
     * 
     * @param teachers
     * @return
     */
    public static CrossMessage teacherListResponse(List<String> teachers) {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", Type.response.toString());
    	mesg.put("teachers", teachers);
    	
    	return mesg;
    	
    }
	
	
	/**
	 * Request a list of classes taught by the named teacher.
	 * 
	 * Queryable for teacher's name().
	 * The response message is a classListResponse(), or error.
	 * 
	 * @param teacherName
	 * @return a request suitable for writing directly to the network.
	 */
	public static CrossMessage teacherClassListRequest(String teacherName) {
        CrossMessage mesg = new CrossMessage();
        mesg.put("type", Type.request_teacher_class_list.toString());
        mesg.put("name", teacherName);
        
        return mesg;
        
    }
	
	
	/**
	 * Request a list of teachers currently teaching the named class.
	 * 
	 * Queryable for className().
	 * The response message is a teacherListResponse(), or error.
	 * 
	 * @param className
	 * @return a request suitable for writing directly to the network.
	 */
	public static CrossMessage classTeacherListRequest(String className) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "request teachers for class");
		mesg.put("class", className);
		
		return mesg;
		
	}
	
	
	/**
	 * Add a student record to the Matherator Serer.
	 * Duplicate names will cause not conflict, but confusion on the part of the student.
	 * Null credential will cause a token to be randomly selected.
	 * 
	 * Queryable in the same way as for updateStudentRecord().
	 * The response message is a completeStudentInformationResponse(), or error.
	 * 
	 * @param studentName - the new student's name
	 * @param studentCredential - the new student credential token
	 * @param teacherName - for authentication
	 * @param teacherCredential - for authentication
	 * @return a missive suitable for writing directly to the network
	 */
	public static CrossMessage addStudent(
			String studentName,
			Konstants.StudentToken studentCredential,
			String teacherName,
			String teacherCredential) {
		
		return updateStudentRecord(
				null,
				studentName,
				studentCredential,
				teacherName,
				teacherCredential);
	}
	
	
	/**
	 * Update a student record in the Matherator Server.
	 * 
	 * All fields are required. Student is identified by ID; their name is updated.
	 * Does not affect enrollment status. Credential may be null to be auto-reselected.
	 * 
	 * Bug: If you refer to a student whose ID does not already exist,
	 * they will be added with that ID.
	 * 
	 * Queryable for the student's name(), token(), studentID();
	 * and the teacher's credential() and teacherName().
	 * The response message is a completeStudentInformationResponse(), or error.
	 * 
	 * @param studentID - the server ID for the student to update
	 * @param studentName - the updated name for the student (pass existing name to leave unchanged.)
	 * @param studentCredential - the updated student credential token
	 * @param teacherName - for authentication
	 * @param teacherCredential - for authentication
	 * 
	 * @return a missive suitable for writing directly to the network.
	 */
	public static CrossMessage updateStudentRecord(
			Long studentID,
			String studentName,
			Konstants.StudentToken studentCredential,
			String teacherName,
			String teacherCredential) {
		
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "add or update student");
		
		if (studentID != null)
			mesg.put("student id", studentID);
		
		if (studentCredential != null)
			mesg.put("token", studentCredential.name());
		
		mesg.put("name", studentName);
		mesg.put("teacher name", teacherName);
		mesg.put("credential", teacherCredential);
		
		return mesg;
	}
	
	
	/**
	 * Update the enrollment status of a student.
	 * 
	 * Either enroll a student in a class, or remove them from the class,
	 * as indicated by the boolean unEnroll flag.
	 * 
	 * Queryable for studentID(), className(), teacherName(), credential().
	 * Enrollment requests have type() == "enroll student", where
	 * un-enrollment requests have type() == "un-enroll student".
	 * The response message is a success(), or error.
     * Since there is no way to tell if an enrollment was actually eviscerated,
     * you should request, and refresh your copy of, the appropriate student list
     * after performing this enrollment update.
	 * 
	 * @param studentID - the internal student id to alter
	 * @param className - the name of the class to (un)enroll in
	 * @param unEnroll - un-enroll if true; otherwise enroll
	 * @param teacherName - for authentication
	 * @param credential - teacher's passphrase, for authentication
	 * @return a missive suitable for writing directly to the network.
	 */
	public static CrossMessage enrollmentUpdate(long studentID, String className, boolean unEnroll,
			String teacherName, String credential ) {
		
		CrossMessage mesg = new CrossMessage();
		mesg.put("type",
				unEnroll ? "un-enroll student" : "enroll student");
		
		mesg.put("student id", studentID);
		mesg.put("class", className);
		mesg.put("teacher name", teacherName);
		mesg.put("credential", credential);
		
		return mesg;
		
	}
	
	
	/**
	 * Request the availability of levels for a particular class.
	 * This is requestable by anybody, including teachers and student clients.
	 * 
	 * Queryable for className().
	 * The response message is an availableLevelsResponse(), or error.
	 * 
	 * @param className
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage availableLevelsRequest(String className) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "request available levels for class");
		mesg.put("class", className);
		
		return mesg;
		
	}
	
	
	/**
	 * The response from an availableLevelsRequest().
	 * 
	 * The format is that for availableLevelsUpdate(), except that
	 * all known levels are returned.
	 * 
	 * Queryable for availableLevels().
	 * Does not require a response message, being itself one.
	 * 
	 * @param className
	 * @param availability - the availability dictionary
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage availableLevelsResponse(String className, Map<Integer, Boolean> availability) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "response");
		mesg.put("available levels", availability);
		
		return mesg;
		
	}
	
	
	/**
	 * Update the available levels for a particular class.
	 * 
	 * Takes a dictionary of levels -> availability, where each level is an integer,
	 * and availability is indicated by a value of true. It is not required to send
	 * every single level in one message; only specified levels will be updated. (The
	 * default availability is true, if never set.)
	 * 
	 * Queryable for className(), availableLevels(), teacherName(), credential().
	 * The response message is a CrossMessage.success(), or error.
	 * 
	 * @param className
	 * @param availableLevels - the dictionary as described above
	 * @param teacherName
	 * @param credential - teacher's password
	 * @return a message suitable for writing directly to the network.
	 */
	public static CrossMessage availableLevelsUpdate(String className, Map<Integer, Boolean> availableLevels,
			String teacherName, String credential) {
		
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "update available levels for class");
		mesg.put("class", className);
		mesg.put("available levels", availableLevels);
		mesg.put("teacher name", teacherName);
		mesg.put("credential", credential);
		
		return mesg;
		
	}
	
	
	
	
	/**
	 * Add a teacher to the server.
	 * May only be performed by the Teacherator.
	 * 
	 * Queryable for the new teacher's name(),
	 * as well as the teacherator's teacherName() and credential().
	 * Response is a CrossMessage.success(), or error.
	 * 
	 * @param teacherNameToAdd
	 * @param teacherNameDoingAdding
	 * @param credential  teacherator's
	 * @return a request suitable for printing directly to the server.
	 */
	public static CrossMessage createTeacherRequest(String teacherNameToAdd, String teacheratorName, String credential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "create teacher");
		mesg.put("name", teacherNameToAdd);
		mesg.put("teacher name", teacheratorName);
		mesg.put("credential", credential);
		
		return mesg;
	}
	
	
	
	/**
	 * Delete a teacher from the server.
	 * May only be performed by the Teacherator; but
	 * teacherator cannot delete themself.
	 * 
	 * Queryable for the poofing teacher's name(),
	 * as well as the teacherator's teacherName() and credential().
	 * Response is a CrossMessage.success(), or error.
	 * 
	 * @param teacherNameToDelete
	 * @param teacheratorName
	 * @param credential
	 * @return
	 */
	public static CrossMessage deleteTeacherRequest(String teacherNameToDelete, String teacheratorName, String credential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "delete teacher");
		mesg.put("name", teacherNameToDelete);
		mesg.put("teacher name", teacheratorName);
		mesg.put("credential", credential);
		
		return mesg;
	}
	
	
	
	
	public static CrossMessage updatePassphraseRequest(String teacherName, String presentCredential, String newPassphrase) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "update passphrase");
		mesg.put("name", teacherName);
		mesg.put("credential", presentCredential);
		mesg.put("passphrase", newPassphrase);
		
		return mesg;
	}
	
	
	
	
	
	
	public static CrossMessage teacheratorinessRequest(String teacherName, String credential) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "request teacheratoriness");
		mesg.put("name", teacherName);
		mesg.put("credential", credential);
		
		return mesg;
	}
	
	
	public static CrossMessage teacheratorinessResponse(boolean isTeacherator) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", "response");
		mesg.put("teacheratoriness", isTeacherator);
		
		return mesg;
	}
	
	
	
    
    
    
	// A teacher class list response is the same
	// as a general class list response.
    
    
    // Student list request & response are likewise previously implemented.
    
    
    
	/**
	 * Make a request for (semi-)complete student information.
	 * 
	 * You may specify:
	 *  - just the student id,
	 *  - just the student name,
	 *  - just the class name (all students in class), or
	 *  - the student and class names.
	 * In all but the first case, multiple records may be returned.
	 * Any may return no record, if no matching student was found.
	 * 
	 * The information returned does not include game progression or scores.
	 * Send a gameProgressionRequest() to retrieve that information.
	 * 
	 * Queryable for className(); student's name() and studentID();
	 * and teacher's teacherName() and credential(). 
	 * The response message is a completeStudentInformationResponse(), whose documentation
	 * you should read to see the returned data format; or error.
	 * 
	 * @param studentID
	 * @param studentName
	 * @param className
	 * @param teacherName - for authentication
	 * @param credential - for authentication
	 * @return a missive suitable for writing directly to the network.
	 */
    public static CrossMessage completeStudentInformationRequest(Long studentID, String studentName, String className,
    		String teacherName, String credential) {
    	
        CrossMessage mesg = new CrossMessage();
        mesg.put("type", Type.request_complete_student_information.toString());
        
        mesg.put("class", className);
        mesg.put("name", studentName);
        mesg.put("student id", studentID);
        
        mesg.put("teacher name", teacherName);
        mesg.put("credential", credential);
        
        return mesg;
        
    }
    
    
    /**
     * Response containing (semi-)complete information about one or more students.
     * 
     * The dictionary contained within this response maps student IDs -> records,
     * and contains zero or more entries (as per the parameters of the request and the
     * relevant results in the database).
     * 
     * A record is itself a map, with the following structure:
     * (Keys are strings; values are strings, or lists thereof):
     *  - name: the student's name
     *  - credential: the student's login token*
     *  - classes: a list of the class names in which the student is enrolled
     * 
     * *note: this is not the appropriate way for the student client to log in, but is
     *        intended instead for the teacher client. Student clients should use
     *        the gameProgressionRequest/Response as a litmus test indicating
     *        whether their login was successful.
     *        
     * This message is queryable for studentInformation(), returning
     * the dictionary described above.
     * Does not require a response from the receiver, as it is itself a response.
     * 
     * @param information
     * @return a missive suitable for writing directly to the network.
     */
    public static CrossMessage completeStudentInformationResponse(Map information) {
        CrossMessage mesg = new CrossMessage();
        mesg.put("type", Type.response.toString());
        mesg.put("student information", information);
        
        return mesg;
        
    }
    
    
    
    
    /**
     * Make a request to delete a student from the server.
     * Implies that the student should also be un-enrolled from
     * classes and have their game scores deleted.
     * Must be done by the teacherator; regular teachers should just
     * un-enroll students from their classes.
     * 
     * Queryable for studentID(), and teacherator's name() and credential().
     * 
     * @param studentID  The ID of the student must be given, to prevent ambiguity.
     * @param teacherName  for authentication
     * @param credential  for authentication
     * @return
     */
    public static CrossMessage deleteStudentRequest(long studentID, String teacherName, String credential) {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", "delete student");
    	mesg.put("student id", studentID);
    	mesg.put("name", teacherName);
    	mesg.put("credential", credential);
    	
    	return mesg;
    }
    
    
    
    
    
    /**
     * Make a request to add a class to the server.
     * Implies that the requesting teacher be added to the class, even if the teacher is teacherator.
     * 
     * Queryable for className(), and teacher's name() and credential().
     * 
     * @param newClassName
     * @param teacherName  to be added to the class
     * @param credential  for authentication
     * @return
     */
    public static CrossMessage createClassRequest(String newClassName, String teacherName, String credential) {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", "create class");
        mesg.put("class", newClassName);
        mesg.put("name", teacherName);
        mesg.put("credential", credential);
        
        return mesg;
    }
    
    
    
    /**
     * Make a request to delete a class.
     * Teacher may only delete a class of which they are in charge (unless they're the teacherator).
     * Does *not* delete any students, but does remove their enrollment in the class.
     * Does *not* affect game scores, as those are unrelated to the class particular.
     * 
     * Queryable for className(), and teacher's name() and credential().
     * 
     * @param classNameToDelete
     * @param teacherName  for authentication
     * @param credential  for authentication
     * @return
     */
    public static CrossMessage deleteClassRequest(String classNameToDelete, String teacherName, String credential) {
    	CrossMessage mesg = new CrossMessage();
        mesg.put("type", "delete class");
        mesg.put("class", classNameToDelete);
        mesg.put("name", teacherName);
        mesg.put("credential", credential);
        
        return mesg;
    }
    
    public static CrossMessage deleteClassResponse()
    {
    	return success();
    }
    
    
    /**
     * Make a request to add a teacher to a class.
     * This must be a class which the teacher doing the adding is already in charge of,
     * unless they're the teacherator.
     * 
     * Queryable for className(), teacherName() to add, and the adding teacher's name() and credential().
     * 
     * @param className  must already exist
     * @param teacherNameToAdd  new teacher to add to the db, by name
     * @param teacherDoingTheAdding  who must already be attached to the class
     * @param credential  for authentication
     * @return
     */
    public static CrossMessage assignTeacherToClassRequest(String className, String teacherNameToAdd, String teacherDoingTheAdding, String credential) {
    	CrossMessage mesg = new CrossMessage();
        mesg.put("type", "assign teacher to class");
        mesg.put("class", className);
        mesg.put("teacher name", teacherNameToAdd);
        mesg.put("name", teacherDoingTheAdding);
        mesg.put("credential", credential);
        
        return mesg;
    }
    
    /**
     * Make a request to change the name of the class. 
     * 
     * @param newClassName
     * @param oldClassName 
     * @param teacherName
     * @param credential
     * @return 
     */
    public static CrossMessage changeClassNameRequest(String newClassName, String oldClassName, String teacherName, String credential)
    {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", "change class name"); 
    	mesg.put("class", oldClassName);
    	mesg.put("name", newClassName); 
        mesg.put("teacher name", teacherName);
        mesg.put("credential", credential);
    	
    	return mesg;
    }
    
    public static CrossMessage changeClassNameResponse()
    {
    	return success();
    }
    
    
    /**
     * Make a request to boot a teacher from a class.
     * The teacher to boot must match the teacher doing the booting, unless the teacher
     * doing the booting is the teacherator.
     * 
     * Queryable for className(), teacherName() to boot, and the booting teacher's name() and credential().
     * 
     * @param className
     * @param teacherNameToBoot  who must currently be assigned to the class
     * @param teacherNameDoingTheBooting  who must be the teacher to boot, or teacherator.
     * @param credential  for authentication
     * @return
     */
    public static CrossMessage bootTeacherFromClassRequest(String className, String teacherNameToBoot, String teacherNameDoingTheBooting, String credential) {
    	CrossMessage mesg = new CrossMessage();
        mesg.put("type", "boot teacher from class");
        mesg.put("class", className);
        mesg.put("teacher name", teacherNameToBoot);
        mesg.put("name", teacherNameDoingTheBooting);
        mesg.put("credential", credential);
        
        return mesg;
    }
    
    
    public static CrossMessage bootTeacherFromClassResponse()
    {
    	return success();
    }
    
    
    
    
    
    /**
     * The message sent when no explicit response was requested,
     * and no error occurred, but indicates that the requested operation
     * was received and completed without problem.
     * 
     * @return such a message for returning to the requestor.
     */
    public static CrossMessage success() {
    	CrossMessage mesg = new CrossMessage();
    	mesg.put("type", "success");
    	
    	return mesg;
    	
    }
	
	
	
	
	
	
	
	
	/**
	 * Make a general, runtime error response.
	 * 
	 * This is like a login fail, in that it is not exceptional;
	 * but more generic in that it can have any arbitrary meaning.
	 * (Not-understood is an example of a true exception.)
	 * @param message - the human-readable problem that was encountered
	 * @return a complaint suitable for writing directly to the network
	 */
	public static CrossMessage generalError(String message) {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.error.toString());
		mesg.put("error", message);
		
		return mesg;
		
	}
	
	
	/**
	 * Make a not-understood response.
	 * Should not happen in common usage. 
	 */
	public static CrossMessage notUnderstood() {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.not_understood.toString());
		
		return mesg;
		
	}
	
	
	/**
	 * Make a login-failed response.
	 */
	public static CrossMessage loginFail() {
		CrossMessage mesg = new CrossMessage();
		mesg.put("type", Type.login_fail.toString());
		
		return mesg;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//
	// Getting Info
	// 
	
	
	
	
	/**
	 * The semantic type of this message.
	 * For the list of known values, see the CrossMessage.Type enum.
	 * 
	 * @return null if unknown
	 */
	public String type() {
		return stringGet("type");
		
	}
	
	
	/**
	 * The string value of any information provided by this message,
	 * as accessed by the keey.
	 * @param keey - The name of the property of the message to get.
	 * @return null if unknown
	 */
	public String stringGet(String keey) {
		Object vahl = content.get(keey);
		if (vahl == null)
			return null;
		
		return vahl.toString();  // though we hope it's already a string!
		
	}
	
	
	/**
	 * @param keey - The name of the property of the message to get.
	 * @return the numeric value provided by this message associated with keey.
	 */
	public Number numericGet(String keey) {
		Object vahl = content.get(keey);
		if (!(vahl instanceof Number))
			return null;
		
		return (Number)vahl;
	}
	
	
	/**
	 * The list value of any information provided by this message,
	 * as accessed by the keey.
	 * @param keey - The name of the property of the message to get.
	 * @return null if unknown or not list-type.
	 */
	public List listGet(String keey) {
		Object vahl = content.get(keey);
		if (!(vahl instanceof List))
			return null;
		
		return (List)vahl;
		
	}
	
	
	public Boolean booleanGet(String keey) {
		Object vahl = content.get(keey);
		if (!(vahl instanceof Boolean))
			return null;
		
		return (Boolean)vahl;
	}
	
	
	public boolean teacheratoriness() {
		Boolean isTeacherator = booleanGet("teacheratoriness");
		if (isTeacherator == null) return false;
		return isTeacherator;
	}
	
	
	private boolean allItemsAre(List collection, Class type) {
//		for (Object item : collection)
//			if (!type.isInstance(item))
//				return false;
		
		return true;
	}
	
	
	private boolean allItemsAre(Map collection, Class keyType, Class valueType) {
//		for (Object keey : collection.keySet())
//			if (!keyType.isInstance(keey))
//				return false;
//		
//		for (Object vahl : collection.values())
//			if (!valueType.isInstance(vahl))
//				return false;
		
		return true;
	}
	
	
	/**
	 * It's just like ListGet, but returns a general Map.
	 * @param keey
	 * @return
	 */
    public Map mapGet(String keey) {
        Object vahl = content.get(keey);
        if (!(vahl instanceof Map))
            return null;
        
        return (Map)vahl;
    }
	
    
	/**
	 * Name is the name of whoever's the subject, whether that be
	 * a student or a teacher.
	 * @return the user's name
	 */
	public String name() {
		return stringGet("name");
	}
	
	
	/**
	 * If both a student and teacher are involved in a transaction,
	 * name() is the student's, and this is the teacher's.
	 * @return the teacher's name; usu. for authentication purposes.
	 */
	public String teacherName() {
		return stringGet("teacher name");
	}
    
    
	/**
	 * @return the student's server id
	 */
    public Long studentID() {
        Number stid = numericGet("student id");
        if (stid == null)
            return null;
        return  stid.longValue();
    }
    
    
    public List<Long> studentIDs() {
        List iids = listGet("student ids");
        if (!allItemsAre(iids, Number.class)) {
            System.err.println("Non-number student ids received");
            return null;
        }
        
        List<Long> result = new ArrayList<Long>( iids.size() );
        for (Number received : (List<Number>)iids)
            result.add(received.longValue());
        
        return result;
    }
	
	
	/**
	 * @return the user's credentials
	 */
	public String credential() {
		return stringGet("credential");
	}
    
    
    /**
     * @return the student's updated token
     */
    public StudentToken token() {
        String strTok = stringGet("token");
        StudentToken tokTok;
        try {
        	tokTok = StudentToken.valueOf(strTok);
        	
        } catch (IllegalArgumentException|NullPointerException except) {
        	return null;
        }
        
        return tokTok;
    }
    
	
	/**
	 * @return the specified class
	 */
	public String className() {
		return stringGet("class");
	}
	
	
	/**
	 * @return the list of class names
	 */
	public List<String> classNames() {
		// TODO bake in type satety
		return listGet("classes");
	}
	
	
	/**
	 * @return the list of student names
	 */
	public List<String> studentNames() {
		// TODO bake in type satety
		return listGet("students");
	}
    
    
	/**
	 * @return the list of teacher names
	 */
    public List<String> teacherNames() {
    	// TODO bake in type satety
        return listGet("teachers");
    }
	
	
    /**
     * TODO let -1 be unspecified
     * @return the game level specified, or zero if unspecified.
     */
	public int level() {
		Number level = numericGet("level");
		return  level == null  ? 0  : level.intValue();
	}
	
	
	/**
	 * The available levels, as described in availableLevelsUpdate().
	 * The key is guaranteed to either be an Integer or Long, so you can ask for its longValue().
	 * @return the available levels record.
	 */
	public Map<Number, Boolean> availableLevels() {
		Map record = mapGet("available levels");
		return  allItemsAre(record, Integer.class, Boolean.class)
			 || allItemsAre(record, Long.class,    Boolean.class)
					? (Map<Number, Boolean>)record
					: null;
	}
	
	
	/**
	 * @return the name of the game associated with this message
	 */
	public String gameName() {
		return stringGet("game");
	}
	
	
	/**
	 * @return the score of the game associated with this message
	 */
	public double gameScore() {
		Number score = numericGet("score");
		return  score == null  ? -1  : score.doubleValue();
	}
	
	
	/**
	 * @return the detailed game progression info associated with this message
	 */
	public Map gameProgression() {
        return mapGet("game progression");
    }
    
	
	/**
	 * The student information record set, as described in completeStudentInformationResponse().
	 * Studend ID keys are guaranteed to respond to longValue(), but the internal maps,
	 * as yet, are unchecked.
	 * @return the student information record set.
	 */
    public Map<Number, Map> studentInformation() {
        Map stinfo = mapGet("student information");
//        if ( !allItemsAre( stinfo, Integer.class, Map.class )   ||
//        	 !allItemsAre( stinfo, Long.class,    Map.class ) )
//        	return null;
        
        return (Map<Number, Map>)stinfo;
    }
    
    
    
    /**
     * @return the error message included with general, error-type responses.
     */
    public String error() {
    	return stringGet("error");
    }
    
    
    
    /**
     * @return whether this message indicates some sort of error (or has no type).
     */
    public boolean isErrorific() {
    	String type = type();
    	
    	if (	type == null                                   ||
    			Type.error.toString().equals(          type )  ||
    			Type.not_understood.toString().equals( type )  ||
    			Type.login_fail.toString().equals(     type )     )
    		return true;
    	
    	return false;
    }
	
	
	
    
    
    
    
    
	
	//
	// Sending
	// 
	
	
	
	
	/**
	 * A string suitable for sending over the network,
	 * and subsequent reception by the expectant party
	 * (especially another CrossMessage listener).
	 */
	public String toString() {
		return multiYaml.dump(content);
		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Modify one of the values in the message.
	 * This is not available to the public, so as to make the structure immutable.
	 * @param keey - the string key
	 * @param vahl - the object value
	 */
	private void put(String keey, Object vahl) {
		content.put(keey, vahl);
		
	}
	
	
	
	
		
	
	
	
	
	
	//
	//
	// Reading CrossMessages from the network
	// 
	// 
	
	
	
	
	
	
	
	/**
	 * An iterator over CrossMessages
	 * being live-parsed from the network.
	 * 
	 */
	private static class CrossMessageIterator implements Iterator<CrossMessage> {
		private BufferedReader internalStream;
		
		private CrossMessageIterator(InputStream stream) {
			internalStream = new BufferedReader( new InputStreamReader( stream ) );
			
		}

		public boolean hasNext() {
			return true;  // Because we can't return 'maybe' or 'try and see.'
		}
		
		private String readDocument() {
			StringWriter builder = new StringWriter();
			PrintWriter collector = new PrintWriter( builder );
			
			try {
				String lastLine = "";
				do {
					lastLine = internalStream.readLine();
					if (lastLine == null)  break;
					collector.println(lastLine);
					
				} while ( !lastLine.startsWith("...") );  // End-of-document marker
				
			} catch (IOException ioe) {  }  // Expected; ret. what we have so far.
			
			return builder.toString();
		}
		
		
		public CrossMessage next() {
			try {
            
    			String document = this.readDocument();
    			if (document == null) {  return null;  }  // End-of-stream
    			
    			Object mesg = safeYaml.load(document);
    			
    			if (!(mesg instanceof Map)) {  return null;  }
    			
    			Map<String, Object> message = (Map<String, Object>)mesg;
    			return new CrossMessage(message);
			
            } catch (RuntimeException error) {
                // If something unforseeable goes wrong,
                // assume a stream failure and terminate the sequence.
            	// This includes null-pointer and index-bounded exceptions.
            	try { internalStream.close(); } catch (IOException|NullPointerException except) {  }
                return null;
                
            }
            
		}

		public void remove() {
			throw new UnsupportedOperationException();
			
		}

		
	}
	
	
	
	
	
	/**
	 * An iterable over CrossMessages
	 * being live-parsed over the network.
	 *
	 */
	private static class CrossMessageStreamer implements Iterable<CrossMessage> {
		private InputStream internalIterable;
		
		private CrossMessageStreamer(InputStream stream) {
			internalIterable = stream;
			
		}
		
		public Iterator<CrossMessage> iterator() {
			return new CrossMessageIterator(internalIterable);
			
		}
		
	}

	
	
	
}
