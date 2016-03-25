package acceleration;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shape.Shape;
import shape.Triangle;

/**
 * A axis aligned box class following page 357 and 360 of the course text.
 * 
 * @author moritz
 *
 */

public class AxisAlignedBox implements Shape {
	public final Point p0;
	public final Point p1;
	public final Transformation transformation;
	public List<Triangle> trianglesInBox = new ArrayList<Triangle>();
	protected Camera cam;
	
	/**
	 * Create a new axis aligned bounding box.
	 * @param p0 the lower point (p0 < p1) along x, y and z.
	 * @param p1 the upper point.
	 * @param transformation, is applied to all triangles in the box.
	 */
	public AxisAlignedBox(Point p0, Point p1, Transformation transformation, Camera cam) {

		if ((p0.x > p1.x) || (p0.y > p1.y) || (p0.z > p1.z)) {
			//System.err.println("Illegal box.");
			throw new IllegalArgumentException("Illegal box.");
		}

		this.p0 = p0;
		this.p1 = p1;
		this.transformation = transformation;
		this.cam = cam;
	}
	
	public void split(int depth) {
		System.err.println("Splitting implemented in child classes");
	}
	
		
	protected void checkBox(AxisAlignedBox box, AxisAlignedBox box2){
		Boolean fail = false;
		if ((box.p0.x > box.p1.x) || (box.p0.y > box.p1.y) || (box.p0.z > box.p1.z)) {
			fail = true;
			System.err.println("Found illegal box");
		};
		
		for (Triangle triangle : box.trianglesInBox){
			Point a = triangle.a;
			Point b = triangle.b;
			Point c = triangle.c;
			
			boolean aIn = false;
			boolean bIn = false;
			boolean cIn = false;

			if  (((a.x > box.p0.x) && (a.y  > box.p0.y) && (a.z > box.p0.z))
					&& ((a.x < box.p1.x) && (a.y < box.p1.y) && (a.z < box.p1.z))) {
				// a is in
				aIn = true;
			} 
			if (((b.x > box.p0.x) && (b.y > box.p0.y) && (b.z > box.p0.z))
					&& ((b.x < box.p1.x) && (b.y < box.p1.y) && (b.z < box.p1.z))) {
				// b is in
				bIn = true;
			}
			if (((c.x > box.p0.x) && (c.y > box.p0.y) && (c.z > box.p0.z))
					&& ((c.x < box.p1.x) && (c.y < box.p1.y) && (c.z < box.p1.z))) {
				// c is in
				cIn = true;
			}
			if ((aIn == false) || (bIn == false) || (cIn == false)) {
				fail = true;
				System.err.println("Found triangle out of box.");				
			}
			
			double maxVal = triangle.getLargestCoord('x');
			if ((maxVal < a.x) || (maxVal < b.x) || (maxVal < c.x)) {
				fail = true;
				System.err.println("Max is not max along x");				
			}
			maxVal = triangle.getLargestCoord('y');
			if ((maxVal < a.y) || (maxVal < b.y) || (maxVal < c.y)) {
				fail = true;
				System.err.println("Max is not max along y");				
			}
			maxVal = triangle.getLargestCoord('z');
			if ((maxVal < a.z) || (maxVal < b.z) || (maxVal < c.z)) {
				fail = true;
				System.err.println("Max is not max along z");				
			}
			
			double minVal = triangle.getSmallestCoord('x');
			if ((minVal > a.x) || (minVal > b.x) || (minVal > c.x)) {
				fail = true;
				System.err.println("Min is not min along x");				
			}
			minVal = triangle.getSmallestCoord('y');
			if ((minVal > a.y) || (minVal > b.y) || (minVal > c.y)) {
				fail = true;
				System.err.println("Min is not min along y");				
			}
			minVal = triangle.getSmallestCoord('z');
			if ((minVal > a.z) || (minVal > b.z) || (minVal > c.z)) {
				fail = true;
				System.err.println("Min is not min along z");				
			}
			
			
			
			
		}
	}
	
	
	/**
	 * A simple and fast way to find out if a ray has hit the bounding box
	 * 
	 * @param ray
	 *            , the ray the box should be intersected with.
	 * @return a boolean indicating an intersection.
	 */
	public boolean intersectBool(Ray ray) {

		//Ray rayInv = ray;
		Ray rayInv = this.transformation.transformInverse(ray);
		Point ro = rayInv.origin;
		Vector rd = rayInv.direction;

		double x0 = p0.x; double y0 = p0.y;	double z0 = p0.z;
		double x1 = p1.x; double y1 = p1.y;	double z1 = p1.z;
		
		double ox = ro.x; double oy = ro.y;	double oz = ro.z;
		double dx = rd.x; double dy = rd.y;	double dz = rd.z;

		double txMin, tyMin, tzMin;
		double txMax, tyMax, tzMax;

		double a = 1.0 / dx;
		if (a >= 0) {
			txMin = (x0 - ox) * a;
			txMax = (x1 - ox) * a;
		} else {
			txMin = (x1 - ox) * a;
			txMax = (x0 - ox) * a;
		}

		double b = 1.0 / dy;
		if (b >= 0) {
			tyMin = (y0 - oy) * b;
			tyMax = (y1 - oy) * b;
		} else {
			tyMin = (y1 - oy) * b;
			tyMax = (y0 - oy) * b;
		}

		double c = 1.0 / dz;
		if (c >= 0) {
			tzMin = (z0 - oz) * c;
			tzMax = (z1 - oz) * c;
		} else {
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

		return ((t0 < t1) && (t1 > Constants.epsilon));
	}

	
	public List<Intersection> intersectNormal(Ray ray) {
		List<Intersection> intList = new ArrayList<Intersection>();
		intList.clear();

		Point ro;
		Vector rd;
		Ray rayInv = this.transformation.transformInverse(ray);
		ro = rayInv.origin;
		rd = rayInv.direction;

		double x0 = p0.x; double y0 = p0.y; double z0 = p0.z;
		double x1 = p1.x; double y1 = p1.y;	double z1 = p1.z;

		double ox = ro.x; double oy = ro.y;	double oz = ro.z;
		double dx = rd.x; double dy = rd.y;	double dz = rd.z;

		double txMin, tyMin, tzMin;
		double txMax, tyMax, tzMax;

		double a = 1.0 / dx;
		if (a >= 0) {
			txMin = (x0 - ox) * a;
			txMax = (x1 - ox) * a;
		} else {
			txMin = (x1 - ox) * a;
			txMax = (x0 - ox) * a;
		}

		double b = 1.0 / dy;
		if (b >= 0) {
			tyMin = (y0 - oy) * b;
			tyMax = (y1 - oy) * b;
		} else {
			tyMin = (y1 - oy) * b;
			tyMax = (y0 - oy) * b;
		}

		double c = 1.0 / dz;
		if (c >= 0) {
			tzMin = (z0 - oz) * c;
			tzMax = (z1 - oz) * c;
		} else {
			tzMin = (z1 - oz) * c;
			tzMax = (z0 - oz) * c;
		}

		double t0, t1;
		int faceIn, faceOut;

		// find largest entering t value.
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

		// find smallest exiting t value.
		if (txMax < tyMax) {
			t1 = txMax;
			faceOut = (a >= 0.0) ? 3 : 0;
		} else {
			t1 = tyMax;
			faceOut = (b >= 0.0) ? 4 : 1;
		}

		if (tzMax < t1) {
			t1 = tzMax;
			faceOut = (c >= 0.0) ? 5 : 2;
		}

		double tMin;
		Normal normal;
		Vector hitPoint;
		if ((t0 < t1) && (t1 > Constants.epsilon)) { // hit condition
			if (t0 > Constants.epsilon) {
				tMin = t0;
				normal = getNormal(faceIn);
			} else {
				tMin = t1;
				normal = getNormal(faceOut);
			}
			hitPoint = ro.toVector().add(rd.scale(tMin));
			hitPoint = this.transformation.transform(hitPoint);

			normal = this.transformation.transformInverseTranspose(normal);
			normal = normal.toVector().normalize().toNormal();

			intList.add(new Intersection(hitPoint.toPoint(), normal, new Color(
					100, 10, 10), 10));
		}

		// if (intersectBool(ray)){
		// System.out.println("HIT!!");
		// }

		return intList;
	}

	protected Normal getNormal(int faceHit) {
		switch (faceHit) {
		case 0:
			return new Normal(-1, 0, 0);
		case 1:
			return new Normal(0, -1, 0);
		case 2:
			return new Normal(0, 0, -1);
		case 3:
			return new Normal(1, 0, 0);
		case 4:
			return new Normal(0, 1, 0);
		case 5:
			return new Normal(0, 0, 1);
		default:
			System.err.println("aabb normal error.");
			return null;
		}

	}

	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		
		if (Constants.compVisualization){
			ray.countIntersection();
		}
		
		//maximum depth reached.
		if (this.intersectBool(ray)){
			for (Triangle tri :	this.trianglesInBox) {
				hits.addAll(tri.intersect(ray));
			}
		}
		return hits;
	}
	
	protected double smallestInLst(List<Triangle> lst, Character axis){
		double smlst = lst.get(0).getSmallestCoord(axis);
		double current;
		
		for (Triangle tr: lst) {
			current = tr.getSmallestCoord(axis); 
			if (current < smlst) {
				smlst = current;
			}
		}
		return smlst;		
	}
	protected double largestInLst(List<Triangle> lst, Character axis){
		double lrgst = lst.get(0).getLargestCoord(axis);
		double current;
		
		for (Triangle tr: lst) {
			current = tr.getLargestCoord(axis); 
			if (current > lrgst) {
				lrgst = current;
			}
		}
		return lrgst;		
	}
	
	public Point getCenter(){
		double cx = (1.0/2.0) * ((this.p0.x) + (this.p1.x));
		double cy = (1.0/2.0) * ((this.p0.y) + (this.p1.y));
		double cz = (1.0/2.0) * ((this.p0.z) + (this.p1.z));
		Point center = new Point(cx,cy,cz);
		return center;
	}
	
	
	/**
	 * Learn if a point is in the transformed box.
	 * Only for intersection testing not for tree construction.
	 * @param   toTest transformed interseciton point.
	 * @return  true if point in the transformed box,
	 * 			false if not.
	 */	
	public boolean pointInBox(Point toTest){
		Point p0 = this.transformation.transform(this.p0);
		Point p1 = this.transformation.transform(this.p1);
		
		if ((toTest.x < p0.x) || (toTest.x > p1.x)) {
			return false;
		}
		if ((toTest.y < p0.y) || (toTest.y > p1.y)) {
			return false;
		}
		if ((toTest.z < p0.z) || (toTest.z > p1.z)) {
			return false;
		}
		return true;
	}
	
	public boolean triInBox(Triangle triangle,Point p0,Point p1 ) {
		double eps = Constants.treeEpsilon;
		Point centroid = triangle.getCentroid();

		if  (((centroid.x + eps > p0.x) && (centroid.y + eps > p0.y) && (centroid.z + eps > p0.z))
				&& ((centroid.x - eps < p1.x) && (centroid.y - eps < p1.y) && (centroid.z - eps < p1.z))) {
			// a is in
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Add a triangle to the internal triangle list if triangle centroid is within the box-bounds.
	 * @param triangle 
	 */
	public void addIfIn(Triangle triangle){
		double eps = Constants.treeEpsilon;
		Point centroid = triangle.getCentroid();

		if  (((centroid.x + eps > p0.x) && (centroid.y + eps > p0.y) && (centroid.z + eps > p0.z))
				&& ((centroid.x - eps < p1.x) && (centroid.y - eps < p1.y) && (centroid.z - eps < p1.z))) {
			// a is in
			this.trianglesInBox.add(triangle);
		}
	}
	
	public double getVolume() {
		double x = Math.abs(p0.x - p1.x);
		double y = Math.abs(p0.y - p1.y);
		double z = Math.abs(p0.z - p1.z);
		return x*y*z;
	}
	
	public double getSurface(Character axis){
		double x = Math.abs(p0.x - p1.x);
		double y = Math.abs(p0.y - p1.y);
		double z = Math.abs(p0.x - p1.z);
		
		if (axis == 'x'){
			return x*y;
		} else if (axis == 'y'){
			return y*z;
		} else {
			return z*x;
		}
		
	}
	
}