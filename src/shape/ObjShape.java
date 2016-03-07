package shape;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import acceleration.AxisAlignedBox;
import math.Color;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.Transformation;

public class ObjShape implements Shape {
	public final String path;
	public final Transformation transformation;
	public final double reflectivity;
	public final Color color;
	public final List<Triangle> triangleList;
	private AxisAlignedBox aab;
	
	public ObjShape(String path, Transformation transformation,Color color, double reflectivity){
		this.path = path;
		this.transformation = transformation;
		this.reflectivity = reflectivity;
		this.color = color;
		this.triangleList = new ArrayList<Triangle>();
		
		try {
			this.read();
		} catch (IOException e) {
			System.out.println("File not found: " + e.getMessage());
		}
	}
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		
		//go trough the triangles and find intersections.
		List<Intersection> intersections = new ArrayList<Intersection>();
		intersections.clear();
		
		//intersections.addAll(this.aab.intersect(ray));
		
		//if (false) {
		if (aab.intersectBool(ray)) {
			for (Triangle triangle : this.triangleList) {
				List<Intersection> currentInter;
				currentInter = triangle.intersect(ray);
				if (currentInter.isEmpty() == false) {
					intersections.addAll(currentInter);
				}
			}
		}
		return intersections;
	}

	/**
	 * Read in the object data from an ".obj" file.
	 * @throws IOException the path might not be correct.
	 */
	private void read() throws IOException {
			
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
				List<Double> normals = normList.get(asmblyList.get(0).get(2) - 1);
				x = normals.get(0);
				y = normals.get(1);
				z = normals.get(2);
				Normal an = new Normal(x,y,z);
				
				//b
				vertex = normList.get(asmblyList.get(1).get(2) - 1);
				x = normals.get(0);
				y = normals.get(1);
				z = normals.get(2);
				Normal bn = new Normal(x,y,z);
				
				//c
				vertex = normList.get(asmblyList.get(2).get(2) - 1);
				x = normals.get(0);
				y = normals.get(1);
				z = normals.get(2);
				Normal cn = new Normal(x,y,z);
				
				//(Point a, Point b, Point c,
				//Normal an, Normal bn, Normal cn, Color color,
				//double reflectivity, Transformation transformation)
				Triangle triangle = new Triangle(a,b,c,an,bn,cn,this.color,this.reflectivity,this.transformation);
				this.triangleList.add(triangle);
				
			}
			this.aab = new AxisAlignedBox(new Point(minmax.xMin,minmax.yMin,minmax.zMin),
										  new Point(minmax.xMax,minmax.yMax,minmax.zMax),
										  this.transformation);
			
	}
	
	private class Extremes{
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
		Transformation testTrans = Transformation.createIdentity();
			@SuppressWarnings("unused")
			ObjShape testObj = new ObjShape("./obj/plane.obj",testTrans,new Color(10,10,10),1.0);
	}
	 

}
