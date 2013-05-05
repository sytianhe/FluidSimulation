package cs567.smoke;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
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
	
	/** Angular position */
	double 	 theta = 0;
	
	/** Density */
	double   density = 0;
	
	/** Mass */
	double   mass;
	
	/** Volume */
	double volume;
	
	/** MomentOfInertia */
	double momentOfIntertia;
	
	public RigidBody (Point2d centerOfMass, Vector2d LinVel, double theta, double AngVel, double d){
		x = centerOfMass;
		v = LinVel;
		omega = AngVel;
		density = d;
		this.theta = theta;
	}
	
	/** Return the w ratio for cell (i,j) */
	public double wRatio (int column, int row){
		return 0;
	}
	
	public void display(GL2 gl){
		
	}
}