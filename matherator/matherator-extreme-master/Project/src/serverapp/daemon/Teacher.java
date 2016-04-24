package serverapp.daemon;

import java.sql.*;

import common.CrossMessage;

public class Teacher {

	
	public static boolean validate(String teacherName, String credential, Connection database) {
		boolean ookk = false;
		
		try {
			PreparedStatement asker = database.prepareStatement(
					" select id from Teacher where name = ? and passphrase = ?; ");
			asker.setString(1, teacherName);
			asker.setString(2, credential);
			
			ResultSet iter = asker.executeQuery();
			if (iter.next())
				ookk = true;
			
		} catch (SQLException sqe) { ookk = false; }
		
		return ookk;
	}
	
	
	public static Long idForTeacherSpecifiedIn(CrossMessage message, Connection database) {
		return idForTeacherSpecifiedIn(message, true, false, database);
		
	}
	
	
	public static Long idForTeacherAlsoSpecifiedIn(CrossMessage message, Connection database) {
		return idForTeacherSpecifiedIn(message, true, true, database);
		
	}
	
	
	public static Long idForTeacherSpecifiedIn(CrossMessage message, boolean checkCredential, boolean teacherIsSecondary, Connection database) {
		if (message == null)
			return null;
		
		String teachName;
		if (teacherIsSecondary)
			teachName = message.teacherName();
		else
			teachName = message.name();
		
		String credential = message.credential();
		
		if (teachName == null)
			return null;
		
		if (checkCredential && credential == null)
			return null;
		
		if (checkCredential && !validate(teachName, credential, database))
			return null;
		
		
		return idForTeacher(teachName, database);
		
	}
	
	public static boolean isTeacherator(long teacherID, Connection database)
	{		
		boolean teacherator = false; 
		
		try {
			
			PreparedStatement question = database.prepareStatement(
					"select teacher from Teacherator " + 
					"	where teacher = ?            " +
					";");
			
			question.setLong(1, teacherID); 
		
			ResultSet result = question.executeQuery();
			if(result.next())
				teacherator = true;
		
		
		} catch(SQLException sqle)
		{
			System.err.printf("SQLException trying to get id from Teacherator. \n%s\n", sqle);
		}
		
		return teacherator;
	}
	
	
	private static Long idForTeacher(String teacherName, Connection database) {
		Long teachID = null;
		
		try {
			PreparedStatement asker = database.prepareStatement(
					" select id from Teacher where name = ?; ");
			asker.setString(1, teacherName);
			
			ResultSet iter = asker.executeQuery();
			if (iter.next())
				teachID = iter.getLong(1);
			
			
		} catch (SQLException sqe) {
			System.err.printf("SQLException trying to get id for teacher:\n%s\n", sqe);
			teachID = null;
			
		}
		
		return teachID;
		
	}
	
	

}
