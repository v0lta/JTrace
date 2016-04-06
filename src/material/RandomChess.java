package material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.Color;
import math.TextPoint;
import math.Vector;

public class RandomChess implements Material {
	public final ColorMap map;
	public final int colorNo;
	public final double s;
	
	public RandomChess(ColorMap colors, double s, int colorNo) {
		this.map = colors;
		this.colorNo = colorNo;
		this.s = s;
		this.map.shuffleColors();
	}

	@Override
	public Color getColor(TextPoint texPoint) {
		int tmpU = (int) Math.round((0.5 + texPoint.u/2)*10); 
		int tmpV = (int) Math.round((0.5 + texPoint.v/2)*10);
		int seed = tmpU + tmpV;
		
		Random r1 = new Random(tmpU);
		Random r2 = new Random(tmpV);
		//int rndInt = r1.nextInt(colorNo)*r2.nextInt(colorNo);
		//int index =(int) Math.round(rndInt%colorNo);
		
		double rndDbl = r1.nextDouble()*r2.nextDouble()*100;
		rndDbl = rndDbl*r1.nextDouble()*100;
		rndDbl = rndDbl*r2.nextDouble()*100;
		int index =(int) Math.round(rndDbl)%colorNo;
		
		return this.map.getColors().get(index);
		//return new Color(index,index,index);
	}
	
	@Override
	public Color getColor(Vector texPoint) {
		int tmpX = (int) Math.round( texPoint.x/s); 
		int tmpY = (int) Math.round( texPoint.y/s);
		int tmpZ = (int) Math.round( texPoint.y/s);
		int index1 = tmpX%colorNo;
		int index2 = tmpY%colorNo;
		int index3 = tmpZ%colorNo;
		
		int tmp = (  (int) Math.round( texPoint.x/s) 
			       + (int) Math.round( texPoint.y/s)
			       + (int) Math.round( texPoint.z/s) );
		int tmp2 = tmp%2;
		
		if (   tmp2  == 0) {
			return this.map.getColors().get(index1);
	    		    
		} else {
			return this.map.getColors().get(index2);
		}
	}



}
