package main;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ComputeMSE {
	/**
	 * Compare two images by computing their mean square difference. 
	 */
	
	public static void main(String[] args) {
		
		//set the image paths.
		//String path1 = "50spp.png";
		String path1 = "output.png";
		String path2 = "output1500.png";
		
		BufferedImage image1 = null;
		BufferedImage image2 = null;
		
		//read in the image data.
		try {
			image1 = ImageIO.read(new File(path1));
			image2 = ImageIO.read(new File(path2));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Raster imgGrid1 = image1.getData();
		Raster imgGrid2 = image2.getData();
		
		if (image1.getHeight() != image2.getHeight()) {
			System.err.println("Images must have the same width for mse computation.");
			return;
		}
		
		if (image1.getWidth() != image2.getWidth()) {
			System.err.println("Images must have the same width for mse computation.");
			return;
		}
		
		
		//at this point the images must have the same height and width. Start with mse computation.
		int w = image1.getWidth(); 
		int h = image1.getHeight();
		
		double sum = 0;
		for (int i=0; i < w; i++) {
			for (int j=0; j < h; j++) {
				int[] pixels1 = null;
				pixels1 = imgGrid1.getPixel(i, j, pixels1);
				double greyVal1 = ((double)pixels1[0] + pixels1[1] + pixels1[2])/3.0;
				
				int[] pixels2 = null;
				pixels2 = imgGrid2.getPixel(i, j, pixels2);
				double greyVal2 = ((double)pixels2[0] + pixels2[1] + pixels2[2])/3.0;
				
				sum = sum + (greyVal1 - greyVal2) * (greyVal1 - greyVal2);
				
//				if (pixels1[1] != 0) {
//					System.out.println(pixels1[0]);
//				}
				
			}
			
		}
		sum = sum / (w*h);
		System.out.println("mse:");
		System.out.println(sum);
	}
	
	
}
