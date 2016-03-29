package light;

import shape.LightableShape;
import material.Material;
import math.Color;
import math.Intersection;
import math.Normal;
import math.Point;
import math.TextPoint;
import math.Vector;

public class AreaLight {
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
	
	public Normal getNormal() {
		return this.shape.getNormal();
	}
	
	/**
	 * Get the normalized vector pointing to the light source	
	 */
	public Vector l(Point hitPoint, Point pPrime) {
		Vector toLight = pPrime.toVector().subtract(hitPoint.toVector());
		toLight = toLight.normalize();
		return toLight;
	}
	
	/**
	 * Get the lights color scaled by its intensity.
	 */	
	public Vector L(Point pPrime) {
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
		Vector nPrime = this.getNormal().toVector();
		double cosThetaI = np.dot(toLight);
		double cosThetaPrime = nPrime.dot(toLight.scale(-1));
		
		return (cosThetaI * cosThetaPrime)/lengthSquared;
	}
	
	

}
