package acceleration;

import java.util.Comparator;

import shape.Triangle;

public class TriangleMaxPosComp  implements Comparator<Triangle>{
	
	private Character axis;
	
	public TriangleMaxPosComp(Character axis){
		this.axis = axis;
	}
	
	@Override
	public int compare(Triangle tri1, Triangle tri2) {
		double maxPos1 = tri1.getLargestCoord(this.axis);
		double maxPos2 = tri2.getLargestCoord(this.axis);
		
		if (maxPos1 > maxPos2) {
			return 1;
		}
		if (maxPos1 < maxPos2) {
			return -1;
		}
		else {
			return 0;
		}
	}


}
