package material;

import math.Color;
import math.TextPoint;
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
	public Color getColor(TextPoint txtPnt);	
	public Color getColor(Vector hitPoint );
}
