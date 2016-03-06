package material;

import math.Color;
import math.Vector;

public class Monochrome implements Material {

	public final Color color;
	
	public Monochrome(Color color) {
		this.color = color;
	}
	
	@Override
	public Color getColor(Vector pointVec) {
		return this.color;
	}

}
