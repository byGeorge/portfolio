package serverapp.daemon;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.CrossMessage;
import common.Konstants;





/*
 * It is VERY IMPORTANT that
 * the database matheratord is using
 * is not tied to any actual, production database.
 * This test will perform destructive changes, and not clean them up.
 * The database must be re-set to known defaults before each run.
 */






public class TestMatheratorD {
	Socket extremeServer;
	PrintWriter matheratord;
	Iterator<CrossMessage> responses;

	@Before
	public void setUp() throws Exception {
		extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
		matheratord = new PrintWriter( extremeServer.getOutputStream(), true );
		responses = CrossMessage.messagesFrom( extremeServer.getInputStream() ).iterator();
	}

	@After
	public void tearDown() throws Exception {
		extremeServer.close();
	}

	
	
	
	
	
	
	@Test
	public void testDeleteStudent() {
		
		// get student information, check for Jimmy Wicket.
		// get student list for classes 1 and 2, check for same.
		// delete student 21
		// get student information again, check for absence of Jimmy Wicket.
		// get student lists again, check for same.
		
		matheratord.println( CrossMessage.completeStudentInformationRequest(21l, null, null, "Matsuzaki Satomi", "hamburger") );
		matheratord.println( CrossMessage.studentListRequest("Matsuzaki/Truffle's 1st Graders") );
		matheratord.println( CrossMessage.studentListRequest("Mr. Beam's 1st Graders") );
		
		String completeInfo = responses.next().toString();
		String class1Stus = responses.next().toString();
		String class2Stus = responses.next().toString();
		
		assertTrue("Jimmy Wicket must be in the DB to start with.",
				completeInfo.contains("Jimmy Wicket"));
		
		assertTrue("Jimmy Wicket must be in class 1 to start with.",
				class1Stus.contains("Jimmy Wicket"));
		
		assertTrue("Jimmy Wicket must be in class 2 to start with.",
				class2Stus.contains("Jimmy Wicket"));
		
		
		// perform deletion
		
		matheratord.println( CrossMessage.deleteStudentRequest(21l, "Matsuzaki Satomi", "hamburger") );
		assertFalse("Deletion mustn't fail.", responses.next().isErrorific());
		
		
		
		// repeat test, inverse.
		
		matheratord.println( CrossMessage.completeStudentInformationRequest(21l, null, null, "Matsuzaki Satomi", "hamburger") );
		matheratord.println( CrossMessage.studentListRequest("Matsuzaki/Truffle's 1st Graders") );
		matheratord.println( CrossMessage.studentListRequest("Mr. Beam's 1st Graders") );
		
		completeInfo = responses.next().toString();
		class1Stus = responses.next().toString();
		class2Stus = responses.next().toString();
		
		assertFalse("Jimmy Wicket must be deleted from the db.",
				completeInfo.contains("Jimmy Wicket"));
		
		assertFalse("Jimmy Wicket must no longer be in class 1.",
				class1Stus.contains("Jimmy Wicket"));
		
		assertFalse("Jimmy Wicket must no longer be in class 2.",
				class2Stus.contains("Jimmy Wicket"));
		
		
		
		// TODO verify scores deleted aswell.
		// ...
		// (Aswell is a town in Texas.)
		
		
	}
	
	
	
	
	
	
	@Test
	public void testCreateClass() {
		
		// Confirm that:
		// - class did not exist
		// - class gets created
		// - class requesting teacher is added to class
		
		matheratord.println( CrossMessage.classListRequest() );
		String classList = responses.next().toString();
		assertFalse("Class Royal Dunces must not yet exist.",
				classList.contains("Royal Dunces"));
		
		matheratord.println( CrossMessage.createClassRequest("Royal Dunces", "Bonnie Dicks", "hamburger") );
		assertFalse("Class creation mustn't fail.",
				responses.next().isErrorific());
		
		matheratord.println( CrossMessage.classListRequest() );
		matheratord.println( CrossMessage.teacherClassListRequest("Bonnie Dicks") );
		
		assertTrue("Royal Dunces class must be present on the server after creating it.",
				responses.next().toString().contains("Royal Dunces"));
		assertTrue("Mrs. Dicks must be assigned to teach the Royal Dunces after creating it.",
				responses.next().toString().contains("Royal Dunces"));
		
	}
	
	
	
	
	
	@Test
	public void testDeleteClass() {
		
		// Confirm that:
		// - class "Mrs. Dicks's 2nd Graders" exists
		// - deleting under a different teacher fails
		// - class gets deleted
		// - class is no longer associated with Mrs. Dicks
		// - class no longer reports available levels
		// - class no longer reports students enrolled
		
		matheratord.println( CrossMessage.classListRequest() );
		assertTrue("Mrs. Dicks's 2nd Graders must already exist.",
				responses.next().toString().contains("Mrs. Dicks's 2nd Graders"));
		
		matheratord.println( CrossMessage.deleteClassRequest("Mrs. Dicks's 2nd Graders", "Jim Beam", "hamburger") );
		assertTrue("Deleting the class by a non-teacherator, non-owner, must fail.",
				responses.next().isErrorific());
		
		matheratord.println( CrossMessage.deleteClassRequest("Mrs. Dicks's 2nd Graders", "Bonnie Dicks", "hamburger") );
		assertFalse("Deleting the class by an owner must not fail.",
				responses.next().isErrorific());
		
		
		
		matheratord.println( CrossMessage.classListRequest() );
		matheratord.println( CrossMessage.teacherClassListRequest("Mrs. Dicks") );
		matheratord.println( CrossMessage.availableLevelsRequest("Mrs. Dicks's 2nd Graders") );
		matheratord.println( CrossMessage.completeStudentInformationRequest(
				null, null, "Mrs. Dicks's 2nd Graders", "Bonnie Dicks", "hamburger") );
		
		assertFalse("Mrs. Dicks's class must no longer be present in the server.",
				responses.next().toString().contains("Dicks"));
		assertFalse("Mrs. Dicks's class list must no longer show her class.",
				responses.next().toString().contains("2nd Graders"));
		assertTrue("Mrs. Dicks's available levels must have been obliterated.",
				responses.next().isErrorific());
		assertFalse("Mrs. Dicks must no longer be associated with Stalin.",
				responses.next().toString().contains("Stalin"));
		
		
	}
	
	
	
	
	@Test
	public void testAvailableLevels() {
		
		// Confirm that:
		// - Mrs. Rene's 2nd Graders can play levels including 4,5,7.
		// - Mrs. Rene can add level 8,
		// -           can remove level 5,
		// - those changes are reflected,
		// - and level 4 is still available,
		// - but level 1 is still not.
		
		matheratord.println(CrossMessage.availableLevelsRequest("Mrs. Rene's 2nd Graders"));
		CrossMessage levelsResponse = responses.next();
		assertTrue("Mrs. Rene's 2nd Graders must be able to play level 5 at first.",
				levelsResponse.availableLevels().get(5));
		assertFalse("Mrs. Rene's 2nd Gradres must not be able to play level 8 at first.",
				levelsResponse.availableLevels().get(8));
		
		Map<Integer, Boolean> levelChanges = new HashMap<Integer, Boolean>();
		levelChanges.put(5, false);
		levelChanges.put(8, true);
		
		matheratord.println(CrossMessage.availableLevelsUpdate("Mrs. Rene's 2nd Graders", levelChanges, "Mrs. Rene", "hamburger"));
		matheratord.println(CrossMessage.availableLevelsRequest("Mrs. Rene's 2nd Graders"));
		
		assertFalse("Changing the level availability mustn't outright fail.",
				responses.next().isErrorific());
		
		CrossMessage levelsLeft = responses.next();
		assertFalse("Mrs. Rene's 2nd Gradres must not be able to play level 5.",
				levelsLeft.availableLevels().get(5));
		assertTrue("Mrs. Rene's 2nd Gradres must be able to play level 8.",
				levelsLeft.availableLevels().get(8));
		assertTrue("Mrs. Rene's 2nd Gradres must be able to play level 4.",
				levelsLeft.availableLevels().get(4));
		assertFalse("Mrs. Rene's 2nd Gradres must not be able to play level 1.",
				levelsLeft.availableLevels().get(1));
		
		
		
		
	}
	
	
	
	
	@Test
	public void testAssignOtherTeacherToClass() {
		
		// Verify that:
		// - other teacher is not in class
		// - Mr. Truffle cannot add himself
		// - other teacher gets added
		
		matheratord.println( CrossMessage.classTeacherListRequest("Mrs. Rene's 2nd Graders") );
		assertFalse("Mrs. Rene's class mustn't have Mr. Truffle to begin with.",
				responses.next().toString().contains("Alfred Truffle"));
		
		
		matheratord.println( CrossMessage.assignTeacherToClassRequest(
				"Mrs. Rene's 2nd Graders", "Alfred Truffle", "Alfred Truffle", "hamburger") );
		matheratord.println( CrossMessage.assignTeacherToClassRequest(
				"Mrs. Rene's 2nd Graders", "Alfred Truffle", "Mrs. Rene", "hamburger") );
		
		matheratord.println( CrossMessage.classTeacherListRequest("Mrs. Rene's 2nd Graders") );
		
		assertTrue("Alfred Truffle mustn't be able to add himself to the class.",
				responses.next().isErrorific());
		assertFalse("Mrs. Rene must be able to add Mr. Truffle to the class.",
				responses.next().isErrorific());
		
		String listOfTeachersNow = responses.next().toString();
		assertTrue("Mrs. Rene's 2nd graders must now be associated with both Mrs. Rene and Alfred Truffle.",
				listOfTeachersNow.contains("Mrs. Rene") && listOfTeachersNow.contains("Alfred Truffle"));
		
	}
	
	
	
	@Test
	public void testRecuseSelfFromClass() {
		
		// Verify that:
		// - teacher is over class
		// - other teacher cannot boot them
		// - teacher can recuse themself
		// - teacherator can add teacher back
		// - teacherator can remove them once again
		
		// teacherator is Matsuzaki Satomi.
		
		matheratord.println( CrossMessage.classTeacherListRequest("Mr. Beam's 1st Graders") );
		assertTrue("Jim Beam must be in charge of his class to start out.",
				responses.next().toString().contains("Jim Beam"));
		
		matheratord.println( CrossMessage.bootTeacherFromClassRequest(
				"Mr. Beam's 1st Graders", "Jim Beam", "Bonnie Dicks", "hamburger") );
		matheratord.println( CrossMessage.bootTeacherFromClassRequest(
				"Mr. Beam's 1st Graders", "Jim Beam", "Jim Beam", "hamburger") );
		matheratord.println( CrossMessage.teacherClassListRequest("Jim Beam") );
		matheratord.println( CrossMessage.classTeacherListRequest("Mr. Beam's 1st Graders"));
		
		assertTrue("Mr. Beam cannot be booted by Mrs. Dicks.",
				responses.next().isErrorific());
		assertFalse("Mr. Beam must be able to recuse himself from his own class.",
				responses.next().isErrorific());
		assertFalse("Mr. Beam must not be in charge of any more first graders.",
				responses.next().toString().contains("1st Graders"));
		assertFalse("Mr. Beam must not be in charge of his 1st graders.",
				responses.next().toString().contains("Beam"));
		
		// TODO add back with teacherator.
		
		
		
	}
	
	
	
	
	
	@Test
	public void testCreateDeleteTeacher() {
		
		// Confirm that:
		// Carter the Valiant is not present;
		// Teacherator can add him;
		// and that he is reflected later.
		
		
		matheratord.println(CrossMessage.teacherListRequest());
		assertFalse("Carter the Valiant must not already be in the database.",
				responses.next().toString().contains("Carter"));
		
		matheratord.println(CrossMessage.createTeacherRequest("Carter the Valiant", "Matsuzaki Satomi", "hamburger"));
		matheratord.println(CrossMessage.teacherListRequest());
		
		assertFalse("Adding Carter the Valiant must not fail outright.",
				responses.next().isErrorific());
		assertTrue("Carter the Valiant must have been added to the database.",
				responses.next().toString().contains("Carter"));
		
		
		
		// Assign Carter to a class.
		
		matheratord.println(CrossMessage.assignTeacherToClassRequest("Mr. Beam's 1st Graders", "Carter the Valiant", "Matsuzaki Satomi", "hamburger"));
		
		
		
		// Confirm that:
		// We can delete Carter from the database;
		// he is gone when we check;
		// and he is not listed under Mr. Beam's 1st graders anymore.
		
		matheratord.println(CrossMessage.deleteTeacherRequest("Carter the Valiant", "Matsuzaki Satomi", "hamburger"));
		matheratord.println(CrossMessage.teacherListRequest());
		matheratord.println(CrossMessage.classTeacherListRequest("Mr. Beam's 1st Graders"));
		
		responses.next();  // We assume assigning to class worked.
		
		assertFalse("Deleting Carter the Valiant must not fail outright.",
				responses.next().isErrorific());
		assertFalse("Carter the Valiant must no longer be in the database.",
				responses.next().toString().contains("Carter"));
		assertFalse("Carter the Valiant must no longer be in charge of Mr. Beam's class.",
				responses.next().toString().contains("Carter"));
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}








