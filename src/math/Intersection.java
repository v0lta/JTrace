package math;

public class Intersection {
	public final boolean hit;
	public final Point point;
	public final Normal normal;
	public final Color color;
	public final double reflectivity;
	
	public Intersection(boolean hit, Point point, Normal normal, Color color, double ref) {
		this.hit = hit;
		this.point = point;
		this.normal = normal;
		this.color = color;
		this.reflectivity = ref;
	}
}
