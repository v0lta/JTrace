package material;

import math.Vector;

public class PhongSpecular implements Specular {
	public final double n; //phong exponent
	public final double rhos; //scaling
	
	public PhongSpecular(double p0,double p1, double rhos){
		this.n = p0;
		this.rhos = rhos;
	}

	@Override
	public double getSpecular(Vector N, Vector L, Vector V) {
		Vector tmp = N.scale(N.dot(L)).scale(2);
		Vector R = L.scale(-1).add(tmp); 
		R = R.normalize();
		double dot = R.dot(V);
		double	res = 0;
		if (N.dot(V) > 0){
			if (dot > 0){
				res = rhos*((n+2)/(2*Math.PI))*Math.pow(dot, n);
			}
		}
		return res;
	}

}
