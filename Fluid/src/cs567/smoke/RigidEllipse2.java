package cs567.smoke;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RigidEllipse2 extends RigidPolarShape {

	double majorAxis;
	double minorAxis;
	
	public RigidEllipse2(Point2d centerOfMass, Vector2d LinVel, double theta,
			double AngVel, double d, double a, double b) {
		super(centerOfMass, LinVel, theta, AngVel, d);
		majorAxis = a;
		minorAxis = b;
		init();
	}
	
	@Override
	public double radialFunction(double theta){
		theta = modAngle(theta);
		return majorAxis * minorAxis / Math.sqrt( Math.pow(minorAxis * Math.cos(theta), 2.0) + Math.pow(majorAxis * Math.sin(theta), 2.0)   );
	}
	

}
