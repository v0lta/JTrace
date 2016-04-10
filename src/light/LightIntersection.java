package light;

import math.Normal;
import math.Point;
import math.TextPoint;

public class LightIntersection {
	public final TextPoint txtPnt;
	public final Point pPrime;
	public final Normal nPrime;
	
	public LightIntersection(TextPoint txtPnt, Point pPrime, Normal nPrime){
		this.txtPnt = txtPnt;
		this.pPrime = pPrime;
		this.nPrime = nPrime;
	}

}
