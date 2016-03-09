package material;

import math.Color;
import math.TextPoint;
import math.Vector;


public class Monochrome implements Material {

	public final Color color;
	
	public Monochrome(Color color) {
		this.color = color;
	}
	
	@Override
	public Color getColor(TextPoint txtPnt) {
		return this.color;
	}
	public Color getColor(Vector hitPoint) {
		return this.color;
	}

}
