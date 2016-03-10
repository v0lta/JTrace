package math;

public class Color {
	public final double r;
	public final double g;
	public final double b;
	
	public Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public double[] toArray() {
		return new double[] { r, g, b };
	}
	
	/**
	 * Converts this {@link Color} to a {@link Vector}.
	 * 
	 * @return this {@link Color} as a {@link Vector}.
	 */
	public Vector toVector() {
		return new Vector(r, g, b);
	}

	
}
