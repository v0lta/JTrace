package acceleration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import math.Constants;
import math.Point;
import math.Transformation;
import camera.Camera;

public class ParallelSahBox extends SahBox {
	public int maxDepth;
	public final int cores; 
	final ExecutorService service;

	/**
	 * Create an axis aligned box with an acceleration structure generated
	 * using the surface area heuristic. After the first split further splitting
	 * is done on other cores.
	 * @param p0 bottom position of the axis aligned box.
	 * @param p1 top position of the axis aligned box
	 * @param transformation matrix of the top level box.
	 * @param cam the camera.
	 * @param treeEpsilon tree generation epsilon.
	 * @param objIntersEpsilon object intersection epsilon.
	 */	
	public ParallelSahBox(Point p0, Point p1, Transformation transformation,
			Camera cam, int maxDepth, int depth, ExecutorService service, double treeEpsilon, double objIntersEpsilon) {
		super(p0, p1, transformation, cam, treeEpsilon, objIntersEpsilon);
		this.maxDepth = maxDepth;
		this.cores = Runtime.getRuntime().availableProcessors();

		if (depth == maxDepth) {
			this.service = Executors.newFixedThreadPool(Runtime
					.getRuntime().availableProcessors());
		} else {
			this.service = service;
		}
	}

	@Override
	public void split(final int depth) {
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
					SahBox xBox = new ParallelSahBox(pSt,pEd,this.transformation,
							this.cam, this.maxDepth, depth -1,
							this.service, treeEpsilon, objIntersEpsilon); 

					xBox.checkAndAdd(depth,trianglesInBox);
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

					SahBox yBox = new ParallelSahBox(pSt,pEd,this.transformation,
							this.cam, this.maxDepth, depth -1,
							this.service, treeEpsilon, objIntersEpsilon);  

					yBox.checkAndAdd(depth,trianglesInBox);
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
					SahBox zBox= new ParallelSahBox(pSt,pEd,this.transformation,
							this.cam, this.maxDepth, depth -1,
							this.service, treeEpsilon, objIntersEpsilon);  

					zBox.checkAndAdd(depth,trianglesInBox);
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
				SahBox lftBox = new ParallelSahBox(this.p0,this.p1,this.transformation,this.cam,
						this.maxDepth, depth - 1,	this.service, treeEpsilon, objIntersEpsilon);
				SahBox rgtBox = new ParallelSahBox(this.p0,this.p1,this.transformation,this.cam,
						this.maxDepth, depth - 1,	this.service, treeEpsilon, objIntersEpsilon);
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

			if (this.right != null) {
				if (this.right.trianglesInBox.isEmpty()) {
					this.right = null;
				} else {
					double check = Math.pow(2, (this.maxDepth - depth));
					if (check > this.cores ){
						//no need to create a new thread do it here.
						this.right.split(depth - 1);
					} else {
						//out-source the work to another core.
						Thread thread = new Thread() {
							/*
							 * (non-Javadoc)
							 * 
							 * @see java.lang.Thread#run()
							 */
							@Override
							public void run() {
								right.split(depth - 1);									
							}
						};
						service.submit(thread);
					}
				}

				if (this.left != null) {
					if (this.left.trianglesInBox.isEmpty()) {
						this.left = null;
					} else {
						this.left.split(depth - 1);
					}
				}

			}

			if (this.maxDepth == depth){
				service.shutdown();
				// wait until the threads have finished
				try {
					service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

/*	@Override
	public void checkAndAdd(int depth, List<Triangle> triList){
		final ExecutorService triLoopService = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		double check = Math.pow(2, (this.maxDepth - depth));
		//if (false) {
		if (check < this.cores ){
			int available = this.cores/(this.maxDepth - depth + 1);
			int tris = triList.size();
			int step = tris/available;
			for (int i = 0; i < available; i++){
				int fromIndex = i*step + 1;
				int toIndex   = (i+1)*step - 1;
				final List<Triangle> subList = triList.subList(fromIndex, toIndex);
				//out-source the work to another core.
				Thread thread = new Thread() {
					@Override
					public void run() {
						for (Triangle triangle : subList) {
							addIfIn(triangle);
						}
					}
				};
				triLoopService.submit(thread);				
			}
			triLoopService.shutdown();
		} else {
			for (Triangle triangle : triList) {
				this.addIfIn(triangle);
			}
		}
	}
*/
}
