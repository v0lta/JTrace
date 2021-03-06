package material;

import java.util.Random;

import math.Color;
import math.Point;
import math.TextPoint;
import math.Transformation;
import math.Vector;

public class RandomChess implements Material {
	public final ColorMap map;
	public final int colorNo;
	public final double s;
	public final Specular spec;
	public final Diffuse diff;
	private final Transformation reverseTrans;
	
	/**
	 * Create a material consiting of candomized pixels.
	 * @param spec specular part of material brdf.
	 * @param diff diffuse part of material brdf.
	 * @param colors {@link ColorMap} object.
	 * @param s pixel size.
	 * @param colorNo color number.
	 */	
	public RandomChess(Specular spec, Diffuse diff, ColorMap colors,
						double s, int colorNo) {
		this.map = colors;
		this.colorNo = colorNo;
		this.s = s;
		this.map.shuffleColors();
		this.spec = spec;
		this.diff = diff;
		this.reverseTrans = null;
	}
	
	private RandomChess(RandomChess rndChs, Transformation reverseTrans){
		this.map = rndChs.map;
		this.colorNo = rndChs.colorNo;
		this.s = rndChs.s;
		this.spec = rndChs.spec;
		this.diff = rndChs.diff;
		this.reverseTrans = reverseTrans;
		
		
	}
	
	
	@Override
	public Color getColor(TextPoint texPoint) {
		if (this.reverseTrans != null){
			Point texPointPt = new Point(texPoint.u,texPoint.v,0.0);
			texPointPt = reverseTrans.transform(texPointPt);
			texPoint = new TextPoint(texPointPt.x,texPointPt.y);
		}

		
		int tmpU = (int) Math.round((0.5 + texPoint.u/2)*10); 
		int tmpV = (int) Math.round((0.5 + texPoint.v/2)*10);
				
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
		if (this.reverseTrans != null){
			texPoint = reverseTrans.transform(texPoint);
		}
		
		int tmpX = (int) Math.round( texPoint.x/s); 
		int tmpY = (int) Math.round( texPoint.y/s);
		//int tmpZ = (int) Math.round( texPoint.y/s);
		int index1 = tmpX%colorNo;
		int index2 = tmpY%colorNo;
		//int index3 = tmpZ%colorNo;
		
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
	
	@Override
	public double getSpecular(Vector N, Vector L, Vector V) {
		return this.spec.getSpecular(N, L, V);
	}

	@Override
	public double getDiffuse(Vector N, Vector L) {
		return this.diff.getDiffuse(N, L);
	}

	@Override
	public double getDiffuseRho() {
		return this.diff.getRho();
	}

	@Override
	public Material setInvTrans(Transformation reverseTrans) {
		return new RandomChess(this,reverseTrans);
	}



}
