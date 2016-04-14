package shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import light.LightIntersection;
import material.Material;
import math.Constants;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
import math.Transformation;
import math.Vector;

public class Rectangle extends Plane implements LightableShape {
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
			double reflectivity) {
		super(transformation, mat, reflectivity);
		this.inverseArea = 1.0/(2*2);
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
                
                if (((Math.abs(pointVec.x) < 1.0) && 
                	 (Math.abs(pointVec.y) < 1.0))) {
                	Point hitPoint = this.transformation.transform( pointVec.toPoint() );
                    Normal hitNormal = this.transformation.transformInverseTranspose( this.n);
                    TextPoint txtPoint = new TextPoint(pointVec.x,pointVec.y); //the z coordinate of the unit plane is always zero.
                    Material hitMat = mat;
                    
                    hits.add(new Intersection( hitPoint, txtPoint, hitNormal, hitMat,reflectivity));
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
		return this.transformation.transformInverseTranspose(this.n);
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
			if (((Math.abs(hitPoint.x) < 1.0) && 
					(Math.abs(hitPoint.y) < 1.0))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	
	/**
	 * Get UV coordinates from point in world space.
	 */
	@Override
	public TextPoint getUV(Point pPrime) {
		//transform the point onto the z plane.
		pPrime = this.transformation.transformInverse(pPrime);
		//the z coordinate is zero and can be neglected.
		TextPoint txtPoint = new TextPoint(pPrime.x,pPrime.y);
		return txtPoint;
	}
	
	
	@Override
	public LightIntersection getRandomPoint(Point hitPoint) {
		Random r = new Random();
		double randomX = -1.0 + 2.0 * r.nextDouble();
		double randomY = -1.0 + 2.0  * r.nextDouble();		
		Point p = new Point(randomX,randomY,0);
		TextPoint txtPoint = new TextPoint(p.x,p.y);
		p = this.transformation.transform(p);
		Normal n = this.transformation.transformInverseTranspose(this.n);
		return new LightIntersection(txtPoint,p,n);
	}
	
	
	@Override
	public Point getCenter(){
		return this.transformation.transform(new Point(0,0,0));
	}

	@Override
	public List<LightableShape> subdivide(int subdivisions) {
		List<LightableShape> subShapes = new ArrayList<LightableShape>();
		//generate transformation matrices
		double frac = 1.0/(subdivisions+1);
		Transformation scale = Transformation.scale(frac, frac, frac);
		Transformation toCenterScale = Transformation.IDENTITY;
		double xStart;
		double xEnd;
		double yStart;
		double yEnd;
		double centroidX;
		double centroidY;
		//double step = ((double) subdivisions)/(subdivisions + 1.0);
		double step = 2.0/(subdivisions + 1);
		for (int i = 0; i < (subdivisions + 1); i++){
			xStart = -1 + i*step;
			xEnd = -1 + (i+1)*step;
			centroidX = (xStart + xEnd)/2;
			for (int j = 0; j < (subdivisions + 1); j++){
				yStart = -1 + j*step;
				yEnd = -1 + (j+1)*step;
				centroidY = (yStart + yEnd)/2;
				//toCenterScale = scale.append(Transformation.translate(centroidX, centroidY, 0));
				toCenterScale = Transformation.translate(centroidX, centroidY, 0).append(scale);
				Rectangle subRect = new Rectangle(toCenterScale, this.mat, this.reflectivity);
				subShapes.add(subRect);
			}
		}
		return subShapes;
	}
	


}
