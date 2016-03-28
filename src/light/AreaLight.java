package light;

import shape.LightableShape;
import math.Normal;
import math.Point;
import math.Vector;

public class AreaLight implements Light {
	public final LightableShape shape;

	public AreaLight(LightableShape shape) {
		this.shape = shape;
	}
	
	public double pdf(){
		return this.shape.getInverseArea();
	}
	
	public Normal getNormal() {
		return this.shape.getNormal();
	}
	

	@Override
	public Vector l(Point hitPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector L() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
