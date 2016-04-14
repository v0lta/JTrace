package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Constants;
import math.Intersection;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;
import math.Normal;

/**
 *A class, which allows to intersect planes. 
 */
public class Plane implements Shape {
	public final Point a = new Point(0,0,0); //point a describing, where the plane is.
	public final Normal n = new Normal(0,0,1); //normal n describing the plane's orientation.
	public final Transformation transformation;
	public final Material mat; //the material describes the plane's coloring.
	
    public Plane(Transformation transformation, Material mat) {
    	this.transformation = transformation;
        this.mat = mat;
    }

    
    /**
     * The ray object intersection function for a planes with transformations. 
     */    
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		Vector ro; 
        Vector rd;
        
        if (Constants.compVisualization){
        	ray.countIntersection();
        }
        
        Ray rayInv = this.transformation.transformInverse(ray);
        ro = rayInv.origin.toVector();
        rd = rayInv.direction;

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
                
                Point hitPoint = this.transformation.transform( pointVec.toPoint() );
                Normal hitNormal = this.transformation.transformInverseTranspose( this.n);
                TextPoint txtPoint = new TextPoint(pointVec.x,pointVec.y); //the z coordinate of the unit plane is always zero.
                Material hitMat = mat;
                hits.add(new Intersection( hitPoint, txtPoint, hitNormal, hitMat));
                return hits;
            }
        }
	}

}
