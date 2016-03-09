package material;

import math.Color;
import math.TextPoint;
import math.Vector;

public class Chess implements Material{
	public final Color color1;
	public final Color color2;
	public final double s;
	
	/**
	 * Creates material, which looks like a chess board.
	 * @param color1 first rectangle color
	 * @param color2 second rectangle color
	 * @param s 	the rectangle size.
	 */	
	public Chess(Color color1, Color color2, double s) {
		this.color1 = color1;
		this.color2 = color2;
		this.s = s;
	}

	@Override
	public Color getColor(TextPoint texPoint) {
		int tmp = ( (int) ( texPoint.u/s) 
			       + (int) ( texPoint.v/s));
		int tmp2 = tmp%2;
		
		if (   tmp2  == 0) {
	        return this.color1;
	    		    
		} else {
			return this.color2;
		}
	}
	
	@Override
	public Color getColor(Vector texPoint) {
		int tmp = ( (int) ( texPoint.x/s) 
			       + (int) ( texPoint.y/s)
			       + (int) ( texPoint.z/s) );
		int tmp2 = tmp%2;
		
		if (   tmp2  == 0) {
	        return this.color1;
	    		    
		} else {
			return this.color2;
		}
	}

}
