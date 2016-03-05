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
	
	public ColorMap(double minVal, double maxVal, double[][] set,double scale){
		//if (colors.size() != limits.size()) {
		//	throw new IllegalArgumentException("need as many colors as limits!");
		//}
		
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.set = set;	
		
		this.parulaRescale(scale);
		//this.parula();
		
		//this.limitVals = limits;
	}
	public Color getColor(int indexX, int indexY){
		Vector retColor = new Color(0,0,0).toVector();
		
		double step = (minVal + maxVal)/colors.size();
		double arg = set[indexX][indexY];
		
		for (int i = 0; i < (colors.size()); i++) {
			double lb = minVal + i*step;
			double ub = lb + step;
			if ((lb <= arg) && (arg <= ub)) {
				retColor = colors.get(i).toVector();
				//break;
			}	
		}
		return retColor.toColor();
	}
	
	/**
	 * use parula colors from matlab.
	 */
	private void parulaRescale(double alpha){
		this.parula();
		
		List<Color> tmpClrs = new ArrayList<Color>(); 
		for (Color color : this.colors) {
			Vector tmp = color.toVector();
			tmp = tmp.scale(alpha);
			tmpClrs.add(tmp.toColor());
		}
		this.colors.addAll(tmpClrs);
	}
	
	private void parula(){
		this.colors.clear();
		this.colors.add(new Color(0.2081,    0.1663,    0.5292));
		this.colors.add(new Color(0.2116,    0.1898,    0.5777));
		this.colors.add(new Color(0.2123,    0.2138,    0.6270));
		this.colors.add(new Color(0.2081,    0.2386,    0.6771));
		this.colors.add(new Color(0.1959,    0.2645,    0.7279));
		this.colors.add(new Color(0.1707,    0.2919,    0.7792));
		this.colors.add(new Color(0.1253,    0.3242,    0.8303));
		this.colors.add(new Color(0.0591,    0.3598,    0.8683));
		this.colors.add(new Color(0.0117,    0.3875,    0.8820));
		this.colors.add(new Color(0.0060,    0.4086,    0.8828));
		this.colors.add(new Color(0.0165,    0.4266,    0.8786));
		this.colors.add(new Color(0.0329,    0.4430,    0.8720));
		this.colors.add(new Color(0.0498,    0.4586,    0.8641));
		this.colors.add(new Color(0.0629,    0.4737,    0.8554));
		this.colors.add(new Color(0.0723,    0.4887,    0.8467));
		this.colors.add(new Color(0.0779,    0.5040,    0.8384));
		this.colors.add(new Color(0.0793,    0.5200,    0.8312));
		this.colors.add(new Color(0.0749,    0.5375,    0.8263));
		this.colors.add(new Color(0.0641,    0.5570,    0.8240));
		this.colors.add(new Color(0.0488,    0.5772,    0.8228));
		this.colors.add(new Color(0.0343,    0.5966,    0.8199));
		this.colors.add(new Color(0.0265,    0.6137,    0.8135));
		this.colors.add(new Color(0.0239,    0.6287,    0.8038));
		this.colors.add(new Color(0.0231,    0.6418,    0.7913));
		this.colors.add(new Color(0.0228,    0.6535,    0.7768));
		this.colors.add(new Color(0.0267,    0.6642,    0.7607));
		this.colors.add(new Color(0.0384,    0.6743,    0.7436));
		this.colors.add(new Color(0.0590,    0.6838,    0.7254));
		this.colors.add(new Color(0.0843,    0.6928,    0.7062));
		this.colors.add(new Color(0.1133,    0.7015,    0.6859));
		this.colors.add(new Color(0.1453,    0.7098,    0.6646));
		this.colors.add(new Color(0.1801,    0.7177,    0.6424));
		this.colors.add(new Color(0.2178,    0.7250,    0.6193));
		this.colors.add(new Color(0.2586,    0.7317,    0.5954));
		this.colors.add(new Color(0.3022,    0.7376,    0.5712));
		this.colors.add(new Color(0.3482,    0.7424,    0.5473));
		this.colors.add(new Color(0.3953,    0.7459,    0.5244));
		this.colors.add(new Color(0.4420,    0.7481,    0.5033));
		this.colors.add(new Color(0.4871,    0.7491,    0.4840));
		this.colors.add(new Color(0.5300,    0.7491,    0.4661));
		this.colors.add(new Color(0.5709,    0.7485,    0.4494));
		this.colors.add(new Color(0.6099,    0.7473,    0.4337));
		this.colors.add(new Color(0.6473,    0.7456,    0.4188));
		this.colors.add(new Color(0.6834,    0.7435,    0.4044));
		this.colors.add(new Color(0.7184,    0.7411,    0.3905));
		this.colors.add(new Color(0.7525,    0.7384,    0.3768));
		this.colors.add(new Color(0.7858,    0.7356,    0.3633));
		this.colors.add(new Color(0.8185,    0.7327,    0.3498));
		this.colors.add(new Color(0.8507,    0.7299,    0.3360));
		this.colors.add(new Color(0.8824,    0.7274,    0.3217));
		this.colors.add(new Color(0.9139,    0.7258,    0.3063));
		this.colors.add(new Color(0.9450,    0.7261,    0.2886));
		this.colors.add(new Color(0.9739,    0.7314,    0.2666));
		this.colors.add(new Color(0.9938,    0.7455,    0.2403));
		this.colors.add(new Color(0.9990,    0.7653,    0.2164));
		this.colors.add(new Color(0.9955,    0.7861,    0.1967));
		this.colors.add(new Color(0.9880,    0.8066,    0.1794));
		this.colors.add(new Color(0.9789,    0.8271,    0.1633));
		this.colors.add(new Color(0.9697,    0.8481,    0.1475));
		this.colors.add(new Color(0.9626,    0.8705,    0.1309));
		this.colors.add(new Color(0.9589,    0.8949,    0.1132));
		this.colors.add(new Color(0.9598,    0.9218,    0.0948));
		this.colors.add(new Color(0.9661,    0.9514,    0.0755));
		this.colors.add(new Color(0.9763,    0.9831,    0.0538));		
	}
	
}
