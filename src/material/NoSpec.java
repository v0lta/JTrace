package material;

import math.Vector;

/**
 * Class to set the specular part of the brdf to zero.
 * @author moritz
 *
 */
public class NoSpec implements Specular {

	@Override
	public double getSpecular(Vector N, Vector L, Vector V) {
		return 0.0;
	}

}
