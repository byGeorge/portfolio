package studentapp.monty;

import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.*;

import common.BezierTiming;
import common.CrossMessage;
import common.Img;
import common.Konstants;
import studentapp.AnimatedSprite;
import studentapp.AnimationUtil;
import studentapp.MatheratorStudent;
import studentapp.BoringSprite;
import studentapp.GameSelect;
import studentapp.QuestionatorExtreme;

public class MazeOfMontyHalls extends JPanel implements ActionListener {
	
	MatheratorStudent parentContainer;
	AtomicInteger mode;
	/*
	 * Modes:
	 *   0 - startup
	 *  10 - default: waiting on input, updating fields, ...
	 *  20 - animating
	 *  90 - teardown 
	 */
	
	
	Timer eventPipe;
	
	
	QuestionatorExtreme questionator;
	final int currentLevel;
	final String studentName;
	final String className;
	final String studentToken;
	
	JButton exitButton;
	
	volatile String currentQuestion;
	volatile int leftAnswer, centerAnswer, rightAnswer;
	Door leftDoor, centerDoor, rightDoor;
	volatile Door.Which correctDoor;
	boolean didChangeQuestionThisAnimation;
	boolean scoredThisChamber;
	JLabel questionArea;
	JLabel leftAnswerLabel, centerAnswerLabel, rightAnswerLabel;
	
	Image chamberImage;
	Image tigerImage;
	
	Color bgcolor = Color.white;
	Color cromulentGrey = new Color(181/255f, 175/255f, 175/255f);
	
	Socket extremeServer;
	PrintWriter toServer;
	Iterator<CrossMessage> serverResponses;
	Executor serverQueue;
	
	final int questionCount = 20;
	int questionsAsked;
	int questionsCorrect;  // on first try, of course
	
	
	
	
	
	
	public MazeOfMontyHalls(MatheratorStudent appc, String student, String clas, int level, String token) {
		super();
		this.setLayout(null);
		
		currentLevel = level;
		parentContainer = appc;
		studentName = student;
		className = clas;
		studentToken = token;
		
		
		serverQueue = Executors.newFixedThreadPool(1);
		questionator = new QuestionatorExtreme(level);
		
		questionsAsked = 0;
		questionsCorrect = 0;
		mode = new AtomicInteger(0);
		eventPipe = AnimationUtil.globalTimer();
		didChangeQuestionThisAnimation = false;
		scoredThisChamber = false;
		
		
		
		layShitOut();
		setUpNewQuestion();
		openServerConnection();
		
		
		
		
		SwingUtilities.invokeLater( new Runnable () { public void run() {
			mode.set(10);
		} } );

		
		
	}
	
	
	
	private void openServerConnection() {
		try {
			extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
			
			toServer = new PrintWriter( extremeServer.getOutputStream(), true );
			serverResponses = CrossMessage.messagesFrom( extremeServer.getInputStream() ).iterator();
			
		} catch (IOException ioex) { System.err.println("Nerpaderp."); ioex.printStackTrace(); }
	}
	
	
	
	private void layShitOut() {
		JLabel[] answerLabels = new JLabel[3];
		
		// TODO remove magic numbers
		int horzwiseCenterOfScreen = 1000 / 2;
		int gapBetweenDoors = 12;
		int leftSideOfCenterDoor = horzwiseCenterOfScreen - Door.DoorWidth / 2;
		int leftSideOfLeftDoor = leftSideOfCenterDoor - gapBetweenDoors - Door.DoorWidth;
		int leftSideOfRightDoor = horzwiseCenterOfScreen + Door.DoorWidth / 2 + gapBetweenDoors;
		
		int vertwiseCenterOfScreen = 700 / 2;
		int topSideOfAllDoors = vertwiseCenterOfScreen - 35 - Door.DoorHeight / 2;
		
		int questionAreaWidth = 180;
		int questionAreaHeight = 60;
		int leftSideOfQuestionArea = horzwiseCenterOfScreen - questionAreaWidth / 2;
		
		int labelHeight = 17;
		int gapBetweenTexts = 5;
		int topSideOfAnswerLabels = topSideOfAllDoors + Door.DoorHeight + gapBetweenTexts;
		int topSideOfQuestionArea = topSideOfAnswerLabels + labelHeight + 4 * gapBetweenTexts;
		int inset = 75;
		
		
		
		// questionArea = new JTextArea();
		questionArea = new JLabel();
		questionArea.setBounds(leftSideOfQuestionArea, topSideOfQuestionArea, questionAreaWidth, questionAreaHeight);
		questionArea.setHorizontalAlignment(SwingConstants.CENTER);
		questionArea.setBackground(cromulentGrey);
		questionArea.setOpaque(true);
		add(questionArea);
		
		
		
		for (int labelIndex = 0; labelIndex < 3; labelIndex++) {
			JLabel cromulentLabel = new JLabel();
			cromulentLabel.setHorizontalAlignment(SwingConstants.CENTER);
			cromulentLabel.setSize(Door.DoorWidth - inset - inset, labelHeight);
			cromulentLabel.setBackground(cromulentGrey);
			cromulentLabel.setOpaque(true);
			
			add(cromulentLabel);
			answerLabels[labelIndex] = cromulentLabel;
		}
		
		leftAnswerLabel   = answerLabels[0];
		centerAnswerLabel = answerLabels[1];
		rightAnswerLabel  = answerLabels[2];
		leftAnswerLabel.setLocation(   leftSideOfLeftDoor + inset,   topSideOfAnswerLabels );
		centerAnswerLabel.setLocation( leftSideOfCenterDoor + inset, topSideOfAnswerLabels );
		rightAnswerLabel.setLocation(  leftSideOfRightDoor + inset,  topSideOfAnswerLabels );
		
		
		
		try {
			
			leftDoor = new Door( Door.Which.ON_LEFT, leftSideOfLeftDoor, topSideOfAllDoors,
					new Runnable() {public void run() { answerODasu(Door.Which.ON_LEFT); }} );
			
			centerDoor = new Door( Door.Which.CENTER, leftSideOfCenterDoor, topSideOfAllDoors,
					new Runnable() {public void run() { answerODasu(Door.Which.CENTER); }} );
			
			rightDoor = new Door( Door.Which.ON_RIGHT, leftSideOfRightDoor, topSideOfAllDoors,
					new Runnable() {public void run() { answerODasu(Door.Which.ON_RIGHT); }} );
			
			for (Door door : Arrays.asList(leftDoor, centerDoor, rightDoor))
				add(door);
			
			tigerImage = ImageIO.read( Img.get("/studentapp/monty/graphics/tiger-leaping.png") );
			chamberImage = ImageIO.read( Img.get("/studentapp/monty/graphics/chamber.png") );
			
			
			
		} catch (IOException ioex) {
			// TODO meaningful error message & handling
			ioex.printStackTrace();
			backToGameSelect();
		}
		
		
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener( new ActionListener() { public void actionPerformed(ActionEvent actevt) {
			backToGameSelect();
		} } );
		exitButton.setBounds(Konstants.ExitButtonBounds);
		addZ(exitButton, 0);
 		
		
		
	}
	
	
	
	public void setNeedsDisplay() {
		SwingUtilities.invokeLater( new Runnable () { public void run() {
			mvSync();
			revalidate();
			repaint();
		} } );
	}
	
	
	
	
	private void mvSync() {
		questionArea.setText(String.format("<html><p><b>%s</b></p></html>", currentQuestion));
		leftAnswerLabel.setText(String.format("<html><p><b>%d</b></p></html>", leftAnswer));
		centerAnswerLabel.setText(String.format("<html><p><b>%d</b></p></html>", centerAnswer));
		rightAnswerLabel.setText(String.format("<html><p><b>%d</b></p></html>", rightAnswer));
	}
	
	
	
	protected void setUpNewQuestion() {
		// This all is like three times as verbose as it needs
		// to be to get the job done. Oh, well.
		
		questionator.newQ(currentLevel);
		
		currentQuestion = questionator.simpleMathQ();
		int correctPosition = Konstants.RandomatorExtreme.nextInt(3);
		switch (correctPosition) {
			case 0:		correctDoor = Door.Which.ON_LEFT;	break;
			case 1:		correctDoor = Door.Which.CENTER;	break;
			case 2:		correctDoor = Door.Which.ON_RIGHT;	break;
		}
		
		int correctAnswer = questionator.getAnswer();
		int[] incorrectAnswers = questionator.getWrongNumbers(2);
		
		int[] answerSlots = new int[3];
		answerSlots[ correctPosition ] = correctAnswer;
		answerSlots[ (correctPosition + 1) % 3 ] = incorrectAnswers[0];
		answerSlots[ (correctPosition + 2) % 3 ] = incorrectAnswers[1];
		
		leftAnswer = answerSlots[0];
		centerAnswer = answerSlots[1];
		rightAnswer = answerSlots[2];
		
		leftDoor.setDoorOpen(false);
		centerDoor.setDoorOpen(false);
		rightDoor.setDoorOpen(false);
		
		scoredThisChamber = false;
		
		setNeedsDisplay();
		
	}
	
	
	
	protected void answerODasu(Door.Which whichDoor) {
		int tehMode = mode.get();
		if (tehMode < 10 || tehMode >= 20)
			return;
		
		if (whichDoor == correctDoor) {
			if (!scoredThisChamber)
				recordAnswer(true);
			animateToNewChamber();
			
		} else {
			if (!scoredThisChamber)
				recordAnswer(false);
			animateTiger(whichDoor);
			
		}
		
		
	}
	
	
	
	private void recordAnswer(boolean wasCorrect) {
		// Record exactly one answer per chamber.
		scoredThisChamber = true;
		
		// This may only ever happen on the interface thread.
		final int questionsAnswered = ++questionsAsked;
		if (wasCorrect)
			questionsCorrect++;
		final int questionsRight = questionsCorrect;
		
		if (questionsAnswered >= 3) {
			// Send off to the server.
			serverQueue.execute(new Runnable() { public void run() {
				double earnedScore = (double)questionsRight / questionsAnswered;
				toServer.println(CrossMessage.gameScoreUpdate(studentName,
						className,
						currentLevel,
						"Maze of Monty Halls",
						earnedScore,             studentToken));
				
				CrossMessage result = serverResponses.next();
				if (result.isErrorific()) {
					System.err.println("Oopsies:");
					System.err.println(result);
					
				} else {
					// noop
					
				}
				
			} });
		}
	}
	
	
	
	protected void animateToNewChamber() {
		boolean couldStartAnimation = kickOffAnimation();
		if (!couldStartAnimation)  return;
		
		Door throughway = doorAt(correctDoor);
		throughway.setDoorOpen(true);
		
		Image snapshot = currentScreen();
		final BoringSprite zommage = new BoringSprite(snapshot, 0, 0);
		final BezierTiming glideAmount = new BezierTiming(0.4, 0, 0.99, 0.99);
		
		
		final long delayToStartTime = 1000 / 4;
		final double startTime = System.currentTimeMillis() + delayToStartTime;
		final double duration = 1.5 * 1000;
		final TimerTask chamberStep;
		
		// TODO eliminate magic numbers:
		final double swoopFinish = 0.5;
		final double fadeStart = 0.8;
		
		final int viewportWidth = this.getWidth();
		final int viewportHeight = this.getHeight();
		final int doorLeft = throughway.getX() + 95;
		final int doorTop = throughway.getY() + 90;
		final int doorWidth = throughway.getWidth() - 95 - 95;
		
		final int snapshotEndWidth = (int)Math.round( (double)viewportWidth * viewportWidth / doorWidth );
		final int snapshotEndLeft = (int)Math.round( -1.0 * snapshotEndWidth * doorLeft / viewportWidth );
		final int snapshotEndTop = (int)Math.round( -1.0 * ( (double)viewportHeight / viewportWidth )
														  * snapshotEndWidth * doorTop / viewportHeight );
		
		
		
		addZ(zommage, 1);
		
		
		/*
		 * Schedule a recurring timer task to perform the animation.
		 * Within the timer task, invoke back to the interface queue
		 * to perform the actual drawring.
		 */
        final AtomicBoolean stillAnimating = new AtomicBoolean(true);
		chamberStep = new TimerTask() { public void run() {
			final TimerTask internalReferenceToTheCurrentTimer = this;
			
				// Here we're invoking and waiting so that the timer doesn't 
				// back up with spurious drawing events.
				SwingUtilities.invokeLater( new Runnable() { public void run() {
					
					// Do the following at each step in the animation:
					double currentTime = System.currentTimeMillis();
					double progression = (currentTime - startTime) / duration;
					
					if (progression < 0) {
						// noop (yet highly unlikely)
						
					} else if (progression < fadeStart) {
						// do swoop,
						// but be careful to truncate the black here and not over-animate.
						if (progression > swoopFinish)
							progression = swoopFinish;
						
						double swoopP = progression / swoopFinish;
						double animP = glideAmount.at(swoopP);  // 0..1
						zommage.scaleToWidth( (int)( (animP) * snapshotEndWidth + (1 - animP) * viewportWidth ) );
						zommage.setLocation( (int)(animP * snapshotEndLeft), (int)(animP * snapshotEndTop));
						
						
					} else if (progression >= 1) {
						// do end
                        boolean willPerformStop = stillAnimating.compareAndSet(true, false);
                        if (!willPerformStop)  return;
                        
						internalReferenceToTheCurrentTimer.cancel();
						remove(zommage);
						cleanupAfterAnimation();
						
						if (questionsAsked >= 15)
							backToGameSelect();
						
						
					} else if (progression >= fadeStart) {
						// TODO fade
						if (!didChangeQuestionThisAnimation) {
							setUpNewQuestion();
							didChangeQuestionThisAnimation = true;
						}
						
						
					}
					
					setNeedsDisplay();

					
				} } );
			
			
		} };
		
		// Start the whole shebang after a moment's pause:
		eventPipe.schedule(chamberStep, delayToStartTime, Konstants.AnimationFrameDelayMS);
		
	}
	
	
	
	protected void animateTiger(Door.Which fromDoor) {
		Door deathway = doorAt(fromDoor);
		if (deathway.doorIsOpen())
			return;
		
		boolean couldStartAnimation = kickOffAnimation();
		if (!couldStartAnimation)  return;
		
		deathway.setDoorOpen(true);
		
		final BezierTiming glideAmount = new BezierTiming(0.9, 0, 1, 0.99);
		final BezierTiming glideHeight = new BezierTiming(0, 0.9, 0.99, 1);
		
		
		final double startTime = System.currentTimeMillis();
		final double duration = 3/5. * 1000;
		final TimerTask taigaStep;
		
		final int viewportWidth = this.getWidth();
		final int viewportHeight = this.getHeight();
		final int doorCenterX = deathway.getX() + deathway.getWidth() / 2;
		final int doorCenterY = deathway.getY() + deathway.getHeight() / 2;
		
		
		final BoringSprite taiga = new BoringSprite(tigerImage, doorCenterX, doorCenterY);
		taiga.scaleToWidth(1);
		addZ(taiga, 1);
		
		/*
		 * Schedule a recurring timer task to perform the animation.
		 * Within the timer task, invoke back to the interface queue
		 * to perform the actual drawring.
		 */
		taigaStep = new TimerTask() { public void run() {
			final TimerTask internalReferenceToTheCurrentTimer = this;
			
				// Here we're invoking and waiting so that the timer doesn't 
				// back up with spurious drawing events.
				SwingUtilities.invokeLater( new Runnable() { public void run() {
					
					// Do the following at each step in the animation:
					double currentTime = System.currentTimeMillis();
					double progression = (currentTime - startTime) / duration;
					
					if (progression <= 0) {
						// noop (yet kinda unlikely)
						
					} else if (progression < 1) {
						// do swoop
						
						// The trick here is that taiga, in scaling to width,
						// immediately knows its own height!  ^_^
						double animP = glideAmount.at(progression);  // 0..1
						double heightP = glideHeight.at(progression);
						taiga.scaleToWidth( (int)( (animP) * 2 * viewportWidth ) );
						taiga.setLocation( (int)( (1-animP) * doorCenterX  -  (animP) * viewportWidth / 4 ),
								(int)( doorCenterY - (heightP * taiga.getHeight() / 2) ));
						
						
					} else {
						// do end
						internalReferenceToTheCurrentTimer.cancel();
						remove(taiga);
						cleanupAfterAnimation();
						
						
					}
					
					setNeedsDisplay();

					
				} } );
			
			
		} };
		
		// Start the whole shebang after a moment's pause:
		eventPipe.schedule(taigaStep, Konstants.AnimationFrameDelayMS, Konstants.AnimationFrameDelayMS);
		
		
	}
	
	
	
	protected boolean kickOffAnimation() {
		return mode.compareAndSet(10, 20);
	}
	
	
	
	private void cleanupAfterAnimation() {
		didChangeQuestionThisAnimation = false;
		mode.set(10);
	}
	
	
	
	public Image currentScreen() {
		BufferedImage animFrame = new BufferedImage(1000, 700, BufferedImage.TYPE_INT_ARGB);
		Graphics2D animGrapheur = animFrame.createGraphics();
		
		this.paint(animGrapheur);
		
		return animFrame;
	}
	
	
	
	protected Door doorAt(Door.Which whichDoor) {
		
		switch (whichDoor) {
		case ON_LEFT:
			return leftDoor;
		case CENTER:
			return centerDoor;
		default:
			return rightDoor;
			
		}
		
	}
	
	
	
	protected void paintComponent(Graphics giblet) {
		super.paintComponent(giblet);
		
		float fillWidth = this.getWidth();
		float fillHeight = this.getHeight();
		float pictureWidth = chamberImage.getWidth(null);
		float pictureHeight = chamberImage.getHeight(null);
		
		double fillAspect = fillWidth / fillHeight;
		double pictureAspect = pictureWidth / pictureHeight;
		double scalingRatio = (pictureAspect > fillAspect)
				? fillHeight / pictureHeight
				: fillWidth / pictureWidth;
		
		int displayWidth = (int)Math.round( pictureWidth * scalingRatio );
		int displayHeight = (int)Math.round( pictureHeight * scalingRatio );
		int originX = (int)Math.round( (fillWidth - displayWidth) / 2 );
		int originY = (int)Math.round( (fillHeight - displayHeight) / 2 );
		
		giblet.drawImage(chamberImage,
				originX,                  originY,
				originX + displayWidth,   originY + displayHeight,
				
				0,                        0,
				(int)pictureWidth,        (int)pictureHeight,
				
				bgcolor,
				null);
		
	}
	
	
	
	
	public void actionPerformed(ActionEvent event) {
		Object sender = event.getSource();
		
		
		// TODO switch on sender
		
		setNeedsDisplay();
	}
	
	
	
	public Component addZ(Component comp, int zIndex) {
		add(comp);
		setComponentZOrder(comp, zIndex);
		return comp;
	}
	
	
	
	public boolean isOptimizedDrawingEnabled() {
		return false;
		// because components can overlap.
	}
	
	
	
	private void backToGameSelect() {
		try { extremeServer.close(); } catch (IOException|NullPointerException except) { }
		parentContainer.setView(new GameSelect(parentContainer, className, studentName, studentToken));
	}
	
	

}

























