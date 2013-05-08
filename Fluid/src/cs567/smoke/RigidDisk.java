package cs567.smoke;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RigidDisk extends RigidPolarShape {

	double radius;
	
	public RigidDisk(Point2d centerOfMass, Vector2d LinVel, double theta, double AngVel, double d, double r) {
		super(centerOfMass, LinVel, theta, AngVel, d);
		radius = r;
		init();
	}

	@Override
	public double radialFunction(double theta){
		return 1 * radius;
	}
	
}
