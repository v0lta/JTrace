package light;

import java.util.List;
import java.util.Random;

import shape.LightableShape;
import shape.Shape;
import material.Material;
import math.Color;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Vector;

public class AreaLight implements Shape  {
	public final LightableShape shape;
	public final Material mat;
	public final double intensity;
	public final int sampleNo;

	public AreaLight(LightableShape shape, double intensity, int sampleNo) {
		this.shape = shape;
		this.mat = shape.getMaterial();
		this.intensity = intensity;
		this.sampleNo = sampleNo;
	}
	
	public double pdf(){
		return this.shape.getInverseArea();
	}
	
	public Normal getNormal(Point pPrime) {
		return this.shape.getNormal(pPrime);
	}
	
	/**
	 * Get the normalized vector pointing to the light source	
	 */
	public Vector l(Vector hitPoint, Vector pPrime) {
		Vector toLight = pPrime.subtract(hitPoint);
		toLight = toLight.normalize();
		return toLight;
	}
	
	/**
	 * Get the lights color scaled by its intensity.
	 */	
	public Vector L(Point pPrime) {
		//transform the point onto the z plane.
		pPrime = this.shape.getTransformation().transformInverse(pPrime);
		//the z coordinate is zero and can be neglected.
		TextPoint txtPoint = new TextPoint(pPrime.x,pPrime.y);
		Color color = this.mat.getColor(txtPoint);
		return color.toVector().scale(intensity);
	}
	
	public double G(Intersection intersection, Point pPrime){
		Point p = intersection.point;
		Vector toLight = pPrime.toVector().subtract(p.toVector());
		double lengthSquared = toLight.lengthSquared(); 
		toLight = toLight.normalize();
		Vector np = intersection.normal.toVector();
		Vector nPrime = this.getNormal(pPrime).toVector();
		double cosThetaI = np.dot(toLight);
		double cosThetaPrime = nPrime.dot(toLight.scale(-1));
		
		return (cosThetaI * cosThetaPrime)/lengthSquared;
	}
	
	public Point getpPrime(){
		Point pPrime = this.shape.getRandomPoint();
		return pPrime;
	}

	@Override
	public List<Intersection> intersect(Ray ray) {
		return this.shape.intersect(ray);
	}
	
	

}
