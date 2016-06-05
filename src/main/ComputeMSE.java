package main;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

public class ComputeMSE {
	/**
	 * Compare two images by computing their mean square difference. 
	 */	
	public static List<File> findPNG(final File folder, File target) {
		List<File> fileList = new ArrayList<File>();
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	findPNG(fileEntry, target);
	        } else {
	        	String fileName = fileEntry.getName();
	        	String extension = "";
	        	int i = fileName.lastIndexOf('.');
	        	if (i > 0) {
	        	    extension = fileName.substring(i+1);
	        	    if (extension.equals("png")) {
	        	    	if (fileEntry.getName().equals(target.getName()) == false) {
	        	    		fileList.add(fileEntry);
	        	    	}		        			        		
		        	}
	        	}
	        }
	    }
	    return fileList;
	}
	
	
	/**
	 * Computes the mean squared error for a target file and all the .png files in a given folder.
	 * The resolution of the target image and all the images in the specified folder have to
	 * be the same.
	 */
	public static void main(String[] args) {
		//set the paths.
	
		final File folder = new File("/home/moritz/workspace/JTrace/");

		//simple experiment
		File target = new File("/home/moritz/workspace/JTrace/errComp/output2500.png");
        //final File folder = new File("/home/moritz/workspace/JTrace/errComp/exp2NoPrior");
        //final File folder = new File("/home/moritz/workspace/JTrace/errComp/exp3Prior");
		//final File folder = new File("/home/moritz/workspace/JTrace/errComp/splitNoAnalysis");
		
        //with walls and objects
		//File target = new File("/home/moritz/workspace/JTrace/errCompExt/exp4Output2500.png");
        //final File folder = new File("/home/moritz/workspace/JTrace/errCompExt/exp4NoPrior");
        //final File folder = new File("/home/moritz/workspace/JTrace/errCompExt/exp4Prior");
	    
        
        List<File> pngList = findPNG(folder, target);
        pngList = sort(pngList);

		//read in the image data.
		Raster trgtImg = read(target).getData();
		
		List<Raster> imgList = new ArrayList<Raster>();
		for (File toLoad: pngList){
			imgList.add(read(toLoad).getData());
		}
		
		int w = trgtImg.getWidth(); 
		int h = trgtImg.getHeight();
		
		List<Double> sumLst = new ArrayList<Double>();
		List<Double> sumLstGrey = new ArrayList<Double>();
		for (Raster currentImg : imgList) {
			
			//at this point the images must have the same height and width. Start with mse computation.
			if (currentImg.getHeight() != h){
				System.err.println("All images must have the same hights.");
				return;
			}
			if (currentImg.getWidth() != w){
				System.err.println("All images must have the same width.");
				return;
			}
				
			double sum = 0;
			double sumGrey = 0;
			for (int i=0; i < w; i++) {
				for (int j=0; j < h; j++) {
					int[] pixels1 = null;
					pixels1 = trgtImg.getPixel(i, j, pixels1);
					double greyVal1 = ((double)pixels1[0] + pixels1[1] + pixels1[2])/3.0;
					
					int[] pixels2 = null;
					pixels2 = currentImg.getPixel(i, j, pixels2);
					double greyVal2 = ((double)pixels2[0] + pixels2[1] + pixels2[2])/3.0;
					
					double rDiffSquare = (pixels1[0] - pixels2[0]) * (pixels1[0] - pixels2[0]);
					double bDiffSquare = (pixels1[1] - pixels2[1]) * (pixels1[1] - pixels2[1]);
					double gDiffSquare = (pixels1[2] - pixels2[2]) * (pixels1[2] - pixels2[2]);
					
					sum = sum + (rDiffSquare + bDiffSquare + gDiffSquare)/3;
					sumGrey = sumGrey + ( greyVal1 - greyVal2) * ( greyVal1 - greyVal2); 
	//				if (pixels1[1] != 0) {
	//					System.out.println(pixels1[0]);
	//				}
					
				}
				
			}
			sum = sum / (w*h);
			sumLst.add(sum);
			sumGrey = sumGrey / (w*h);
			sumLstGrey.add(sumGrey);
		}
		
		System.out.println("mse color:");
		System.out.println(sumLst);
		System.out.println("mse grey");
		System.out.println(sumLstGrey);
	}
	
	public static BufferedImage read(File toRead){
		BufferedImage image = null; 
		try {
			image =  ImageIO.read(toRead);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static List<File> sort(List<File> pngList){
		Collections.sort(pngList, new Comparator<File>() {
			public int compare(File f1, File f2) {
				try {
					String fileName = f1.getName();
		        	int i = fileName.lastIndexOf('.');
		        	String f1NumStr = fileName.substring(0,i);
		        	String fileName2 = f2.getName();
		        	i = fileName2.lastIndexOf('.');
		        	String f2NumStr = fileName2.substring(0,i);
					int i1 = Integer.parseInt(f1NumStr);
					int i2 = Integer.parseInt(f2NumStr);
					if (i1 > i2){
						return 1;
					} else if (i1 < i2){
						return -1;
					}else {
						return 0;
					}					
				} catch(NumberFormatException e) {
					throw new AssertionError(e);
				}
			}
		});
		return pngList;
	}
	
}
