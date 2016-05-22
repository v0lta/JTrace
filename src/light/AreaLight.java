package light;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
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
	
	/**
	 * Instantiate an area light with a shape.
	 * @param shape the geometric shape of the new light.
	 * @param intensity the brightness of the new light.
	 * @param sampleNo the monte-carlo integration samples.
	 */
	public AreaLight(LightableShape shape, double intensity, int sampleNo) {
		this.shape = shape;
		this.mat = shape.getMaterial();
		this.intensity = intensity;
		this.sampleNo = sampleNo;
	}
	
	/**
	 * Pdf for uniformly distributed samples.
	 * @return the pdf value for any sample from this sight sources surface.
	 */
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
	 * Get the light's color scaled by its intensity.
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
		if (G < 0){
			G = 0;
		}
		return G;
	}
	
	public List<EvalLightInt> getpPrime(Intersection inter, Camera cam){
		Point p = inter.point;
		Vector N = inter.normal.toVector();
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		
		List<EvalLightInt> lightSamples = new ArrayList<EvalLightInt>();
		for (int i = 0; i < this.sampleNo; i++) {
			//create random number generator with seed for reproducibility.
			LightIntersection lghtInt = this.shape.getRandomPoint(p);
			Point pPrime = lghtInt.pPrime;
			double G = this.G(inter, pPrime);
			Vector L = pPrime.subtract(p).normalize();
			double spec = inter.mat.getSpecular(N, L, V);
			double diff = inter.mat.getDiffuse(N, L);
			EvalLightInt evlInt = new EvalLightInt(lghtInt,G, spec, diff , this.shape.getInverseArea());
			lightSamples.add(evlInt);
		}
		return lightSamples;
	}

	@Override
	public List<Intersection> intersect(Ray ray) {
		return this.shape.intersect(ray);
	}

}
