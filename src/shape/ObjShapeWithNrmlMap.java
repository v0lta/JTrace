package shape;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shape.ObjShape.Extremes;
import acceleration.ParallelSahBox;
import acceleration.SahBox;
import material.Material;
import material.ObjTextureFile;
import math.Constants;
import math.Normal;
import math.Point;
import math.TextPoint;
import math.Transformation;
import camera.Camera;

public class ObjShapeWithNrmlMap extends ObjShape {
	ObjTextureFile nMapFile;
	
	public ObjShapeWithNrmlMap(String path,String nMapPath, Transformation transformation,
			Material mat, double reflectivity, int treeDepth, Camera camera) {
		super(path, nMapPath, transformation, mat, reflectivity, treeDepth, camera);
		this.nMapFile = new ObjTextureFile(nMapPath, 1.0);
		
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
	 * Read in the object data from an ".obj" file.
	 * @throws IOException the path might not be correct.
	 */
	@Override
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
			}
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
				//Triangle triangle = new Triangle(a,b,c,an,bn,cn,at,bt,ct,this.mat,this.reflectivity,this.transformation);
				//TriangleWithNrmlMap triNrmlMap = new TriangleWithNrmlMap(this.nMapFile,triangle);
				TriangleWithNrmlMap triNrmlMap = new TriangleWithNrmlMap(this.nMapFile, a,b,c,an,bn,cn,at,bt,ct,
													this.mat,this.reflectivity,this.transformation);
				this.triangleList.add(triNrmlMap);
				
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

}
