package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;

public class Triangle implements Shape {

	public final Point a;
	public final Point b;
	public final Point c;
	public final Normal an;
	public final Normal bn;
	public final Normal cn;
	public final TextPoint at;
	public final TextPoint bt;
	public final TextPoint ct;
	public final Material mat;
	public final double reflectivity;
	public final Transformation transformation;

	
	
	public Triangle (Point a, Point b, Point c,
			Normal an, Normal bn, Normal cn,
			TextPoint at, TextPoint bt, TextPoint ct, Material mat,
			double reflectivity, Transformation transformation) {
		
		this.a = a;
		this.b = b;
		this.c = c;
		if ((an != null) && (bn != null) && (bn != null)) {
			this.an = an;
			this.bn = bn;
			this.cn = cn;
		} else {
			// np.cross((self.c -self.a), (self.b - self.a))
			this.an = c.subtract(a).cross(b.subtract(a)).toNormal();
			//self.bn = np.cross((self.a - self.b), (self.c -self.b))
			this.bn = a.subtract(b).cross(c.subtract(b)).toNormal();
			//np.cross((self.a - self.c), (self.b -self.c))
			this.cn = a.subtract(c).cross(b.subtract(c)).toNormal();			
		}
		this.at = at;
		this.bt = bt;
		this.ct = ct;
		this.mat = mat;
		this.reflectivity = reflectivity;
		this.transformation = transformation;		
	}
	
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
        Point pa = this.a; Point pb = this.b; Point pc = this.c;
        double a,b,c,d,e,f,g,h,i,j,k,l,m,n,p,s,q,r,invDenom,e1,e2,e3,beta,gamma,t;
        Point  ro; 
        Vector rd;
        
        Ray rayInv = this.transformation.transformInverse(ray);
        ro = rayInv.origin;
        rd = rayInv.direction;
        
        a = pa.x - pb.x; b = pa.x - pc.x; c = rd.x; d = pa.x - ro.x;
        e = pa.y - pb.y; f = pa.y - pc.y; g = rd.y; h = pa.y - ro.y;
        i = pa.z - pb.z; j = pa.z - pc.z; k = rd.z; l = pa.z - ro.z;
        
        m = f*k - g*j; n = h*k - g*l; p = f*l - h*j;
        q = g*i - e*k; s = e*j - f*i;
        
        invDenom = 1.0 / (a*m + b*q + c*s);
        
        e1 = d*m - b*n - c*p;
        beta = e1 * invDenom;
        
        if (beta < 0.0) {
            return hits;
        }
            		
        r = e*l - h*i;
        e2 = a*n + d*q + c*r;
        gamma = e2*invDenom;
        
        if (gamma < 0.0) {
        	return hits;
        }
        
        if (beta + gamma > 1.0) {
        	return hits;
        }
        
        e3 = a*p - b*r + d*s;
        t = e3 * invDenom;
        
        if (t < Constants.epsilon) {
        	return hits;
        }
        
        Point hitPoint;
        Vector hitNormVec;
        Normal hitNormal;
        TextPoint hitTxt;
        
        Vector an = this.an.toVector();
        Vector bn = this.bn.toVector();
        Vector cn = this.cn.toVector();
        
        hitPoint = ro.add(rd.scale(t)); 
        hitPoint = this.transformation.transform( hitPoint );
        
        hitNormVec = an.scale(1 - beta - gamma).add(bn.scale(beta)).add(cn.scale(gamma));
        hitNormal = this.transformation.transformInverseTranspose( hitNormVec.toNormal());
        
        hitTxt = at.scale(1 - beta - gamma).add(bt.scale(beta)).add(ct.scale(gamma));
        Color hitClr = mat.getColor(hitTxt);
        
        hits.add(new Intersection(true, hitPoint, hitNormal, hitClr,this.reflectivity));
        return hits;
	}

}
