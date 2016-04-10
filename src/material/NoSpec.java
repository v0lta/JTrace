package material;

import math.Vector;

public class NoSpec implements Specular {

	@Override
	public double getSpecular(Vector N, Vector L, Vector V) {
		return 0.0;
	}

}
