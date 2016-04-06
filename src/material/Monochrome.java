package material;

import math.Color;
import math.TextPoint;
import math.Vector;


public class Monochrome implements Material {

	public final Color color;
	public final Specular spec;
	
	public Monochrome(Specular spec, Color color) {
		this.color = color;
		this.spec = spec;
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

}
