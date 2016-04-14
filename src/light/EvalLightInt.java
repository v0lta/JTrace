package light;

import math.Normal;
import math.Point;
import math.TextPoint;

public class EvalLightInt extends LightIntersection {
	public final double G;
	public final double spec;
	public final double diff;
	
	public EvalLightInt(TextPoint txtPnt, Point pPrime, Normal nPrime,
						double G, double spec, double diff) {
		super(txtPnt, pPrime, nPrime);
		this.G = G;
		this.spec = spec;
		this.diff = diff;
	}

	public EvalLightInt(LightIntersection lightInt, double G, double spec, double diff) {
		super(lightInt.txtPnt, lightInt.pPrime, lightInt.nPrime);
		this.G = G;
		this.spec = spec;
		this.diff = diff;
	}
}
