package material;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import math.Color;
import math.Vector;

public class TextureFile implements Material {
	private Raster fileData;
	
	public void read() throws IOException{
		 String path = "./obj/apple/apple_texture.jpg";
	        File file = new File(path);
	        fileData = ImageIO.read(file).getData();			
	}
	

	@Override
	public Color getColor(Vector pointVec) {
		// TODO Auto-generated method stub
        
        System.out.println(fileData.getHeight());
        System.out.println(fileData.getWidth());
        int[] pixel = new int[3];
        fileData.getPixel(100,100,pixel);
        System.out.println(pixel[1]);
        
		return new Color(pixel[0],pixel[1],pixel[2]);
	}
	
	/** 
	 * A main for debugging.
	 */
	public static void main(String[] arguments){
		TextureFile test = new TextureFile();
		try {
			test.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
