package utfpr.tcc.vrp.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	
	private BufferedImage image;

    public ImagePanel(BufferedImage image) {
    	this.image = image;
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
    
    public BufferedImage getImage() {
    	return image;
    }
    public void setImage(BufferedImage image) {
    	this.image = image;
    }
}
