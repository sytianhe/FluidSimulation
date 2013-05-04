package cs567.smoke;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RigidBody
{
	/** Position of center of mass (world frame)  */
	Point2d  x  = new Point2d();
	
	/** Linear velocity (world frame) (init=0) */
	Vector2d v = new Vector2d(); 
	
	/** Angular velocity (init=0) */
	double   omega = 0;
	
	/** Density */
	double   density = 0;
	
	/** Mass */
	double   mass = 0;
	
	public RigidBody (Point2d centerMass, Vector2d LinVel, double AngVel, double d){
		x = centerMass;
		v = LinVel;
		omega = AngVel;
		density = d;
	}
	
	/** Return the w ratio for cell (i,j) */
	public double wRatio (int column, int row){
		return 0;
	}
	
}