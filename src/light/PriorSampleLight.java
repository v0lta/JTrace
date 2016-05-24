package light;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.Renderer;
import math.Color;
import math.Intersection;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Vector;
import shape.LightableShape;
import camera.Camera;


public class PriorSampleLight extends AreaLight {
	public final int subdivisions;
	public List<LightableShape> subLights;
	private int subLightNo;
	public final double subIntensity;
	
	/**
	 * Creates an area light with a subdivided surface, which
	 * can be importance sampled.
	 * @param shape    the shape of the area light
	 * @param intensity the area light's intensity
	 * @param sampleNo the number of sampled for the entire light
	 * @param subdivisions the number of parts the light will be split into.
	 */
	public PriorSampleLight(LightableShape shape, double intensity,
			int sampleNo, int subdivisions) {
		super(shape, intensity, sampleNo);
		
		this.subdivisions = subdivisions;
		subLights = shape.subdivide(subdivisions);
		this.subLightNo = (this.subdivisions +1 ) * (this.subdivisions +1 );
		if (subLights == null){
			System.err.println("shape type not supported.");
		}
		//if (sampleNo  < ((subdivisions + 1)*(subdivisions + 1))){
		//	System.err.println("Number of samples must be greater than (number of subdivisions + 1)^2.");
		//}
		this.subIntensity = this.intensity;
	}
	
	@Override
	public List<EvalLightInt> getpPrime(Intersection inter, Camera cam){
		List<EvalLightInt> lightSamples = new ArrayList<EvalLightInt>();
		
		//compute the g factor for all the sub light sources.
		double[] funArray = new double[subLights.size()];
		double funTot = 0;
		for (int i = 0; i < subLights.size(); i++){
			LightableShape current = subLights.get(i);
			double prob = current.getInverseArea();
			
			Point p = inter.point;
			Vector N = inter.normal.toVector();
			Vector V = cam.getOrigin().subtract(inter.point).normalize();
			LightIntersection currentP = current.getRandomPoint(p);
			//Evaluate 
			double G = this.G(inter, currentP.pPrime);
			Vector L = currentP.pPrime.subtract(p).normalize();
			double spec = inter.mat.getSpecular(N, L, V);
			double diff = inter.mat.getDiffuse(N, L);

			//EvalLightInt evlInt = new EvalLightInt(currentP,G,spec, diff, current.getInverseArea());
			//AreaLight currentAI = new AreaLight(current, this.intensity, this.sampleNo/this.subLightNo);
			//Vector testRes = Renderer.computeAlShading(inter, currentAI, evlInt, cam);
			//funArray[i] = (testRes.x + testRes.y + testRes.z)/3;
			
			funArray[i] = G*(diff + spec);
			//funArray[i] = evlInt.diff + evlInt.spec;
			//funArray[i] = 1;
			funTot = funTot + funArray[i];
		}
		
		int remainingSamples = this.sampleNo - lightSamples.size();
		if (remainingSamples < 0) {
			System.err.println("remaining samples negative.");
			System.err.println(remainingSamples);
		}
		
		//check if the probs add up to one.
		double test = 0;
		double[] pjArray = new double[subLights.size()];
		boolean uniform = false;
		for (int i = 0; i < subLights.size(); i++){
			pjArray[i] = funArray[i]/funTot;
			test = test + pjArray[i];			
		}
		//debug array.
		int[] IArray = new int[this.sampleNo];
		
		// if that is not the case use uniform sampling
		//System.out.println(test);
		if (Double.isNaN(test) || Double.isInfinite(test)) {
			uniform = true;
		}
		if (uniform){
			//without a probability density function there is nothing we can do.
			//return super.getpPrime(inter, cam);
			return lightSamples;
		} else {
			//do priority sampling.
			for (int j = 0; j < this.sampleNo; j++){
				Random uniformDist = new Random();
				
				double u = uniformDist.nextDouble();
				
				//find the index I.
				int I = -1;
				double tmp = 0;
				double prev = 0;
				for (int i = 0; i < this.subLightNo; i++){
					prev = tmp;	
					tmp  = tmp + pjArray[i];	 
					if ((prev <= u) && (u < tmp)) {
						I = i;
						break;
					}		
				}
				IArray[j] = I;
				
				if (I == -1){
					System.err.println("I not assined!!!");
				}
								
				// use I the pick a sublight.
				LightableShape current = this.subLights.get(I);
				
				EvalLightInt sample = evaluate(inter,current, cam,  ((current.getInverseArea())*(pjArray[I])));
				//EvalLightInt sample = evaluate(inter,current, cam, current.getInverseArea()*(1.0/pjArray[I]));	
				lightSamples.add(sample);
			}			
			return lightSamples;
		}
	}
	
	
//	/**
//	 * Get the sublights light's color scaled by its intensity.
//	 */	
//	@Override
//	public Vector L(Point pPrime) {	
//		TextPoint txtPoint = super.shape.getUV(pPrime);
//		Color color = super.mat.getColor(txtPoint);
//		return color.toVector().scale(this.subIntensity);
//	}
	
	
	public List<LightableShape> getSubLightShapes(){
		return this.subLights;
	}
	
	public EvalLightInt evaluate(Intersection inter,LightableShape currentShape, Camera cam, double pVal){
		Point p = inter.point;
		Vector N = inter.normal.toVector();
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		LightIntersection currentP = currentShape.getRandomPoint(p);
		//Evaluate 
		double G = this.G(inter, currentP.pPrime);
		Vector L = currentP.pPrime.subtract(p).normalize();
		double spec = inter.mat.getSpecular(N, L, V);
		double diff = inter.mat.getDiffuse(N, L);
		EvalLightInt evlInt = new EvalLightInt(currentP,G,spec, diff, pVal);
		return evlInt;
	}
	
}
