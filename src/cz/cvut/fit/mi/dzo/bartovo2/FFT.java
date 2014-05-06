package cz.cvut.fit.mi.dzo.bartovo2;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
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
public class FFT {

	private static int KERNEL_LINE = 1;
	private static int KERNEL_POINT = 2;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) 
	{
		
		int type = FFT.KERNEL_LINE;
		int size = 10;
		
		FFT.blure("lena512.jpg", "lena512_blured.jpg", size, type);
		FFT.deBlure("lena512_blured.jpg", "lena512_deblured.jpg", size, type);
		
	}
	
	public static void blure(String srcName, String resName, int kernel_size, int kernel_type) 
	{
		BufferedImage srcImg, resImg;
		try {
			
			srcImg = FFT.readImage(srcName);
			resImg = FFT.getEmptyImage(srcImg);
			
			int width = srcImg.getWidth();
			int height = srcImg.getHeight();
			
			double [][] src2D = FFT.getPixels(srcImg);
			//double [][] kernel2D = FFT.getPixels(kernelImg);
			
			double [][] kernel2D = FFT.getKernel(kernel_type, kernel_size, width, height);
			
			double max = FFT.maxValue(src2D);
			double min = FFT.minValue(src2D);
			
			src2D = FFT.normalize(src2D, width, height);
			kernel2D = FFT.normalizeKernel(kernel2D, width, height);
			//FFT.showValues(src2D, width, height);
			

			src2D = FFT.doubleWidth(src2D, width, height);
			src2D = FFT.forward(src2D, width, height);
			kernel2D = FFT.doubleWidth(kernel2D, width, height);
			kernel2D = FFT.forward(kernel2D, width, height);
			
			src2D = FFT.multiply(src2D, kernel2D, width, height);
			src2D = FFT.computeResult(src2D, width, height, min, max);
			//src2D = FFT.computeFrekvence(src2D, width, height, min, max);

			//double sum = FFT.sum(FFT.abc(FFT.substruct(kernel2D, src2D, width, height), width, height), width, height);
			//src2D = FFT.substruct(kernel2D, src2D, width, height);
			//System.out.println("SUM=" + sum);
			
			int[] pixels = FFT.toOneDimInt(src2D, width, height);
			resImg.setRGB(0, 0, width, height, pixels, 0, width);
			FFT.showImage(resImg);
			FFT.saveImage(resImg, resName);
			/* */
			
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void deBlure(String srcName, String resName, int kernel_size, int kernel_type) 
	{
		BufferedImage srcImg, resImg;
		try {
			
			srcImg = FFT.readImage(srcName);
			resImg = FFT.getEmptyImage(srcImg);
			
			int width = srcImg.getWidth();
			int height = srcImg.getHeight();
			
			double [][] src2D = FFT.getPixels(srcImg);
			//double [][] kernel2D = FFT.getPixels(kernelImg);
			
			double [][] kernel2D = FFT.getKernel(kernel_type, kernel_size, width, height);
			
			double max = FFT.maxValue(src2D);
			double min = FFT.minValue(src2D);

			src2D = FFT.normalize(src2D, width, height);
			kernel2D = FFT.normalizeKernel(kernel2D, width, height);
			// FFT.showValues(src2D, width, height);
			
			src2D = FFT.doubleWidth(src2D, width, height);
			src2D = FFT.forward(src2D, width, height);
			kernel2D = FFT.doubleWidth(kernel2D, width, height);
			kernel2D = FFT.forward(kernel2D, width, height);
			
			src2D = FFT.divide(src2D, kernel2D, width, height);
			
			src2D = FFT.computeResult(src2D, width, height, min, max);
			//src2D = FFT.computeFrekvence(src2D, width, height, min, max);
			
			//double sum = FFT.sum(FFT.abc(FFT.substruct(kernel2D, src2D, width, height), width, height), width, height);
			//src2D = FFT.substruct(kernel2D, src2D, width, height);
			//System.out.println("SUM=" + sum);
			
			int[] pixels = FFT.toOneDimInt(src2D, width, height);
			resImg.setRGB(0, 0, width, height, pixels, 0, width);
			FFT.showImage(resImg);
			//FFT.showImage(kernelImg);
			FFT.saveImage(resImg, resName);
			/* */
			
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static double[][] getKernel(int type, int length, int width, int height)
	{
		if(type == FFT.KERNEL_LINE)
		{
			return FFT.getLineKernel(length, width, height);
		} else {
			return FFT.getPointKernel(length, width, height);
		}
	}
	
	public static double[][] getLineKernel(int length, int width, int height)
	{
		double [][] kernel2D = new double[height][width];
		for(int i = 1; i <= length; i++) 
		{
			kernel2D[height/2+1][width/2-length/2+i] = 1;
		}
		return kernel2D;
	}
	
	public static double[][] getPointKernel(int length, int width, int height)
	{
		double [][] res = new double[height][width];
		int x_pivot = width/2;
		int y_pivot = height/2;
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				int x = x_pivot - i;
				int y = j - y_pivot;
				double distance = Math.sqrt(x*x + y*y);
				
				if(distance <= (length/2.0)) {
					res[j][i] = 1;
				} else {
					res[j][i] = 0;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Provede na matici furierovu transformaci.
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] forward(double[][] arr, int width, int height)
	{
		DoubleFFT_2D fft = new DoubleFFT_2D(height, width);

		fft.realForwardFull(arr);

		return arr;
	}
	
	/**
	 * Provede na matici zpětnou furierovu transformaci.
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] backward(double[][] arr, int width, int height)
	{
		DoubleFFT_2D fft = new DoubleFFT_2D(height, width);

		fft.complexInverse(arr, true);
		
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = arr[j][2*i]; // forward
			}
		}
		return res;
	}
	
	/**
	 * Naskejluje barvy obrázku na interval &lt 0,255 &gt
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @param min_col Minimum z hodnot v matici
	 * @param max_col Maximum z hodnot v matici
	 * @return 
	 */
	public static double[][] scale(double[][] arr, int width, int height, double min_col, double max_col)
	{
		double max = FFT.maxValue(arr);
		
		double c = 255 / (Math.log(1 + max));
		
		double [][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = c * Math.log(1 + arr[j][i]);

			}
		}
		return res;
	}
	
	/**
	 * Naskejluje barvy obrázku na interval &lt 0,255 &gt
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @param min_col Minimum z hodnot v matici
	 * @param max_col Maximum z hodnot v matici
	 * @return 
	 */
	public static double[][] scale2(double[][] arr, int width, int height, double min_col, double max_col)
	{
		double max = FFT.maxValue(arr);
		double min = FFT.minValue(arr);
		
		System.out.println("MAX:"+max+", MIN:"+min);
		System.out.println("MAX_COL:"+max_col+", MIN_COL:"+min_col);
		
		double c = max_col - min_col;
		
		double [][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = c * ((arr[j][i] - min) / (max - min + 1)) + min_col;
			}
		}
		return res;
	}
	
	public static double[][] scale3(double[][] arr, int width, int height, double min_col, double max_col)
	{
		double max = FFT.maxValue(arr);
		double min = FFT.minValue(arr);
		
		System.out.println("MAX:"+max+", MIN:"+min);
		System.out.println("MAX_COL:"+max_col+", MIN_COL:"+min_col);
		
		double [][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = (Math.log((arr[j][i] - min_col)) / Math.log((max_col - min_col + 1)));
			}
		}
		return res;
	}
	
	public static double[][] beforeScale4(double[][] arr, int width, int height, double min_col, double max_col)
	{
		double min = FFT.minValue(arr);
		double [][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				if(arr[j][i] < 0) 
				{
					res[j][i] = 0;
				} else {
					res[j][i] = arr[j][i];
				}
			}
		}
		
		double max = FFT.maxValue(res);
		double delitel = max / (max_col / 255);
		System.out.println(max+" / "+(max_col/255)+" = "+delitel);
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				//res[j][i] /= delitel;
			}
		}
		return res;
	}
	
	/**
	 * Naskejluje barvy obrázku na interval &lt 0,255 &gt
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @param min_col Minimum z hodnot v matici
	 * @param max_col Maximum z hodnot v matici
	 * @return 
	 */
	public static double[][] scale4(double[][] arr, int width, int height, double min_col, double max_col)
	{
		double max = FFT.maxValue(arr);
		double min = FFT.minValue(arr);
		
		System.out.println("MAX:"+max+", MIN:"+min);
		System.out.println("MAX_COL:"+max_col+", MIN_COL:"+min_col);
		
		double [][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				//arr[j][i] = arr[j][i] < 0 ? -1 * arr[j][i] : arr[j][i];
				res[j][i] = arr[j][i] * 255;
			}
		}
		return res;
	}
	
	/**
	 * Normalize colors
	 * @param arr První činitel
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] normalize(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = arr[j][i]/255;
			}
		}
		return res;
	}
	
	public static double[][] normalizeKernel(double[][] arr, int width, int height)
	{
		int count = 0;
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				if(arr[j][i] > 0) 
				{
					count++;
				}
			}
		}
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				if(arr[j][i] > 0) 
				{
					res[j][i] = 1.0 / count;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Normalize colors
	 * @param arr První činitel
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] showValues(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		Set set = new TreeSet();
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				set.add(arr[j][i]);
			}
		}
		
		Iterator i = set.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
			i.remove();
		}
		
		return res;
	}
	
	/**
	 * Odčítání matic reálných čísel
	 * @param first První činitel
	 * @param second Druhý činitel
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] substruct(double[][] first, double[][] second, int width, int height)
	{
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = first[j][i] - second[j][i];
			}
		}
		return res;
	}
	
	public static double[][] abs(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				res[j][i] = Math.abs(arr[j][i]);
			}
		}
		return res;
	}
	
	public static double[][] abc(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				if(arr[j][i] < 0) {
					res[j][i] = 0;
				} else if(arr[j][i] > 255) {
					res[j][i] = 255;
				} else {
					res[j][i] = arr[j][i];
				}
			}
		}
		return res;
	}
	
	/**
	 * Spočte sumu čísel v matici
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double sum(double[][] arr, int width, int height)
	{
		double sum = 0;
		
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				sum += arr[j][i];
			}
		}
		return sum;
	}
	
	/**
	 * Násobení matic s komplexními čísly
	 * @param first První činitel
	 * @param second Druhý činitel
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] multiply(double[][] first, double[][] second, int width, int height)
	{
		double[][] res = new double[height][2*width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				double re1 = first[j][2*i]; // forward
				double im1 = first[j][2*i+1];
				double re2 = second[j][2*i]; // forward
				double im2 = second[j][2*i+1];
				res[j][2*i] = re1*re2-im1*im2;
				res[j][2*i+1] = re1*im2 + im1*re2;
			}
		}
		return res;
	}
	
	/**
	 * Dělení matic komplexních čísel.
	 * @param first První činitel
	 * @param second Druhý činitel
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	public static double[][] divide(double[][] first, double[][] second, int width, int height)
	{
		double[][] res = new double[height][2*width];
		float eps = 0.00000001f;
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				double re1 = first[j][2*i]; // forward
				double im1 = first[j][2*i+1];
				double re2 = second[j][2*i]; // forward
				double im2 = second[j][2*i+1];
				double re2inv = re2 / (re2*re2+im2*im2 + eps); // forward
				double im2inv = -1 * im2 / (re2*re2+im2*im2 + eps);
				res[j][2*i] = re1*re2inv-im1*im2inv;
				res[j][2*i+1] = re1*im2inv + im1*re2inv;
			}
		}
		return res;
	}
	
	/**
	 * Načte z disku obrázek.
	 * @param name Jméno obrázku na disku.
	 * @return
	 * @throws IOException 
	 */
	protected static BufferedImage readImage(String name) throws IOException
	{
		File f = new File(name);
		BufferedImage srcImg = ImageIO.read(f);
		return srcImg;
	}
	
	/**
	 * Uloží obrázek na disk.
	 * @param img Obrázek
	 * @param name Jméno výstupního souboru.
	 * @throws IOException 
	 */
	protected static void saveImage(BufferedImage img, String name) throws IOException
	{
		File outputfile = new File(name);
		ImageIO.write(img, "jpg", outputfile);
	}
	
	/**
	 * Zobrazí obrázek na obrazovce.
	 * @param img 
	 */
	protected static void showImage(BufferedImage img)
	{
		JFrame frame = new JFrame("Result");
		int width = img.getWidth();
		int height = img.getHeight();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		ImageIcon iconSrc = new ImageIcon(img);
		JLabel labelSrc = new JLabel(iconSrc);

		Container container = frame.getContentPane();
		GridLayout layout = new GridLayout(1, 1);
		container.setLayout(layout);

		container.add(labelSrc);
		container.setSize(width, height);
		frame.setSize(width + 40, height + 60);
		frame.setVisible(true);
	}
	
	/**
	 * Z obrázku vytvoří dvojrozměrnou matici čísel s hodnotou 0-255 (barevný odstín).
	 * @param srcImg Obrázek
	 * @return 
	 */
	protected static double[][] getPixels(BufferedImage srcImg)
	{
		int width = srcImg.getWidth();
		int height = srcImg.getHeight();
		int [] srcPixels = new int[width*height];
		srcImg.getRGB(0, 0, width, height, srcPixels, 0, width);
		
		
		for(int i = 0; i < srcPixels.length; i++)
		{
			Color col = new Color(srcPixels[i]);
			int r = col.getRed();
			int g = col.getGreen();
			int b = col.getBlue();
			
			srcPixels[i] = b;
		}
		
		double[][] res = FFT.toTwoDim(srcPixels, width, height);
		return res;
	}
	
	/**
	 * Vytvoří prázdný obrázek se stejnými parametry, jako má zadaný obrázek.
	 * @param srcImg
	 * @return 
	 */
	protected static BufferedImage getEmptyImage(BufferedImage srcImg)
	{
		BufferedImage desImg;
		ColorModel clrMod = srcImg.getColorModel();
		int width = srcImg.getWidth();
		int height = srcImg.getHeight();
		WritableRaster rast = clrMod.createCompatibleWritableRaster(width, height);
		boolean isAlpha = clrMod.isAlphaPremultiplied();
		desImg = new BufferedImage(clrMod, rast, isAlpha, null);
		return desImg;
	}
	
	/**
	 * Převede jednorozměrné pole na dvojrozměrnou matici.
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static double[][] toTwoDim(int[] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		for(int j=0;j<height;++j){
			for(int i=0;i<width;++i){
				res[j][i] = arr[j*width+i];
			}
		}
		return res;
	}
	
	protected static double[][] doubleWidth(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][2*width];
		for(int j=0;j<height;++j){
			for(int i=0;i<width;++i){
				res[j][i] = arr[j][i];
			}
		}
		return res;
	}
	
	/**
	 * Převede dvojrozměrnou matici na jednorozměrné pole. Řádky jsou skládány
	 * za sebou.
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static double[] toOneDim(double[][] arr, int width, int height)
	{
		
		double[] res = new double[width*height];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				int index = j*width+i;
				res[index] = arr[j][i];
			}
		}
		return res;
	}
	
	/**
	 * Převede dvojrozměrnou matici na jednorozměrné pole integerů. Řádky jsou skládány
	 * za sebou.
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static int[] toOneDimInt(double[][] arr, int width, int height)
	{
		
		int[] res = new int[width*height];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				int index = j*width+i;
				int g = (int) Math.round(arr[j][i]);
				if(g < 0) {
					// System.out.println("value out of range: "+g);
					g = 0;
				} else if(g > 255) {
					// System.out.println("value out of range: "+g);
					g = 255;
				}else {
//					System.out.println("OKOKOK");
				}
				
				res[index] = (new Color(g, g, g)).getRGB();
			}
		}
		return res;
	}
	
	/**
	 * Spočátá magnitudu matice komplexních čísel (výsledek FFT)
	 * @param arr
	 * @param width
	 * @param height
	 * @return 
	 */
	protected static double[][] magnitude(double[][] arr, int width, int height)
	{
		double[][] magnitude = new double[height][width];
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				double re = arr[j][2*i]; // forward
				double im = arr[j][2*i+1];
				//magnitude[index] = Math.sqrt((im*im+re*re));
				//magnitude[j][i] = 1 + Math.sqrt((im*im+re*re));
				magnitude[j][i] = Math.log(1 + Math.sqrt((im*im+re*re))); // Pak normalizovat barvy
			}
		}
		return magnitude;
	}
	
	/**
	 * Zobrazí matici jako obrázek.
	 * @param arr Matice
	 */
	public static void showImage(double[][] arr)
	{
		int height = arr.length;
		int width = arr[0].length;
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		double[] pom = FFT.toOneDim(arr, width, height);
		int[] pixels = new int[height*width];
		for(int i = 0; i < pom.length; i++)
		{
			pixels[i] = (int) pom[i];
		}
		res.setRGB(0, 0, width, height, pixels, 0, width);
		FFT.showImage(res);
	}
	
	/**
	 * 
	 */
	public static double[][] computeFrekvence(double[][] arr, int width, int height, double min, double max)
	{
		double [][] res;
		res = FFT.magnitude(arr, width, height);
		res = FFT.scale4(res, width, height, min, max);
			
		res = FFT.shift(res, width, height);
		
		return res;
	}
	
	/**
	 * 
	 */
	public static double[][] computeResult(double[][] arr, int width, int height, double min, double max)
	{
		double [][] res;
		res = FFT.backward(arr, width, height);
		//FFT.showValues(res, width, height);
		//res = FFT.beforeScale4(res, width, height, min, max);
		res = FFT.scale4(res, width, height, min, max);
		//res = FFT.scale2(res, width, height, min, max);
			
		res = FFT.shift(res, width, height);
		
		return res;
	}
	
	/**
	 * Prohodí 1 se 3 a 2 se 4 částí matice:
	 * <pre>
	 * {@code
	 * 
	 *   1  2      3  4
	 *   4  3  =>  2  1
	 * }
	 * </pre>
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static double[][] shift(double[][] arr, int width, int height)
	{
		double[][] res = FFT.shiftHorizontaly(arr, width, height);
		res = FFT.shiftVerticaly(res, width, height);
		return res;
	}
	
	/**
	 * Prohodí horní a dolní polovinu matice
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static double[][] shiftHorizontaly(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		int half_height = height / 2;
		for(int i=0;i<width; ++i)
		{
			for(int j=0;j<half_height;++j)
			{
				res[j][i] = arr[half_height+j][i];
				res[half_height + j][i] = arr[j][i];
			}
		}
		return res;
	}
	
	/**
	 * Prohodí levou a pravou polovinu matice
	 * @param arr Matice
	 * @param width Šířka
	 * @param height Výška
	 * @return 
	 */
	protected static double[][] shiftVerticaly(double[][] arr, int width, int height)
	{
		double[][] res = new double[height][width];
		int half_width = width / 2;
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<half_width; ++i)
			{
				res[j][i] = arr[j][half_width + i];
				res[j][half_width + i] = arr[j][i];
			}
		}
		return res;
	}
	
	/**
	 * Najde minimální hodnotu v poli
	 * @param arr pole
	 * @return 
	 */
	protected static double minValue(double[] arr)
	{
		double min = arr[0];
		for(int i = 1; i < arr.length; i++)
		{
			if(arr[i] < min)
			{
				min = arr[i];
			}
		}
		return min;
	}
	
	/**
	 * Najde minimální hodnotu v matici.
	 * @param arr matice
	 * @return 
	 */
	protected static double minValue(double[][] arr)
	{
		double min = arr[0][0];
		for (double[] arr1 : arr) {
			for (int i = 0; i < arr1.length; i++) {
				if (arr1[i] < min) {
					min = arr1[i];
				}
			}
		}
		return min;
	}
	
	/**
	 * Najde maximální hodnotu v poli
	 * @param arr pole
	 * @return 
	 */
	protected static double maxValue(double[] arr)
	{
		double max = arr[0];
		for(int i = 1; i < arr.length; i++)
		{
			if(arr[i] > max)
			{
				max = arr[i];
			}
		}
		return max;
	}
	
	/**
	 * Najde maximální hodnotu v matici.
	 * @param arr matice
	 * @return 
	 */
	protected static double maxValue(double[][] arr)
	{
		double max = arr[0][0];
		for (double[] arr1 : arr) {
			for (int i = 0; i < arr1.length; i++) {
				if (arr1[i] > max) {
					max = arr1[i];
				}
			}
		}
		return max;
	}
	
	protected static void printArr(double[][] arr, int width, int height)
	{
		for(int j=0;j<height;++j)
		{
			for(int i=0;i<width; ++i)
			{
				System.out.print(" "+arr[j][i]);
			}
			System.out.println();
		}
	}
}
