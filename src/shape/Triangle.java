package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;

/**
 * The triangle primitive.
 * @author moritz
 *
 */

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
	public final Transformation transformation;

	/**
	 * Create a triangle. 
	 * @param a triangle edge.
	 * @param b triangle edge.
	 * @param c triangle edge.
	 * @param an normal at triangle edge.
	 * @param bn normal at triangle edge.
	 * @param cn normal at triangle edge.
	 * @param at texture coordinate at triangle edge.
	 * @param bt texture coordinate at triangle edge.
	 * @param ct texture coordinate at triangle edge.
	 * @param mat triangle material.
	 * @param transformation matrix scaling, relocating and re-orienting the triangle. 
	 */	
	public Triangle (Point a, Point b, Point c,
			Normal an, Normal bn, Normal cn,
			TextPoint at, TextPoint bt, TextPoint ct, Material mat,
			Transformation transformation) {
		
		this.a = a;
		this.b = b;
		this.c = c;
		if ((an != null) && (bn != null) && (bn != null)) {
			this.an = an;
			this.bn = bn;
			this.cn = cn;
		} else {
			// np.cross((self.c -self.a), (self.b - self.a))
			Normal normal = b.subtract(a).cross(c.subtract(a)).scale(
					1/(b.subtract(a).cross(c.subtract(a)).length())).toNormal();
			this.an = normal;
			this.bn = normal;
			this.cn = normal;
		}
		this.at = at;
		this.bt = bt;
		this.ct = ct;
		this.mat = mat;
		this.transformation = transformation;		
	}
	
	public Point getCentroid(){
		double cx = (1.0/3.0) * ((this.a.x) + (this.b.x) + (this.c.x));
		double cy = (1.0/3.0) * ((this.a.y) + (this.b.y) + (this.c.y));
		double cz = (1.0/3.0) * ((this.a.z) + (this.b.z) + (this.c.z));
		return new Point(cx,cy,cz);
	}
	
	public double getLargestCoord(Character axis){
		switch (axis) {
		case 'x':
			if ((a.x >= b.x) && (a.x >= c.x)) {
				return a.x;
			}
			else if ((b.x >= a.x) && (b.x >= c.x)) {
				return b.x;
			} else {
				return c.x;
			}
		case 'y':
			if ((a.y >= b.y) && (a.y >= c.y)) {
				return a.y;
			}
			else if ((b.y >= a.y) && (b.y >= c.y)) {
				return b.y;
			} else {
				return c.y;
			}
		case 'z':
			if ((a.z >= b.z) && (a.z >= c.z)) {
				return a.z;
			}
			else if ((b.z >= a.z) && (b.z >= c.z)) {
				return b.z;
			} else {
				return c.z;
			}
		default:
			System.err.println("Illegal axis in Triangle.largestCoord.");
			return 0;			
		}		
	}
	
	
	public double getSmallestCoord(Character axis){
		switch (axis) {
		case 'x':
			if ((a.x <= b.x) && (a.x <= c.x)) {
				return a.x;
			}
			else if ((b.x <= a.x) && (b.x <= c.x)) {
				return b.x;
			} else {
				return c.x;
			}
		case 'y':
			if ((a.y <= b.y) && (a.y <= c.y)) {
				return a.y;
			}
			else if ((b.y <= a.y) && (b.y <= c.y)) {
				return b.y;
			} else {
				return c.y;
			}
		case 'z':
			if ((a.z <= b.z) && (a.z <= c.z)) {
				return a.z;
			}
			else if ((b.z <= a.z) && (b.z <= c.z)) {
				return b.z;
			} else {
				return c.z;
			}
		default:
			System.err.println("Illegal axis in Triangle.largestCoord.");
			return 0;			
		}		
	}
	
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		
		if (Constants.compVisualization){
			ray.countIntersection();
		}
		
		Point  ro; 
        Vector rd;
        
		Ray rayInv = this.transformation.transformInverse(ray);
	    ro = rayInv.origin;
	    rd = rayInv.direction;
		
		Point pa = this.a; Point pb = this.b; Point pc = this.c;
        double a,b,c,d,e,f,g,h,i,j,k,l,m,n,p,s,q,r,invDenom,e1,e2,e3,beta,gamma,t;
         
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
        
        hitNormVec = an.scale(1.0 - beta - gamma).add(bn.scale(beta)).add(cn.scale(gamma));
        hitNormal = this.transformation.transformInverseTranspose( hitNormVec.toNormal());
        
        hitTxt = at.scale(1 - beta - gamma).add(bt.scale(beta)).add(ct.scale(gamma));
        hits.add(new Intersection(hitPoint, hitTxt, hitNormal, this.mat));
        return hits;
	}

}
