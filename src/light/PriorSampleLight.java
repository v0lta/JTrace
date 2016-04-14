package light;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Vector;
import shape.LightableShape;


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
			int sampleNo, int subdivisions) {
		super(shape, intensity, sampleNo);
		this.subdivisions = subdivisions;
		subLights = shape.subdivide(subdivisions);
		if (subLights == null){
			System.err.println("shape type not supported.");
		}
		if (sampleNo  < subdivisions){
			System.err.println("Number of samples must be greater then nuber of subdivisions.");
		}
	}
	
	@Override
	public List<EvalLightInt> getpPrime(Intersection inter, Camera cam){
		List<EvalLightInt> lightSamples = new ArrayList<EvalLightInt>();
		
		Point p = inter.point;
		Vector N = inter.normal.toVector();
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		//compute the g factor for all the sub light sources.
		double[] funArray = new double[subLights.size()];
		double funTot = 0;
		for (int i = 0; i < subLights.size(); i++){
			LightableShape current = subLights.get(i);
			LightIntersection subSample = transformLightInt(current.getRandomPoint(p));
			Point pPrime = subSample.pPrime;
			double G = this.G(inter, pPrime);
			Vector L = pPrime.subtract(p).normalize();
			double spec = inter.mat.getSpecular(N, L, V);
			double diff = inter.mat.getDiffuse(N, L);
			
			//funArray[i] = p.subtract(pPrime).length();
			funArray[i] = G*(spec + diff);
			//funArray[i] = spec;
			funTot = funTot + funArray[i];
			lightSamples.add(new EvalLightInt(subSample.txtPnt, pPrime, subSample.nPrime,
							G, spec, diff));
		}
		int remainingSamples = this.sampleNo - lightSamples.size();
		//compute and generate the samples for each subsource.
		for (int i = 0; i < subLights.size(); i++){
			LightableShape current = subLights.get(i); 
			double scale = funArray[i]/funTot;
			int n = (int) Math.round((remainingSamples) * scale);

			for (int m = 0; m < n; m++){
				LightIntersection pWorldSpace = transformLightInt(current.getRandomPoint(p));
				//Evaluate 
				Point pPrime = pWorldSpace.pPrime;
				double G = this.G(inter, pPrime);
				Vector L = pPrime.subtract(p).normalize();
				double spec = inter.mat.getSpecular(N, L, V);
				double diff = inter.mat.getDiffuse(N, L);
				EvalLightInt evlInt = new EvalLightInt(pWorldSpace,G,spec, diff);
				lightSamples.add(evlInt);
			}
		}
		//System.out.println(lightSamples.size());
		return lightSamples;
	}
	
	private LightIntersection transformLightInt(LightIntersection lightInt){
		Point pPrime = this.shape.getTransformation().transform(lightInt.pPrime);
		Normal nPrime = this.shape.getTransformation().transformInverseTranspose(lightInt.nPrime);
		TextPoint txtPnt = this.shape.getUV(pPrime);
		return new LightIntersection(txtPnt,pPrime,nPrime);
	}
	
	
	//@Override
	public List<Intersection> intersect2(Ray ray) {
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
        return new Intersection( hitPoint, planeIntersection.txtPnt, hitNormal, inter.mat);
	}
}
