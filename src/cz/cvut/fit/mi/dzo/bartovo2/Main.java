package cz.cvut.fit.mi.dzo.bartovo2;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Vojta Bartoš
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		BufferedImage srcImg = null, desImg = null;
		try {
			File f = new File("flower.jpg");
			srcImg = ImageIO.read(f);
			BufferedImage arrow = ImageIO.read(new File("arrow.png"));
			int width = srcImg.getWidth();
			int height = srcImg.getHeight();
			
			ColorModel clrMod = srcImg.getColorModel();
			WritableRaster rast = clrMod.createCompatibleWritableRaster(width, height);
			boolean isAlpha = clrMod.isAlphaPremultiplied();
			desImg = new BufferedImage(clrMod, rast, isAlpha, null);
			
			
			int n = 5;
			int n2 = n*n;
			float[] matrix = new float[n2];
			for (int i = 0; i < n2; i++) 
			{
				matrix[i] = 1.0f / (float) n2;
			}
			
			BufferedImageOp op = new ConvolveOp(new Kernel(n, n, matrix), ConvolveOp.EDGE_NO_OP, null);
			BufferedImage res = op.filter(srcImg, null);
			
			
			
			
			
			JFrame frame = new JFrame("Result");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			ImageIcon iconSrc = new ImageIcon(srcImg);
			JLabel labelSrc = new JLabel(iconSrc);
			ImageIcon iconDes = new ImageIcon(res);
			JLabel labelDes = new JLabel(iconDes);
			ImageIcon iconArr = new ImageIcon(arrow);
			JLabel labelArr = new JLabel(iconArr);
			
			Container container = frame.getContentPane();
			GridLayout layout = new GridLayout(1, 4);
			container.setLayout(layout);
			
			container.add(labelSrc);
			container.add(labelArr);
			container.add(labelDes);
			container.setSize(2*width + 200, height + 80);
			frame.setSize(2*width + 200, height + 80);
			frame.setVisible(true);
			
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
