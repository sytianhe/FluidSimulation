package cs567.smoke;

import java.io.IOException;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import cs567.framework.Texture2D;


public class RigidEllipse extends RigidBody
{
	/** Radius */
	double 	 majorRadius;
	double   minorRadius;
	private Texture2D texture;
	
	public RigidEllipse(Point2d centerOfMass, Vector2d LinVel,double theta, double AngVel, double d, int r1, int r2) {
		super(centerOfMass, LinVel,theta, AngVel, d);
		majorRadius = r1;
		minorRadius = r2;
		maxRadius = r1;
		mass = Math.PI*r1*r2*d;
		momentOfInertia = mass * r1 * r2 / 2; // not correct
	}
	
	/** Get the radius (relative to the center) of the ellipse at current angle */
	public double getRadius(double angle){
		return majorRadius*minorRadius/Math.sqrt(Math.pow(minorRadius*Math.cos(angle), 2) + Math.pow(majorRadius*Math.sin(angle), 2));
	}

	@Override
	/** Return the w ratio for cell (i,j) */
	public double wRatio (int row, int column){
		
		int numSample = 4;
		int counter = 0;
		
		for (int i=0; i<numSample; i++){
			for(int j=0; j<numSample; j++){
				double xCoord = row + (i+0.5)/numSample - x.x;
				double yCoord = column + (j+0.5)/numSample - x.y;
				//Transform to ellipse coordinate
				// subject to check
				double xCoordPrime = Math.cos(theta)*xCoord - Math.sin(theta)*yCoord;
				double yCoordPrime = Math.sin(theta)*xCoord + Math.cos(theta)*yCoord;
				
				if (Math.pow(xCoordPrime, 2.0)/(majorRadius*majorRadius) + Math.pow(yCoordPrime, 2.0)/(minorRadius*minorRadius) <= 1.0){
					counter +=1;
				}
			}
		}
		
		return counter * 1.0/(numSample*numSample);
	}
	
	@Override
	public void applyConstraintForces() {
		/// APPLY PENALTY FORCE IF IN CONTACT:
		double penDepth = 0.1 - (x.y-this.getRadius(-Math.PI-theta)); // subject to check
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
	public synchronized void display(GL2 gl){
		/// SETUP TRANSFORM: 
		gl.glPushMatrix();
		{
			applyGLTransform(gl);

			{///DISPLAY:
				draw (gl);
			}
		}
		gl.glPopMatrix();
	}
	
	public void draw(GL2 gl){
    	if(texture == null){
    		try {
    			texture = new Texture2D(gl, "images/yinYang.gif");
    		}
    		catch (IOException e) {
    			System.out.print("Cannot load texture: ");
    			System.out.println(e.getMessage());
    		}
    	}

		texture.use();

		//float cx = (float) (this.x.x/Constants.N);
		//float cy = (float) (this.x.y/Constants.N);
		gl.glLineWidth(3.0f);
		gl.glBegin(GL2.GL_POLYGON);
		int intervals = 100;
		for (int i=0; i<intervals; i++)
		{
			// NOT RIGHT
			double degInRad = ((double)i) * 2.0 * Math.PI / ((double)intervals);
			float r = (float) this.getRadius(degInRad)/Constants.N;
			//Point2d pos = new Point2d(Math.cos(degInRad)*r, Math.sin(degInRad)*r);
			//double xCoordPrime = Math.cos(theta)*pos.x - Math.sin(theta)*pos.y;
			//double yCoordPrime = Math.sin(theta)*pos.y + Math.cos(theta)*pos.y;
			gl.glVertex2d(Math.cos(degInRad)*r, Math.sin(degInRad)*r);
			gl.glTexCoord2d(0.5 + Math.cos(degInRad)*0.5, 0.5 + Math.sin(degInRad)*0.5);
			gl.glColor3d(1.0, 215.0/256.0, 0.0);
		}

		gl.glEnd();
		texture.unuse();
	}
}