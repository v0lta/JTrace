package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import material.ObjTextureFile;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;

/**
 * Modified triangle primitive with normal lookup from
 * a file.
 * @author moritz
 *
 */
public class TriangleWithNrmlMap extends Triangle {
	ObjTextureFile nMapFile;

	public TriangleWithNrmlMap(ObjTextureFile nMapFile,Point a, Point b, Point c, Normal an, Normal bn,
			Normal cn, TextPoint at, TextPoint bt, TextPoint ct, Material mat,
			Transformation transformation) {
		super(a, b, c, an, bn, cn, at, bt, ct, mat, transformation);
		this.nMapFile = nMapFile;
	}
	
	public TriangleWithNrmlMap(ObjTextureFile nMapFile,Triangle tri) {
		super(tri.a, tri.b, tri.c, tri.an, tri.bn, tri.cn, tri.at, tri.bt,
			  tri.ct, tri.mat, tri.transformation);
		this.nMapFile = nMapFile;
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
        Normal hitNormal;
        TextPoint hitTxt;
        
        hitPoint = ro.add(rd.scale(t)); 
        hitPoint = this.transformation.transform( hitPoint );
        
        hitTxt = at.scale(1 - beta - gamma).add(bt.scale(beta)).add(ct.scale(gamma));
        
        Color nrmlClr = nMapFile.getColor(hitTxt);
        hitNormal = new Normal(-1.0 + 2*(nrmlClr.r/255), -1.0 + 2*(nrmlClr.g/255), -1.0 + 2*(nrmlClr.b/255));
        hitNormal = this.transformation.transformInverseTranspose( hitNormal);
        
        hits.add(new Intersection(hitPoint, hitTxt, hitNormal, this.mat));
        return hits;
	}
	
	
	
}
