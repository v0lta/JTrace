package acceleration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera.Camera;
import shape.Triangle;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;

public class SortSplitBox extends AxisAlignedBox {
	public SortSplitBox left = null;
	public SortSplitBox right = null;
	
	
	public SortSplitBox(Point p0, Point p1, Transformation transformation, Camera cam) {
		super(p0, p1, transformation, cam);
		
	}
	
	public SortSplitBox getLeft(){
		return this.left;
	}
	public SortSplitBox getRight(){
		return this.right;
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
				
			
			this.left = new SortSplitBox(this.p0, pLeft,
					this.transformation, this.cam);
			this.right = new SortSplitBox(pRight, this.p1,
					this.transformation, this.cam);

			left.trianglesInBox = leftList;
			right.trianglesInBox = rightList;
			
			//checkBox(this.left,this.right);
			//checkBox(this.right,this.left);

			depth = depth - 1;
			left.split(depth);
			right.split(depth);
		}
	}
	
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();

		if (Constants.compVisualization){
			ray.countIntersection();
		}
		
		if ((this.getLeft() != null) && (this.getRight() != null)) {
			Point leftCenter = this.left.getCenter();
			leftCenter = this.transformation.transform(leftCenter);
			Point rightCenter = this.right.getCenter();
			rightCenter = this.transformation.transform(rightCenter);
			
			Vector camPos = super.cam.getOrigin().toVector();

			double leftDist = camPos.subtract(leftCenter.toVector()).length();
			double rightDist = camPos.subtract(rightCenter.toVector()).length();
			
			//check if the distance is significant.
			//if (false) {
			if ((Math.abs(leftDist - rightDist)) > 0.1) {			
				if (leftDist < rightDist){
					if (this.left.intersectBool(ray)) {
						hits.addAll(left.intersect(ray));
					}
					if (hits.isEmpty()) {
						if (this.right.intersectBool(ray)) {
							hits.addAll(right.intersect(ray));
						}
					} else {
						boolean checkBox = false;
						for(Intersection inter: hits ){
							if (right.pointInBox(inter.point)) {
								checkBox = true;
							}
						}
						if (checkBox) {
							if (this.right.intersectBool(ray)) {
								hits.addAll(right.intersect(ray));
							}
						}
					} 
				} else {
					if (this.right.intersectBool(ray)) {
						hits.addAll(right.intersect(ray));
					}
					if (hits.isEmpty()) {
						if (this.left.intersectBool(ray)) {
							hits.addAll(left.intersect(ray));
						}
					} else {
						boolean checkBox = false;
						for(Intersection inter: hits ){
							if (left.pointInBox(inter.point)) {
								checkBox = true;
							}
						}
						if (checkBox) {
							if (this.left.intersectBool(ray)) {
								hits.addAll(left.intersect(ray));
							}
						}
					} 
				}
			} else {
				if (this.right.intersectBool(ray)) {
					hits.addAll(right.intersect(ray));
				}
				if (this.left.intersectBool(ray)) {
					hits.addAll(left.intersect(ray));
				}
			} 
		} 

		if ((this.getLeft() != null) && (this.getRight() == null)) {
			if (this.left.intersectBool(ray)) {
				hits.addAll(left.intersect(ray));
			}
		}
		if ((this.getRight() != null) && (this.getLeft() == null)) {
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
		return hits;
	}
}


