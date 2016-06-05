package material;

import math.Vector;

/**
 * Create a Lambertian brdf part.
 * @author moritz
 */
public class Lambertian implements Diffuse {
	public final double rhod;
	
	public Lambertian(double rhod){
		this.rhod = rhod;
	}

	@Override
	public double getDiffuse(Vector N, Vector L) {
		double dot = N.dot(L);
		return rhod/Math.PI*dot;
	}

	@Override
	public double getRho() {
		return rhod/Math.PI;
	}

}
