package serverapp.daemon;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.*;
import common.CrossMessage;




/**
 * 
 * This is a model class for interfacing with the database.
 * It is strictly for use within the server app, and has
 * ABSOLUTELY NO RELATION WHATSOEVER to possible student objects
 * in other applications.
 * 
 * If a client needs to talk about students, it should do so
 * on the network using the standard CrossMessaging system.
 * 
 * Bug: Students in the same class must have unique names.
 * 
 *
 */

public class Student {
	
	
	/**
	 * Validate the login credential of the student.
	 * 
	 * @param studentName
	 * @param className
	 * @param credential - like a password or something
	 * @param database - an open and active database connection to use
	 * @return true  (currently stubbed)
	 */
	public static boolean validate(String studentName, String className, String credential, Connection database) {
		boolean ookk = false;
		
		try {
			PreparedStatement asker = database.prepareStatement(
					" select  Student.id                                       " +
					"   from  Student                                          " +
					"           join Enrolled on Student.id = Enrolled.student " +
					"           join Class    on Enrolled.class = Class.id     " +
					"  where  Student.name = ?                                 " +
					"    and  Student.token = ?                                " +
					"    and  Class.name = ?                                   " );
			asker.setString(1, studentName);
			asker.setString(2, credential);
			asker.setString(3, className);
			
			ResultSet iter = asker.executeQuery();
			if (iter.next())
				ookk = true;
			
		} catch (SQLException sqex) { ookk = false; }
		
		return ookk;
	}
	
	
	public static boolean validate(long studentID, String credential, Connection database) {
		boolean ookk = false;
		
		try {
			PreparedStatement asker = database.prepareStatement(
					" select name  from Student     " +
					"   where id = ?  and token = ? " );
			asker.setLong(1,  studentID);
			asker.setString(2, credential);
			
			ResultSet iter = asker.executeQuery();
			if (iter.next())
				ookk = true;
			
		} catch (SQLException sqex) { ookk = false; }
		
		return ookk;
	}
	
	
	/**
	 * Fetch the database ID of the student mentioned in message.
	 * Requires that the message contains name, class, and credential.
	 * 
	 * @param message containing student-identifying information
	 * @param database - and open and active database connection to use
	 * @return the database ID of the student, or -1 on error or not found.
	 */
	public static long idForStudentSpecifiedIn(CrossMessage message, Connection database) {
		if (message == null)
			return -1;
		
		String stuName = message.name();
		String stuClass = message.className();
		String credential = message.credential();
		
		if (stuName == null || stuClass == null || credential == null)
			return -1;
		
		if (!validate(stuName, stuClass, credential, database))
			return -1;
		
		
		return idForStudent(stuName, stuClass, database);
		
	}
	
	public static Long idForStudentSpecifiedIn(CrossMessage message, boolean checkCredential, Connection database) {
		if (message == null)
			return null;
		
		Long stuID = message.studentID();
		String stuName = message.name();
		String stuClass = message.className();
		String credential = message.credential();
		
		if ( stuID == null && (stuName == null || stuClass == null) )
			return null;
		
		if (checkCredential && credential == null)
			return null;
		
		if (checkCredential) {
			if (stuID != null) {
				if (!validate(stuID, credential, database))
					return null;
			} else {
				if (!validate(stuName, stuClass, credential, database))
					return null;
			}
			
		}
		
		
		return (stuID == null) ? idForStudent(stuName, stuClass, database) : stuID;
		
	}
	
	
	/**
	 * Fetch the database ID of the student in the specified class.
	 * 
	 * @param studentName
	 * @param className
	 * @param database
	 * @return the database ID, or -1 on error or not found.
	 */
	public static long idForStudent(String studentName, String className, Connection database) {
		long stuID = -1;
		
		try {
			PreparedStatement asker = database.prepareStatement(
					" select Student.id from Student, Enrolled, Class " +
					" where Student.name = ?  and  Class.name = ? " +
					" and Student.id = Enrolled.student  and  Enrolled.class = Class.id ;" );
			asker.setString(1, studentName);
			asker.setString(2, className);
			
			ResultSet iter = asker.executeQuery();
			if (iter.next())
				stuID = iter.getLong(1);
			
			
		} catch (SQLException sqe) {
			System.err.printf("SQLException trying to get id for student:\n%s\n", sqe.toString());
			stuID = -1;
			
		}
		
		return stuID;
		
	}

}
