package material;

import java.util.ArrayList;
import java.util.List;

import math.Color;
import math.Vector;

public class ColorMap {
	private List<Color> colors = new ArrayList<Color>(); 
	private double minVal;
	private double maxVal;
	private double[][] set;
	//private List<Double> limitVals = new ArrayList<Double>(); 
	
	public ColorMap(List<Color> colors, double minVal, double maxVal, double[][] set) throws IllegalArgumentException{
		//if (colors.size() != limits.size()) {
		//	throw new IllegalArgumentException("need as many colors as limits!");
		//}
		
		this.colors = colors;
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.set = set;		
		//this.limitVals = limits;
	}
	public Color getColor(int indexX, int indexY){
		Vector retColor = new Color(0,0,0).toVector();
		
		double step = (minVal + maxVal)/colors.size();
		double arg = set[indexX][indexY];
		
		for (int i = 0; i < (colors.size()-1); i++) {
			double lb = minVal + i*step;
			double ub = lb + step;
			if ((lb < arg) && (arg < ub)) {
				retColor = colors.get(i).toVector().scale(set[indexX][indexY]);
				break;
			}	
		}
		return retColor.toColor();
	}
}
