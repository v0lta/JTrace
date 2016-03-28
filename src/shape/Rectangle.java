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

public class Rectangle extends Plane implements LightableShape {
	public final double width;
	public final double length;
	public final double inverseArea;

	/**
	 * Create a two dimensional rectangle
	 * @param transformation moves the rectangle away from the x-y-planes center.
	 * @param mat material of the rectangle.
	 * @param reflectivity how well it reflects light.
	 * @param length length of the rectangle
	 * @param width width of the rectangle
	 */
	
	public Rectangle(Transformation transformation, Material mat,
			double reflectivity, double length, double width) {
		super(transformation, mat, reflectivity);
		this.width = width;
		this.length = length;
		this.inverseArea = 1.0/(length*width);
	}
	
    /**
     * The ray object intersection function for a rectangles with transformations. 
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
                
                if (((Math.abs(pointVec.x) < this.length) && 
                	 (Math.abs(pointVec.y) < this.width))) {
                	Point hitPoint = this.transformation.transform( pointVec.toPoint() );
                    Normal hitNormal = this.transformation.transformInverseTranspose( this.n);
                    TextPoint txtPoint = new TextPoint(pointVec.x,pointVec.y); //the z coordinate of the unit plane is always zero.
                    Color hitClr = mat.getColor(txtPoint);
                    
                    hits.add(new Intersection( hitPoint, hitNormal, hitClr,reflectivity));
                    return hits;
                } else {
                	return hits;
                }
            }
        }
	}

	@Override
	public double getInverseArea() {
		return this.inverseArea;
	}
	
	@Override
	public Normal getNormal() {
		return this.transformation.transformInverseTranspose( this.n);
	}

}
