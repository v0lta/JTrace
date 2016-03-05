package material;

public class Complex {
	public final double re; //real part
	public final double im; //imaginary part
	
	/**
	 * Create a new complex number
	 * @param real the real part
	 * @param imag the imaginary part
	 */
	public Complex(double real, double imag) {
		re = real;
		im = imag;		
	}
	

	/**
	 * @return the absolute value of this complex number.
	 */
	public double abs() {
		return Math.sqrt(re*re + im*im);
	}
	
	/**
	 * Add two complex numbers a and b 
	 * @param b the complex number to be added.
	 * @return the sum.
	 */
	public Complex plus(Complex b) {
	    Complex a = this;             // invoking object
	    double real = a.re + b.re;
	    double imag = a.im + b.im;
        return new Complex(real, imag);
	}
	
	/**
	 * Subtract two complex numbers a and b 
	 * @param b the complex number to be subtracted.
	 * @return the difference.
	 */
	public Complex minus(Complex b) {
	    Complex a = this;
	    double real = a.re - b.re;
	    double imag = a.im - b.im;
	    return new Complex(real, imag);
	}

	/**
	 * Multiply two complex numbers a and b 
	 * @param b the other complex factor.
	 * @return the product.
	 */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }
    /**
     * Scalar multiplication
     * @param alpha the scalar factor.
     * @return the product
     */
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }
    
    /**
     * @return the conjugate of this number.
     */
    public Complex conjugate() {  return new Complex(re, -im); }

    

}
