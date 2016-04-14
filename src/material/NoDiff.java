package material;

import math.Vector;

public class NoDiff implements Diffuse {

	@Override
	public double getDiffuse(Vector N, Vector L) {
		return 0.0;
	}

	@Override
	public double getRho() {
		return 0;
	}

}
