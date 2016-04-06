package math;

import material.Material;

public class Intersection {
	public final Point point;
	public final TextPoint txtPnt;
	public final Normal normal;
	public final Material mat;
	public final double reflectivity;
	
	public Intersection(Point point, TextPoint txtPnt, Normal normal, Material mat, double ref) {
		this.point = point;
		this.txtPnt = txtPnt;
		this.normal = normal;
		this.mat = mat;
		this.reflectivity = ref;
	}
}
