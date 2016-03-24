package acceleration;

import java.util.ArrayList;
import java.util.List;

import shape.Triangle;
import math.Point;
import math.Transformation;
import camera.Camera;

public class SahBox extends AxisAlignedBox  {
	public SahBox left = null;
	public SahBox right = null;

	public SahBox(Point p0, Point p1, Transformation transformation, Camera cam) {
		super(p0, p1, transformation, cam);
		// TODO Auto-generated constructor stub
	}
	
	
	public void split(int depth) {
		this.depth = depth -1;
		int triCount = this.trianglesInBox.size();

		if ((depth > 0) && (triCount > 1)) {
		//if (triCount > 1) {
				List<AxisAlignedBox> boxes = new ArrayList<AxisAlignedBox>();
				List<Triangle> leftList = new ArrayList<Triangle>();
				List<Triangle> rightList = new ArrayList<Triangle>();
				final int cuts = 4;
				Character axis;
				
				// split the aab in two along the longest Axis.
				double xLength = Math.abs(this.p0.x - this.p1.x);
				double yLength = Math.abs(this.p0.y - this.p1.y);
				double zLength = Math.abs(this.p0.z - this.p1.z);

				Point pRight;
				Point pLeft;

				if ((xLength > yLength) && (xLength > zLength)) {
					// the box' largest dimension is x
					axis = 'x';
					double xStep = (this.p0.x + this.p1.x) / cuts;

					for (int i = 0; i < cuts; i++) {
						System.out.println(i);
						double xSrt = p0.x + i*xStep;
						double xEnd = xSrt + xStep;
						
						Point pSt = new Point(xSrt, this.p0.y, this.p0.z);
						Point pEd = new Point(xEnd, this.p1.y, this.p1.z);
						AxisAlignedBox xBox= new AxisAlignedBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							xBox.addIfIn(triangle);
						}
						xBox = xBox.adjustBounds(axis);
						boxes.add(xBox);					
					}
				} else if ((yLength > xLength) && (yLength > zLength)) {
					// the box largest dimension is y
					axis = 'y';
					double yStep = (this.p0.y + this.p1.y) / cuts;

					for (int i = 0; i < cuts; i++) {
						System.out.println(i);
						double ySrt = p0.y + i*yStep;
						double yEnd = ySrt + yStep;
						
						Point pSt = new Point(this.p0.x, ySrt, this.p0.z);
						Point pEd = new Point(this.p1.y, yEnd, this.p1.z);
						AxisAlignedBox yBox= new SahBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							yBox.addIfIn(triangle);
						}
						yBox = yBox.adjustBounds(axis);
						boxes.add(yBox);					
					}
				} else {
					// the largest dimension is z split along z.
					axis = 'z';
					double zStep = (this.p0.z + this.p1.z) / cuts;

					for (int i = 0; i < cuts; i++) {
						double zSrt = p0.y + i*zStep;
						double zEnd = zSrt + zStep;
						
						Point pSt = new Point(this.p0.x, this.p0.y, zSrt);
						Point pEd = new Point(this.p1.x, this.p1.y, zEnd);
						AxisAlignedBox zBox= new AxisAlignedBox(pSt,pEd,this.transformation,this.cam); 

						for (Triangle triangle : trianglesInBox) {
							zBox.addIfIn(triangle);
						}
						zBox = zBox.adjustBounds(axis);
						boxes.add(zBox);					
					}
				}
				
				//Explore various box combinations.
				AxisAlignedBox lftBoxBest = null;
				AxisAlignedBox rgtBoxBest = null;
				double nLft = 0;
				double vLft = 0;
				double nRgt = 0;
				double vRgt = 0;
				double cost = this.getVolume() * this.trianglesInBox.size();

				for (int i = 0; i < cuts; i++){
					//create large boxes. Dimensions will change according to assigned triangles.
					AxisAlignedBox lftBox = new SahBox(this.p0,this.p1,this.transformation,this.cam);
					AxisAlignedBox rgtBox = new SahBox(this.p0,this.p1,this.transformation,this.cam);
					for(AxisAlignedBox lftPartBox : boxes.subList(0, i)) {
						lftBox.trianglesInBox.addAll(lftPartBox.trianglesInBox);
					}
					lftBox = lftBox.adjustBounds(axis);
					for(AxisAlignedBox rgtPartBox : boxes.subList(i, cuts)) {
						rgtBox.trianglesInBox.addAll(rgtPartBox.trianglesInBox);
					}
					rgtBox = rgtBox.adjustBounds(axis);
					
					nLft = lftBox.trianglesInBox.size();
					vLft = lftBox.getVolume();
					nRgt = rgtBox.trianglesInBox.size();
					vRgt = rgtBox.getVolume();
					double crntCost = nLft * vLft + nRgt * vRgt;
					if (crntCost < cost) {
						cost = crntCost;
						lftBoxBest = lftBox;
						rgtBoxBest = rgtBox;
					}
				}

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

	
	
	
	
	

}
