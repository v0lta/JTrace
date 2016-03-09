package math;

public class TextPoint {
	public double u;
	public double v;
	
	public TextPoint(double u, double v) {
		this.u = u;
		this.v = v;		
	}
	
	public TextPoint add(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u + other.u;
		tmp.v = this.v + other.v;
		return tmp;		
	}
	
	public TextPoint sub(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u - other.u;
		tmp.v = this.v - other.v;
		return tmp;		
	}
	
	public TextPoint mul(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u * other.u;
		tmp.v = this.v * other.v;
		return tmp;		
	}
	
	public TextPoint div(TextPoint other){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u / other.u;
		tmp.v = this.v / other.v;
		return tmp;		
	}
	
	public TextPoint scale(double alpha){
		TextPoint tmp = new TextPoint(0.0,0.0);
		tmp.u = this.u * alpha;
		tmp.v = this.v * alpha;
		return tmp;		
	}
	
	
	
	
	

}
