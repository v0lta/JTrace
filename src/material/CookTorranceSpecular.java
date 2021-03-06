package material;

import math.Vector;

/**
 * This class implements Cook-Torrance parts of
 * bidirectional radiance distribution functions.
 * @author moritz
 *
 */
public class CookTorranceSpecular implements Specular {
	double f0;
	double n; //index of refraction.
	//double k; //extinction coefficient.
	double m; //root mean square slope of facets = p1
	double rhos = 1; //scaling
	
	
	/**
	 * Crate a Cook-Torrance specular reflection function.
	 * @param p0 value of the Fresnel equation at normal incidence.
	 * @param p1 root mean square slope.
	 * @param rhos specualr scaling factor.
	 */

	public CookTorranceSpecular(double p0, double p1, double rhos){
		this.f0 = p0;
		this.m = p1;
		this.rhos = rhos;
		
		this.n = (1 + Math.sqrt(f0)) / (1 - Math.sqrt(f0));
	}
	
	@Override
	public double getSpecular(Vector N, Vector L, Vector V){
		
		Vector sum = V.add(L);
		Vector H = sum.normalize();
		
		double dot = H.dot(N); //alpha
		double delta = Math.acos(dot);
		double c = V.dot(H);
		double g = getg(n,c);
		double G = getG(N,H,L,V);
		double D = getD(m,delta);
		//double D = getD(0.1,delta);
		double fresnel = fresnelPaper(c,g);
		//double fresnel = fresnelBook(N,V,n);
		
		return rhos/Math.PI * (D*G)/(N.dot(L)*N.dot(V))*fresnel;
		//return rhos/Math.PI * (D*G)*fresnel;
		//return frac;
	}
	
	/**
	 * Compute the Fresnel equation as done in cook torrance's original paper.
	 * @param c cosine of angle between V and H. 
	 * @param g result of {@link getg}
	 * @return the value of the Fresnel equation.
	 */	
	private double fresnelPaper(double c,double g){
		double frac1 = ((g - c)*(g - c))/(2 * (g + c)*(g + c));
		
		double frac2Num = c*(g + c) - 1;
		double frac2Dem = c*(g - c) + 1;
		
		return frac1 + (1 + (frac2Num * frac2Num)/(frac2Dem * frac2Dem ));
	}
	
//	private double fresnelBook(Vector N, Vector V, double eta){
//		double cosThetaI = N.dot(V);
//		double cosThetaT = Math.sqrt(1 - 1/(eta*eta) * (1 / cosThetaI*cosThetaI));
//		
//		double rPrl = (eta*cosThetaI - cosThetaT) / (eta*cosThetaI + cosThetaT);
//		double rOrth = (cosThetaI - eta*cosThetaT) / (cosThetaI + eta*cosThetaT);
//		
//		double kr = 0.5*(rPrl*rPrl + rOrth*rOrth);
//		return kr;
//	}
	
	/**
	 * Compute g as defined by Cook and Torrance.
	 */
	private double getg(double n, double c){
		return Math.sqrt(n*n + c*c - 1);
	}
	
	/**
	 * Find the geometric attenuation factor.  
	 */
	private double getG(Vector N, Vector H, Vector L, Vector V){
		double tmp1 = 2*(N.dot(H)*N.dot(V))/(V.dot(H));
		double tmp2 = 2*(N.dot(H)*N.dot(L))/(V.dot(H));
		return Math.min(Math.min(tmp1, tmp2),1);
	}
	
	/**
	 * Evaluate the facet slope distribution function D
	 * @param m root mean square slope of facets
	 * @param delta angle spanned by H and N.
	 * @return
	 */	
	private double getD(double m, double delta){
		double frac = 1.0/(m*m * Math.pow(Math.cos(delta), 4.0));
		double exp = Math.exp( (-1)*((Math.tan(delta)/m)*(Math.tan(delta)/m)) );
		return frac*exp;
	}
	

}
