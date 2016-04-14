package material;

import math.Color;
import math.TextPoint;
import math.Vector;

public class Chess implements Material{
	public final Color color1;
	public final Color color2;
	public final double s;
	public final Specular spec;
	public final Diffuse diff;
	
	/**
	 * Creates material, which looks like a chess board.
	 * @param spec the specular part of the brdf.
	 * @param diff the diffuse part of the brdf.
	 * @param color1 first rectangle color
	 * @param color2 second rectangle color
	 * @param s 	the rectangle size.
	 */	
	public Chess(Specular spec, Diffuse diffuse, Color color1, Color color2, double s) {
		this.spec = spec;
		this.diff = diffuse;
		this.color1 = color1;
		this.color2 = color2;
		this.s = s;
	}

	@Override
	public Color getColor(TextPoint texPoint) {
		int tmp = ( (int) Math.round( texPoint.u/s) 
			       + (int) Math.round( texPoint.v/s));
		int tmp2 = tmp%2;
		
		if (   tmp2  == 0) {
	        return this.color1;
	    		    
		} else {
			return this.color2;
		}
	}
	
	@Override
	public Color getColor(Vector texPoint) {
		int tmp = (  (int) Math.round( texPoint.x/s) 
			       + (int) Math.round( texPoint.y/s)
			       + (int) Math.round( texPoint.z/s) );
		int tmp2 = tmp%2;
		
		if (   tmp2  == 0) {
	        return this.color1;
	    		    
		} else {
			return this.color2;
		}
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
