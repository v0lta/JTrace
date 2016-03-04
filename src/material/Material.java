package material;

import math.Color;
import math.Vector;



/**
 * Interface which should be implemented by all {@link material}s.
 * 
 * @author Moritz
 */
public interface Material {
	/**
	 * Returns the color corresponding to the given texture coordinates.
	 * 
	 * @param coord : the coordinates where the color needs to be determined.
	 *    
	 * @return a color object.
	 */
	public Color getColor(Vector pointVec);
}
