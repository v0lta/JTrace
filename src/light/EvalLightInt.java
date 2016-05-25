package light;

import math.Normal;
import math.Point;
import math.TextPoint;
import math.Vector;

public class EvalLightInt extends LightIntersection {
	public final double G;
	public final double spec;
	public final double diff;
	public final double prob;
	public final Vector Lp;
	
	
	/**
	 * Create an intersection object with evaluated entries for key quantieties.
	 * @param txtPnt the texture coordinate at the hit point.
	 * @param pPrime point on the light source.
	 * @param nPrime normal on the light source.
	 * @param G geometric term.
	 * @param spec specular part of the brdf.
	 * @param diff diffuse art of the brdf.
	 * @param prob inverse local probability density function value for sample numbers p_j.
	 * @param Lp intensity scaled Area light color.
	 */	
	public EvalLightInt(TextPoint txtPnt, Point pPrime, Normal nPrime,
						double G, double spec, double diff, Vector Lp, double prob) {
		super(txtPnt, pPrime, nPrime);
		this.G = G;
		this.spec = spec;
		this.diff = diff;
		this.prob = prob;
		this.Lp = Lp;
	}

	/**
	 * Create an intersection object with evaluated entries for key quantieties.
	 * @param lightInt light intersection object without evaluated values.
	 * @param G geometry term.
	 * @param spec secular brdf term.
	 * @param diff diffuse part of the brdf
	 * @param prob inverse local probability density function value for sample numbers p_j..
	 * @param Lp intensity scaled Area light color.
	 */
	public EvalLightInt(LightIntersection lightInt, double G, double spec,
				double diff, Vector Lp, double prob) {
		super(lightInt.txtPnt, lightInt.pPrime, lightInt.nPrime);
		this.G = G;
		this.spec = spec;
		this.diff = diff;
		this.prob = prob;
		this.Lp = Lp;
	}
}
