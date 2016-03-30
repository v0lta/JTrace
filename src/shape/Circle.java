package shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class Circle extends Plane implements LightableShape {
	public final double radius;
	public final double inverseArea;
	
	public Circle(Transformation transformation, Material mat,
			double reflectivity, double radius) {
		super(transformation, mat, reflectivity);
		this.radius = radius;
		this.inverseArea = 1.0/(Math.PI * radius * radius);
	}
	
	 /**
     * The ray object intersection function for a circles with transformations. 
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
                
                double x = pointVec.x;
                double y = pointVec.y;
                if (((Math.sqrt( x*x + y*y) < this.radius))) {
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
	public Normal getNormal(Point pPrime) {
		return this.transformation.transformInverseTranspose( this.n );
	}
	
	@Override
	public Material getMaterial() {
		return this.mat;
	}
	
	@Override 
	public Transformation getTransformation() {
		return this.transformation;
	}
	
	/**
	 * Learn if a transformed hit point is on the surface.
	 */
	public boolean inShape(Point hitPoint) {
		hitPoint = this.transformation.transformInverse(hitPoint);
		if (Math.abs(hitPoint.z) < Constants.epsilon){
			if (((Math.sqrt( hitPoint.x*hitPoint.x +
					         hitPoint.y*hitPoint.y) < this.radius))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Get a transformed random point on the circles
	 * surface.
	 */
	@Override
	public Point getRandomPoint() {
		Random r = new Random();
		double rndR = this.radius * r.nextDouble();
		double rndAngle = (2*Math.PI)  * r.nextDouble();
		
		double randomX = Math.cos(rndAngle)*rndR;
		double randomY = Math.sin(rndAngle)*rndR;
		Point p = new Point(randomX,randomY,0.0);
		p = this.transformation.transform(p);
		return p;
	}
	
}