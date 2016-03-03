package light;

import math.Color;
import math.Point;
import math.Vector;

/**
 * Interface which should be implemented by all {@link Light}s
 * @author moritz
 */

public interface Light {
	
	/*
	 *  Return the vector pointing towards the light source.
	 */
	public Vector l(Point hitPoint);
	/*
	 *   Return the light source intensity.
	 */
	public Vector L();		
}
