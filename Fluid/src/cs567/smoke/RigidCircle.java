package cs567.smoke;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;


public class RigidCircle extends RigidBody
{
	/** Radius */
	double 	 radius;
	
	public RigidCircle(Point2d centerOfMass, Vector2d LinVel,double theta, double AngVel, double d, int r) {
		super(centerOfMass, LinVel,theta, AngVel, d);
		radius =r;
		mass = Math.PI*r*r*d;
		momentOfIntertia = mass * r * r / 2;
	}

	@Override
	/** Return the w ratio for cell (i,j) */
	public double wRatio (int row, int column){
		
		int numSample = 4;
		int counter = 0;
		
		for (int i=0; i<numSample; i++){
			for(int j=0; j<numSample; j++){
				double xCoord = row + (i+0.5)/numSample;
				double yCoord = column + (j+0.5)/numSample;
				if (Math.pow(xCoord - x.x, 2.0) + Math.pow(yCoord - x.y, 2.0) <= radius*radius){
					counter +=1;
				}
			}
		}
		
		return counter * 1.0/(numSample*numSample);
	}
	
	@Override
	public void applyConstraintForces() {
		/// APPLY PENALTY FORCE IF IN CONTACT:
		double penDepth = 0.1 - (x.y-radius);
		if(penDepth > 0) {//overlap
			/// PENALTY CONTACT FORCE:
			double k     = Constants.CONTACT_STIFFNESS * mass;
			double f =  k * penDepth;

			/// DAMPING: 
			double fDamp = + 0.004 * k * (v.y);
			
			force.y+= f;


		}
	}
	
	@Override
	/**  Display disk  */
	public void display(GL2 gl){
		
		float cx = (float) (this.x.x/Constants.N);
		float cy = (float) (this.x.y/Constants.N);
		float r = (float) (radius/Constants.N);
		gl.glLineWidth(3.0f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		int intervals = 100;
		for (int i=0; i<intervals; i++)
		{
			double degInRad = ((double)i) * 2.0 * Math.PI / ((double)intervals);
			gl.glVertex2d(cx + Math.cos(degInRad)*r, cy + Math.sin(degInRad)*r);
			gl.glColor3d(1.0, 215.0/256.0, 0.0);
		}

		gl.glEnd();

	}
}