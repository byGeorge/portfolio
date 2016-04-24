package studentapp;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

public class AnimationUtil {
	
	// This is not lazily initialized because
	// we need animations to start like lightning.
	private static final Timer fooTimer = new Timer();
	
	private AnimationUtil() { }
	
	
	/**
	 * If you schedule something on this timer, make sure it does
	 * not block or run very long. That means use
	 * SwingUtilities.invokeLater(...), not .invokeAndWait(...)
	 * 
	 * @return the shared animation timer instance.
	 */
	public static Timer globalTimer() {
		return fooTimer;
	}
	
	

}
