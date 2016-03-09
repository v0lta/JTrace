package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import film.FrameBuffer;
import film.Tile;
import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;
import main.World;
import math.Intersection;
import math.Ray;
import math.Vector;
import light.PointLight;
import sampling.Sample;
import shape.Shape;

/**
 * Entry point of your renderer.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Renderer {
	/**
	 * Entry point of your renderer.
	 * 
	 * @param arguments
	 *            command line arguments.
	 */
	public static void main(String[] arguments) {
		int width = 640;
		int height = 640;
		double sensitivity = 1.0;
		double gamma = 1.5;
		boolean gui = true;
		

		/**********************************************************************
		 * Parse the command line arguments
		 *********************************************************************/

		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i].startsWith("-")) {
				try {
					if (arguments[i].equals("-width"))
						width = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-height"))
						height = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-gui"))
						gui = Boolean.parseBoolean(arguments[++i]);
					else if (arguments[i].equals("-sensitivity"))
						sensitivity = Double.parseDouble(arguments[++i]);
					else if (arguments[i].equals("-gamma"))
						gamma = Double.parseDouble(arguments[++i]);
					else if (arguments[i].equals("-help")) {
						System.out
								.println("usage: "
										+ "[-width  <integer> width of the image] "
										+ "[-height  <integer> height of the image] "
										+ "[-sensitivity  <double> scaling factor for the radiance] "
										+ "[-gamma  <double> gamma correction factor] "
										+ "[-gui  <boolean> whether to start a graphical user interface]");
						return;
					} else {
						System.err.format("unknown flag \"%s\" encountered!\n",
								arguments[i]);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.format("could not find a value for "
							+ "flag \"%s\"\n!", arguments[i]);
				}
			} else
				System.err.format("unknown value \"%s\" encountered! "
						+ "This will be skipped!\n", arguments[i]);
		}

		/**********************************************************************
		 * Validate the input
		 *********************************************************************/

		if (width <= 0)
			throw new IllegalArgumentException("the given width cannot be "
					+ "smaller than or equal to zero!");
		if (height <= 0)
			throw new IllegalArgumentException("the given height cannot be "
					+ "smaller than or equal to zero!");
		if (gamma <= 0)
			throw new IllegalArgumentException("the gamma cannot be "
					+ "smaller than or equal to zero!");
		if (sensitivity <= 0)
			throw new IllegalArgumentException("the sensitivity cannot be "
					+ "smaller than or equal to zero!");

		/**********************************************************************
		 * Initialize the graphical user interface
		 *********************************************************************/

		// initialize the frame buffer
		final FrameBuffer buffer = new FrameBuffer(width, height);

		// initialize the progress reporter
		final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
				width * height, false);

		// initialize the graphical user interface if desired
		final ImagePanel panel;
		if (gui) {
			panel = new ImagePanel(width, height, sensitivity, gamma);
			RenderFrame frame = new RenderFrame("JTrace", panel);
			reporter.addProgressListener(frame);
		} else
			panel = null;

		/**********************************************************************
		 * Initialize the scene
		 *********************************************************************/
		//final World world = new World(width, height, "planeAndSphere");
		//final World world = new World(width, height, "Julia");
		//final World world = new World(width, height, "bunny");
		final World world = new World(width, height, "apple");
		
		/**********************************************************************
		 * Multi-threaded rendering of the scene
		 *********************************************************************/
		final ExecutorService service = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		
		// subdivide the buffer in equal sized tiles
		for (final Tile tile : buffer.subdivide(64, 64)) {
			// create a thread which renders the specific tile
			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					final List<Intersection> intersections = new ArrayList<Intersection>();
					final List<Intersection> shadowInters = new ArrayList<Intersection>();					
					final List<Double> dists = new ArrayList<Double>();
					// iterate over the contents of the tile
					for (int y = tile.yStart; y < tile.yEnd; ++y) {
						for (int x = tile.xStart; x < tile.xEnd; ++x) {
							// create a ray through the center of the
							// pixel.
							Ray ray = world.camera.generateRay(new Sample(x + 0.5,
									y + 0.5));

							// test the scene on intersections 
							intersections.clear();
							for (Shape shape : world.shapes) {
								List<Intersection> currentInter = new ArrayList<Intersection>();
								currentInter = shape.intersect(ray);
								if (currentInter.isEmpty() == false) {
									intersections.addAll(currentInter);
								}
							}
							//see if an intersection was found
							if (intersections.isEmpty()) {
								buffer.getPixel(x, y).add(0, 0, 0);
							} else {
																		
									//find the intersection closest to the camera.
									Vector camPos = world.camera.getOrigin().toVector();
									Vector intPos;
									Double dist;
									dists.clear();
									for (Intersection currentInter: intersections){
										intPos = currentInter.point.toVector();
							            dist = camPos.subtract(intPos).lengthSquared();
										dists.add(dist);
									}
									int index = dists.indexOf(Collections.min(dists));
									Intersection closestInt = intersections.get(index);
									
									//add a color contribution to the pixel based in the closest intersection.
									//double color[];
									//color = closestInt.hitColor().toArray();
									//buffer.getPixel(x, y).add(color[0], color[1], color[2]);
									
					                //use the found intersection for rendering.
					                double La = world.ambient;
					                double Rs = closestInt.reflectivity;
					                Vector Cs = closestInt.color.toVector();
					                double[] ambRes = Cs.scale(La).scale(Rs).toArray();
					                buffer.getPixel(x, y).add(ambRes[0], ambRes[1], ambRes[2],1.0);
					                
					                
					                //add a contribution for each light.
					                for (PointLight pl: world.plights){
					                Vector l  = pl.l(closestInt.point);
					                Vector n  = closestInt.normal.toVector();
					                Vector toLight = pl.origin.toVector().subtract(closestInt.point.toVector()); 
					                
					                double dot = (l.dot(n));
					                
					                    if (dot > 0){
					                    	if (pl.shadows) {
					                    		//launch a shaow ray.					                    		
					                    		Ray shadowRay = new Ray(closestInt.point,toLight);
					                    		shadowInters.clear();
					                    		for (Shape shadowShape : world.shapes) {
													List<Intersection> shadowInter = new ArrayList<Intersection>();
													shadowInter.addAll(shadowShape.intersect(shadowRay));
													if (shadowInter.isEmpty() == false) {
														shadowInters.addAll(shadowInter);
													}
												}
												//see if an intersection was found
												if (shadowInters.isEmpty()) {
													//its not in the shadow.
													//compute distance to light source
													double d = toLight.lengthSquared();
													Vector Lp = pl.L();
								                    Vector Cp = pl.color.toVector();								                    
								                    double[] lghtRes = Cs.elPrd(Lp).elPrd(Cp).scale(dot).scale(Rs/3.14).scale(1/d).toArray();
							                    	buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);										
													
												} 
					                    	} else {
					                    		//there are no shadwos directly shade things
												double d = toLight.lengthSquared();
												Vector Lp = pl.L();
							                    Vector Cp = pl.color.toVector();								                    
							                    double[] lghtRes = Cs.elPrd(Lp).elPrd(Cp).scale(dot).scale(Rs/3.14).scale(1.0/d).toArray();
						                    	buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);
					                    	}
					                    	
					                    }
					                }
							}
						}
					}

					// update the graphical user interface
					if (panel != null)
						panel.update(tile);

					// update the progress reporter
					reporter.update(tile.getWidth() * tile.getHeight());
				}
			};
			service.submit(thread);
		}

		// execute the threads
		service.shutdown();

		// wait until the threads have finished
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// signal the reporter that the task is done
		reporter.done();

		/**********************************************************************
		 * Export the result
		 *********************************************************************/

		BufferedImage result = buffer.toBufferedImage(sensitivity, gamma);
		try {
			ImageIO.write(result, "png", new File("output.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
