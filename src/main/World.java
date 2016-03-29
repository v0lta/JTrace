package main;

import java.util.ArrayList;
import java.util.List;

import light.AreaLight;
import light.PointLight;
import material.Chess;
import material.Complex;
import material.Julia;
import material.Material;
import material.Monochrome;
import material.ObjTextureFile;
import math.Color;
import math.Point;
import math.Transformation;
import math.Vector;
import camera.Camera;
import camera.PerspectiveCamera;
import shape.Circle;
import shape.ObjShape;
import shape.Plane;
import shape.Rectangle;
import shape.Shape;
import shape.Sphere;

/**
 * Sets up the scene to be rendered.
 */

public class World{
	public Camera camera;
	public List<PointLight> plights = new ArrayList<PointLight>();
	public List<AreaLight>  alights = new ArrayList<AreaLight>();
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
		else if (choice == "dragon") {
			dragon(width,height);
		}
		else if (choice == "venus") {
			venus(width,height);
		}
		else if (choice == "buddha") {
			buddha(width,height);
		}
		else if (choice == "tea") {
			tea(width,height);
		}
		else
			throw new IllegalArgumentException();
	}
	
	
	public void initialWorld(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 0, 0), new Point(0, 0, 1), new Vector(0, 1, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight blueLight = new PointLight(new Point(2,2,0),new Color(0,0,1), 500,true);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(-2,2,0),new Color(1.,0,0), 500,true);
		this.plights.add(redLight);
		PointLight greenLight = new PointLight(new Point(2,-2,0),new Color(0,1,0), 500,true);
		this.plights.add(greenLight);
		
		this.ambient = 0;
		
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
		this.shapes.add(new Sphere(t1,new Monochrome(new Color(1.0,1.0,1.0)),1.));
		this.shapes.add(new Sphere(t2,new Monochrome(new Color(1.0,1.0,1.0)),1.));
		this.shapes.add(new Sphere(t3,new Monochrome(new Color(1.0,1.0,1.0)),1.));
		this.shapes.add(new Sphere(t4,new Monochrome(new Color(1.0,1.0,1.0)),1.));
		this.shapes.add(new Sphere(t5,new Monochrome(new Color(1.0,1.0,1.0)),1.));
	}
	
	
	public void planeAndSphere(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(2, 2, 2), new Point(0, 0, 0), new Vector(0, 0, 1), 90);
		
		
		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.01,true);
		this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(8,8,10),new Color(10,10,100), 0.5,true);
		//this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,-8,10),new Color(100,10,10), 0.5,true);
		//this.plights.add(redLight);
		
		this.ambient = 0.000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.translate(0.0, 0.0, 0.0).append(
							Transformation.rotateX(0)).append(
							Transformation.rotateY(0)).append(
							Transformation.rotateZ(180));
		//Material globeTexture = new ObjTextureFile("./obj/txt/EarthMap.jpg",1.0);
		Material globeTexture = new ObjTextureFile("./obj/txt/EarthHighRes.jpg",1.0);
		//Material globeTexture = new ObjTextureFile("./obj/txt/SphereGrid.jpg",1.0);
		Sphere globe = new Sphere(t1,globeTexture,1.0);
		this.shapes.add(globe);
		
		//Transformation t2 = Transformation.translate(0, 0, 0.0);
		//Material sphereText = new Monochrome(new Color(100.0,100.0,100.0));
		//Material sphereText = new Julia(new Complex(-0.02,0.8),800,1,400, 10, "parula");
		//Material sphereText = new ObjTextureFile("./obj/txt/SphereGrid.jpg",1.0);
		//ObjShape tessSphere = new ObjShape("./obj/sphere.obj",t2,sphereText,1.0,5, this.camera);
		//this.shapes.add(tessSphere);
		
		//ObjShape table = new ObjShape("./obj/table.obj",t2,sphereText,1.5);
		//this.shapes.add(table);
		
		
		
		Material mPlane;
		mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mat = new Julia(new Complex(-0.076,0.652),200,1,100, 10);
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(1,1,-2);
		Plane plane = new Plane(tPlane,mPlane,0.5);
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
		//mPlane = new Julia(new Complex(-0.07,0.652),800,1,400, 10);
		//mPlane = new Julia(new Complex(-0.02,0.652),800,1,400, 10, "parula");
		
		//lightning = new Julia(new Complex(-0.02,0.8),800,1,400, 10);
		mPlane = new Julia(new Complex(-0.02,0.8),800,1,400, 10, "parula");
		
		//mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
		
	}
	
	public void bunny(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(10, 0, 10), new Point(0, 0, 0), new Vector(0, 0, 1), 45);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		//PointLight whiteLight = new PointLight(new Point(0,2,10),new Color(100,100,100), 0.001,true);
		//this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(0,8,4),new Color(50,100,50), 0.01,true);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,0,4),new Color(100,50,50), 0.01,true);
		this.plights.add(redLight);
		
		this.ambient = 0.0000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0.5, -1)).append(
				Transformation.rotateX(90)).append(
				Transformation.rotateY(180)).append( //-90, 0
				Transformation.rotateZ(0));
		

		
		Material mat;
		mat = new Monochrome( new Color(100,100,100));
		ObjShape bunny = new ObjShape("./obj/bunny.obj",t1,mat,2.0,30, this.camera);
		//ObjShape bunny = new ObjShape("./obj/teapot.obj",t1,mat,2.0,30, this.camera);
		this.shapes.add(bunny);
		
		
		Material mPlane;
		//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -5);
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
			
	}
	
	public void venus(int width,int height) {
		//set the camera.
				this.camera = new PerspectiveCamera(width, height,
						new Point(10, 0, 10), new Point(0, 0, 0), new Vector(-1, -0, 0), 90);
				PointLight blueLight = new PointLight(new Point(0,8,8),new Color(50,100,50), 0.01,true);
				this.plights.add(blueLight);
				PointLight redLight = new PointLight(new Point(8,0,8),new Color(100,50,50), 0.01,true);
				this.plights.add(redLight);
				
				this.ambient = 0.000;
				
				//setup the objects in the scene.
				Transformation t1 = Transformation.IDENTITY;
				t1 = t1.append(Transformation.translate(0, 0, 2.5)).append(
						Transformation.rotateZ(90).append(Transformation.rotateX(50)));
				
				Material mat;
				mat = new Monochrome( new Color(100,100,100));
				ObjShape venus = new ObjShape("./obj/venus.obj",t1,mat,2.0,20, this.camera);
				this.shapes.add(venus);
				
				
				Material mPlane;
				//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
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
		objMat = new ObjTextureFile("./obj/apple/apple_texture.jpg",1.0);
		ObjShape apple = new ObjShape("./obj/apple/apple.obj",t1,objMat,1.0,5, this.camera);
		this.shapes.add(apple);
		
		t1 = t1.append(Transformation.rotateX(90));
		Material houseText = new ObjTextureFile("./obj/house/house_texture.jpg",1.0);
		ObjShape house = new ObjShape("./obj/house/house.obj",t1,houseText,0.6,0, this.camera);
		//this.shapes.add(house);
		
		
		Material mPlane;
		//mPlane = new Monochrome(new Color(100,100,100));
		//mPlane = new TextureFile("./obj/apple/apple_texture.jpg",1.0);
		mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -5);
		Plane plane = new Plane(tPlane,mPlane,10);
		this.shapes.add(plane);		
	}
	
	public void dragon(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(5, 0, 5), new Point(0, 0, 0), new Vector(-1, -0, 0), 80);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,2,10),new Color(10,10,10), 0.01,true);
		this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(8,8,8),new Color(50,100,50), 0.05,true);
		this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(-8,0,8),new Color(100,0,0), 0.05,true);
		this.plights.add(redLight);
		
		this.ambient = 0.000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0, 1)).append(
				Transformation.rotateY(180).append(Transformation.scale(5, 5, 5)));
		
		Material mat;
		mat = new Monochrome( new Color(50,100,50));
		//ObjShape dragon = new ObjShape("./obj/dragonLowPoly/dragonLowPoly.obj",t1,mat,1.0,20, this.camera);
		ObjShape dragon = new ObjShape("./obj/dragonLowPoly/dragonHighPoly.obj",t1,mat,2.0,25, this.camera);
		this.shapes.add(dragon);
		
		
		Material mPlane;
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane,1);
		this.shapes.add(plane);
	}

	public void buddha(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(4, 4, 4), new Point(0, 0, 0), new Vector(0, 0, 1), 70);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(-4,4,4),new Color(10,10,10), 1.0,true);
		this.plights.add(whiteLight);
		PointLight whiteLight2 = new PointLight(new Point(4,4,4),new Color(10,10,10), 1.0,true);
		this.plights.add(whiteLight2);
		
		this.ambient = 0.0000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0, 0)).append(
				Transformation.scale(5, 5, 5).append(
				Transformation.rotateY(180)).append(
				Transformation.rotateZ(-120)));
		
		Material mat;
		mat = new Monochrome( new Color(0.5,1,0.5));
		//mat = new Julia(new Complex(-0.02,0.652),800,0.5,400, 100, "parula");
		//ObjShape buddha = new ObjShape("./obj/buddhaLowPoly/buddhaLowPoly.obj",t1,mat,10.0,25, this.camera);
		ObjShape buddha = new ObjShape("./obj/buddhaLowPoly/buddhaHighPoly.obj",t1,mat,10.0,25, this.camera);
		this.shapes.add(buddha);
		
		
		Material mPlane;
		mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		tPlane = tPlane.append(Transformation.translate(0, 0, -5));
		Plane plane = new Plane(tPlane,mPlane,0.1);
		this.shapes.add(plane);
	}
	
	public void tea(int width,int height) {
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 5, 2), new Point(0, 0, 1), new Vector(0, 0, 1), 100);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight1 = new PointLight(new Point(-2,0,4),new Color(100,10,10), 0.001,true);
		this.plights.add(whiteLight1);
		PointLight whiteLight2 = new PointLight(new Point(2,0,4),new Color(10,100,10), 0.001,true);
		this.plights.add(whiteLight2);
		
		//area light
		Material mLight = new Monochrome( new Color(100,10,10));
		Transformation tLight = Transformation.translate(0, 0, 4.4).append(Transformation.rotateY(180));
		Circle circle = new Circle(tLight,mLight,1,1);
		AreaLight al1 = new AreaLight(circle,1.0,500);
		this.alights.add(al1);

		
		this.ambient = 0.0000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0, 0)).append(
				Transformation.rotateX(90).append(
				Transformation.scale(1, 1, 1)).append(
				Transformation.rotateZ(0)));
		
		Material mat;
		mat = new Monochrome( new Color(100,100,100));
		ObjShape tea = new ObjShape("./obj/teapot.obj",t1,mat,2.0,30, this.camera);
		this.shapes.add(tea);
		
		/*Transformation t2 = Transformation.IDENTITY;
		t2 = t2.append(Transformation.translate(0, 1, 0)).append(
				Transformation.rotateX(90).append(
				Transformation.scale(0.5, 0.5, 0.5)).append(
				Transformation.rotateZ(0)));
		
		ObjShape tea2 = new ObjShape("./obj/teapot.obj",t2,mat,2.0,30, this.camera);
		this.shapes.add(tea2);*/
		
		Material mPlane1 = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		Transformation tPlane = Transformation.translate(0, 0, 0);
		Plane plane1 = new Plane(tPlane,mPlane1,1);
		this.shapes.add(plane1);
		Material mPlane2 = new Monochrome( new Color(100,0,0));
		Transformation tPlane2 = Transformation.translate(-3.0, 0, 0).append(Transformation.rotateY(90));
		Plane plane2 = new Plane(tPlane2,mPlane2,1);
		this.shapes.add(plane2);
		Material mPlane3 = new Monochrome( new Color(0,0,100));
		Transformation tPlane3 = Transformation.translate(3.4, 0, 0).append(Transformation.rotateY(-90));
		Plane plane3 = new Plane(tPlane3,mPlane3,1);
		this.shapes.add(plane3);
		Material mPlane4 = new Monochrome( new Color(0,100,0));
		Transformation tPlane4 = Transformation.translate(0, -1, 0).append(Transformation.rotateX(-90));
		Plane plane4 = new Plane(tPlane4,mPlane4,1);
		this.shapes.add(plane4);
		Material mPlane5 = new Monochrome( new Color(10,10,10));
		Transformation tPlane5 = Transformation.translate(0, 0, 4.5).append(Transformation.rotateY(180));
		Plane plane5 = new Plane(tPlane5,mPlane5,1);
		this.shapes.add(plane5);

		
			
	}
	
	
	
}