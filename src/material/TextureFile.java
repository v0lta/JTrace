package material;


import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import math.Color;
import math.TextPoint;
import math.Vector;

public class TextureFile implements Material {
	private Raster fileData;
	private int height;
	private int width;
	public final String path;
	
	
	public TextureFile(String path){
		this.path = path;
		this.read();
	}
	
	public void read() {
	        File file = new File(this.path);
	        try {
				this.fileData = ImageIO.read(file).getData();
			} catch (IOException e) {
				System.err.println("Texture file not found. Path incorrect.");
				e.printStackTrace();
			}
	        this.height = fileData.getHeight();
	        this.width = fileData.getWidth();
	}
	
	@Override
	public Color getColor(Vector pointVec) {
		System.err.println("3d point intput to texture file not possible");
		return null;
	}
	
	
	@Override
	public Color getColor(TextPoint txtPnt) {
        

        int[] pixel = new int[3];
        int u = (int) (txtPnt.u*this.width);
        int v = (int) (txtPnt.v*this.height);
        System.out.println(u);
        System.out.println(v);
        fileData.getPixel(u,v,pixel);
        //System.out.println(pixel[1]);        
		return new Color(pixel[0],pixel[1],pixel[2]);
	}
	
	/** 
	 * A main for debugging.
	 */
	public static void main(String[] arguments){
		TextureFile test = new TextureFile("./obj/apple/apple_texture.jpg");
		test.read();
	}

}
