package material;

import math.Vector;

public interface Specular {
	
	public double getSpecular(Vector N, Vector L, Vector V);
}
