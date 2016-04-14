package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Intersection;
import math.Normal;
import math.Point;
import math.Ray;
import math.Transformation;

public class Cube implements Shape {
	public final Point center = new Point(0,0,0);
	public final Transformation transformation;
	public final Material mat;
	public final List<Rectangle> rects;
	
	public Cube(Transformation transformation, Material mat){
		this.transformation = transformation;
		this.rects = new ArrayList<Rectangle>();
		this.mat = mat;
		this.setUp();
	}
	
	private void setUp(){
		Material mat = this.mat;
		
		Transformation trafox1 = Transformation.IDENTITY.append(
				Transformation.translate(1, 0, 0)).append(
				Transformation.rotateY(-90));
		Rectangle x1 = new Rectangle(trafox1, mat);
		Transformation trafoxm1 = Transformation.IDENTITY.append(
				Transformation.translate(-1, 0, 0)).append(
				Transformation.rotateY(90));
		Rectangle xm1 = new Rectangle(trafoxm1, mat);
		Transformation trafoy1 = Transformation.IDENTITY.append(
				Transformation.translate(0, 1, 0)).append(
				Transformation.rotateX(-90));
		Rectangle y1  = new Rectangle(trafoy1, mat);
		Transformation trafoym1 = Transformation.IDENTITY.append(
				Transformation.translate(0, -1, 0)).append(
				Transformation.rotateX(90));
		Rectangle ym1 = new Rectangle(trafoym1, mat);
		Transformation trafoz1 = Transformation.IDENTITY.append(
				Transformation.translate(0, 0, 1));
		Rectangle z1  = new Rectangle(trafoz1, mat);
		Transformation trafozm1 = Transformation.IDENTITY.append(
				Transformation.translate(0, 0, -1)).append(
				Transformation.rotateY(180));
		Rectangle zm1 = new Rectangle(trafozm1, mat);
		
		this.rects.add(x1);
		this.rects.add(xm1);
		this.rects.add(y1);
		this.rects.add(ym1);
		this.rects.add(z1);
		this.rects.add(zm1);
	}

	@Override
	public List<Intersection> intersect(Ray ray) {
		Ray rayInv = this.transformation.transformInverse(ray);
		List<Intersection> inters = new ArrayList<Intersection>();
		for (Rectangle rect: this.rects) {
			inters.addAll(rect.intersect(rayInv));
		}
		//Transform intersections
		List<Intersection> intersTransformed = new ArrayList<Intersection>();
		for (Intersection inter: inters) {
			intersTransformed.add(TransformIntersection(inter));
		}
		return intersTransformed;
	}
	
	private Intersection TransformIntersection(Intersection inter) {
        Point hitPoint = this.transformation.transform( inter.point );
        Normal hitNormal = this.transformation.transformInverseTranspose( inter.normal);
        return new Intersection( hitPoint, inter.txtPnt, hitNormal, inter.mat);
	}

}
