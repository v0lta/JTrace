package main;

import java.util.ArrayList;
import java.util.List;

import light.PointLight;
import material.Chess;
import material.Complex;
import material.Julia;
import material.Material;
import material.Monochrome;
import material.TextureFile;
import math.Color;
import math.Point;
import math.Transformation;
import math.Vector;
import camera.Camera;
import camera.PerspectiveCamera;
import shape.ObjShape;
import shape.Plane;
import shape.Shape;
import shape.Sphere;

/**
 * Sets up the scene to be rendered.
 */

public class World{
	public Camera camera;
	public List<PointLight> plights = new ArrayList<PointLight>();
	public List<Shape> shapes = new ArrayList<Shape>();
	public double ambient;
	
	public World(int width,int height,String choice) throws IllegalArgumentException {
		if (choice == "initialWorld")
			initialWorld(width,height);
		else if (choice == "planeAndSphere") {
			planeAndSphere(width,height);
		}
		else if (choice == "Julia") {
			julia(width,height);
		}
		else if (choice == "bunny") {
			bunny(width,height);
		}
		else if (choice == "apple") {
			apple(width,height);
		}
		else
			throw new IllegalArgumentException();
	}
	
	
	public void initialWorld(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 0, 0), new Point(0, 0, 1), new Vector(0, 1, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight blueLight = new PointLight(new Point(8,-8,10),new Color(0,0,200), 10,false);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,8,10),new Color(200.,0,0), 10,false);
		this.plights.add(redLight);
		PointLight greenLight = new PointLight(new Point(-8,0,0),new Color(0,200,0), 1,false);
		this.plights.add(greenLight);
		
		this.ambient = 1;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.translate(0, 0, 10).append(
				Transformation.scale(5, 5, 5));
		Transformation t2 = Transformation.translate(4, -4, 12).append(
				Transformation.scale(4, 4, 4));
		Transformation t3 = Transformation.translate(-4, -4, 12).append(
				Transformation.scale(4, 4, 4));
		Transformation t4 = Transformation.translate(4, 4, 12).append(
				Transformation.scale(4, 4, 4));
		Transformation t5 = Transformation.translate(-4, 4, 12).append(
				Transformation.scale(4, 4, 4));
		this.shapes.add(new Sphere(t1,new Color(1.0,1.0,1.0),1.));
		this.shapes.add(new Sphere(t2,new Color(1.0,1.0,1.0),1.));
		this.shapes.add(new Sphere(t3,new Color(1.0,1.0,1.0),1.));
		this.shapes.add(new Sphere(t4,new Color(1.0,1.0,1.0),1.));
		this.shapes.add(new Sphere(t5,new Color(1.0,1.0,1.0),1.));
	}
	
	
	public void planeAndSphere(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(6, 0, 6), new Point(0, 0, 0), new Vector(-1, 0, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.01,true);
		//this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(8,8,10),new Color(0,0,100), 0.1,true);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,-8,10),new Color(100,0,0), 0.1,true);
		this.plights.add(redLight);
		
		this.ambient = 0.1;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.translate(0.0, 0.0, 0.0);		
		//this.shapes.add(new Sphere(t1,new Color(5.0,5.0,5.0),1));
		
		Transformation t2 = Transformation.translate(0, 0, 0.0);
		Material sphereText = new Monochrome(new Color(100.0,100.0,100.0));
		ObjShape tessSphere = new ObjShape("./obj/sphere.obj",t2,sphereText,1.5);
		this.shapes.add(tessSphere);
		
		//ObjShape table = new ObjShape("./obj/table.obj",t2,sphereText,1.5);
		//this.shapes.add(table);
		
		
		
		Material mPlane;
		mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mat = new Julia(new Complex(-0.076,0.652),200,1,100, 10);
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(1,1,-1);
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
		
	}

	public void julia(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(1, 1, 1), new Point(1, 1, 0), new Vector(1, 0, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.00,false);
		this.plights.add(whiteLight);
		this.ambient = 1.0;
		
		//setup the objects in the scene.
		
		
		Material mPlane;
		//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		mPlane = new Julia(new Complex(-0.076,0.652),500,1,200, 10);
		//mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
		
	}
	
	public void bunny(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(10, 0, 10), new Point(0, 0, 0), new Vector(-1, 0, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		//PointLight whiteLight = new PointLight(new Point(0,2,10),new Color(100,100,100), 0.001,true);
		//this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(0,8,4),new Color(50,100,50), 0.005,true);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,0,4),new Color(100,50,50), 0.005,true);
		this.plights.add(redLight);
		
		this.ambient = 0.0000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 2, -1)).append(
				Transformation.rotateX(90).append(Transformation.rotateY(90)));
		
		//Triangle triangle = new Triangle(new Point(0,0,0), new Point(1,0,0), new Point(0,1,0),
		//								new Normal(0,0,1), new Normal(0,0,1), new Normal(0,0,1),
		//								new Color(100,0,0),1.0,t1);
		//this.shapes.add(triangle);
		Material mat;
		mat = new Monochrome( new Color(100,100,100));
		ObjShape bunny = new ObjShape("./obj/bunny.obj",t1,mat,2.0);
		//ObjShape bunny = new ObjShape("./obj/teapot.obj",t1,mat,2.0);
		//ObjShape bunny = new ObjShape("./obj/sphere.obj",t1,mat,2.0);
		this.shapes.add(bunny);
		
		
		Material mPlane;
		//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mPlane = new Julia(new Complex(-0.076,0.652),200,1,100, 10);
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
			
	}
	
	public void apple(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0.8, 0.8, 1.0), new Point(0, 0, 0), new Vector(-1, -1, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(10,10,10),new Color(100.0,100.0,100.0), 0.1,false);
		this.plights.add(whiteLight);
		
		this.ambient = 0.000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		//Triangle triangle = new Triangle(new Point(0,0,0), new Point(1,0,0), new Point(0,1,0),
		//								new Normal(0,0,1), new Normal(0,0,1), new Normal(0,0,1),
		//								new Color(100,0,0),1.0,t1);
		//this.shapes.add(triangle);
		Material objMat;
		//objMat = new Julia(new Complex(-0.076,0.652),600,1,400,2.5);
		//objMat = new Monochrome( new Color(100,100,100));
		objMat = new TextureFile("./obj/apple/apple_texture.jpg",1.0);
		ObjShape apple = new ObjShape("./obj/apple/apple.obj",t1,objMat,1.0);
		//this.shapes.add(apple);
		
		t1 = t1.append(Transformation.rotateX(90));
		Material houseText = new TextureFile("./obj/house/house_texture.jpg",1.0);
		ObjShape house = new ObjShape("./obj/house/house.obj",t1,houseText,0.6);
		this.shapes.add(house);
		
		
		Material mPlane;
		//mPlane = new Monochrome(new Color(100,100,100));
		//mPlane = new TextureFile("./obj/apple/apple_texture.jpg",1.0);
		mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -5);
		Plane plane = new Plane(tPlane,mPlane,10);
		this.shapes.add(plane);		
	}

}
