package material;

import math.Vector;

/**
 * Class to set the diffuse part of the brdf to zero.
 * @author moritz
 *
 */
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
