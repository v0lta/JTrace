package math;

public class Intersection {
	private boolean hit;
	private Point point;
	private Normal normal;
	private Color color;
	
	public Intersection(boolean hit, Point point, Normal normal, Color color ) {
		this.hit = hit;
		this.point = point;
		this.normal = normal;
		this.color = color;
	}
	
	public boolean isHit() {
		return this.hit;
	}
	
	public Point hitPoint() {
		return this.point;
	}
	
	public Normal hitNormal() {
		return this.normal;
	}
	
	public Color hitColor() {
		return this.color;
	}
	public void set(Intersection inter) {
		this.hit = inter.hit;
		this.point = inter.point;
		this.normal = inter.normal;
		this.color = inter.color;
	}
	
	
}
