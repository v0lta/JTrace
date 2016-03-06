package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.Vector;
import math.Normal;

/**
 *A class, which allows to intersect planes. 
 */
public class Plane implements Shape {
	public final Point a; //point a describing, where the plane is.
	public final Normal n; //normal n describing the plane's orientation.
	public final Material m; //the material describes the plane's coloring.
	public final double reflectivity; //determines how well the plane reflects light.
	
    public Plane(Point a,Normal n, Material m,double reflectivity) {
        this.a = a;  
        this.n = n;  
        this.m = m;
        this.reflectivity = reflectivity;
        
    }

    
    /**
     * The ray object intersection function for a planes without transformations. 
     */    
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		
    	Vector ro = ray.origin.toVector();
        Vector rd = ray.direction;
        
        double t;
        double denomDot = n.toVector().dot(rd); 
        
        if (Math.abs(denomDot) < Constants.epsilon){
            return hits;
        } else {
            t = (n.toVector().dot(a.toVector())) - (n.toVector().dot(ro)) / denomDot;
            if (t <= Constants.epsilon) {
                return hits;
            } else {
                Vector pointVec = ro.add(rd.scale(t));
                Color color = m.getColor(pointVec);
                hits.add(new Intersection(true, pointVec.toPoint(), n, color,reflectivity));
                return hits;
            }
        }
	}

}
