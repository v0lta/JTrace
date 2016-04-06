package shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import light.LightIntersection;
import material.Material;
import math.Constants;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;
import math.Intersection;


/**
 * Represents a three-dimensional {@link Sphere} with radius one and centered at
 * the origin, which is transformed by the given {@link Transformation}.
 * 
 * @author Niels Billen and Moritz Wolter
 * @version 0.2
 */
public class Sphere implements LightableShape {
	public final Transformation transformation;
	public final Material material;
	public final double reflectivity;

	/**
	 * Creates a new unit {@link Sphere} at the origin, transformed by the given
	 * {@link Transformation}.
	 * 
	 * @param transformation
	 *          the transformation applied to this {@link Sphere}.
	 * @param material
	 * 			defines the color of the spheres surface.
	 * @param reflectivity 
	 * 			determines how well the sphere is going to reflect the light.
	 * @throws NullPointerException
	 *             when the transformation is null.
	 */
	public Sphere(Transformation transformation, Material material, double reflectivity) {

		this.transformation = transformation;
		this.material = material;
		this.reflectivity = reflectivity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Shape#intersect(geometry3d.Ray3D)
	 */
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		
        if (Constants.compVisualization){
        	ray.countIntersection();
        }
        
        Ray transformed = transformation.transformInverse(ray);
		
		Vector o = transformed.origin.toVector();

		double a = transformed.direction.dot(transformed.direction);
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - 1.0;

		double d = b * b - 4.0 * a * c;

		if (d < 0)
			return hits;
		double dr = Math.sqrt(d);

		// numerically solve the equation a*t^2 + b * t + c = 0
		double q = 0;
		if (b < 0) {
			q = -0.5 * (b - dr);
		} else {
			q = -0.5 * (b + dr);
		}
		
		double t0 = q / a;
		double t1 = c / q;

		//hit points with t<0 are behind the camera.
		boolean hasInt = t0 >= Constants.epsilon || t1 >= Constants.epsilon;
		
		
		if (hasInt == true) {
			//found an intersection compute the hit point.
			Vector hitPoint;
			Point hitPntT;
			Normal hitNmlT;
			//the hit point with the smaller t is closer to the camera.
			if (t0 < t1) {
				hitPoint = o.add(transformed.direction.scale(t0));
				hitPntT = this.transformation.transform(hitPoint.toPoint());
				hitNmlT = this.transformation.transformInverseTranspose(hitPoint.toNormal());
				hits.add(new Intersection(hitPntT,getUV(hitPoint.toPoint()),hitNmlT,this.material,this.reflectivity));
				return hits;
			} else {
				hitPoint = o.add(transformed.direction.scale(t1));
				hitPntT = this.transformation.transform(hitPoint.toPoint());
				hitNmlT = this.transformation.transformInverseTranspose(hitPoint.toNormal());
				hits.add(new Intersection(hitPntT,getUV(hitPoint.toPoint()),hitNmlT,this.material,this.reflectivity));
				return hits;
			}
		} else {
			// No intersection worth reporting
			return hits;
		}
	}
	
	@Override
	public TextPoint getUV(Point hitPoint) {
		double u,v, theta, phi;
		phi = Math.atan(hitPoint.y/hitPoint.x) + Math.PI/2;
		if (hitPoint.x < 0) {
			phi = phi + Math.PI;
		}
		u = phi/(Math.PI*2);
		theta = Math.acos(hitPoint.z);
		v = 1 - theta/(Math.PI);
		
		TextPoint txtPoint = new TextPoint(u,v);
		
		if (u > 1) {
			System.err.println("error u index to large.");
		}
		if (v > 1) {
			System.err.println("error v index to large.");
		}
		if (u < 0) {
			System.err.println("error u index to small.");
		}
		if (v < 0) {
			System.err.println("error v index to small.");
		}
		
		return txtPoint;
		
	}

	@Override
	public double getInverseArea() {
		Point test = new Point(1,1,1);
		test = this.transformation.transform(test);
		double area = Math.PI*(4/3)*(test.x*test.y*test.z);
		return 1/area;
	}

	@Override
	public Normal getNormal(Point pPrime) {
		pPrime = this.transformation.transformInverse(pPrime);
		Normal normal = pPrime.toNormal(); 
		normal = this.transformation.transformInverseTranspose(normal);
		return normal;
	}

	@Override
	public Material getMaterial() {
		return this.material;
	}

	@Override
	public Transformation getTransformation() {
		return this.transformation;
	}

	@Override
	public LightIntersection getRandomPoint(Point hP) {
		Vector vhP = hP.toVector();
		vhP = this.transformation.transform(new Vector(0,0,0)).subtract(vhP);
		hP = this.transformation.transformInverse(vhP.toPoint());
		double r = Math.sqrt(hP.x*hP.x + hP.y*hP.y + hP.z*hP.z);
		
		double theta = Math.acos(hP.z/r);
		double phi   = Math.atan2(hP.x,hP.y); //TODO: Fallunterscheidung warum nicht y,x?
		Random rnd = new Random();
		double rndTheta = -Math.PI/2 + Math.PI*rnd.nextDouble();
		double rndPhi   = -Math.PI/2 + Math.PI*rnd.nextDouble();
		
		theta = theta + rndTheta;
		phi   = phi   + rndPhi;
		//radius is 1 
		double randomX = Math.sin(theta)*Math.cos(phi);
		double randomY = Math.sin(theta)*Math.sin(phi);
		double randomZ = Math.cos(theta);
		
		Point pPrime = new Point(randomX,randomY,randomZ);
		TextPoint txtPnt = getUV(pPrime);
		
		pPrime = this.transformation.transform(pPrime);
		return new LightIntersection(txtPnt, pPrime);
	}

	@Override
	public boolean inShape(Point hitPoint) {
		hitPoint = this.transformation.transformInverse(hitPoint);
		double x = hitPoint.x;
		double y = hitPoint.y;
		double z = hitPoint.z;
		if (Math.abs(x*x + y*y + z*z) <= (1.0 + Constants.epsilon)) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
