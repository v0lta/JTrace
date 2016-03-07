package material;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import math.Color;
import math.Vector;

public class Julia implements Material {
	public final double[][] set;
	private final int N;
	private final double bound;
	
	private ColorMap colorMap = null;
	
	/**
	 * Creates a Julia-set covered material
	 * @param c complex seed
	 * @param N for loop counter
	 * @param bound 0.5 of the rectangle size.
	 * @param lim kernel limit.
	 * @param colorScale color factor.
	 */
	public Julia(Complex c, int N, double bound, int lim, double colorScale){
		this.N = N;
		this.bound = bound;
		this.set = computeJulia(c ,N,bound,lim);	
		
			//find min and max values;
			double tmpMin = this.set[0][0];
			double tmpMax = this.set[0][0];;
			for (int i = 0; i<(N-1);i++) {
				for (int j = 0; j<(N-1);j++) {
					if (tmpMin > this.set[i][j]) {
						tmpMin = this.set[i][j];
					}
					if (tmpMax < this.set[i][j]) {
						tmpMax = this.set[i][j];
					}
				}
			}
		this.colorMap = new ColorMap(tmpMin,tmpMax,this.set, colorScale);
		
	}
	
	/**
	 * A two dimensional material definition
	 */

	@Override
	public Color getColor(Vector point){
		//map input into the space where the julia set has been computed.
		double x = Math.abs(point.x);
		double y = Math.abs(point.y);
		double mapBnd = 2*bound;
		
		double dx = (mapBnd)/(N-1);
		double tx = x - mapBnd*( (int) (x/mapBnd) );
		int indexX = (int) (tx/dx);
		
		double dy = dx;
		double ty = y - mapBnd*( (int) (y/mapBnd) );
		int indexY = (int) (ty/dy);
		return this.colorMap.getColor(indexX, indexY);
	}
	

	private int kernel(Complex z,  Complex c,double lim) {
	    int count = 0;
	    while (z.abs() < lim) {
	    	z = z.times(z).plus(c) ;
	    	count += 1;
	    }
	    return count;
	}


	private double[][] computeJulia(Complex c ,int N,double bound,int lim){
	 double[][] julia = new double[N][N]; //array of integers initialized to 0 by default.
	 List<Complex> gridX = new ArrayList<Complex>();
	 List<Complex> gridY = new ArrayList<Complex>();
	 
	 gridX = realSpace(-bound, bound, N);
	 gridY = imagSpace(-bound, bound, N);
	 
	 
	 for (int i= 0; i<(N-1);i++) {
       for (int j = 0; j<(N-1);j++){
    	   Complex x = gridX.get(i);
    	   Complex y = gridY.get(j);    	   
    	   julia[i][j] = Math.log(kernel(x.plus(y), c, lim));
       }
	 }
	 return julia;
	}
	
	private List<Complex> realSpace(double minArg, double maxArg, int N) {
		List<Complex> list = new ArrayList<Complex>();
		double dx;
	    
	    dx = (maxArg-minArg)/(N-1);
	    for (int i = 0;i<(N-1);i++) {
	    	//array = (/ ((i*dx+minArg),i=0,(sizeOfArray-1)) /)
	    	list.add(new Complex(i*dx+minArg,0.0));	    	
	    }
	    return list;	    
	}

	private List<Complex> imagSpace(double minArg, double maxArg, int N) {
		List<Complex> list = new ArrayList<Complex>();
		double dy;
	    
	    dy = (maxArg-minArg)/(N-1);
	    for (int i = 0;i<(N-1);i++) {
	    	//array = (/ ((i*dx+minArg),i=0,(sizeOfArray-1)) /)
	    	list.add(new Complex(0.0,i*dy+minArg));	    	
	    }
	    return list;	
	}
	
	/**
	 * for debugging creates a matlab script which plots the computed set.
	 * @throws IOException
	 */	
	public static void main(String[] arguments){
		int N = 200;
		Julia mat;
		mat = new Julia(new Complex(-0.076,0.652),N,1,100,10);

		BufferedWriter br;
		try {
			br = new BufferedWriter(new FileWriter("julia.m"));
			StringBuilder sb = new StringBuilder();
			sb.append("juliaSet = [");
			for (int i=0; i < (N - 1); i++) {
				for (int j = 1; j<(N-1); j++) {
					sb.append(mat.set[i][j]);
					sb.append(" ");
				}
				sb.append(";"); sb.append(System.getProperty("line.separator"));
			}
			sb.append("];"); sb.append(System.getProperty("line.separator"));
			sb.append("surf(juliaSet);shading('flat');view(0,90);");
			br.write(sb.toString());
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
