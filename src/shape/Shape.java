package shape;

import math.Ray;
import math.Intersection;

/**
 * Interface which should be implemented by all {@link Shape}s.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public interface Shape {
	/**
	 * Returns whether the given {@link Ray} intersects this {@link Shape}.
	 * False when the given ray is null.
	 * 
	 * @param ray
	 *            the {@link Ray} to intersect with.
	 * @return true when the given {@link Ray} intersects this {@link Shape}.
	 */
	public Intersection intersect(Ray ray);
}
