package material;

import math.Vector;

/**
 * Interface implemented by specular functions.
 * @author moritz
 *
 */
public interface Specular {
	public double getSpecular(Vector N, Vector L, Vector V);
}
