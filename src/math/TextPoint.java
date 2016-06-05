package math;

public class TextPoint {
	public double u;
	public double v;
	
	/**
	 * Create a point in the texture coordinate frame.
	 * @param u along the x-axis.
	 * @param v y-axis coordinate.
	 */
	
	public TextPoint(double u, double v) {
		this.u = u;
		this.v = v;		
	}
	
	/**
	 * Add another {@link TextPoint}.
	 * @param other, second TextPoint
	 * @return the sum of the two.
	 */
	public TextPoint add(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u + other.u;
		tmp.v = this.v + other.v;
		return tmp;		
	}
	
	/**
	 * Subtract another {@link TextPoint}.
	 * @param other, second TextPoint
	 * @return the difference of the two.
	 */
	public TextPoint sub(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u - other.u;
		tmp.v = this.v - other.v;
		return tmp;		
	}
	
	
	/**
	 * Multiply with another {@link TextPoint}.
	 * @param other, second TextPoint
	 * @return the difference of the two.
	 */
	public TextPoint mul(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u * other.u;
		tmp.v = this.v * other.v;
		return tmp;		
	}
	
	/**
	 * Divide by another {@link TextPoint}.
	 * @param other, second TextPoint
	 * @return the quotient of the two.
	 */
	public TextPoint div(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u / other.u;
		tmp.v = this.v / other.v;
		return tmp;		
	}
	
	/**
	 * Scalar multiplication
	 * @param alpha the scalar 
	 * @return all vector elements multiplied with alpha.
	 */
	public TextPoint scale(double alpha){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u * alpha;
		tmp.v = this.v * alpha;
		return tmp;		
	}
}
