package acceleration;

import java.util.ArrayList;
import java.util.List;

import shape.Triangle;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import camera.Camera;

public class SahBox extends AxisAlignedBox  {
	public SahBox left = null;
	public SahBox right = null;

	public SahBox(Point p0, Point p1, Transformation transformation, Camera cam) {
		super(p0, p1, transformation, cam);
		// TODO Auto-generated constructor stub
	}
	
	
	public void split(int depth) {
		int triCount = this.trianglesInBox.size();

		if ((depth > 0) && (triCount > 1)) {
		//if (triCount > 1) {
				List<AxisAlignedBox> boxes = new ArrayList<AxisAlignedBox>();
				final int cuts = Constants.sahCuts;
				Character axis;
				
				// split the aab in two along the longest Axis.
				double xLength = Math.abs(this.p1.x - this.p0.x);
				double yLength = Math.abs(this.p1.y - this.p0.y);
				double zLength = Math.abs(this.p1.z - this.p0.z);

				if ((xLength > yLength) && (xLength > zLength)) {
					// the box' largest dimension is x
					axis = 'x';
					double xStep = xLength / (cuts+1);

					for (int i = 0; i < (cuts+1); i++) {
						double xSrt = p0.x + i*xStep;
						double xEnd = xSrt + xStep;
						
						Point pSt = new Point(xSrt, this.p0.y, this.p0.z);
						Point pEd = new Point(xEnd, this.p1.y, this.p1.z);
						SahBox xBox = new SahBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							xBox.addIfIn(triangle);
						}
						xBox = xBox.adjustBounds(axis);
						boxes.add(xBox);					
					}
				} else if ((yLength > xLength) && (yLength > zLength)) {
					// the box largest dimension is y
					axis = 'y';
					double yStep = yLength / (cuts + 1);


					for (int i = 0; i < (cuts + 1); i++) {
						double ySrt = p0.y + i*yStep;
						double yEnd = ySrt + yStep;
						
						Point pSt = new Point(this.p0.x, ySrt, this.p0.z);
						Point pEd = new Point(this.p1.x, yEnd, this.p1.z);
						
						SahBox yBox = new SahBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							yBox.addIfIn(triangle);
						}
						yBox = yBox.adjustBounds(axis);
						boxes.add(yBox);					
					}
				} else {
					// the largest dimension is z split along z.
					axis = 'z';
					double zStep = zLength / (cuts + 1);

					for (int i = 0; i < (cuts + 1); i++) {
						double zSrt = p0.z + i*zStep;
						double zEnd = zSrt + zStep;
						
						Point pSt = new Point(this.p0.x, this.p0.y, zSrt);
						Point pEd = new Point(this.p1.x, this.p1.y, zEnd);
						SahBox zBox= new SahBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							zBox.addIfIn(triangle);
						}
						zBox = zBox.adjustBounds(axis);
						boxes.add(zBox);					
					}
				}
				
				//Explore various box combinations.
				SahBox lftBoxBest = null;
				SahBox rgtBoxBest = null;
				double vTop = this.getVolume();
				//double sTop = this.getSurface(axis);
				double nTop = this.trianglesInBox.size();
				double cost = vTop * nTop;
				//double cost = sTop * nTop;
				//double cost = Double.POSITIVE_INFINITY;
				
				for (int i = 1; i < (cuts+1); i++){
					//create large boxes. Dimensions will change according to assigned triangles.
					SahBox lftBox = new SahBox(this.p0,this.p1,this.transformation,this.cam);
					SahBox rgtBox = new SahBox(this.p0,this.p1,this.transformation,this.cam);
					for(AxisAlignedBox lftPartBox : boxes.subList(0, i)) {
						lftBox.trianglesInBox.addAll(lftPartBox.trianglesInBox);
					}
					lftBox = lftBox.adjustBounds(axis);
					for(AxisAlignedBox rgtPartBox : boxes.subList(i, (cuts+1))) {
						rgtBox.trianglesInBox.addAll(rgtPartBox.trianglesInBox);
					}
					rgtBox = rgtBox.adjustBounds(axis);
					
					double nLft = lftBox.trianglesInBox.size();
					double vLft = lftBox.getVolume();
					//double sLft = lftBox.getSurface(axis);
					double nRgt = rgtBox.trianglesInBox.size();
					double vRgt = rgtBox.getVolume();
					//double sRgt = rgtBox.getSurface(axis);
					double crntCost = nLft * vLft + nRgt * vRgt;
					//double crntCost = nLft * sLft + nRgt * sRgt;
					if (crntCost < cost) {
						cost = crntCost;
						lftBoxBest = lftBox;
						rgtBoxBest = rgtBox;
					}
				}
				
				this.left  = lftBoxBest;
				this.right = rgtBoxBest;
				
				depth = depth -1;
				if (this.left != null) {
					if (this.left.trianglesInBox.isEmpty()) {
						this.left = null;
					} else {
						this.left.split(depth);
					}
				}
				if (this.right != null) {
					if (this.right.trianglesInBox.isEmpty()) {
						this.right = null;
					} else {
						this.right.split(depth);
					}
				}
		}
	}
	
	public SahBox adjustBounds(Character axis){
		if (this.trianglesInBox.isEmpty() == false) {
			Point adjP0;
			Point adjP1;
			
			if (axis == 'x'){
				double minX = this.smallestInLst(this.trianglesInBox, axis);
				double maxX = this.largestInLst(this.trianglesInBox, axis);

				adjP0 = new Point(minX, this.p0.y, this.p0.z);
				adjP1 = new Point(maxX, this.p1.y, this.p1.z);
			} else if (axis == 'y') {
				double minY = this.smallestInLst(this.trianglesInBox, axis);
				double maxY = this.largestInLst(this.trianglesInBox, axis);

				adjP0 = new Point(this.p0.x, minY, this.p0.z);
				adjP1 = new Point(this.p1.x, maxY, this.p1.z);
			} else {
				double minZ = this.smallestInLst(this.trianglesInBox, axis);
				double maxZ = this.largestInLst(this.trianglesInBox, axis);

				adjP0 = new Point(this.p0.x, this.p0.y, minZ);
				adjP1 = new Point(this.p1.x, this.p1.y, maxZ);
			}
			SahBox fixedBox = new SahBox(adjP0,adjP1,this.transformation,this.cam);
			fixedBox.trianglesInBox.addAll(this.trianglesInBox);
			return fixedBox;
		} else {
			return this;
		}
	}

	//@Override
	public List<Intersection> intersectOld(Ray ray) {
	//public List<Intersection> intersect(Ray ray) {
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

	@Override
	//public List<Intersection> intersectNew(Ray ray) {
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();

		if (Constants.compVisualization){
			ray.countIntersection();
		}
		
		if ((this.left != null) && (this.right != null)) {
			Point leftCenter = this.left.getCenter();
			leftCenter = this.transformation.transform(leftCenter);
			Point rightCenter = this.right.getCenter();
			rightCenter = this.transformation.transform(rightCenter);

			Vector camPos = super.cam.getOrigin().toVector();

			double leftDist = camPos.subtract(leftCenter.toVector()).lengthSquared();
			double rightDist = camPos.subtract(rightCenter.toVector()).lengthSquared();
			
			if (Math.abs(leftDist - rightDist) > Constants.objIntersEpsilon) {
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
					//if (true) {
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
		}
			
		}

		if ((this.left != null) && (this.right == null)) {
			if (this.left.intersectBool(ray)) {
				hits.addAll(left.intersect(ray));
			}
		}
		if ((this.right != null) && (this.left == null)) {
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

}
