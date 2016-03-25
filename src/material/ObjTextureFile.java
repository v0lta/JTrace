package material;


import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import math.Color;
import math.TextPoint;
import math.Vector;

public class ObjTextureFile implements Material {
	private Raster fileData;
	private int height;
	private int width;
	public final String path;
	public final double size;
	
	
	/**
	 * Create a texture file, to make an image file available as texture. 
	 * @param path the path to the image.
	 * @param size the size with with the image will contain in the scene.
	 */
	
	public ObjTextureFile(String path, double size){
		this.path = path;
		this.read();
		this.size = size;
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
        //u and v need to be in [0,1] intitially;
		double u = Math.abs(txtPnt.u/this.size) % this.size;
		double v = Math.abs(txtPnt.v/this.size) % this.size;
		
		int indu;
		int indv;
	
        int[] pixel = new int[3];
        indu = ((int) (u*this.width)) - 1;// % this.width;
        indv = this.height - ((int) (v*this.height)) - 1;// % this.height;
        if (indu > this.width - 1) {
        	indu = this.width - 1;
        } else if (indu < 0) {
        	indu = 0;
        }
        if (indv > this.width - 1)  {
        	indv = this.width - 1;
        } else if (indv < 0){
        	indv = 0;
        }
        
        
        fileData.getPixel(indu,indv,pixel);
        //System.out.println(pixel[1]);        
		return new Color(pixel[0],pixel[1],pixel[2]);
        //return new Color(1.0,1.0,1.0);
	}
	
	/** 
	 * A main for debugging.
	 */
	public static void main(String[] arguments){
		ObjTextureFile test = new ObjTextureFile("./obj/apple/apple_texture.jpg",1.0);
		test.read();
	}
	


}
