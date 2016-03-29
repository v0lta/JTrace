package shape;

import java.util.List;

import math.Ray;
import math.Intersection;
import math.Transformation;

/**
 * Interface which should be implemented by all {@link Shape}s.
 * 
 * @author Niels Billen, Moritz Wolter
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
	public List<Intersection> intersect(Ray ray);
}
