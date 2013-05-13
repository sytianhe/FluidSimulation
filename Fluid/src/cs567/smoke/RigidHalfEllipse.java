package cs567.smoke;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RigidHalfEllipse extends RigidPolarShape {

	double majorAxis;
	double minorAxis;
	
	public RigidHalfEllipse(Point2d centerOfMass, Vector2d LinVel, double theta,
			double AngVel, double d, double a, double b) {
		super(centerOfMass, LinVel, theta, AngVel, d);
		majorAxis = a;
		minorAxis = b;
		init();
	}
	
	@Override
	public double radialFunction(double theta){
		double t = (theta + 2* Math.PI) % ( 2* Math.PI )  ; 
		if ( t > Math.PI && t < 2*Math.PI)  return 0;
		return majorAxis * minorAxis / Math.sqrt( Math.pow(minorAxis * Math.cos(theta), 2.0) + Math.pow(majorAxis * Math.sin(theta), 2.0)   );
	}
	

}
