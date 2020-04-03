package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import utfpr.tcc.vrp.prop.Path;

public class DialogView extends JDialog {
	 
	protected String iconsPath;
	public static final Color ALTERNATE_COLOR_1 = new Color(179,179,179);
	public static final Color ALTERNATE_COLOR = new Color(252,242,206);
	
	
	public DialogView() {
		this.iconsPath = Path.getInstance().getIconsPath();
	}
	
	public DialogView(JDialog owner) {
		super(owner);
	}
	
	
	public DialogView(JFrame owner) {
		super(owner);
	}
	
	protected void centerDialog() {
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - this.getWidth()) / 2;
        final int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y); 
	}

}
