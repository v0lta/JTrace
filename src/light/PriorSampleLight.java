package light;

import java.util.ArrayList;
import java.util.List;

import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import shape.LightableShape;
import shape.Rectangle;
import shape.Shape;

public class PriorSampleLight extends AreaLight {
	public final int subdivisions;
	public List<LightableShape> subLights;
	
	/**
	 * Creates an area light with a subdivided surface, which
	 * can be importance sampled.
	 * @param shape    the shape of the area light
	 * @param intensity the area light's intensity
	 * @param sampleNo the number of sampled for the entire light
	 * @param beta allowed light angle normal.
	 * @param subdivisions the number of parts the light will be split into.
	 */
	public PriorSampleLight(LightableShape shape, double intensity,
			int sampleNo, double beta, int subdivisions) {
		super(shape, intensity, sampleNo, beta);
		this.subdivisions = subdivisions;
		subLights = shape.subdivide(subdivisions);
		if (subLights == null){
			System.err.println("shape type not supported.");
		}
	}
	
	@Override
	public List<LightIntersection> getpPrime(Intersection inter){
		List<LightIntersection> lightSamples = new ArrayList<LightIntersection>();
		
		Point hitPoint = inter.point;
		//compute the g factor for all the sub light sources.
		double[] gArray = new double[subLights.size()];
		double gTot = 0;
		for (int i = 0; i < subLights.size(); i++){
			LightableShape current = subLights.get(i);
			Point pPrime = this.shape.getTransformation().transform(current.getCenter());
			gArray[i] = this.G(inter, pPrime);
			gTot = gTot + gArray[i];
			
		}
		//compute and generate the samples for each subsource.
		for (int i = 0; i < subLights.size(); i++){
			LightableShape current = subLights.get(i); 
			double scale = gArray[i]/gTot;
			int n = (int) Math.round(this.sampleNo * scale);
			
			//if (n != 0){
			//	System.out.println(n);
			//}
			
			//TODO: remove.
			//n = 10;
			for (int m = 0; m < n; m++){
				LightIntersection pWorldSpace = transformLightInt(current.getRandomPoint(hitPoint));
				lightSamples.add(pWorldSpace);
			}
		}
		//System.out.println(lightSamples.size());
		return lightSamples;
	}
	
	private LightIntersection transformLightInt(LightIntersection lightInt){
		TextPoint txtPnt = lightInt.txtPnt;
		Point pPrime = this.shape.getTransformation().transform(lightInt.pPrime);
		Normal nPrime = this.shape.getTransformation().transformInverseTranspose(lightInt.nPrime);
		return new LightIntersection(txtPnt,pPrime,nPrime);
		
	}
	
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		
		Ray rayInv = this.shape.getTransformation().transformInverse(ray);
		List<Intersection> inters = new ArrayList<Intersection>();
		for (LightableShape subShape: this.subLights) {
			inters.addAll(subShape.intersect(rayInv));
		}
		//Transform intersections
		List<Intersection> intersTransformed = new ArrayList<Intersection>();
		for (Intersection inter: inters) {
			intersTransformed.add(TransformIntersection(inter, ray));
		}
		return intersTransformed;
	}
	
	private Intersection TransformIntersection(Intersection inter, Ray ray) {
		Intersection planeIntersection = this.shape.intersect(ray).get(0);
        Point hitPoint = this.shape.getTransformation().transform( inter.point );
        Normal hitNormal = this.shape.getTransformation().transformInverseTranspose( inter.normal);
        return new Intersection( hitPoint, planeIntersection.txtPnt, hitNormal, inter.mat, inter.reflectivity);
	}
	

}
