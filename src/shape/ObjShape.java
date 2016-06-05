package shape;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import acceleration.AxisAlignedBox;
import acceleration.ParallelSahBox;
import acceleration.SahBox;
import material.Material;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;

/**
 * A class which loads and makes simple wavefront files (.obj)
 * available to the ray tracer.
 * @author moritz
 *
 */

public class ObjShape implements Shape {
	public final String path;
	public final Transformation transformation;
	public final Material mat;
	public final List<Triangle> triangleList;
	protected AxisAlignedBox aab;
	//private int accessCount = 0;
	protected int treeDepth;
	protected Camera cam;
	
	public final double treeEpsilon; //triangle inclusion noise prevention. dragon  0.00001;
	public final double objIntersEpsilon; //0.1
	
	/**
	 * Create a triangle mesh from a wavefront (.obj) file.
	 * @param path string with path to the .obj file.
	 * @param transformation collection of matrices changing
	 * 		   position and scale.
	 * @param mat a material object.
	 * @param reflectivity changes the brightness.
	 * @param treeDepth the depth of the acceleration tree.
	 * @param camera object needed for camera position.
	 */	
	public ObjShape(String path, Transformation transformation, Material mat,
					int treeDepth, Camera camera,
					double treeEps, double objInterEps){
		this.path = path;
		this.transformation = transformation;
		this.mat = mat;
		this.triangleList = new ArrayList<Triangle>();
		this.treeDepth = treeDepth;
		this.cam = camera;
		this.treeEpsilon = treeEps;
		this.objIntersEpsilon = objInterEps;
		
		long t = System.nanoTime();
		try {
			this.read();
		} catch (IOException e) {
			System.out.println("File not found: " + e.getMessage());
		}
		long elTimens = System.nanoTime() - t;
		//conversion from ns to s.
		double elTime = elTimens * Math.pow(10,-9);
		System.out.println("Tree creation took [s]:");
		System.out.println(elTime);
	}
	
	/**
	 * Second constructor for use in children where read() is not executed,
	 */
	protected ObjShape(String path, String nMapPath, Transformation transformation, Material mat,
			int treeDepth, Camera camera, double treeEps, double objInterEps){
		this.path = path;
		this.transformation = transformation;
		this.mat = mat;
		this.triangleList = new ArrayList<Triangle>();
		this.treeDepth = treeDepth;
		this.cam = camera;
		this.treeEpsilon = treeEps;
		this.objIntersEpsilon = objInterEps;
	}
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		
		//go trough the triangles and find intersections.
		List<Intersection> intersections = new ArrayList<Intersection>();
		//intersections.clear();
		
		if (Constants.useAccTree) {
			intersections.addAll(this.aab.intersect(ray));
		} else {
			if (aab.intersectBool(ray)) {
				for (Triangle triangle : this.triangleList) {
					List<Intersection> currentInter;
					currentInter = triangle.intersect(ray);
					if (currentInter.isEmpty() == false) {
						intersections.addAll(currentInter);
					}
				}
			}
		}	
		return intersections;
	}

	/**
	 * Read in the object data from an ".obj" file.
	 * @throws IOException the path might not be correct.
	 */
	protected void read() throws IOException {
			long t = System.nanoTime();
			
			BufferedReader br = new BufferedReader( new FileReader(this.path));
			

			String currentLine;
			
			List<List<Double>> vertList = new ArrayList<List<Double>>();
			List<List<Double>> txtvList = new ArrayList<List<Double>>();
			List<List<Double>> normList = new ArrayList<List<Double>>();
			List<List<List<Integer>>> fList = new ArrayList<List<List<Integer>>>();

			while ((currentLine = br.readLine()) != null) {
				//Find the vertices.
				if (currentLine.substring(0, 2).equals("v ")) {
					List<Double> vertices = new ArrayList<Double>();
					for (String strDouble :(currentLine.substring(2, currentLine.length()).split(" "))) {
						if (strDouble.isEmpty() == false){
							vertices.add(Double.parseDouble(strDouble));
						}
					}
					vertList.add(vertices);
				}
				//Load the texture coordinates.
				if (currentLine.substring(0, 2).equals("vt")) {
					List<Double> txtvert = new ArrayList<Double>();
					for (String strDouble :(currentLine.substring(2, currentLine.length()).split(" "))) {
						if (strDouble.isEmpty() == false){
							txtvert.add(Double.parseDouble(strDouble));
						}
					}
					txtvList.add(txtvert);
				}
				//Load the normals.
				if (currentLine.substring(0, 2).equals("vn")) {
					List<Double> normals = new ArrayList<Double>();
					for (String strDouble :(currentLine.substring(2, currentLine.length()).split(" "))) {
						if (strDouble.isEmpty() == false){
							normals.add(Double.parseDouble(strDouble));
						}
					}
					normList.add(normals);
				}
				//Get the triangle assembly information.
				if (currentLine.substring(0, 2).equals("f ")) {
					List<List<Integer>> fsOuter = new ArrayList<List<Integer>>();
					for (String strInt :(currentLine.substring(2, currentLine.length()).split(" "))) {
						
						if (strInt.isEmpty() == false){
							List<Integer> fsInner = new ArrayList<Integer>();
							for (String strInt2 :strInt.split("/")) {
								fsInner.add(Integer.parseInt(strInt2));								
							}
							fsOuter.add(fsInner);						
						}
					}
					fList.add(fsOuter);
				}
				//For debug print the current line.
				//System.out.println(currentLine);	
			}
			
			//For debug print what was read in.
			//System.out.println(vertList);
			//System.out.println(txtvList);
			//System.out.println(normList);
			//System.out.println(fList);
			
			//close the buffered reader.
			br.close();		
			
			//create the triangles.
			Extremes minmax = new Extremes();
			for (List<List<Integer>> asmblyList : fList) {
				//create the point objects
				List<Double> vertex = vertList.get(asmblyList.get(0).get(0) - 1);
				Double x = vertex.get(0);
				Double y = vertex.get(1);
				Double z = vertex.get(2);
				Point a = new Point(x,y,z);
				minmax.checkVals(x, y, z);
				//System.out.println(asmblyList.get(1).get(0));
				
				//b
				vertex = vertList.get(asmblyList.get(1).get(0) - 1);
				x = vertex.get(0);
				y = vertex.get(1);
				z = vertex.get(2);
				Point b = new Point(x,y,z);
				minmax.checkVals(x, y, z);
				
				//c
				vertex = vertList.get(asmblyList.get(2).get(0) - 1);
				x = vertex.get(0);
				y = vertex.get(1);
				z = vertex.get(2);
				Point c = new Point(x,y,z);
				minmax.checkVals(x, y, z);
				
				//create the normal objects.
				List<Double> normal = normList.get(asmblyList.get(0).get(2) - 1);
				x = normal.get(0);
				y = normal.get(1);
				z = normal.get(2);
				Normal an = new Normal(x,y,z);
				
				if (Math.sqrt(x*x + y*y + z*z) < 0.1) {
					System.err.println("an very small");
				}
				
				//b
				normal = normList.get(asmblyList.get(1).get(2) - 1);
				x = normal.get(0);
				y = normal.get(1);
				z = normal.get(2);
				Normal bn = new Normal(x,y,z);
				
				if (Math.sqrt(x*x + y*y + z*z) < 0.1) {
					System.err.println("bn very small");
				}
				
				//c
				normal = normList.get(asmblyList.get(2).get(2) - 1);
				x = normal.get(0);
				y = normal.get(1);
				z = normal.get(2);
				Normal cn = new Normal(x,y,z);
				
				if (Math.sqrt(x*x + y*y + z*z) < 0.1) {
					System.err.println("cn veeery small");
				}
				
				//find the texture coordinates.
				//create the normal objects.
				//System.out.println(asmblyList);
				List<Double> textVerts = txtvList.get(asmblyList.get(0).get(1) - 1);
				double u = textVerts.get(0);
				double v = textVerts.get(1);
				TextPoint at = new TextPoint(u,v);
				
				
				textVerts = txtvList.get(asmblyList.get(1).get(1) - 1);
				u = textVerts.get(0);
				v = textVerts.get(1);
				TextPoint bt = new TextPoint(u,v);
				
				textVerts = txtvList.get(asmblyList.get(2).get(1) - 1);
				u = textVerts.get(0);
				v = textVerts.get(1);
				TextPoint ct = new TextPoint(u,v);
				
				
				//(Point a, Point b, Point c,
				//Normal an, Normal bn, Normal cn, Color color,
				//double reflectivity, Transformation transformation)
				Triangle triangle = new Triangle(a,b,c,an,bn,cn,at,bt,ct,this.mat,this.transformation);
				this.triangleList.add(triangle);
				
			}
			long elTimens = System.nanoTime() - t;
			//conversion from ns to s.
			double elTime = elTimens * Math.pow(10,-9);
			System.out.println("File reading took [s]:");
			System.out.println(elTime);
			
			long t2 = System.nanoTime();

			this.createTree(minmax);
			
			long elTimens2 = System.nanoTime() - t2;
			//conversion from ns to s.
			elTime = elTimens2 * Math.pow(10,-9);
			System.out.println("splitting took [s]:");
			System.out.println(elTime);
	}
	
	
	/**
	 * Creates the acceleration tree for the wavefront object.
	 * @param minmax contains the extreme points of the considered mesh.
	 */	
	protected void createTree(Extremes minmax){
		boolean singleCore = true;
		if (singleCore){
		//this.aab = new AxisAlignedBox(new Point(minmax.xMin - Constants.treeEpsilon,
		//this.aab = new SortSplitBox(new Point(minmax.xMin - this.treeEpsilon,
		//this.aab = new MiddleSplitBox(new Point(minmax.xMin - this.treeEpsilon,
		this.aab = new SahBox(new Point(minmax.xMin - this.treeEpsilon,
												minmax.yMin - this.treeEpsilon,
												minmax.zMin - this.treeEpsilon),
									  new Point(minmax.xMax + this.treeEpsilon,
											    minmax.yMax + this.treeEpsilon,
											    minmax.zMax + this.treeEpsilon),
									  this.transformation, this.cam, treeEpsilon, objIntersEpsilon);
		} else {
		this.aab = new ParallelSahBox(new Point(minmax.xMin - this.treeEpsilon,
				minmax.yMin - this.treeEpsilon,
				minmax.zMin - this.treeEpsilon),
	    new Point(minmax.xMax + this.treeEpsilon,
			    minmax.yMax + this.treeEpsilon,
			    minmax.zMax + this.treeEpsilon),
	    this.transformation, this.cam, this.treeDepth, this.treeDepth, null, treeEpsilon, objIntersEpsilon);
		}
		
		aab.trianglesInBox.addAll(triangleList);
		aab.split(this.treeDepth); //recursively split the box until the max depth is reached.
	}

	/**
	 * A class which stores extreme points of a given mesh.
	 * @author moritz
	 *
	 */
	
	protected class Extremes{
		public double xMax = 0;public  double xMin = 0;
		public double yMax = 0;public  double yMin = 0;
		public double zMax = 0;public  double zMin = 0;
		
		public Extremes(){};
		
		public void checkVals(double x,double y,double z){
			if (xMin > x){
				this.xMin = x;
			}
			if (xMax < x){
				this.xMax = x;
			}
			if (yMin > y){
				this.yMin = y;
			}
			if (yMax < y){
				this.yMax = y;
			}
			if (zMin > z){
				this.zMin = z;
			}
			if (zMax < z){
				this.zMax = z;
			}
		}
	}
	
	/*
	 *A main for debugging purposes.
	 */
	public static void main(String[] arguments){
		Camera cam = null;
		Transformation testTrans = Transformation.createIdentity();
		Material mat = null;
		@SuppressWarnings("unused")
		ObjShape testObj = new ObjShape("./obj/bunny.obj",testTrans,mat, 1, cam, 0.01, 0.01);
		System.out.println("done.");
	}
	 

}
