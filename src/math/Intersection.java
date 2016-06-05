package math;

import material.Material;


/**
 * An intersection object created when a hit point is found.
 * @author moritz
 */
public class Intersection {
	public final Point point;
	public final TextPoint txtPnt;
	public final Normal normal;
	public final Material mat;

	/**
	 * Set up a intersection object.
	 * @param point hit point.
	 * @param txtPnt texture coordinates at hit point.
	 * @param normal hit point normal.
	 * @param mat material at hit-point.
	 */
	public Intersection(Point point, TextPoint txtPnt, Normal normal, Material mat) {
		this.point = point;
		this.txtPnt = txtPnt;
		this.normal = normal;
		this.mat = mat;
	}
}
