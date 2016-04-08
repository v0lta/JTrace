package acceleration;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shape.Triangle;

public class MiddleSplitBox extends AxisAlignedBox {
	public MiddleSplitBox left = null;
	public MiddleSplitBox right = null;

	public MiddleSplitBox(Point p0, Point p1, Transformation transformation, Camera cam, double treeEps, double oEps) {
		super(p0, p1, transformation, cam, treeEps, oEps);

	}

	public MiddleSplitBox getLeft(){
		return this.left;
	}
	public MiddleSplitBox getRight(){
		return this.right;
	}


	public void split(int depth) {
		depth = depth -1;
		int triCount = this.trianglesInBox.size();

		if ((depth > 0) && (triCount > 1)) {
		//if (triCount > 1) {
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
						if (triInBox(triangle,this.p0, pLeft)) {
							leftList.add(triangle);
						}
						if (triInBox(triangle, pRight, this.p1)) {
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
						if (triInBox(triangle,this.p0, pLeft)) {
							leftList.add(triangle);
						}
						if (triInBox(triangle, pRight, this.p1)) {
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
						if (triInBox(triangle,this.p0, pLeft)) {
							leftList.add(triangle);
						}
						if (triInBox(triangle, pRight, this.p1)) {
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
						this.transformation, this.cam, this.treeEpsilon, this.objIntersEpsilon);
				this.left.trianglesInBox = leftList;

				this.right = new MiddleSplitBox(pRight, this.p1,
						this.transformation, this.cam, this.treeEpsilon, this.objIntersEpsilon);
				this.right.trianglesInBox = rightList;
				this.left.split(depth);
				this.right.split(depth);

				if (this.left.trianglesInBox.isEmpty()) {
					this.left = null;
				}
				if (this.right.trianglesInBox.isEmpty()) {
					this.right = null;
				}

		}
	}
	
	@Override
	public List<Intersection> intersect(Ray ray) {
			List<Intersection> hits = new ArrayList<Intersection>();

			if (Constants.compVisualization){
				ray.countIntersection();
			}
			
			if (this.left != null)  {
				if (this.left.intersectBool(ray)) {
					hits.addAll(left.intersect(ray));
				}
			}
			if (this.right != null) {
				if (this.right.intersectBool(ray)) {
					hits.addAll(right.intersect(ray));
				}
			}
			if ((this.left == null) && (this.right == null)) {
				//maximum depth reached.
				if (this.intersectBool(ray)){
					for (Triangle tri :	this.trianglesInBox) {
						hits.addAll(tri.intersect(ray));
					}
				}
			}		
			return hits;
		}

	//@Override
	public List<Intersection> intersectNew(Ray ray) {
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


