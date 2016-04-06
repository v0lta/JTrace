package light;

import java.util.List;

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
	public final double beta;
	
	/**
	 * Instantiate an area light of form shape.
	 * @param shape the geometric shape of the new light.
	 * @param intensity the brightness of the new light.
	 * @param sampleNo the monte-carlo integration samples.
	 * @param beta the allowed angle of incoming angles with the light normal.
	 */

	public AreaLight(LightableShape shape, double intensity, int sampleNo, double beta) {
		this.shape = shape;
		this.mat = shape.getMaterial();
		this.intensity = intensity;
		this.sampleNo = sampleNo;
		this.beta = (180 - beta) * (Math.PI/180);
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
		TextPoint txtPoint = this.shape.getUV(pPrime);
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
		
		double G = (cosThetaI * cosThetaPrime)/lengthSquared;
		
		nPrime = this.getNormal(pPrime).toVector();
		double betaTest = Math.acos(toLight.dot(nPrime));
		if (betaTest < beta){
			//G = 0;
		}
		if (G < 0){
			G = 0;
		}
		
		return G;
	}
	
	public LightIntersection getpPrime(Point hitPoint){
		LightIntersection pPrime = this.shape.getRandomPoint(hitPoint);
		return pPrime;
	}

	@Override
	public List<Intersection> intersect(Ray ray) {
		return this.shape.intersect(ray);
	}

}
