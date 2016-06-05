package material;

import math.Vector;

/**
 * Interface to be implemented by diffuse brdf
 * summands.
 * @author moritz
 */
public interface Diffuse {
	public double getDiffuse(Vector N, Vector L);
	public double getRho();
}
