package math;

public class Intersection {
	public final Point point;
	public final Normal normal;
	public final Color color;
	public final double reflectivity;
	public int accessCount;
	
	public Intersection(Point point, Normal normal, Color color, double ref, int accessCount) {
		this.point = point;
		this.normal = normal;
		this.color = color;
		this.reflectivity = ref;
		this.accessCount = accessCount;
	}
}
