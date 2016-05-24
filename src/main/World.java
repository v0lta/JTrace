package main;

import java.util.ArrayList;
import java.util.List;

import light.AreaLight;
import light.PointLight;
import light.PriorSampleLight;
import material.Chess;
import material.ColorMap;
import material.Complex;
import material.CookTorranceSpecular;
import material.Diffuse;
import material.Julia;
import material.Lambertian;
import material.Material;
import material.Monochrome;
import material.MyTextureFile;
import material.NoSpec;
import material.ObjTextureFile;
import material.PhongSpecular;
import material.RandomChess;
import material.Specular;
import math.Color;
import math.Point;
import math.Transformation;
import math.Vector;
import camera.Camera;
import camera.PerspectiveCamera;
import shape.Circle;
import shape.Cube;
import shape.JuliaMesh;
import shape.ObjShape;
import shape.ObjShapeWithNrmlMap;
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
	public int spp = 1;
	
	public World(int width,int height,String choice, int sampleNo) throws IllegalArgumentException {
		if (choice == "initialWorld")
			initialWorld(width,height);
		else if (choice == "sphereWorld") {
			sphereWorld(width,height);
		}
		else if (choice == "planeAndSphere") {
			planeAndSphere(width,height);
		}
		else if (choice == "Julia") {
			julia(width,height);
		}
		else if (choice == "Julia2") {
			julia2(width,height);
		}
		else if (choice == "Julia3d") {
			julia3d(width,height);
		}
		else if (choice == "Julia3d2") {
			julia3d2(width,height);
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
		else if (choice == "sun") {
			sun(width,height);
		}
		else if (choice == "richter") {
			richter(width,height,sampleNo);
		}
		else if (choice == "richterExtended") {
			richterExtended(width,height,sampleNo);
		}
		else if (choice == "twoLights") {
			twoLights(width,height,sampleNo);
		}		
		else if (choice == "debug") {
			debug(width,height,sampleNo);
		}
		else {
			throw new IllegalArgumentException("World not found");
		}
	}
	
	public void sphereWorld(int width,int height) {
		//this.ambient = 0.002;
	        //Set up a scene with a transformed sphere.
			this.spp = 1;
			//set the camera.
			this.camera = new PerspectiveCamera(width, height,
				new Point(-2.0, 0, -5.0), new Point(0, 0, 0), new Vector(0, 0, -1), 60);
	        
			PointLight pointLight1 = new PointLight(new Point(0,10,-4.5),new Color(8,8,8), 2.5, false);
			this.plights.add(pointLight1);
        
	        //background plane
			Material mPlane;
			Specular spec = new NoSpec();
			Diffuse lamb = new Lambertian(1.0);
			mPlane = new Chess(spec, lamb, new Color(40,40,40), new Color(10,10,10),0.5);
			Transformation tPlane = Transformation.translate(0,0,5).append(Transformation.rotateX(-180));
			Plane plane = new Plane(tPlane,mPlane);
			this.shapes.add(plane);
	        
	        //sphere 1
			Material ms1 = new Monochrome(spec,lamb, new Color(0,6,0));
			Transformation ts1 = Transformation.translate(1.5,0,0).append(Transformation.scale(0.75, 0.75, 0.75));
			Sphere s1 = new Sphere(ts1,ms1);
			this.shapes.add(s1);
			
	        //sphere 2
			Material ms2 = new Monochrome(spec, lamb, new Color(0,6,6));
			Transformation ts2 = Transformation.translate(-1,0.1,0).append(Transformation.scale(0.75, 0.75, 0.75));
			Sphere s2 = new Sphere(ts2,ms2);
			this.shapes.add(s2);
	}
	
	public void initialWorld(int width,int height) {
		this.spp = 1;
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
		
		Specular phong = new PhongSpecular(25.0,0,0.5);
		Diffuse lamb = new Lambertian(0.5);
		this.shapes.add(new Sphere(t1,new Monochrome(phong, lamb, new Color(1.0,1.0,1.0))));
		this.shapes.add(new Sphere(t2,new Monochrome(phong, lamb, new Color(1.0,1.0,1.0))));
		this.shapes.add(new Sphere(t3,new Monochrome(phong, lamb, new Color(1.0,1.0,1.0))));
		this.shapes.add(new Sphere(t4,new Monochrome(phong, lamb, new Color(1.0,1.0,1.0))));
		this.shapes.add(new Sphere(t5,new Monochrome(phong, lamb, new Color(1.0,1.0,1.0))));
	}
	
	
	public void planeAndSphere(int width,int height) {
		this.spp = 50;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(2, 0, 2), new Point(0, 0, 0), new Vector(0, 0, 1), 90);
		
		
		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.01,true);
		//this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(8,8,10),new Color(10,10,100), 0.5,true);
		//this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(8,-8,10),new Color(100,10,10), 0.5,true);
		//this.plights.add(redLight);
		
		//this.ambient = 0.000;
		this.ambient = 0.002;
		
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.translate(0.0, 0.0, 0.0).append(
							Transformation.rotateX(0)).append(
							Transformation.rotateY(0)).append(
							Transformation.rotateZ(180));
		Specular spec = new PhongSpecular(200,0,0.1);
		Diffuse lamb = new Lambertian(0.9);
		//Specular spec = new CookTorranceSpecular(0.1,0.1,1);
		//Material globeTexture = new ObjTextureFile("./obj/txt/EarthMap.jpg",1.0);
		Material globeTexture = new ObjTextureFile(spec, lamb,"./obj/txt/EarthHighRes.jpg",1.0);
		//Material globeTexture = new ObjTextureFile("./obj/txt/SphereGrid.jpg",1.0);
		Sphere globe = new Sphere(t1,globeTexture);
		//this.shapes.add(globe);
		
		//Transformation t2 = Transformation.translate(0, 0, 0.0);
		//Material sphereText = new Monochrome(new Color(100.0,100.0,100.0));
		//Material sphereText = new Julia(new Complex(-0.02,0.8),800,1,400, 10, "parula");
		//Material sphereText = new ObjTextureFile("./obj/txt/SphereGrid.jpg",1.0);
		//ObjShape tessSphere = new ObjShape("./obj/sphere.obj",t2,sphereText,1.0,5, this.camera);
		//this.shapes.add(tessSphere);
		
		//ObjShape table = new ObjShape("./obj/table.obj",t2,sphereText,1.5);
		//this.shapes.add(table);
		
		Material mPlane;
		mPlane = new Chess(spec,lamb, new Color(100,100,100), new Color(1,1,1),1);
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mat = new Julia(new Complex(-0.076,0.652),200,1,100, 10);
		//mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(1,1,-2);
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
		
	}

	public void julia(int width,int height) {
		this.spp = 2;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(1, 1, 1), new Point(1, 1, 0), new Vector(1, 0, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.00,false);
		this.plights.add(whiteLight);
		this.ambient = 1.0;
		
		//setup the objects in the scene.
		
		
		Material mPlane;
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mPlane = new Julia(new Complex(-0.07,0.652),800,1,400, 10, "parula");
		mPlane = new Julia(new Complex(-0.1,0.651),800,1,400, 10, "jet");
		
		//mPlane = new Julia(new Complex(-0.02,0.652),800,1,400, 10, "parula");
		
		//lightning = new Julia(new Complex(-0.02,0.8),800,1,400, 10);
		//mPlane = new Julia(new Complex(-0.02,0.8),800,1,400, 10, "parula");
		
		//mPlane = new Monochrome(new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
		
	}

	public void julia2(int width,int height) {
		this.spp = 2;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(1, 1, 1), new Point(1, 1, 0), new Vector(1, 0, 0), 90);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.00,false);
		this.plights.add(whiteLight);
		this.ambient = 1.0;
		
		//setup the objects in the scene.
		Material mPlane;
		//cr = -0.076, ci = 0.651, N = 200, bound = 1, lim = 100
		//mPlane = new Julia(new Complex(-0.07,0.652),800,1,400, 10, "parula");
		//mPlane = new Julia(new Complex(-0.8,0.156),800,1,100, 10, "jet");
		mPlane = new Julia(new Complex(-0.8,0.175),800,1,100, 10, "jet");
		
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
		
	}
	
	public void julia3d2(int width,int height) {
		this.spp = 2;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 1, 1.2), new Point(0.0, 0.0, -0.14), new Vector(0, 0, 1), 70);
		
		
		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.00,false);
		this.plights.add(whiteLight);
		this.ambient = 1.0;
		
		//setup the objects in the scene.
		Transformation trans = Transformation.scale(1.2, 1.2, 1.2);
		
																	//800
		//JuliaMesh juliaMesh = new JuliaMesh(new Complex(-0.8,0.156),800,2,400, 100, "jet",1.0,
		//							trans, camera, 0.01, 100);
		JuliaMesh juliaMesh = new JuliaMesh(new Complex(-0.8,0.175),800,2,400, 100, "jet",1.0,
				trans, camera, 0.01, 100);
		this.shapes.add(juliaMesh);
		
	}

	
	
	public void julia3d(int width,int height) {
		this.spp = 1;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(1.35, -0.85, 0.8), new Point(-0.25, 0.0, -0.55), new Vector(0, 0, 1), 50);
		//this.camera = new PerspectiveCamera(width, height,
		//		new Point(1, 1, 2), new Point(0, 0.0, 0), new Vector(0, 0, 1), 90);
		
		
		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(0,0,10),new Color(100,100,100), 0.00,false);
		this.plights.add(whiteLight);
		this.ambient = 1.0;
		
		//setup the objects in the scene.
		Transformation trans = Transformation.scale(1.2, 1.2, 1.2);
		
																	//800
		JuliaMesh juliaMesh = new JuliaMesh(new Complex(-0.1,0.651),800,2,400, 100, "jet",1.0,
									trans, camera, 0.01, 100);
		this.shapes.add(juliaMesh);
		
	}
	
	
	public void bunny(int width,int height) {
		this.spp = 5;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(8, 0, 8), new Point(0, 0, 0), new Vector(0, 0, 1), 45);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		//PointLight whiteLight = new PointLight(new Point(0,2,10),new Color(100,100,100), 0.001,true);
		//this.plights.add(whiteLight);
		PointLight blueLight = new PointLight(new Point(0,8,4),new Color(50,100,50), 0.001,true);
		//this.plights.add(blueLight);
		PointLight redLight = new PointLight(new Point(4,0,4),new Color(100,50,50), 0.001,true);
		this.plights.add(redLight);
		
		this.ambient = 0.0000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(1.5, 0.5, -0)).append(
				Transformation.rotateX(90)).append(
				Transformation.rotateY(-90)).append( //-90, 0
				Transformation.rotateZ(0));
		

		
		Material mat;
		Specular phong = new PhongSpecular(25.0,0,1);
		Diffuse lamb = new Lambertian(3.6);
		mat = new Monochrome(phong, lamb,  new Color(100,100,100));
		//ObjShape bunny = new ObjShape("./obj/bunny.obj",t1,mat,2.0,30, this.camera, 0.001, 2.0);
		ObjShape bunny = new ObjShape("./obj/teapot.obj",t1,mat,30, this.camera, 0.001, 3.6);
		this.shapes.add(bunny);
		
		//No acceleration structure.
		//this.shapes.addAll(bunny.triangleList);
		
		Material mPlane;
		//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		mPlane = new Monochrome(phong, lamb, new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -0);
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
	}
	
	public void venus(int width,int height) {
		this.spp = 2;
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
				Specular phong = new PhongSpecular(25.0,0,1);
				Diffuse lamb = new Lambertian(2.0);
				mat = new Monochrome(phong,lamb, new Color(100,100,100));
				ObjShape venus = new ObjShape("./obj/venus.obj",t1,mat,20, this.camera, 0.001, 3.0);
				this.shapes.add(venus);
				
				
				Material mPlane;
				//mPlane = new Chess(new Color(100,100,100), new Color(1,1,1),1);
				mPlane = new Monochrome(phong,lamb, new Color(100,100,100));
				
				//(Point a,Normal n, Material m,double reflectivity)
				Transformation tPlane = Transformation.IDENTITY;
				Plane plane = new Plane(tPlane,mPlane);
				this.shapes.add(plane);
	}
	
	public void apple(int width,int height) {
		this.spp = 10;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0.8, 0.8, 1.0), new Point(0, 0, 0), new Vector(-1, -1, 0), 70);

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
		Specular phong = new PhongSpecular(250.0,0,0.005);
		Diffuse lamb = new Lambertian(1.0);
		objMat = new ObjTextureFile(phong, lamb, "./obj/apple/apple_texture.jpg",1.0);
		ObjShape apple = new ObjShape("./obj/apple/apple.obj",t1,objMat,5, this.camera, 0.001, 2.0);
		this.shapes.add(apple);
		
		t1 = t1.append(Transformation.rotateX(90));
		Material houseText = new ObjTextureFile(phong, lamb, "./obj/house/house_texture.jpg",1.0);
		ObjShape house = new ObjShape("./obj/house/house.obj",t1,houseText,0, this.camera, 0.001, 2.0);
		//this.shapes.add(house);
		
		
		Material mPlane;
		//mPlane = new Monochrome(new Color(100,100,100));
		//mPlane = new TextureFile("./obj/apple/apple_texture.jpg",1.0);
		mPlane = new Chess(phong,lamb, new Color(100,100,100), new Color(1,1,1),1);
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -5);
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);		
	}
	
	public void dragon(int width,int height) {
		this.spp = 2;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(5, 0, 5), new Point(0, 0, 0), new Vector(0, 0, 1), 80);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight redLight = new PointLight(new Point(-5,5,8),new Color(100,10,10), 0.01,true);
		this.plights.add(redLight);
		PointLight whiteLight = new PointLight(new Point(5,0,8),new Color(100,100,100), 0.01,true);
		this.plights.add(whiteLight);
		
		
		this.ambient = 0.000;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0, 1)).append(
				Transformation.rotateY(180).append(Transformation.scale(5, 5, 5)));
		
		Material mat;
		Specular phong = new PhongSpecular(25.0,0,1);
		Diffuse lamb = new Lambertian(2.0);
		mat = new Monochrome(phong, lamb, new Color(50,100,50));
		//mat = new ObjTextureFile("./obj/dragonLowPoly/dragonNormalMap4k.jpg",3.0);
		
		
		//ObjShape dragon = new ObjShape("./obj/dragonLowPoly/dragonLowPoly.obj",t1,mat,2.0,20, this.camera);
		//ObjShape dragon = new ObjShapeWithNrmlMap("./obj/dragonLowPoly/dragonLowPoly.obj",
		//		"./obj/dragonLowPoly/dragonNormalMap4k.jpg",t1,mat,20, this.camera, 0.001, 3.0); //png will crash.!!
		ObjShape dragon = new ObjShape("./obj/dragonLowPoly/dragonHighPoly.obj",t1,mat,25, this.camera,0.0001, 0.001);
		this.shapes.add(dragon);
		
		
		Material mPlane;
		mPlane = new Monochrome(phong,lamb, new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.translate(0, 0, -0.5);
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
	}

	public void buddha(int width,int height) {
		this.spp = 5;
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(4, 4, 4), new Point(0, 0, 0), new Vector(0, 0, 1), 70);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight = new PointLight(new Point(-4,4,4),new Color(10,10,10), 1.0,true);
		this.plights.add(whiteLight);
		PointLight whiteLight2 = new PointLight(new Point(4,4,4),new Color(10,10,10), 1.0,true);
		this.plights.add(whiteLight2);
		
		//this.ambient = 0.0500;
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, 0, 0)).append(
				Transformation.scale(5, 5, 5).append(
				Transformation.rotateY(180)).append(
				Transformation.rotateZ(200)));
		
		Material mat;
		Specular phong = new PhongSpecular(100.0,0,1);
		Diffuse lamb = new Lambertian(10.0);
		mat = new Monochrome(phong,lamb, new Color(0.5,1,0.5));
		//mat = new ObjTextureFile("./obj/buddhaLowPoly/buddhaNormalMap4k.jpg",1.0);
		
		//mat = new Julia(new Complex(-0.02,0.652),800,0.5,400, 100, "parula");
		ObjShape buddha = new ObjShape("./obj/buddhaLowPoly/buddhaLowPoly.obj",t1,mat,25, this.camera, 0.001, 2.0);
		//ObjShape buddha = new ObjShapeWithNrmlMap("./obj/buddhaLowPoly/buddhaLowPoly.obj",
		//		"./obj/buddhaLowPoly/buddhaNormalMap4k.jpg",t1,mat,1.0,20, this.camera);
		//ObjShape buddha = new ObjShape("./obj/buddhaLowPoly/buddhaHighPoly.obj",t1,mat,10.0,25, this.camera);
		this.shapes.add(buddha);
		
		
		Material mPlane;
		Diffuse lamb2 = new Lambertian(0.1);
		mPlane = new Monochrome(phong,lamb2, new Color(100,100,100));
		
		//(Point a,Normal n, Material m,double reflectivity)
		Transformation tPlane = Transformation.IDENTITY;
		tPlane = tPlane.append(Transformation.translate(0, 0, -5));
		Plane plane = new Plane(tPlane,mPlane);
		this.shapes.add(plane);
	}
	
	public void tea(int width,int height) {
		this.spp = 5;
		this.ambient = 0.00001;
		
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 6, 4), new Point(0, 0, 2), new Vector(0, 0, 1), 68);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		//PointLight whiteLight1 = new PointLight(new Point(-2,0,4),new Color(10,10,10), 0.01,true);
		//this.plights.add(whiteLight1);
		//PointLight whiteLight2 = new PointLight(new Point(2,0,4),new Color(10,10,10), 0.01,true);
		//this.plights.add(whiteLight2);
		
		//area light
		Specular noSpec = new NoSpec();
		Diffuse lamb = new Lambertian(10000);
		Material mLight = new Monochrome(noSpec, lamb, new Color(1,1,1));
		Transformation tLight = Transformation.translate(0, 1, 5).append(
				                Transformation.rotateY(-180)).append(
				                Transformation.scale(0.5, 0.5, 0.5));
		Rectangle shape = new Rectangle(tLight,mLight);
		//Sphere shape = new Sphere(tLight,mLight,10000);
		AreaLight al1 = new AreaLight(shape,100.0,100);
		this.alights.add(al1);
		this.shapes.add(al1);
		
		Specular phong = new PhongSpecular(100.0,0,0.01);
		
		//setup the objects in the scene.
		Transformation t1 = Transformation.IDENTITY;
		t1 = t1.append(Transformation.translate(0, -0.5, 1.0)).append(
				Transformation.rotateX(92).append(
				Transformation.scale(1, 1, 1)).append(
				Transformation.rotateZ(0)));
		
		Material mat;
	    Diffuse lamb2 = new Lambertian(1.0);
		mat = new Monochrome(phong, lamb2, new Color(35,107,142));
		ObjShape tea = new ObjShape("./obj/teapot.obj",t1,mat,30, this.camera, 0.001, 4.5);
		this.shapes.add(tea);
		
		Material mPlane1 = new Chess(phong,lamb2,  new Color(100,100,100), new Color(1,1,1),1);
		Transformation tPlane = Transformation.translate(0, 0, 0);
		Plane plane1 = new Plane(tPlane,mPlane1);
		this.shapes.add(plane1);
		Material mPlane2 = new Monochrome(phong,lamb2, new Color(100,0,0));
		Transformation tPlane2 = Transformation.translate(-4.0, 0, 0).append(Transformation.rotateY(90));
		Plane plane2 = new Plane(tPlane2,mPlane2);
		this.shapes.add(plane2);
		Material mPlane3 = new Monochrome(phong, lamb2, new Color(0,0,100));
		Transformation tPlane3 = Transformation.translate(4.0, 0, 0).append(Transformation.rotateY(-90));
		Plane plane3 = new Plane(tPlane3,mPlane3);
		this.shapes.add(plane3);
		Material mPlane4 = new Monochrome(phong, lamb2, new Color(0,100,0));
		Transformation tPlane4 = Transformation.translate(0, -4.0, 0).append(Transformation.rotateX(-90));
		Plane plane4 = new Plane(tPlane4,mPlane4);
		this.shapes.add(plane4);
		//Material mPlane5 = new Monochrome( new Color(10,10,10));
		//Transformation tPlane5 = Transformation.translate(0, 0, 5.5).append(Transformation.rotateY(180));
		//Plane plane5 = new Plane(tPlane5,mPlane5,1);
		//this.shapes.add(plane5);
	}

	public void sun(int width,int height) {
		this.spp = 5;
		this.ambient = 0.00001;
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 4, 4), new Point(0, 0, 0), new Vector(0, 0, 1), 75);
		//PointLight whiteLight1 = new PointLight(new Point(-2.5, 4, 1.5),new Color(10,10,10), 0.01,true);
		//this.plights.add(whiteLight1);
		
		//area light
		Specular noSpecular = new NoSpec();
		Diffuse sumLamb = new Lambertian(0.0005);
		Material mLight = new MyTextureFile(noSpecular, sumLamb, "./obj/cubeEarth/texture_sun.jpg");
		//Material mLight = new MyTextureFile("./obj/mageScene/textureSunBlue.jpg");
		Transformation tLight = Transformation.translate(-2.5, 3.5, 1.5);
		Sphere shape = new Sphere(tLight,mLight);
		AreaLight al1 = new AreaLight(shape,100.0,100);
		this.alights.add(al1);
		this.shapes.add(al1);
		
		Specular phong = new NoSpec();
		Diffuse earthLamb = new Lambertian(0.5);
		Material mEarth = new MyTextureFile(phong, earthLamb, "./obj/cubeEarth/EarthWithClouds.jpg");
		Transformation tEarth = Transformation.IDENTITY;
		Sphere earth = new Sphere(tEarth,mEarth);
		this.shapes.add(earth);
		
		Diffuse moonLamb = new Lambertian(0.6);
		Material mMoon = new MyTextureFile(phong, moonLamb, "./obj/cubeEarth/moon.jpg");
		Transformation tMoon = Transformation.translate(-1.5, -0.5, 0.5).append(Transformation.scale(0.27, 0.27, 0.27));
		Sphere moon = new Sphere(tMoon, mMoon);
		this.shapes.add(moon);
		
		Diffuse cubeLamb = new Lambertian(0.1);
		Material mCube = new ObjTextureFile(phong, cubeLamb, "./obj/cubeEarth/borg2.jpg",3.0);
		//Material mCube = new Monochrome(new Color(100,0,0));
		Transformation tCube = Transformation.IDENTITY.append(
							   	Transformation.translate(-1.5, 1.5, 1)).append(
							   	Transformation.rotateZ(8)).append(
							   	Transformation.rotateX(0)).append(
							   	Transformation.scale(0.2, 0.2, 0.2));
		Cube cube = new Cube(tCube, mCube);
		this.shapes.add(cube);
		
		Transformation tCube2 = Transformation.IDENTITY.append(
			   	Transformation.translate(-1.0, 2.0, 0.5)).append(
			   	Transformation.rotateZ(8)).append(
			   	Transformation.rotateX(0)).append(
			   	Transformation.scale(0.25, 0.25, 0.25));
		Cube cube2 = new Cube(tCube2, mCube);
		this.shapes.add(cube2);
		
		
		//Material mPlane1 = new Chess(new Color(100,100,100), new Color(1,1,1),1);
		Material mPlane1 = new ObjTextureFile(phong,cubeLamb, "./obj/cubeEarth/space.jpg",1.0);
		Transformation tPlane = Transformation.translate(4.2, -4, 4).append(
								Transformation.rotateX(-45)).append(
								Transformation.scale(10, 10, 10));
		Rectangle plane1 = new Rectangle(tPlane,mPlane1);
		//this.shapes.add(plane1);		
		AreaLight al2 = new AreaLight(plane1,1.0,1);
		this.alights.add(al2);
		this.shapes.add(al2);
	}
	
	
	public void richter(int width,int height, int sampleNo) {
		this.spp = 4;
		this.ambient = 0.0001;
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 2, 2), new Point(0, 5, 2), new Vector(0, 0, 1), 60);

		ColorMap colorMap = new ColorMap(0.0, 1.0, null, 1.0, "parula");
		Material mRichter;
		Transformation tRichterLight;
		Specular spec;
		
		//Diffuse lampLamb = new Lambertian(0.00025);
			
		
		Specular noSpec = new NoSpec();
		double rhoDiff = 0.8;
		double rhoSpec = 0.2;
		Diffuse lamb = new Lambertian(rhoDiff);
		boolean phong = false;
		if (phong) {
			//Phong
			//spec = new PhongSpecular(2000.0,0,rhoSpec); //ph1
			//spec = new PhongSpecular(1000.0,0,rhoSpec);
			//spec = new PhongSpecular(500.0,0,rhoSpec); //ph2
			spec = new PhongSpecular(100.0,0,rhoSpec); //ph3
		} else {
			//Cook-Torrance	
			//spec = new NoSpec();
			//spec = new CookTorranceSpecular(0.03,0.01,rhoSpec);
			//spec = new CookTorranceSpecular(0.0366,0.276,rhoSpec); //0.0366 0.276s ct1
			spec = new CookTorranceSpecular(0.025,0.076,rhoSpec); //ct2
			//spec = new CookTorranceSpecular(0.015,0.01,rhoSpec);	//ct3
		}
		//area light
		mRichter = new RandomChess(noSpec, lamb, colorMap, 1.0,20);
		tRichterLight = Transformation.translate(0, 8.0, 1.0).append(Transformation.rotateX(90));
		Rectangle shape = new Rectangle(tRichterLight,mRichter);
		//AreaLight al1 = new AreaLight(shape,5.0,sampleNo);
		PriorSampleLight al1 = new PriorSampleLight(shape,5.0,sampleNo,10);
		this.alights.add(al1);
		this.shapes.add(al1);
		
		Material gery = new Monochrome(spec, lamb, new Color(1,1,1));
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane1 = new Plane(tPlane,gery);
		this.shapes.add(plane1);
		
		/*
		Diffuse lambBlue = new Lambertian(0.25);
		Material blue = new Monochrome(noSpec, lambBlue, new Color(1,1,10));
		Transformation tPlane2 = Transformation.translate(0, 8.001, 0).append(
												Transformation.rotateX(90));
		Plane plane2 = new Plane(tPlane2,blue);
		//this.shapes.add(plane2);
		Diffuse lambGreen = new Lambertian(5);
		Material green = new Monochrome(noSpec, lambGreen, new Color(1,10,1));
		Transformation tPlane3 = Transformation.translate(-2, 0, 0).append(
												Transformation.rotateY(90));
		Plane plane3 = new Plane(tPlane3,green);
		this.shapes.add(plane3);
		*/
	}
	
	
	public void richterExtended(int width,int height, int sampleNo) {
		this.spp = 10;
		this.ambient = 0.0000;
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 1, 2), new Point(0, 5, 2), new Vector(0, 0, 1), 60);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight1 = new PointLight(new Point(0,-2,4),new Color(10,10,10), 0.5,false);
		this.plights.add(whiteLight1);

		ColorMap colorMap = new ColorMap(0.0, 1.0, null, 1.0, "parula");
		Material mRichter;
		Transformation tRichterLight;
		Specular spec;
		
		//Diffuse lampLamb = new Lambertian(0.00025);
		Specular noSpec = new NoSpec();
		double rhoDiff = 0.25;
		double rhoSpec = 0.75;
		Diffuse lamb = new Lambertian(rhoDiff);
		boolean phong = false;
		if (phong) {
			//Phong
			spec = new PhongSpecular(2000.0,0,rhoSpec);
			//spec = new PhongSpecular(1000.0,0,rhoSpec);
			//spec = new PhongSpecular(500.0,0,rhoSpec);
			//spec = new PhongSpecular(100.0,0,rhoSpec);
		} else {
			//Cook-Torrance	
			//spec = new NoSpec();
			//spec = new CookTorranceSpecular(0.03,0.01,rhoSpec);
			spec = new CookTorranceSpecular(0.025,0.076,rhoSpec);
			//spec = new CookTorranceSpecular(0.0366,0.276,rhoSpec); //0.0366 0.276
			//spec = new CookTorranceSpecular(0.015,0.01,rhoSpec);
		}
		//area light
		mRichter = new RandomChess(noSpec, lamb, colorMap, 1.0,20);
		tRichterLight = Transformation.translate(0, 8.0, 1.25).append(
						Transformation.rotateX(90)).append(
						Transformation.scale(1.25, 1.0, 1.25));
		Rectangle shape = new Rectangle(tRichterLight,mRichter);
		AreaLight al1 = new AreaLight(shape,20.0,sampleNo);
		//PriorSampleLight al1 = new PriorSampleLight(shape,20.0,sampleNo,10);
		this.alights.add(al1);
		this.shapes.add(al1);
		
		Material gery = new Monochrome(spec, lamb, new Color(1,1,1));
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane1 = new Plane(tPlane,gery);
		this.shapes.add(plane1);
		

		Diffuse lambBlue = new Lambertian(0.25);
		Material blue = new Monochrome(noSpec, lambBlue, new Color(1,1,10));
		Transformation tPlane2 = Transformation.translate(0, 8.001, 0).append(
												Transformation.rotateX(90));
		Plane plane2 = new Plane(tPlane2,blue);
		this.shapes.add(plane2);
		
		Diffuse lambGreen = new Lambertian(5);
		Material green = new Monochrome(noSpec, lambGreen, new Color(1,10,1));
		Transformation tPlane3 = Transformation.translate(-2, 0, 0).append(
												Transformation.rotateY(90));
		Plane plane3 = new Plane(tPlane3,green);
		this.shapes.add(plane3);
		
		Transformation tCube = Transformation.translate(0.5, 5.5, 0.25).append(
							   Transformation.scale(0.25, 0.25, 0.25)).append(
							   Transformation.rotateZ(45));
		Material mCube = new Monochrome(spec, lamb, new Color(1,10,10));
		Cube cube = new Cube(tCube, mCube);
		this.shapes.add(cube);
		
		Transformation tSphere = Transformation.translate(-0.5, 5.5, 0.25).append(Transformation.scale(0.25, 0.25, 0.25));
		Material mSphere = new Monochrome(spec, lamb, new Color(10,10,10));
		Sphere sphere = new Sphere(tSphere, mSphere);
		this.shapes.add(sphere);

	}
	
	
	
	public void debug(int width,int height, int sampleNo) {
		this.spp = 5;
		this.ambient = 0.00001;
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(0, 1, 2), new Point(0, 5, 2), new Vector(0, 0, 1), 40);

		//set up the lights                (Point origin, Color color, double intensity,boolean shadows)
		PointLight whiteLight1 = new PointLight(new Point(0,-2,4),new Color(10,10,10), 100.5,false);
		this.plights.add(whiteLight1);

		ColorMap colorMap = new ColorMap(0.0, 1.0, null, 1.0, "parula");
		Material mRichter;
		Transformation tRichterLight;
		Specular spec = new NoSpec();
		
		//Diffuse lampLamb = new Lambertian(0.00025);
		Specular noSpec = new NoSpec();
		double rhoDiff = 0.25;
		double rhoSpec = 0.75;
		Diffuse lamb = new Lambertian(rhoDiff);
		mRichter = new RandomChess(noSpec, lamb, colorMap, 1.0,20);
		tRichterLight = Transformation.translate(0, 8.0, 1.25).append(
						Transformation.rotateX(90)).append(
						Transformation.scale(1.25, 1.0, 1.25));
		Rectangle shape = new Rectangle(tRichterLight,mRichter);
		PriorSampleLight al1 = new PriorSampleLight(shape,20.0,sampleNo,10);
		this.shapes.addAll(al1.subLights);
		//this.shapes.add(shape);
		
		Material gery = new Monochrome(spec, lamb, new Color(1,1,1));
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane1 = new Plane(tPlane,gery);
		this.shapes.add(plane1);
	}
	
	//TODO.	
	public void twoLights(int width,int height, int sampleNo) {
		this.spp = 1;
		this.ambient = 0.001;
		
		//set the camera.
		this.camera = new PerspectiveCamera(width, height,
				new Point(2.5, 0, 1.5), new Point(0, 0, 0.5), new Vector(0, 0, 1), 90);

	
		ColorMap colorMap = new ColorMap(0.0, 1.0, null, 1.0, "parula");
		ColorMap colorMap2 = new ColorMap(0.0, 1.0, null, 1.0, "hot");
		Specular spec = new NoSpec();
		
		//Diffuse lampLamb = new Lambertian(0.00025);
		Specular noSpec = new NoSpec();
		double rhoDiff = 0.25;
		double rhoSpec = 0.75;

		
		Diffuse lamb = new Lambertian(rhoDiff);
		Material mRichter = new RandomChess(noSpec, lamb, colorMap, 1.0,20);
		Transformation tRichterLight = Transformation.translate(0.0, 1.5, 0.0).append(
							Transformation.rotateX(90).append(
								Transformation.rotateY(0.0)));
		Rectangle shape = new Rectangle(tRichterLight,mRichter);
		PriorSampleLight al1 = new PriorSampleLight(shape,20.0,sampleNo,10);
		this.shapes.add(al1);
		this.alights.add(al1);
		
		Material mRichter2 = new RandomChess(noSpec, lamb, colorMap2, 1.0,20);
		Transformation tRichterLight2 = Transformation.translate(0.0, -1.5, 0.0).append(
							Transformation.rotateX(90).append(
								Transformation.rotateY(180)));
		Rectangle shape2 = new Rectangle(tRichterLight2,mRichter2);
		AreaLight al2 = new AreaLight(shape2,20.0,sampleNo);
		this.shapes.add(al2);
		this.alights.add(al2);
		
		spec = new CookTorranceSpecular(0.015,0.01,rhoSpec);
		Material gery = new Monochrome(spec, lamb, new Color(1,1,1));
		Transformation tPlane = Transformation.IDENTITY;
		Plane plane1 = new Plane(tPlane,gery);
		this.shapes.add(plane1);
	}
	
	
}