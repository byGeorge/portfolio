package studentapp.kittenx.graphics;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.*;

import common.BezierTiming;
import common.CrossMessage;
import common.Img;
import common.Konstants;
import teacherapp.TeacherGUI;
import studentapp.AnimatedSprite;
import studentapp.AnimationUtil;
import studentapp.MatheratorStudent;
import studentapp.BoringSprite;
import studentapp.GameSelect;
import studentapp.QuestionatorExtreme;
import studentapp.kittenx.graphics.*;

/** Please let me know if this doesn't make sense...
 * 
 * @author TAZ
 *
 */
public class KittenX extends JPanel {
	/**
	 * You need this as to not instantiate a new MatheratorStudent..
	 */
	MatheratorStudent apc;
	private BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
	//This is used for the double buffering. Draw to image, then draw image to panel.
	Graphics2D gg = image.createGraphics();
	AtomicInteger mode = new AtomicInteger(0);
	
	Image target, cannonImage, bg, atomicImage, kitten, kittenontarget, springupImage, slinkawayImage;
    BoringSprite loadedCannon, emptyTarget;
    AnimatedSprite cannonblast, flyingKitten, kittenOnTarget, fireworks, backflip, atomic, springup, slinkaway;
	QuestionatorExtreme qe;
	JLabel question;
	JTextField answer;
	JButton launch;
	
	JButton exit;
	
	CrossMessage cm;
	Socket extremeServer;
	PrintWriter toServer;
	
	int amountOfQuestionsAnswered = 0;
	int correctAnswers = 0;
	
	String sn;
	String cn;
	int l;
	String t;
	
	private double width; 
	private double height;
	
	private double hc;
	private double wc;
	
	/** Constructor must get MatheratorStudent arg to make it work.
	 * Must Call Super();
	 * Set your apc variable to your MatheratorStudent arg input.
	 * Putting in the UpdateView Method is the most efficient way to do this. 
	 * **This way when you make updates to your JPanel you can just call updateView And it repaints it.*/
	public KittenX(MatheratorStudent app, String studentName, String className, int level, String token){
		super();
		
		this.setLayout(null);
		
		apc = app;
		width = apc.getSize().width;
		height = apc.getSize().height;
		
		apc.addComponentListener(new ComponentListener() 
		{  
	        public void componentResized(ComponentEvent evt) {
	        	Component c = evt.getComponent();
	            width = c.getSize().width;
	            height = c.getSize().height;
	    		//image = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
	            //Alligator not scaling...
	            image = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
	            gg = image.createGraphics();
                setHeightAndWidthCommon();
	            //repaint();
	            
	            //TODO...
	        }

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		sn = studentName;
		cn = className;
		l = level;
		t = token;
		
		qe = new QuestionatorExtreme(l);
		
		try{
		extremeServer = new Socket("localhost", Konstants.MATH_PORT_EXTREME);
		toServer = new PrintWriter( extremeServer.getOutputStream(), true );
		}catch(IOException ioe){}
		
		
		try{
			target = ImageIO.read(Img.get("/studentapp/kittenx/graphics/Target.png")); 
			cannonImage = ImageIO.read(Img.get("/studentapp/kittenx/graphics/Cannon.png")); 
			kitten = ImageIO.read(Img.get("/studentapp/kittenx/graphics/kitten.gif")); 
			atomicImage = ImageIO.read(Img.get("/studentapp/kittenx/graphics/atomic.gif")); 
			springupImage = ImageIO.read(Img.get("/studentapp/kittenx/graphics/spring-up.gif")); 
			slinkawayImage = ImageIO.read(Img.get("/studentapp/kittenx/graphics/slink-away.gif"));
			kittenontarget = ImageIO.read(Img.get("/studentapp/kittenx/graphics/kitten-on-target.gif")); 
			bg = ImageIO.read(Img.get("/studentapp/kittenx/graphics/bg.png")); 
            
            // AnimatedSprite flyingKitten, kittenOnTarget, fireworks, backflip, atomic, springup, slinkaway;
            
            List<Image> flyingKittenFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 3; fnum++) {
                flyingKittenFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/kitten/Kitten%d.png", fnum)
                    ) ) );
            }
            flyingKitten = new AnimatedSprite(flyingKittenFrames, 0, 0, true, 150);
            
            
            
            List<Image> sittingKittenFrames = new ArrayList<Image>();
            Image lastImage = null;
            for (int fnum = 0; fnum <= 14; fnum++) {
                lastImage = ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/kittentarget/KittenOnTarget%d.png", fnum)
                    ) );
                sittingKittenFrames.add(lastImage);
            }
            for (int fnum = 15; fnum <= 28; fnum++)  sittingKittenFrames.add(lastImage);
            kittenOnTarget = new AnimatedSprite(sittingKittenFrames, 448, 425, true, 1450);
            
            
            
            List<Image> atomicFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 22; fnum++) {
                atomicFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/atomic/Atomic%d.png", fnum)
                    ) ) );
            }
            atomic = new AnimatedSprite(atomicFrames, 269, 348, false, 1100, new Runnable() { public void run() {
                        // when done do:
                        remove(atomic);
                    } });
            
            
            List<Image> cannonblastFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 11; fnum++) {
                cannonblastFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/cannonblast/CannonBlast%d.png", fnum)
                    ) ) );
            }
            cannonblast = new AnimatedSprite(cannonblastFrames, 0, 241, false, 550, new Runnable() { public void run() {
                        // when done do:
                        cannonblast.setVisible(false);
                        loadedCannon.setVisible(true);
                    } });
                    
                    
                    
            List<Image> fireworkFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 31; fnum++) {
                fireworkFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/fireworks/Fireworks%d.png", fnum)
                    ) ) );
            }
            fireworks = new AnimatedSprite(fireworkFrames, 0, 0, false, 1550);
            fireworks.setLocation(1000/2 - fireworks.getWidth()/2,  0);
                    
                    
                    
            List<Image> backflipFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 15; fnum++) {
                backflipFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/backflip/Backflip%d.png", fnum)
                    ) ) );
            }
            backflip = new AnimatedSprite(backflipFrames, 425, 128, false, 950);
            
            
            
            List<Image> springupFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 4; fnum++) {
                springupFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/springup/Springup%d.png", fnum)
                    )));
            }
            springup = new AnimatedSprite(springupFrames, 780, 385, false, 250);
            
            
            
            List<Image> slinkawayFrames = new ArrayList<Image>();
            for (int fnum = 1; fnum <= 19; fnum++) {
                slinkawayFrames.add( ImageIO.read( Img.get(
                    String.format("/studentapp/kittenx/graphics/slinkaway/Slinkaway%d.png", fnum)
                    )));
            }
            slinkaway = new AnimatedSprite(slinkawayFrames, 780, 385, false, 950);
            
            
            
            
            
            
            
            
            
            
            
            
		}catch(IOException ioe){ioe.printStackTrace();}
		
		cannonblast.setVisible(false);							add(cannonblast);
		kittenOnTarget.setVisible(false);						add(kittenOnTarget);
        loadedCannon = new BoringSprite(cannonImage, 0, 241);	add(loadedCannon);
        emptyTarget = new BoringSprite(target, 448, 425);		add(emptyTarget);
        
		question = new JLabel();
		//question.setSize(60, 20);
		question.setBackground(Color.white);
		//question.setLocation(260,10);
		question.setBorder(BorderFactory.createLineBorder(Color.black));
		//question.setFont(new Font());
		
		String qs = qe.getQuestion();
		question.setSize((qs.length()+1)*6,20);
		question.setLocation(360-(qs.length()+1)*6, 10);
		question.setText(qs);
		
		this.add(question);
		//jd.add(question);
		
		answer = new JTextField();
		//answer.setBackground(Color.white);
		answer.setBorder(BorderFactory.createLineBorder(Color.red));
		answer.setSize(40,20);
		answer.setLocation(360, 10);
		answer.setText("        ");
		this.add(answer);
		//jd.add(answer);
		
		launch = new JButton("Launch");
		launch.setSize(80,20);
		launch.setLocation(400,10);
		launch.addActionListener(launchButtonActionListener());
		this.add(launch);
		//jd.add(launch);
		
		exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Calls Method below to make call up to MatheratorStudent Parent... Look below to test yours.
            	updateView();
            }
        });
		
		exit.setBounds(Konstants.ExitButtonBounds);
		this.add(exit);
        
        
        
        
        mode.set(10);  // llet the cartoooons......... beegiin...
		
	}
    
    
    
    
	
	public void paintComponent(Graphics g){
		setHeightAndWidthCommon();
		super.paintComponent(g);
		drawBackground(g);
	}
	
    public void setHeightAndWidthCommon(){
        if(apc.getSize().width != 800){
            wc = (double)(apc.getSize().width)/800.0;
        }else if (apc.getSize().width == 800){
            wc = 1;
        }
            
        if(apc.getSize().height != 600){
            hc = (double)(apc.getSize().height)/600.0;
            //System.out.println("apc: " + apc.getSize().width + " " + apc.getSize().height + "\nw/h " + width  + " " +  height + "\nw/c " + wc + " " + hc);
        }else if (apc.getSize().height == 600){
            hc = 1;
            //System.out.println("apc: " + apc.getSize().width + " " + apc.getSize().height + "\n/h " + width  + " " +  height + "\nw/c " + wc + " " + hc);
        }
    }
    
	/*
	 * 
	    add(new KittenX(this, "","", 1));
	 */
	public void drawBackground(Graphics g){
		g.drawImage(bg, 0, 0, (int)width, (int)height, null);
		//setBackground(new Color(102,178,255));
		//g.setColor(new Color(0,153,0));
		//g.fillRect(0, 525, 660, 75);

        // g.drawImage(cannonImage, -50, (int)(330*hc), null);
		//g.drawImage(cannon, -50, (int)(220*hc), null);
		//System.out.println((200*(height/800)) + " " + (410*(width/600) ) + " " + (365*(height/800)));
		//System.out.println(height + " " + width);
		
        // g.drawImage(target, (int)(410*wc), (int)(365*hc), null);
	}
	
	public void updateView(JComponent update){
		/**Call the constructor of your class here. Replace new BasicJPanel()...
		 * Make sure your class extends JPanel to make this work.
		 */
		apc.setView(update);
		//apc.setView(new KittenX(apc));
	}
	
	public ActionListener launchButtonActionListener(){
		return new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Calls Method below to make call up to MatheratorStudent Parent... Look below to test yours.
            	String given = answer.getText();
            	given = given.replaceAll("\\s+","");
            	try{
            		if (qe.getAnswer() == Integer.parseInt(given)){
            			question.setText("Correct");
            			answer.setText("        ");
            			ifCorrectAnswer();
            		}
            		else if (qe.getAnswer() > Integer.parseInt(given)){
            			question.setText("Incorrect");
            			answer.setText("        ");
            			ifLessIncorrectAnswer();
            		}
            		else if (qe.getAnswer() < Integer.parseInt(given)){
            			question.setText("Incorrect");
            			answer.setText("        ");
            			ifMoreIncorrectAnswer();
            		}
            	} catch (NumberFormatException nfe){ 
            		//Do Nothing
            	}
            }
        };
	}
	
	/** Doing the implicit double buffering within these*/
	public void ifLessIncorrectAnswer(){
        
        
        final int startx = 100;
        final int starty = 330;
        final long flyingDuration = 500;  // ms
        final long explosionDuration = 1100;  // ms
        final double totalDuration = flyingDuration + explosionDuration;
        final double popover = flyingDuration / totalDuration;
        
        final long startTime = System.currentTimeMillis();
        
        if (!kickOffAnimation())  return;
        
        final TimerTask kittenStep;
        final AtomicBoolean stillAnimating = new AtomicBoolean(true);
        
        kittenStep = new TimerTask() { public void run() {
            final TimerTask internalReferenceToTheCurrentTimer = this;
            
            SwingUtilities.invokeLater( new Runnable() { public void run() { 
                
                long currentTime = System.currentTimeMillis();
                double progression = (currentTime - startTime) / totalDuration;
                
                if (progression < 0) {
                    // noop
                    
                } else if (progression < popover) {
                    // kitteh fly!
                    double i = progression / popover * 43;
                    flyingKitten.setLocation(
                        (int)( wc * ( startx + (i*3) ) ),
                        (int)( hc * ( starty + (i*i/20) ) )
                        );
                    
                } else if (stillAnimating.compareAndSet(true, false)) {
                    // evaporate kitteh into nuclear nil,
                    // and replace with atomic animation.
                    internalReferenceToTheCurrentTimer.cancel();
                    flyingKitten.stopAnimation();
                    remove(flyingKitten);
                    
                    addZ(atomic, 0);
                    atomic.startAnimation();
                    // atomic removes itself from the view when it's done.
                    
                    cleanupAfterAnimation();
                    resetGame();
                    
                }
                
                revalidate();
                repaint();
                
            } } );
            
        } };
        
        
        addZ(flyingKitten, 0);
        flyingKitten.startAnimation();
        AnimationUtil.globalTimer().schedule(kittenStep, 0, Konstants.AnimationFrameDelayMS);
        
        cannonblast.setVisible(true);
        cannonblast.startAnimation();
        loadedCannon.setVisible(false);
        
        
        
	}
    
    
    
    
	/** Doing the double buffering within these*/
	public void ifMoreIncorrectAnswer(){
        
        final int startx = 100;
        final int starty = 330;
        final int destx = 770;
        final int desty = 465;
        final int crest = 200;
        final double flyTime = 900;
        final long startTime = System.currentTimeMillis();
        final BezierTiming riseToCrest = new BezierTiming(.16, .31, .43, 1);
        final BezierTiming fallToMouth = new BezierTiming(.49, 0, .92, .78);
        
        if (!kickOffAnimation())  return;
        
        final TimerTask kittenStep;
        final AtomicBoolean stillAnimating = new AtomicBoolean(true);
        final AtomicBoolean alligated = new AtomicBoolean(false);
        
        kittenStep = new TimerTask() { public void run() {
            final TimerTask iRTTCT = this;
            
            SwingUtilities.invokeLater( new Runnable() { public void run() {
                
                long currentTime = System.currentTimeMillis();
                double progression = (currentTime - startTime) / flyTime;
                double i = progression * 88;
                
                if (progression < 0) {
                    // noop
                    
                } else if (progression < .5) {
                	double riseProgression = riseToCrest.at(progression/.5);
                    flyingKitten.setLocation(
                        (int)(     (progression) * destx  +     (1-progression) * startx ),
                        (int)( (riseProgression) * crest  + (1-riseProgression) * starty )
                        );
                    
                } else if (progression < 1) {
                    if (alligated.compareAndSet(false, true)) {
                        springup.startAnimation();
                        add(springup);
                    }
                    
                    double fallProgression = fallToMouth.at((progression-.5)/.5);
                    flyingKitten.setLocation(
                        (int)(     (progression) * destx  +     (1-progression) * startx ),
                        (int)( (fallProgression) * desty  + (1-fallProgression) * crest  )
                        );
                    
                } else if (stillAnimating.compareAndSet(true, false)) {
                    iRTTCT.cancel();
                    flyingKitten.stopAnimation();
                    remove(flyingKitten);
                    
                    remove(springup);
                    slinkaway.whenDoneDo(new Runnable() { public void run() {
                        try { Thread.sleep(250); } catch (InterruptedException fuckThisShit) { }
                        
                        remove(slinkaway);
                        cleanupAfterAnimation();
                        resetGame();
                        revalidate(); repaint();
                    }});
                    
                    slinkaway.startAnimation();
                    add(slinkaway);
                    
                }
                
                revalidate();
                repaint();
                
            } } );
            
        } };
        
        
        
        addZ(flyingKitten, 0);
        flyingKitten.startAnimation();
        AnimationUtil.globalTimer().schedule(kittenStep, 0, Konstants.AnimationFrameDelayMS);
        
        cannonblast.setVisible(true);
        cannonblast.startAnimation();
        loadedCannon.setVisible(false);
        
        
	}
    
    
    
    
    
	/** Doing the double buffering within these*/
	public void ifCorrectAnswer(){
        
        final int startx = 100;
        final int starty = 330;
        final int destx = 448;
        final int desty = 425;
        final double flyTime = 600;
        final long startTime = System.currentTimeMillis();
        
        final BezierTiming fallTiming = new BezierTiming(.51, 0, 1, .65);
        
        if (!kickOffAnimation())  return;
        
        final TimerTask kittenStep;
        final AtomicBoolean stillAnimating = new AtomicBoolean(true);
        
        kittenStep = new TimerTask() { public void run() {
            final TimerTask internalReferenceToTheCurrentTimer = this;
            
            SwingUtilities.invokeLater( new Runnable() { public void run() { 
                
                long currentTime = System.currentTimeMillis();
                double progression = (currentTime - startTime) / flyTime;
                double i = progression * 64;
                
                if (progression < 0) {
                    // noop
                    
                } else if (progression < 1) {
                	double dropProgression = fallTiming.at(progression);
                    flyingKitten.setLocation(
                        (int)( (progression) * destx  + (1-progression) * startx ),
                        (int)( (dropProgression) * desty  + (1-dropProgression) * starty )
                        );
                    
                } else if (stillAnimating.compareAndSet(true, false)) {
                    // replace target with kitten-on-target;
                    // tear down animation.
                    internalReferenceToTheCurrentTimer.cancel();
                    flyingKitten.stopAnimation();
                    remove(flyingKitten);
                    
                    kittenOnTarget.setVisible(true);    kittenOnTarget.startAnimation();
                    emptyTarget.setVisible(false);
                    
                    if (Math.random() < 1./3.) {
                        // Fireworks
                        add(fireworks);
                        fireworks.whenDoneDo( new Runnable() { public void run() {
                            kittenOnTarget.setVisible(false);
                            emptyTarget.setVisible(true);
                            kittenOnTarget.stopAnimation();
                            remove(fireworks);
                            
                            cleanupAfterAnimation();
                            resetGame();
                            revalidate(); repaint();
                            
                        } } );
                        
                        fireworks.startAnimation();
                        revalidate(); repaint();
                        
                        
                    } else {
                        // Backflip
                        AnimationUtil.globalTimer().schedule(new TimerTask() { public void run() {
                            SwingUtilities.invokeLater(new Runnable() { public void run() {
                                kittenOnTarget.setVisible(false);
                                kittenOnTarget.stopAnimation();
                                emptyTarget.setVisible(true);
                                addZ(backflip, 0);
                                
                                backflip.startAnimation();
                                
                                revalidate(); repaint();
                                
                            } });
                            
                            backflip.whenDoneDo(new Runnable() { public void run() {
                                try { Thread.sleep(250); } catch (InterruptedException fuckYouJava) { }
                                
                                remove(backflip);
                                emptyTarget.setVisible(true);
                                cleanupAfterAnimation();
                                resetGame();
                                revalidate(); repaint();
                                
                            } });
                            
                        } }, 400);
                        
                    }
                    
                }
                
                revalidate();
                repaint();
                
                
            } } );
            
        } };
        
        
        
        addZ(flyingKitten, 0);
        flyingKitten.startAnimation();
        AnimationUtil.globalTimer().schedule(kittenStep, 0, Konstants.AnimationFrameDelayMS);
        
        cannonblast.setVisible(true);
        cannonblast.startAnimation();
        loadedCannon.setVisible(false);
        
        
        return;
//		int startx = 100;
//		//beginning of cannon.
//		//endpoint X is 100 + 64*5 for kitten... (kitten picture size.
//		int starty = 330;
//		for(int i = 0; i <= 64; i++){
//			drawBackground(gg);
//			if(i < 40){
//				/*starty = starty -i; /*This is hilarious*/
//				gg.drawImage(kitten, (int)(wc*(startx + (i*5))),(int)(hc*(starty - (i/2))),null);
//			}else{
//				if (i == 40){ starty = starty - ((i-1)/2);}
//				if(i <= 63)
//					gg.drawImage(kitten, (int)(wc*(startx + (i*5))),(int)(hc*(starty + (i))),null);
//				else{
//					gg.drawImage(kittenontarget, (int)(wc*(startx +(i*5) -10)), (int)(hc*(starty+55)), null);
//							//This one should be 100+64*5-10 for end x and end y is 380 - 5 (330+55 now) to match.
//					//Making sleep to show correct and then move on.
//					//This is the part that draws the double buffered image to the screen.
//					this.getGraphics().drawImage(image, 0, 0, null);
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {e.printStackTrace();}
//					//Gets everything to redraw
//					repaint();
//				}
//			}
//			
//			//This is the part that draws the double buffered image to the screen.
//			this.getGraphics().drawImage(image, 0, 0, null);
//			
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {e.printStackTrace();}
//		}		
//		correctAnswers += 1;
//		resetGame();
		
	}
    
    
    
	
	public void resetGame(){
		
		/*
		 * Get new Question. Display new Question.
		 * Update the Question Counter.
		 * if 10, call updateView()(Also fix) else, safe.
		 * 
		 * reset graphics.
		 */
		drawBackground(this.getGraphics());
		qe.newQ(l);
		
		String qs = qe.getQuestion();
		question.setSize((qs.length()+1)*6,20);
		question.setLocation(360-(qs.length()+1)*6, 10);
		question.setText(qs);
		
		question.setText(qs);
		
		
		answer.setText("        ");
		amountOfQuestionsAnswered += 1;
		
		//send message... This sends the update on the score to the server...
		cm = CrossMessage.gameScoreUpdate(sn, cn, l, "Kitten X", correctAnswers / (double)amountOfQuestionsAnswered, t);
		toServer.println(cm);
		
		if(amountOfQuestionsAnswered == 10){
			//Exit Game Basically.
			updateView();
		}
	}
    
    
    
	protected boolean kickOffAnimation() {
		return mode.compareAndSet(10, 20);
	}
    
    
    
    
    
    private void cleanupAfterAnimation() {
        mode.set(10);
        cannonblast.setVisible(false);
        loadedCannon.setVisible(true);
        revalidate(); repaint();
    }
	
	
	
    
    
	
	public Component addZ(Component comp, int zIndex) {
		add(comp);
		setComponentZOrder(comp, zIndex);
		return comp;
	}
	
	
	
	
	public void updateView(){
		//TODO FIX. Also send message to Server.
		apc.setView(new GameSelect(apc,cn,sn,t));
	}	
}