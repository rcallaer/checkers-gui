import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;

import java.io.File;


import javax.imageio.ImageIO;

import javax.swing.JPanel;

 class MyPanel extends JPanel {
	 Image image;
	 String i;
	public MyPanel(String i ) {/*green"+i+"*/
		this.i =i;
	 try
	    {
		 image = ImageIO.read(new File("ak.png"));
	    }
	    catch (Exception e) { /*handled in paintComponent()*/ }
	  }

	  @Override
	  protected void paintComponent(Graphics g)
	  {
	    super.paintComponent(g); 
	    if (image != null)
	    
	    	g.setColor(Color.black);
	    
	    	g.drawString(i, this.getWidth()/18, this.getHeight()/7);
	    	
	  }
}