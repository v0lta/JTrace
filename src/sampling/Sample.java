package sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.Ray;
import camera.Camera;

/**
 * Encapsulates all the data necessary for a {@link Camera} to generate
 * {@link Ray}s.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class Sample {
	/**
	 * x coordinate of the sample in image space.
	 */
	public final double x;
	/**
	 * y coordinate of the sample in image space.
	 */
	public final double y;
	
	public final double width;
	public final Camera cam;
	public List<Ray> sampleRays;
	public final int spp;
	
	
	/**
	 * Creates a new {@link Sample} for a {@link Camera} at the given position
	 * of the image.
	 * 
	 * @param x
	 *            x coordinate of the sample in image space (between 0
	 *            (inclusive) and the horizontal resolution of the image
	 *            (exclusive))
	 * @param y
	 *            y coordinate of the sample in image space (between 0
	 *            (inclusive) and the vertical resolution of the image
	 *            (exclusive))
	 */
	public Sample(double x, double y, double width, int spp, Camera cam) {
		this.x = x + width/2;
		this.y = y + width/2;
		this.width = width;
		this.cam = cam;
		this.spp = spp;
	}
	
	private List<Ray> stratify(){
		List<Ray> rpp = new ArrayList<Ray>(); 
		for (int i = 0; i < spp; i++) {
			Random r = new Random();
			double xDiff = -0.5 + r.nextDouble()/2;
			double yDiff = -0.5 + r.nextDouble()/2;
			
			double x = this.x + xDiff;
			double y = this.y + yDiff;
			rpp.add(this.cam.generateRay(new Sample(x, y, this.width, 
													this.spp, this.cam)));			
		}
		return rpp;
	}
	public List<Ray> getRays() {
		this.sampleRays = this.stratify();
		return this.sampleRays;
	}

	
	
}
