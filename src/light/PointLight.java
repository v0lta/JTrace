package light;

import math.Color;
import math.Point;
import math.Vector;

public class PointLight implements Light{
	public final Point origin;
	public final boolean shadows;
	public final Color color;
	public final double ls;
	
	/*
	 * Constructor, which creates a point light object.
	 */	
	public PointLight(Point origin, Color color, double intensity,boolean shadows){
		this.origin = origin;
		this.color = color;
		this.ls = intensity;
		this.shadows = shadows;
	}
		
	@Override
	public Vector l(Point hitPoint) {
		Vector toLight = origin.toVector().subtract(hitPoint.toVector());
		toLight = toLight.normalize();
		return toLight;
	}
	@Override
	public Vector L() {	
		return color.toVector().scale(ls);
	}

}
