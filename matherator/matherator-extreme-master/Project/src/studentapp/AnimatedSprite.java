package studentapp;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import common.Konstants;

public class AnimatedSprite extends BoringSprite{
	List<Image> 		sprite; // animation frames
	Timer 				ding; // schedules future events
	long 				timeyWimey = 0; // length of animation in milliseconds
	long				switchA = 0;
	boolean 			isLooped = false; // whether or not the image is looped
	AnimationTimer		timeMe;
	volatile Runnable	doneDoer;
	volatile boolean	isAnimating;

	/** 
	 * This class will allow a JPanel to show an animation.
	 * 
	 * @param frames 
	 * @param atX X axis offset
	 * @param atY Y axis offset
	 * @param loopy - whether or not the animation should be looped
	 * @param animationTime - The total animation time
	 */
	public AnimatedSprite(List<Image> frames, int atX, int atY, boolean loopy, long animationTime){
		this(frames, atX, atY, loopy, animationTime, null);
	}
	
	/** 
	 * This class will allow a JPanel to show an animation.
	 * 
	 * @param frames 
	 * @param atX X axis offset
	 * @param atY Y axis offset
	 * @param loopy - whether or not the animation should be looped
	 * @param animationTime - The total animation time
	 * @param whenDoneDo - what to do when the animation finishes
	 */
	public AnimatedSprite(List<Image> frames, int atX, int atY, boolean loopy, long animationTime, Runnable whenDoneDo){
		super(frames.get(0), atX, atY);
		isAnimating = false;
		isLooped = loopy;
		switchA = animationTime;
		timeMe = null;
		sprite = frames;
		ding = AnimationUtil.globalTimer();
		doneDoer = whenDoneDo;
		this.revalidate();
		this.repaint();
	}
	
	public void whenDoneDo(Runnable runoff) {
		doneDoer = runoff;
	}
	
	public void whenDoneDont() {
		doneDoer = null;
	}
	
	public void startAnimation(){
		ding = AnimationUtil.globalTimer();
		timeyWimey = System.currentTimeMillis();
		super.image = sprite.get(0);
		if (!isAnimating) {
			isAnimating = true;
			timeMe = new AnimationTimer(this);
			ding.schedule(timeMe, (switchA/sprite.size()), Konstants.AnimationFrameDelayMS);
		}
	}

	public void nextThing(){
		long cur = System.currentTimeMillis();
		long passed = cur - timeyWimey;
		double progression = (double)passed / (double)switchA;
		
		if (cur < timeyWimey){
			super.image = sprite.get(0);
		}
		else if (passed < switchA){
			super.image = sprite.get((int) (progression * sprite.size()));
		}
		else if (isLooped){
			// The progression - (int)progression slices off the
			// integer part of the floating progression, wrapping it into range.
			// For instance, 19.235 - 19 = 0.235.
			super.image = sprite.get((int) (( progression - (int)progression ) * sprite.size()));
		}
		else{
			stopAnimation();
		}
	}
	
	public void stopAnimation(){
		if (!isAnimating)
			return;
		
		isAnimating = false;
		timeMe.cancel();
		if (doneDoer != null)
			doneDoer.run();
	}
	
	protected class AnimationTimer extends TimerTask{
		AnimatedSprite sprt;
		
		protected AnimationTimer(AnimatedSprite spritely){
			sprt = spritely;
		}
		
		public void run(){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					sprt.nextThing();
					sprt.revalidate();
					sprt.repaint();
				}
			});
			
		}
		
	}
}
