package cs567.smoke;

import javax.vecmath.*;

/**
 * Maintains a 2D rigidbody transform.
 *
 * @author Doug James, March 2007.
 */
public class RigidTransform 
{
	Matrix3d T = new Matrix3d();

	RigidTransform() 
	{
		T.setIdentity();
	}

	public void set(RigidTransform transform) 
	{
		T.set(transform.T);
	}

	/** Transformation specified by rotation angle, theta, and translation. */
	public void set(double theta, Tuple2d translation)
	{
		if(Double.isNaN(theta)) throw new ArithmeticException("theta was NaN");
		T.setIdentity();
		T.rotZ(theta);
		T.m02 = translation.x;
		T.m12 = translation.y;
		//System.out.println("T="+T+", theta="+theta);
	}

	/** Inverts transform. */
	public void invert() { 
		try{
			T.invert();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("e="+e+" \n "+T);
		}
	}

	private Point3d tmp = new Point3d();

	/** Multiply this matrix by the tuple t and place the result back into the tuple (t = this*t). */
	public synchronized void transform(Tuple2d p)
	{
		tmp.x = p.x;
		tmp.y = p.y;
		tmp.z = ((p instanceof Point2d) ? 1 : 0);///SLOTH
		T.transform(tmp);
		p.x = tmp.x;
		p.y = tmp.y;
	}
}
