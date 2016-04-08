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

import camera.Camera;
import film.FrameBuffer;
import film.Tile;
import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;
import main.World;
import math.Color;
import math.Constants;
import math.Intersection;
import math.Ray;
import math.Vector;
import light.AreaLight;
import light.LightIntersection;
import light.PointLight;
import sampling.Sample;
import shape.Shape;
import material.ColorMap;

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
		int width = 600;
		int height = 600;
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
		//final World world = new World(width, height, "initialWorld");
		final World world = new World(width, height, "sphereWorld");
		//final World world = new World(width, height, "planeAndSphere");
		//final World world = new World(width, height, "Julia");
		//final World world = new World(width, height, "apple");
		//final World world = new World(width, height, "bunny");		
		//final World world = new World(width, height, "venus");
		//final World world = new World(width, height, "dragon");
		//final World world = new World(width, height, "buddha");
		//final World world = new World(width, height, "tea");
		//final World world = new World(width, height, "sun");
		//final World world = new World(width, height, "richter");
		//final World world = new World(width, height, "force");
		

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
					List<Intersection> intersections = new ArrayList<Intersection>();
					List<Intersection> shadowInters = new ArrayList<Intersection>();					
					List<Double> dists = new ArrayList<Double>();
					// iterate over the contents of the tile
					for (int y = tile.yStart; y < tile.yEnd; ++y) {
						for (int x = tile.xStart; x < tile.xEnd; ++x) {
							// create a ray 
							//Ray ray = world.camera.generateRay(new Sample(x,y, 0.5));
							Sample sample = new Sample(x, y, 0.5, world.spp, world.camera);
							List<Ray> rpp = sample.getRays();

							for (Ray ray: rpp) {
								//Intersection test
								intersections = testforIntsections(world.shapes,ray);
								//see if an intersection was found
								if (intersections.isEmpty()) {
									buffer.getPixel(x, y).add(0, 0, 0);
									//System.err.println("no hit");
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
									double[] ambRes = computeAmbientShading(closestInt, world.ambient, closestInt.reflectivity);


									if (Constants.normalVisualization) {
										Color Cs;
										Cs = closestInt.normal.toVector().toColor();
										buffer.getPixel(x, y).add(0.5 + 0.5*Cs.r,0.5 + 0.5*Cs.g, 0.5 + 0.5*Cs.b);

									} else if (Constants.compVisualization) {
										int intersectionCount = ray.getIntersectionCounter();
										Color pixelColor;
										//int max = 180;
										int max = 600;
										//int max = 900; //dragon...

										ColorMap colorMap = new ColorMap(0.0, max, null,1.0, "hot");
										pixelColor = colorMap.getCompColor(intersectionCount);

										//intensity = intersections.size()/1.0;
										//double[] compRes = computeAmbientShading(visColor,intensity , 1.0);

										//double greenValue = ((double) intersectionCount)/max;						
										double whiteValue = ((double) intersectionCount)/max;
										//buffer.getPixel(x, y).add(whiteValue,whiteValue,whiteValue);
										buffer.getPixel(x, y).add(pixelColor.r,pixelColor.g,pixelColor.b);


									} else {
										//add the ambient Lighting result.
										buffer.getPixel(x, y).add(ambRes[0], ambRes[1], ambRes[2],1.0);

										//------------------------ point light sources. -----------------------------------------------
										for (PointLight pl: world.plights){
											Vector l  = pl.l(closestInt.point);
											Vector n  = closestInt.normal.toVector();
											Vector toLight = pl.origin.toVector().subtract(closestInt.point.toVector()); 
											double dot = (n.dot(l));						                
											//if (true){
											if (dot > 0){
												if (pl.shadows) {
													//launch a shadow ray.					                    		
													Ray shadowRay = new Ray(closestInt.point,toLight);
													shadowInters = testforIntsections(world.shapes,shadowRay); 
													//see if an intersection was found
													if (shadowInters.isEmpty()) {
														//its not in the shadow.
														//compute distance to light source
														double[] lghtRes = computeShading(closestInt,toLight,pl, dot, world.camera );
														buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);		
													} else {
														//see if the intersection is behind the point light.
														boolean inShadow = false; 
														for (Intersection shadowInt : shadowInters) {
															Vector shadowRayHitPnt = shadowInt.point.toVector();
															double distanceToLight = toLight.lengthSquared();
															double distanceToHit = closestInt.point.toVector().subtract(shadowRayHitPnt).lengthSquared();
															if (distanceToHit < distanceToLight) {
																inShadow = true;  
															}
														}
														if (inShadow == false ){
															double[] lghtRes = computeShading(closestInt,toLight,pl, dot, world.camera);
															buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);	
														}
													}
												} else {
													//there are no shadows directly shade things
													double[] lghtRes = computeShading(closestInt,toLight,pl, dot, world.camera );
													buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);
												}

											}
										}

										// --------------------- handle area lights.------------------------------------------------
										Vector p = closestInt.point.toVector();
										for(AreaLight al : world.alights){

											if (al.shape.inShape(p.toPoint())) {
												// the intersection is on the point light.
												double [] lghtRes = computeAmbientShading(closestInt,closestInt.reflectivity,al.intensity);
												buffer.getPixel(x, y).add(lghtRes[0], lghtRes[1], lghtRes[2],1.0);
											} else {

												Vector lghtVct = new Vector(0.0,0.0,0.0);
												for (int i = 0; i < al.sampleNo; i++) {
													//create random number generator with seed for reproducibility.
													LightIntersection lightInt = al.getpPrime(p.toPoint());
													
													Vector NPrime = lightInt.nPrime.toVector();
													Vector L = p.subtract(lightInt.pPrime.toVector()).normalize();
									
													if (NPrime.dot(L) > 0){
														Ray shadowRay = new Ray(p.toPoint(), lightInt.pPrime.toVector());
														shadowInters = testforIntsections(world.shapes,shadowRay); 
														if (shadowInters.isEmpty()) {
															//its not in the shadow.
															//compute distance to light source
															lghtVct = lghtVct.add(computeAlShading(closestInt,al,p,lightInt, world.camera ));
														} else {
															boolean inShadow = false; 
															for (Intersection shadowInt : shadowInters) {
																Vector shadowRayHitPnt = shadowInt.point.toVector();
																double distanceToLight = p.subtract(lightInt.pPrime.toVector()).lengthSquared();
																double distanceToHit = p.subtract(shadowRayHitPnt).lengthSquared();
																if (distanceToHit < distanceToLight) {
																	if (al.shape.inShape(shadowInt.point) == false) {
																		inShadow = true;
																	} 
																	//buffer.getPixel(x, y).add(0, 10, 0,1.0);
																}
															}
															if (inShadow == false ){
																lghtVct = lghtVct.add(computeAlShading(closestInt,al,p,lightInt, world.camera ));
															}
														}
													
													Color lghtClr = lghtVct.scale(1.0/al.sampleNo).toColor();
													buffer.getPixel(x, y).add(lghtClr.r, lghtClr.g, lghtClr.b,1.0);
													} 
												} 
											}
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

	private static List<Intersection> testforIntsections(List<Shape> shapeList,Ray ray) {
		List<Intersection> interList = new ArrayList<Intersection>();
		for (Shape shape : shapeList) {
			List<Intersection> inter = new ArrayList<Intersection>();
			inter.addAll(shape.intersect(ray));
			if (inter.isEmpty() == false) {
				interList.addAll(inter);
			}
		}
		return interList;
	}

	private static double[] computeAmbientShading(Intersection inter,Double La, Double Rs) {
		Color hitClr = inter.mat.getColor(inter.txtPnt);
		Vector Cs = hitClr.toVector();
		double[] ambRes = Cs.scale(La).scale(Rs).toArray();
		return ambRes;		
	}

	private static double[] computeShading(Intersection inter,Vector toLight,PointLight light, double dot, Camera cam) {
		Vector lightRes;
		Color hitClr = inter.mat.getColor(inter.txtPnt);
		Vector Cs = hitClr.toVector();
		Vector Lp = light.L();
		double Rs = inter.reflectivity;
		double d = toLight.lengthSquared();		
		lightRes = Cs.elPrd(Lp).scale(dot).scale(Rs/Math.PI).scale(1/d);

		//specular
		Vector N = inter.normal.toVector();
		Vector L = light.l(inter.point);
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		double spec = inter.mat.getSpecular(N, L, V);
		lightRes = lightRes.add(Lp.scale(Rs*spec));
		return lightRes.toArray();
	}

	private static Vector computeAlShading(Intersection inter, AreaLight al, Vector p, LightIntersection lightInt, Camera cam ){
		Vector pPrime = lightInt.pPrime.toVector();
		double G = al.G(inter, pPrime.toPoint());
		Vector La = al.L(pPrime.toPoint());
		double Rs = inter.reflectivity;
		Color hitClr = inter.mat.getColor(inter.txtPnt);
		Vector Cs = hitClr.toVector();
		Vector intermediateResult = Cs.elPrd(La).scale(Rs).scale(G).scale(al.shape.getInverseArea());

		//specular
		Vector N = inter.normal.toVector();
		Vector L = pPrime.subtract(p).normalize();
		Vector V = cam.getOrigin().subtract(inter.point).normalize();
		double spec = inter.mat.getSpecular(N, L, V);
		Vector Lp = al.mat.getColor(lightInt.txtPnt).toVector();
		intermediateResult = intermediateResult.add(Cs.elPrd(Lp).scale(spec));
		return intermediateResult;

	}
}
