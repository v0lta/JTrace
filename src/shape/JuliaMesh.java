package shape;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import acceleration.SahBox;
import material.Complex;
import material.Julia;
import material.Material;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;

public class JuliaMesh extends Julia implements Shape {
	public final List<Triangle> mesh;
	public final SahBox aab;
	
	public JuliaMesh (Complex c, int N, double bound, int lim, double colorScale, String colorSet, double ref,
			          Transformation trans, Camera cam, double treeEps, double objIntersEps){
		super(c, N, bound, lim, colorScale, colorSet);
		this.mesh = generateMesh(ref,trans);
		this.aab = new SahBox(new Point(-1.1,-1,0),
							  new Point(1,1,1), trans , cam, treeEps, objIntersEps);
		aab.trianglesInBox.addAll(mesh);
		aab.split(20);
	}
	
	@Override
	public Color getColor(TextPoint point){
		//map input into the space where the julia set has been computed.
		int x = (int)Math.round(point.u);
		int y = (int)Math.round(point.v);
		return this.colorMap.getColor(x, y);
	}
	
	public List<Triangle> generateMesh(double ref, Transformation trans){
		List<Triangle> mesh = new ArrayList<Triangle>();

		//unit mesh has length,width,and height one.
		List<Complex> gridX = new ArrayList<Complex>();
		List<Complex> gridY = new ArrayList<Complex>();
		gridX = realSpace(-bound, bound, N);
		gridY = realSpace(-bound, bound, N);

		for (int i = 0; i <(N-2); i ++){
			for (int j = 0; j < (N-2); j ++){
				Point p00 = new Point(gridX.get(i).re,gridY.get(j).re,1/set[i][j]);
				TextPoint tp00 = new TextPoint(i,j);
				Point p10 = new Point(gridX.get(i+1).re,gridY.get(j).re,1/set[i+1][j]);
				TextPoint tp10 = new TextPoint(i+1,j);
				Point p01 = new Point(gridX.get(i).re,gridY.get(j+1).re,1/set[i][j+1]);
				TextPoint tp01 = new TextPoint(i,j+1);
				Point p11 = new Point(gridX.get(i+1).re,gridY.get(j+1).re,1/set[i+1][j+1]);
				TextPoint tp11 = new TextPoint(i+1,j+1);


				//Point a, Point b, Point c,
				//Normal an, Normal bn, Normal cn,
				//TextPoint at, TextPoint bt, TextPoint ct, Material mat,
				//double reflectivity, Transformation transformation
				//Material mat = new Monochrome(new NoSpec(), new Color(10,0,0));
				Material mat = this;
				Triangle t1 = new Triangle(p00, p10, p11,
						null, null, null,
						tp00, tp10, tp11,
						mat, trans);
				Triangle t2 = new Triangle(p00, p11, p01,
						null, null, null,
						tp00, tp11, tp01,
						mat, trans);
				mesh.add(t1);
				mesh.add(t2);
			}
		}
		return mesh;				
	}

	
	

	@Override
	public List<Intersection> intersect(Ray ray) {
		if (Constants.compVisualization){
			ray.countIntersection();
		}
		
		List<Intersection> hits = new ArrayList<Intersection>();
		if (Constants.useAccTree) {
			hits.addAll(this.aab.intersect(ray));
		} else {
			if (aab.intersectBool(ray)) {
				for (Triangle triangle : this.mesh) {
					List<Intersection> currentInter;
					currentInter = triangle.intersect(ray);
					if (currentInter.isEmpty() == false) {
						hits.addAll(currentInter);
					}
				}
			}
		}	
		return hits;
	}

}
