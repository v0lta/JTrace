package material;

import math.Color;
import math.TextPoint;
import math.Transformation;
import math.Vector;


public class Monochrome implements Material {

	public final Color color;
	public final Specular spec;
	public final Diffuse diff;
	
	/**
	 * Creates a material with just one color.
	 * @param spec the specular brdf part.
	 * @param diff the diffuse brdf part.
	 * @param color the material color.
	 */	
	public Monochrome(Specular spec, Diffuse diff, Color color) {
		this.color = color;
		this.spec = spec;
		this.diff = diff;
	}
	
	@Override
	public Color getColor(TextPoint txtPnt) {
		return this.color;
	}
	public Color getColor(Vector hitPoint) {
		return this.color;
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

	@Override
	public Material setInvTrans(Transformation reverseTrans) {return this;}

}
