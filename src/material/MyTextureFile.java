package material;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import math.Color;
import math.TextPoint;
import math.Vector;

public class MyTextureFile implements Material {

	private Raster fileData;
	private int height;
	private int width;
	public final String path;
	public final Specular spec;
	public final Diffuse diff;
	
	/**
	 * Create a texture file, to make an image file available as texture. 
	 * This implementation follows the procedure outlined in the ray
	 * Tracing from the ground up book.
	 * @param path the path to the image.
	 * @param specular part of the desired brdf.
	 */
	public MyTextureFile(Specular spec, Diffuse diff, String path){
		this.path = path;
		this.read();
		this.spec = spec;
		this.diff = diff;
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
		double u = txtPnt.u;
		double v = txtPnt.v;
		
		int indu;
		int indv;
	
        int[] pixel = new int[3];
        indu = (int) (Math.round(u*(this.width - 1)));
        indv = (int) (Math.round(v*(this.height - 1)));

        fileData.getPixel(indu,indv,pixel);
		return new Color(pixel[0],pixel[1],pixel[2]);
        //return new Color(indu,indv,0);
	}
	
	@Override
	public double getSpecular(Vector N, Vector L, Vector V) {
		return this.spec.getSpecular(N, L, V);
	}


	@Override
	public double getDiffuse(Vector N, Vector L) {
		return this.diff.getDiffuse(N, L);
	}


	@Override
	public double getDiffuseRho() {
		return this.diff.getRho();
	}
	
	
}
	