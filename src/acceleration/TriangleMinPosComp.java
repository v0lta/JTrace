package acceleration;

import java.util.Comparator;

import shape.Triangle;

public class TriangleMinPosComp implements Comparator<Triangle> {
	private Character axis;
	
	public TriangleMinPosComp(Character axis){
		this.axis = axis;
	}
	
	@Override
	public int compare(Triangle tri1, Triangle tri2) {
		double minPos1 = tri1.getSmallestCoord(this.axis);
		double minPos2 = tri2.getSmallestCoord(this.axis);
		
		if (minPos1 > minPos2) {
			return 1;
		}
		if (minPos1 < minPos2) {
			return -1;
		}
		else {
			return 0;
		}
	}
}
