package cs567.smoke;

import java.io.IOException;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import cs567.framework.Texture2D;


public class RigidCircle extends RigidBody
{
	/** Radius */
	double 	 radius;
	private Texture2D texture;
	
	public RigidCircle(Point2d centerOfMass, Vector2d LinVel,double theta, double AngVel, double d, int r) {
		super(centerOfMass, LinVel,theta, AngVel, d);
		radius =r;
		maxRadius = r;
		mass = Math.PI*r*r*d;
		momentOfInertia = mass * r * r / 2;
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
		// loading texture
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

		float r = (float) (radius/Constants.N);
		gl.glLineWidth(3.0f);
		gl.glBegin(GL2.GL_POLYGON);
		int intervals = 100;
		for (int i=0; i<intervals; i++)
		{
			double degInRad = ((double)i) * 2.0 * Math.PI / ((double)intervals);
			Point2d pos = new Point2d(Math.cos(degInRad+theta)*r, Math.sin(degInRad+theta)*r);
			gl.glVertex2d(pos.x, pos.y);
			gl.glTexCoord2d(0.5 + Math.cos(degInRad)*0.5, 0.5 + Math.sin(degInRad)*0.5);
			gl.glColor3d(1.0, 215.0/256.0, 0.0);
		}

		gl.glEnd();
		texture.unuse();
	}
}