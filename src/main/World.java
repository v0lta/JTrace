package main;

import java.util.ArrayList;
import java.util.List;

import light.Light;
import camera.Camera;
import shape.Shape;

/**
 * Sets up the scene to be rendered.
 */

public class World{
	public final Camera camera;
	public final List<Light> lights = new ArrayList<Light>();
	public final List<Shape> shapes = new ArrayList<Shape>();
	
	
	public World(String worldName){
		if (worldName == "initialWorld") {
			return InitialWorld();
		}
	}

	private static World initialWorld() {
		this.camera = 
	}
}
