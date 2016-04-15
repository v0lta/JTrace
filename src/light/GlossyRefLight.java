package light;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.Intersection;
import math.Point;
import math.Ray;
import math.Vector;
import camera.Camera;
import shape.LightableShape;


/*
 * VEBUGGT!!!
 */

public class GlossyRefLight extends AreaLight {

	public GlossyRefLight(LightableShape shape, double intensity, int sampleNo) {
		super(shape, intensity, sampleNo);
	}
	
	@Override
	public List<EvalLightInt> getpPrime(Intersection inter, Camera cam){
		Point p = inter.point;
		Vector N = inter.normal.toVector();
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		Vector lDir = N.scale(N.dot(V)).add(V.scale(-1));
		Ray specRay = new Ray(p,lDir);
		List<Intersection> specInt = this.shape.intersect(specRay);
		List<EvalLightInt> lightSamples = new ArrayList<EvalLightInt>();
		if (specInt.isEmpty() == false ){
			//for (int i = 0; i < this.sampleNo; i++) {
				//Random r = new Random();
				//Vector noise = new Vector(0.01*r.nextFloat(),0.01*r.nextFloat(),0.01*r.nextFloat());
				Intersection sInt =  specInt.get(0);
				LightIntersection lghtInt = new LightIntersection(sInt.txtPnt,sInt.point,sInt.normal);
				Point pPrime = lghtInt.pPrime;
				//pPrime = pPrime.add(noise);
				//double G = this.G(inter, pPrime);
				double G = 1;
				Vector L = pPrime.subtract(p).normalize();
				double spec = inter.mat.getSpecular(N, L, V);
				double diff = inter.mat.getDiffuse(N, L);
				EvalLightInt evlInt = new EvalLightInt(lghtInt,G, spec, diff);
				lightSamples.add(evlInt);
			//}
		}
		return lightSamples;
		
		/*
		List<EvalLightInt> lightSamples = new ArrayList<EvalLightInt>();
		for (int i = 0; i < this.sampleNo; i++) {
			//create random number generator with seed for reproducibility.
			LightIntersection lghtInt = this.shape.getRandomPoint(p);
			Point pPrime = lghtInt.pPrime;
			double G = this.G(inter, pPrime);
			Vector L = pPrime.subtract(p).normalize();
			double spec = inter.mat.getSpecular(N, L, V);
			double diff = inter.mat.getDiffuse(N, L);
			EvalLightInt evlInt = new EvalLightInt(lghtInt,G, spec, diff);
			lightSamples.add(evlInt);
		}
		return lightSamples;
		*/
	}

}
