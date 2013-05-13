package cs567.smoke;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import cs567.smoke.RigidTransform;
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

	/** Initial Angular position */
	double 	 theta0 = 0;
	
	/** Angular position */
	double 	 theta = 0;
	
	/** Density */
	double   density;
	
	/** Mass */
	double   mass;
	
	/** Volume */
	double volume;
	
	/** Maximum radius. */
	double maxRadius;
	
	/** MomentOfInertia */
	double momentOfInertia;
	
	/** Force accumulation vector.  Just in case. */
	Vector2d force = new Vector2d();

	/** Torque accumulation.  Just in case. */
	double torque = 0;

	/** Pin?? Why not. */
	boolean pin = false;
	
	/** Color. */
	Color3f color = new Color3f(1f,1f,1f);
	
	/** body2world transform */
	RigidTransform transformB2W = new RigidTransform();

	/** world2body transform */
	RigidTransform transformW2B = new RigidTransform();
	
	public RigidBody (Point2d centerOfMass, Vector2d LinVel, double th, double AngVel, double d){
		x0.set(centerOfMass);
		theta0 = th;
		v = LinVel;
		omega = AngVel;
		density = d;
		reset();
	}
	
	public void reset(){
		x.set(x0);
		theta = theta0;
		v.set(0,0);
		omega = 0;
		
		updateRigidTransforms();
	}
	
	/** Specifies whether or not this rigidbody is fixed in space via
	 * a full-dof (encastré) pin constraint.  */
	public void setPin(boolean fix) { pin = fix; }

	/** Returns true if currently pinned. */
	public boolean isPinned() { return pin; }
	
	/** Fragile reference to center-of-mass position. */
	Point2d getPosition() { return x; }

	/** Current rotation angle (in radians). */
	double getOrientation() { return theta; }
		
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
		
		updateRigidTransforms();
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
		else return 1/momentOfInertia; 
	}
	
	
	public synchronized void display(GL2 gl){
		
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
	
	/** Refreshes transformB2W and transformW2B using current
	 * position/orientation. */
	private void updateRigidTransforms()
	{
		transformB2W.set(theta, x);
		transformW2B.set(transformB2W);
		transformW2B.invert();
	}
	/** Transforms point/vector from World to Body frame. */
	public void transformW2B(Tuple2d x) {
		transformW2B.transform(x);
	}
	/** Transforms point/vector from Body to World frame. */
	public void transformB2W(Tuple2d x) {
		transformB2W.transform(x);
	}
	
	/** Applies the body-to-world (B2W) transformation. */
	protected void applyGLTransform(GL2 gl)
	{
		gl.glTranslated(x.x/Constants.N, x.y/Constants.N, 0);
		double angleInDegrees = 180./Math.PI * theta;
		gl.glRotated(angleInDegrees, 0, 0, 1);
	}

}