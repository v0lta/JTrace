package acceleration;

import java.util.Comparator;

import math.Point;
import shape.Triangle;

public class TriangleCentComp implements Comparator<Triangle> {
	private Character axis;


	public TriangleCentComp(Character axis){
		this.axis = axis;
	}
	
	public int compare(Triangle firstTriangle, Triangle secondTriangle) {
		Point firstCentroid  = firstTriangle.getCentroid();
		Point secondCentroid = secondTriangle.getCentroid();
		
		switch (this.axis) {
		case 'x':
			if (firstCentroid.x > secondCentroid.x) {
				return 1;
			} else if (firstCentroid.x < secondCentroid.x) {
				return -1;
			} else {
				return 0;
			}
		case 'y':
			if (firstCentroid.y > secondCentroid.y) {
				return 1;
			} else if (firstCentroid.y < secondCentroid.y) {
				return -1;
			} else {
				return 0;
			}
		case 'z':
			if (firstCentroid.z > secondCentroid.z) {
				return 1;
			} else if (firstCentroid.z < secondCentroid.z) {
				return -1;
			} else {
				return 0;
			}
		default:
			System.err.println("Comparison along nonexistent axis.");
			return 0;
		}
	}

}
