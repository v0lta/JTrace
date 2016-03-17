package acceleration;

import java.util.ArrayList;
import java.util.Collections;
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
	public AxisAlignedBox left = null;
	public AxisAlignedBox right = null;
	public List<Triangle> trianglesInBox = new ArrayList<Triangle>();
	private int depth = 0;
	
	/**
	 * Create a new axis aligned bounding box.
	 * @param p0 the lower point (p0 < p1) along x, y and z.
	 * @param p1 the upper point.
	 * @param transformation, is applied to all triangles in the box.
	 */
	public AxisAlignedBox(Point p0, Point p1, Transformation transformation) {

		if ((p0.x > p1.x) || (p0.y > p1.y) || (p0.z > p1.z)) {
			System.err.println("Illegal box.");
		}

		this.p0 = p0;
		this.p1 = p1;
		this.transformation = transformation;
	}
	
	public void split(int depth) {
		this.depth = depth;
		int triCount = this.trianglesInBox.size();
		depth = 1;
		
		if ((depth > 0) && (triCount > 3)) {
			
			// split the aab in two along the longest Axis.
			int halfList = triCount/2;;
			List<Triangle> leftList;
			List<Triangle> rightList;
			
			double xLength = Math.abs(this.p0.x - this.p1.x);
			double yLength = Math.abs(this.p0.y - this.p1.y);
			double zLength = Math.abs(this.p0.z - this.p1.z);

			Point pLeft;
			Point pRight;

			if ((xLength > yLength) && (xLength > zLength)) {
				// the box largest dimension is x
				TriangleCentComp triCentComp = new TriangleCentComp('x');
				
				Collections.sort(this.trianglesInBox,triCentComp);
				
				leftList = this.trianglesInBox.subList(0, halfList);
				rightList = this.trianglesInBox.subList(halfList, triCount);
				
				double xNewLeft = largestInLst(leftList,'x') + Constants.treeEpsilon;
				double xNewRight = smallestInLst(rightList,'x') - Constants.treeEpsilon;
				pLeft = new Point(xNewLeft, this.p1.y, this.p1.z);
				pRight = new Point(xNewRight, this.p0.y, this.p0.z);

			} else if ((yLength > xLength) && (yLength > zLength)) {
				// the largest dimension is z split along y.
				TriangleCentComp triCentComp = new TriangleCentComp('y');
				Collections.sort(this.trianglesInBox,triCentComp);
				
				leftList = this.trianglesInBox.subList(0, halfList);
				rightList = this.trianglesInBox.subList(halfList, triCount);
				
				double yNewLeft = largestInLst(leftList,'y');
				double yNewRight = smallestInLst(rightList,'y');
				pLeft = new Point(this.p1.x, yNewLeft, this.p1.z);
				pRight = new Point(this.p0.x, yNewRight, this.p0.z); 
			} else {
			
				// the largest dimension is z split along z.
				TriangleCentComp triCentComp = new TriangleCentComp('z');
				
				Collections.sort(this.trianglesInBox,triCentComp);
				
				//List<Double> tmpLst = new ArrayList<Double>();
				//for (Triangle tri : this.trianglesInBox ) {
				//	tmpLst.add(tri.getCentroid().z);
				//}
				
				leftList = this.trianglesInBox.subList(0, halfList);
				rightList = this.trianglesInBox.subList(halfList, triCount);
				
				
				double zNewLeft = largestInLst(leftList,'z');
				double zNewRight = smallestInLst(rightList,'z');
				pLeft = new Point(this.p1.x, this.p1.y, zNewLeft);
				pRight = new Point(this.p0.x, this.p0.y, zNewRight);
			}
				
			
			this.left = new AxisAlignedBox(this.p0, pLeft,
					this.transformation);
			this.right = new AxisAlignedBox(pRight, this.p1,
					this.transformation);

			left.trianglesInBox = leftList;
			right.trianglesInBox = rightList;
			
			//checkBox(this.left,this.right);
			//checkBox(this.right,this.left);

			depth = depth - 1;
			left.split(depth);
			right.split(depth);
		}
	}
	
	private void checkBox(AxisAlignedBox box, AxisAlignedBox box2){
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
	
	private double smallestInLst(List<Triangle> lst, Character axis){
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
	private double largestInLst(List<Triangle> lst, Character axis){
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
	
	
	public void splitNoSort(int depth) {
		this.depth = depth;
		int triCount = this.trianglesInBox.size();

		if ((depth > 0) && (triCount > 50)) {
			// split the aab in two along the longest Axis.
			double xLength = Math.abs(this.p0.x - this.p1.x);
			double yLength = Math.abs(this.p0.y - this.p1.y);
			double zLength = Math.abs(this.p0.z - this.p1.z);

			Point p0Split;
			Point p1Split;

			if ((xLength > yLength) && (xLength > zLength)) {
				// the box largest dimension is x
				double xNew = (this.p0.x + this.p1.x) / 2;
				p0Split = new Point(xNew, this.p0.y, this.p0.z);
				p1Split = new Point(xNew, this.p1.y, this.p1.z);

			} else if ((yLength > xLength) && (yLength > zLength)) {
				// the box largest dimension is y
				double yNew = (this.p0.y + this.p1.y) / 2;
				p0Split = new Point(this.p0.x, yNew, this.p0.z);
				p1Split = new Point(this.p1.x, yNew, this.p1.z);
			} else {
				// the largest dimension is z split along z.
				double zNew = (this.p0.z + this.p1.z) / 2;
				p0Split = new Point(this.p0.x, this.p0.y, zNew);
				p1Split = new Point(this.p1.x, this.p1.y, zNew);
			}

			this.left = new AxisAlignedBox(this.p0, p1Split,
					this.transformation);
			this.right = new AxisAlignedBox(p0Split, this.p1,
					this.transformation);

			for (Triangle triangle : trianglesInBox) {
				if (left.isInBox(triangle)) {
					left.trianglesInBox.add(triangle);
				}
				if (right.isInBox(triangle)) {
					right.trianglesInBox.add(triangle);
				}
			}
			depth = depth - 1;
			left.split(depth);
			right.split(depth);
		}

	}

	private boolean isInBox(Triangle triangle) {
		Point a = triangle.a;
		Point b = triangle.b;
		Point c = triangle.c;
		double eps = Constants.treeEpsilon;
		
		//return true;
		boolean isIn = false;

		if  (((a.x + eps > p0.x) && (a.y + eps > p0.y) && (a.z + eps > p0.z))
		 && ((a.x - eps < p1.x) && (a.y - eps < p1.y) && (a.z - eps < p1.z))) {
			// a is in
			isIn = true;
		} 
		if (((b.x + eps > p0.x) && (b.y + eps > p0.y) && (b.z + eps > p0.z))
		 && ((b.x - eps < p1.x) && (b.y - eps < p1.y) && (b.z - eps < p1.z))) {
			// b is in
			isIn = true;
		}
		if (((c.x + eps > p0.x) && (c.y + eps > p0.y) && (c.z + eps > p0.z))
		 && ((c.x - eps < p1.x) && (c.y - eps < p1.y) && (c.z - eps < p1.z))) {
			// c is in
			isIn = true;
		}
		return isIn;
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

	private Normal getNormal(int faceHit) {
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
		
		if ((this.left != null) && (this.right != null)) {
			if (this.left.intersectBool(ray)) {
				hits.addAll(left.intersect(ray));
			}
			if (this.right.intersectBool(ray)) {
				hits.addAll(right.intersect(ray));
			}
		} else {
			//maximum depth reached.
			if (this.intersectBool(ray)){
				for (Triangle tri :	this.trianglesInBox) {
					hits.addAll(tri.intersect(ray));
				}
			}
		}		
		return hits;
	}
	

}
