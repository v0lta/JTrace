package material;

import math.Vector;

public interface Diffuse {
	public double getDiffuse(Vector N, Vector L);
}
