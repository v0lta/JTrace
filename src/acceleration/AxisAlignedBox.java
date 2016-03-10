package acceleration;

import java.util.ArrayList;
import java.util.List;

import math.Color;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shape.Shape;

/**
 * A axis aligned box class following page 357 and 360 of the course
 * text.
 * @author moritz
 *
 */

public class AxisAlignedBox implements Shape {
	public final Point p0;
	public final Point p1;
	public final Transformation transformation;
	
	public AxisAlignedBox(Point p0,Point p1, Transformation transformation){
		this.p0 = p0;
		this.p1 = p1;
		this.transformation = transformation;
	}

	public boolean intersectBool(Ray ray) { 
		
		Point ro;
		Vector rd;
		
        if (this.transformation != null) {
            Ray rayInv = this.transformation.transformInverse(ray);
            ro = rayInv.origin;
            rd = rayInv.direction;
        } else {      
            ro = ray.origin;
            rd = ray.direction;
        }
		
		double x0 = p0.x; double y0 = p0.y; double z0 = p0.z;
		double x1 = p1.x; double y1 = p1.y; double z1 = p1.z;
		
		double ox = ro.x; double oy = ro.y; double oz = ro.z;
		double dx = rd.x; double dy = rd.y; double dz = rd.z;
		
		double txMin, tyMin, tzMin;
		double txMax, tyMax, tzMax; 

		double a = 1.0 / dx;
		if (a >= 0) {
			txMin = (x0 - ox) * a;
			txMax = (x1 - ox) * a;
		}
		else {
			txMin = (x1 - ox) * a;
			txMax = (x0 - ox) * a;
		}
		
		double b = 1.0 / dy;
		if (b >= 0) {
			tyMin = (y0 - oy) * b;
			tyMax = (y1 - oy) * b;
		}
		else {
			tyMin = (y1 - oy) * b;
			tyMax = (y0 - oy) * b;
		}
		
		double c = 1.0 / dz;
		if (c >= 0) {
			tzMin = (z0 - oz) * c;
			tzMax = (z1 - oz) * c;
		}
		else {
			tzMin = (z1 - oz) * c;
			tzMax = (z0 - oz) * c;
		}
		
		double t0, t1;
		
		// find largest entering t value
		
		if (txMin > tyMin)
			t0 = txMin;
		else
			t0 = tyMin;
			
		if (tzMin > t0)
			t0 = tzMin;	
			
		// find smallest exiting t value
			
		if (txMax < tyMax)
			t1 = txMax;
		else
			t1 = tyMax;
			
		if (tzMax < t1)
			t1 = tzMax;
			
		return(t0 < t1 && t1 > Constants.epsilon);		
	}
	
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		
		List<Intersection> intList = new ArrayList<Intersection>();
		
		Point ro;
		Vector rd;
        Ray rayInv = this.transformation.transformInverse(ray);
        ro = rayInv.origin;
        rd = rayInv.direction;
		
		double x0 = p0.x; double y0 = p0.y; double z0 = p0.z;
		double x1 = p1.x; double y1 = p1.y; double z1 = p1.z;
		
		double ox = ro.x; double oy = ro.y; double oz = ro.z;
		double dx = rd.x; double dy = rd.y; double dz = rd.z;
		
		double txMin, tyMin, tzMin;
		double txMax, tyMax, tzMax; 

		double a = 1.0 / dx;
		if (a >= 0) {
			txMin = (x0 - ox) * a;
			txMax = (x1 - ox) * a;
		}
		else {
			txMin = (x1 - ox) * a;
			txMax = (x0 - ox) * a;
		}
		
		double b = 1.0 / dy;
		if (b >= 0) {
			tyMin = (y0 - oy) * b;
			tyMax = (y1 - oy) * b;
		}
		else {
			tyMin = (y1 - oy) * b;
			tyMax = (y0 - oy) * b;
		}
		
		double c = 1.0 / dz;
		if (c >= 0) {
			tzMin = (z0 - oz) * c;
			tzMax = (z1 - oz) * c;
		}
		else {
			tzMin = (z1 - oz) * c;
			tzMax = (z0 - oz) * c;
		}
		
		double t0, t1;
		int faceIn, faceOut;
		
		//find largest entering t value.		
		if (txMin > tyMin) {
			t0 = txMin;
			faceIn = (a >= 0.0) ? 0 : 3;
		} else {
			t0 = tyMin;
			faceIn = (b >= 0.0) ? 1 : 4;
		}
		
		if (tzMin > t0) {
			t0 = tzMin;
			faceIn = (c >= 0.0) ? 2 : 5; 
		}
		
		//find smallest existing t value.
		if (txMax < tyMax) {
			t1 = txMax;
			faceOut = (a <= 0.0) ? 3 : 0;
		} else {
			t1 = tyMax;
			faceOut = (b >= 0.0) ? 4 : 1;
		}
		
		if (tzMax < t1){
			t1 = tzMax;
			faceOut = (c >= 0.0) ? 5:2;
		}
		
		double tMin;
		Normal normal;
		if ((t0 < t1) && (t1 > Constants.epsilon)) { // hit condition
			if (t0 > Constants.epsilon) {
				tMin = t0;
				normal = getNormal(faceIn);
			} else {
				tMin = t1;
				normal = getNormal(faceOut);
			}
			Vector hitPoint = ro.toVector().add(rd.scale(tMin));
	        hitPoint = this.transformation.transform( hitPoint );
			intList.add(new Intersection(true,hitPoint.toPoint(),normal,new Color(100,100,100),10));
		}
		
		//if (intersectBool(ray)){
		//	System.out.println("HIT!!");
		//}
		
		
		return intList;	
		
	}
	
	private Normal getNormal(int faceHit) {
		switch(faceHit) {
		case 0: return new Normal(-1,0,0);
		case 1: return new Normal(0,-1,0);
		case 2: return new Normal(0,0,-1);
		case 3: return new Normal(1,0,0);
		case 4: return new Normal(0,1,0);
		case 5: return new Normal(0,0,1);
		default:  return null;
		}
		
		
		
	}
	
}
