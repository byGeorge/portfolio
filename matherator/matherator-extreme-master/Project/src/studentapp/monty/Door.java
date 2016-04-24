package studentapp.monty;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import common.Img;

public class Door extends JComponent {
	
	public enum Which {
		CENTER, ON_LEFT, ON_RIGHT
	}
	
	public static final int DoorWidth  = 277 /* px */;
	public static final int DoorHeight = 356 /* px */;
	
	
	// Global references to the images
	private static Image doorClosedCenterImage = null;
	private static Image doorClosedLeftImage = null;
	private static Image doorClosedRightImage = null;
	
	private static Image doorOpenCenterImage = null;
	private static Image doorOpenLeftImage = null;
	private static Image doorOpenRightImage = null;
	
	// Local references to two particular of the above images
	private Image doorClosedImage;
	private Image doorOpenImage;
	
	private volatile boolean isOpen = false;
	private final Runnable clickDoer;
	
	
	
	private static void loadImages() throws IOException {
		synchronized (Door.class) {
			if (doorClosedCenterImage == null)
				doorClosedCenterImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch.png") );
			
			if (doorClosedLeftImage == null)
				doorClosedLeftImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch+left.png") );
			
			if (doorClosedRightImage == null)
				doorClosedRightImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch+right.png") );
			
			if (doorOpenCenterImage == null)
				doorOpenCenterImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch+open.png") );
			
			if (doorOpenLeftImage == null)
				doorOpenLeftImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch+left+open.png") );
			
			if (doorOpenRightImage == null)
				doorOpenRightImage = ImageIO.read( Img.get("/studentapp/monty/graphics/doorpunch+right+open.png") );
			
		}
		
	}
	
	
	public Door(Which side, int x, int y, Runnable whenClickedDo) throws IOException {
		// TODO Auto-generated constructor stub
		super();
		loadImages();
		
		setOpaque(false);
		setBounds(x, y, DoorWidth, DoorHeight);
		clickDoer = whenClickedDo;
		
		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (clickDoer != null)  clickDoer.run();
			}

			public void mousePressed( MouseEvent e) { }
			public void mouseReleased(MouseEvent e) { }
			public void mouseEntered( MouseEvent e) { }
			public void mouseExited(  MouseEvent e) { }
		});
		
		switch (side) {
		case CENTER:
			doorClosedImage = doorClosedCenterImage;
			doorOpenImage = doorOpenCenterImage;
			break;
		case ON_LEFT:
			doorClosedImage = doorClosedLeftImage;
			doorOpenImage = doorOpenLeftImage;
			break;
		case ON_RIGHT:
			doorClosedImage = doorClosedRightImage;
			doorOpenImage = doorOpenRightImage;
			break;
		}
		
	}
	
	
	protected void paintComponent(Graphics giblet) {
		super.paintComponent(giblet);
		
		giblet.drawImage(
				doorImage(),
				0, 0, null);
	}
	
	
	
	public void setDoorOpen(boolean isopen) {
		if (isopen != isOpen) {
			revalidate();
			repaint();
		}
		
		isOpen = isopen;
	}
	
	public boolean doorIsOpen() {
		return isOpen;
	}
	
	protected Image doorImage() {
		return doorIsOpen() ? doorOpenImage : doorClosedImage;
	}
	

}



