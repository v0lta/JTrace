package acceleration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera.Camera;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.Transformation;
import shape.Triangle;

public class MiddleSplitBox extends AxisAlignedBox {
	public MiddleSplitBox left = null;
	public MiddleSplitBox right = null;
	public Camera cam = null;
	
	public MiddleSplitBox(Point p0, Point p1, Transformation transformation) {
		super(p0, p1, transformation);
		
	}
	
	public MiddleSplitBox getLeft(){
		return this.left;
	}
	public MiddleSplitBox getRight(){
		return this.right;
	}

	
	public void split(int depth) {
		this.depth = depth -1;
		int triCount = this.trianglesInBox.size();

		if ((depth > 0) && (triCount > 1)) {
		//if (triCount > 3) {
			List<Triangle> leftList = new ArrayList<Triangle>();
			List<Triangle> rightList = new ArrayList<Triangle>();

			// split the aab in two along the longest Axis.
			double xLength = Math.abs(this.p0.x - this.p1.x);
			double yLength = Math.abs(this.p0.y - this.p1.y);
			double zLength = Math.abs(this.p0.z - this.p1.z);

			Point pRight;
			Point pLeft;

			if ((xLength > yLength) && (xLength > zLength)) {
				// the box largest dimension is x
				double xNew = (this.p0.x + this.p1.x) / 2;
				pLeft = new Point(xNew, this.p1.y, this.p1.z);
				pRight = new Point(xNew, this.p0.y, this.p0.z);
				
				for (Triangle triangle : trianglesInBox) {
					if (isInBox(triangle,this.p0, pLeft)) {
						leftList.add(triangle);
					}
					if (isInBox(triangle, pRight, this.p1)) {
						rightList.add(triangle);
					}
				}
				if (leftList.isEmpty() == false){
					double maxL = largestInLst(leftList,'x');
					pLeft = new Point(maxL, this.p1.y, this.p1.z);
				}
				if (rightList.isEmpty() == false) {
					double minR = smallestInLst(rightList,'x');				
					pRight = new Point(minR, this.p0.y, this.p0.z);
				}
					
			} else if ((yLength > xLength) && (yLength > zLength)) {
				// the box largest dimension is y
				double yNew = (this.p0.y + this.p1.y) / 2;
				pLeft = new Point(this.p1.x, yNew, this.p1.z);
				pRight = new Point(this.p0.x, yNew, this.p0.z);
				
				
				for (Triangle triangle : trianglesInBox) {
					if (isInBox(triangle,this.p0, pLeft)) {
						leftList.add(triangle);
					}
					if (isInBox(triangle, pRight, this.p1)) {
						rightList.add(triangle);
					}
				}
				if (leftList.isEmpty() == false){
					double maxL = largestInLst(leftList,'y');
					pLeft = new Point(this.p1.x, maxL, this.p1.z);
				}
				if (rightList.isEmpty() == false) {
					double minR = smallestInLst(rightList,'y');
					pRight = new Point(this.p0.x, minR, this.p0.z);
				}
				
			} else {
				// the largest dimension is z split along z.
				double zNew = (this.p0.z + this.p1.z) / 2;
				pLeft = new Point(this.p1.x, this.p1.y, zNew);
				pRight = new Point(this.p0.x, this.p0.y, zNew);
				
				for (Triangle triangle : trianglesInBox) {
					if (isInBox(triangle,this.p0, pLeft)) {
						leftList.add(triangle);
					}
					if (isInBox(triangle, pRight, this.p1)) {
						rightList.add(triangle);
					}
				}
				if (leftList.isEmpty() == false){
					double maxL = largestInLst(leftList,'z');
					pLeft = new Point(this.p1.x, this.p1.y, maxL);
				}
				if (rightList.isEmpty() == false) {
					double minR = smallestInLst(rightList,'z');
					pRight = new Point(this.p0.x, this.p0.y, minR);
				}

			}
			this.left = new MiddleSplitBox(this.p0, pLeft,
					this.transformation);
			this.left.trianglesInBox = leftList;
			
			this.right = new MiddleSplitBox(pRight, this.p1,
					this.transformation);
			this.right.trianglesInBox = rightList;
			
			this.left.split(this.depth);
			this.right.split(this.depth);

			if (this.left.trianglesInBox.isEmpty()) {
				this.left = null;
			}
			if (this.right.trianglesInBox.isEmpty()) {
				this.right = null;
			}

		}
	}
	
	private boolean isInBox(Triangle triangle,Point p0,Point p1 ) {
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
	
	public void setCamera(Camera cam){
		this.cam = cam;
	}
	
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();

		if (Constants.compVisualization){
			ray.countIntersection();
		}

		if (cam != null) {
			
		} else {
			if (this.getLeft() != null) {
				if (this.left.intersectBool(ray)) {
					hits.addAll(left.intersect(ray));
				}
			}
			if (this.getRight() != null) {
				if (this.right.intersectBool(ray)) {
					hits.addAll(right.intersect(ray));
				}
			}
			if ((this.getLeft() == null) && (this.getRight() == null)) {
				//maximum depth reached.
				if (this.intersectBool(ray)){
					for (Triangle tri :	this.trianglesInBox) {
						hits.addAll(tri.intersect(ray));
					}
				}
			}		
		}
		return hits;
	}

}
