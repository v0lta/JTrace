package light;

import math.Color;
import math.Point;
import math.Vector;

public class PointLight implements Light{
	public final Point origin;
	public final boolean shadows;
	public final Color color;
	public final double ls;
	
	/**
	 * Constructor, which creates a point light object.	
	 * @param origin point of origin
	 * @param color the lights color
	 * @param intensity the lights brightness
	 * @param shadows boolean if false no shadows.
	 */
	public PointLight(Point origin, Color color, double intensity,boolean shadows){
		this.origin = origin;
		this.color = color;
		this.ls = intensity;
		this.shadows = shadows;
	}
	/**
	 * Get the normalized vector pointing to the light source	
	 */
	@Override
	public Vector l(Point hitPoint) {
		Vector toLight = origin.toVector().subtract(hitPoint.toVector());
		toLight = toLight.normalize();
		return toLight;
	}
	
	/**
	 * Get the lights intensity scaled color.
	 */	
	@Override
	public Vector L() {	
		return color.toVector().scale(ls);
	}

}
