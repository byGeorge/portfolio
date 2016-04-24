package studentapp;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class BoringSprite extends JComponent {
	
	volatile Image image;
	double aspectProportion;
	
	public BoringSprite(Image backing, int atX, int atY) {
		super();
		
		if (backing == null) throw new IllegalArgumentException();
		image = backing;
		aspectProportion = (double)backing.getHeight(null) / (double)backing.getWidth(null);
		
		setBounds(atX, atY, backing.getWidth(null), backing.getHeight(null));
		setOpaque(false);
	}
	
	public void scaleToWidth(int widdth) {
		int heigght = (int)Math.round( aspectProportion * widdth );
		super.setSize(widdth, heigght);
	}
	
	public void scaleToHeight(int heigght) {
		int widdth = (int)Math.round( heigght / aspectProportion );
		super.setSize(widdth, heigght);
	}
	
	
	protected void paintComponent(Graphics giblet) {
		super.paintComponent(giblet);
		giblet.drawImage(image,
				/*      dest top left x */ 0,
				/*      dest top left y */ 0,
				/*  dest bottom right x */ getWidth(),
				/*  dest bottom right y */ getHeight(),
				/*     image top left x */ 0,
				/*     image top left y */ 0,
				/* image bottom right x */ image.getWidth(null),
				/* image bottom right y */ image.getHeight(null),
				null );
	}
	
	

}
