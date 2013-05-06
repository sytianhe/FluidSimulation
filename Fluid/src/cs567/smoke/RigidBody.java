package cs567.smoke;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import cs567.smoke.Utils;

/**
 * Basic Rigid Body class.  Based loosly on RigidBody from project 3.
 * The class should be extended for each class of rigid body.  For instance see RigidCircle.
 * @author homoflashmanicus, tianhe
 *
 */
public class RigidBody
{
	/** Initial Position of center of mass (world frame)  */
	Point2d  x0  = new Point2d();
	
	/** Position of center of mass (world frame)  */
	Point2d  x  = new Point2d();
	
	/** Linear velocity (world frame) (init=0) */
	Vector2d v = new Vector2d(); 
	
	/** Angular velocity (init=0) */
	double   omega = 0;
	
	/** Angular position */
	double 	 theta = 0;
	
	/** Density */
	double   density;
	
	/** Mass */
	double   mass;
	
	/** Volume */
	double volume;
	
	/** MomentOfInertia */
	double momentOfIntertia;
	
	/** Force accumulation vector.  Just in case. */
	Vector2d force = new Vector2d();

	/** Torque accumulation.  Just in case. */
	double torque = 0;

	/** Pin?? Why not. */
	boolean pin = false;
	
	public RigidBody (Point2d centerOfMass, Vector2d LinVel, double theta, double AngVel, double d){
		x0 = new Point2d(centerOfMass);
		x = centerOfMass;
		v = LinVel;
		omega = AngVel;
		density = d;
		this.theta = theta;
	}
	
	public void reset(){
		x.set(x0);
		v.set(0,0);
		omega = 0;
	}
	
	/** Specifies whether or not this rigidbody is fixed in space via
	 * a full-dof (encastré) pin constraint.  */
	public void setPin(boolean fix) { pin = fix; }

	/** Returns true if currently pinned. */
	public boolean isPinned() { return pin; }
		
	/** Advances body position, integrating any accumulated force/torque
	 * (which are then set to zero), and updates internal rigid
	 * transforms.   */
	public void updatePosition(double dt)
	{

		/// UPDATE LINEAR POSITION:
		Utils.acc(x, dt, v);

		/// UPDATE ANGULAR POSITION:
		theta += dt * omega;

		/// RESET FORCE/TORQUE ACCUMULATORS:
		force.x = force.y = torque = 0;
	}
	
	/** Return the w ratio for cell (i,j) */
	public double wRatio (int column, int row){
		return 0;
	}
	
	/** Inverse Mass of object. */
	public double getInverseMass() { 
		if (isPinned() ) return 0;
		else return 1/mass; 
	}

	/** Inverse Angular mass, or inertia tensor I_zz, of object.*/
	public double getInverseMomentOfInertia() { 
		if (isPinned() ) return 0;
		else return 1/momentOfIntertia; 
	}
	
	
	public void display(GL2 gl){
		
	}

	/** Accumulates force/torque in world coordinates. Only affects
	 * coming time step. */
	public void applyWrenchW(Vector2d f, double tau) 
	{
		force.add(f);
		torque += tau;
	}

	/**  Get acceleration at point p on the rigid body */
	public Vector2d getAcceleration(Vector2d r) {
		Vector2d a = new Vector2d(force);
		a.scale(getInverseMass());
		
		a.y +=  torque*r.x * getInverseMomentOfInertia();
		a.x +=  -torque*r.y * getInverseMomentOfInertia();

		return a;
	}
	
	/** 
	 * TODO(ADD TORQUE SUPPORT!) Applies contact force (in world coordinates)
	 * @param contactPointW Contact point in world coordinates
	 * @param contactForceW Contact force in world coordinates
	 */
	public void applyContactForceW(Point2d contactPointW, Vector2d contactForceW)
	{
		force.add(contactForceW);

		// ADD TORQUE:  TODO  ######
		Vector2d lever = new Vector2d();
		lever.sub(contactPointW, x);
		double t = lever.x*contactForceW.y-lever.y*contactForceW.x;
		torque += t;
	}

	/** Just do something basic for now to keep the the Rigid bodies from falling into the ground. */
	public void applyConstraintForces() {
		// TODO Auto-generated method stub
		
	}

}