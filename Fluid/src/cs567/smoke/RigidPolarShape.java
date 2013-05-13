package cs567.smoke;


import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;


/**
 * Rigid Body described by arbitrary polar function.
 * 
 * @author homoflashmanicus
 */
public class RigidPolarShape extends RigidBody {
	
	double maxRadius;

	public RigidPolarShape(Point2d centerOfMass, Vector2d LinVel, double theta,
		double AngVel, double d) {
		super(centerOfMass, LinVel, theta, AngVel, d);
	}
	
	public void init(){
		computeVolume();
		computeMomentOfInertia();
		mass = density * volume;
	}
	
	/** Specify a radial function for all subclasses. 
	 *	Ideally, this function is periodic and accepts and angle.
	 */
	public double radialFunction(double theta){
		return 0;
	}
	
	/** Compute the volume (area) of the shape numerically. 
	 *  Might as well compute the madRadius here too.
	 */
	public void computeVolume(){
		volume = 0;

		int nSteps = 10000;
		double dTheta = 2.0 * Math.PI / (double)nSteps;
		double r=0;
		double rMax=0;
		
		for (int i=0 ; i<nSteps ; i ++){
			r = radialFunction(i*dTheta);
			volume += 0.5 * Math.pow(r, 2.0) * dTheta;
			if (r > rMax){
				rMax = r;
			}
		}
		
		maxRadius = rMax;
	}
	
	/** Compute the momentOfInertia of the shape numerically. */
	public void computeMomentOfInertia(){
		momentOfInertia = 0;

		int nStepsTheta = 1000;
		int nStepsR = 10000;		
		double dTheta = 2.0 * Math.PI / (double)nStepsTheta;
		
		for (int i=0 ; i<nStepsTheta ; i ++){
			double r = radialFunction(i*dTheta);
			double dR = r / (double)nStepsR;
			for (int j=0; j< nStepsR ; j++){
				momentOfInertia +=  density * Math.pow( j*dR, 3.0)  * dR *  dTheta;
			}
		}
	}
	
	
	@Override
	/** Return the w ratio for cell (i,j) 
	 *  Kindof inefficient...oh well.
	 */
	public double wRatio (int row, int column){
		
		int numSample = 6;
		int counter = 0;
		Vector2d sep = new Vector2d();
		
		sep.set(row + 0.5,  column + 0.5);
		sep.sub(x);
		this.transformW2B(sep);
		double angle = Math.atan2(sep.y, sep.x);
		double r = radialFunction(angle);
		if (sep.length() <= r - 2){
			return 1;
		}
		else if(sep.length() > r + 2){
			return 0;
		}
		else{
			//Perform subsampleing to 
			for (int i=0; i<numSample; i++){
				for(int j=0; j<numSample; j++){
					sep.set(row + (i+0.5)/numSample,  column + (j+0.5)/numSample);
					sep.sub(x);
					this.transformW2B(sep);
					angle = Math.atan2(sep.y, sep.x);
					r = radialFunction(angle);
					if (sep.length() <= r){
						counter +=1;
					}
				}
			}
			return counter * 1.0/(numSample*numSample);
		}		
	}
	
	@Override
	public void applyConstraintForces() {
		/// APPLY SIMPLY PENALTY FORCE TO KEEP SHAPE FROM FALLING THROUGH THE FLOOR:
		double penDepth = 0.1 - (x.y-maxRadius);
		if(penDepth > 0) {//overlap
			/// PENALTY CONTACT FORCE:
			double k     = Constants.CONTACT_STIFFNESS * mass;
			double f =  k * penDepth;

			/// DAMPING: 
			double fDamp = + 0.004 * k * (v.y);
			
			force.y+= f + fDamp;
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

		gl.glLineWidth(1.0f);
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glColor3f(color.x, color.y, color.z);
		
		int intervals = 100;
		for (int i=0; i<intervals; i++)
		{
			double theta1 = ((double)i) * 2.0 * Math.PI / ((double)intervals);
			double theta2 = ((double)(i+1)) * 2.0 * Math.PI / ((double)intervals);
			double r1 = this.radialFunction(theta1);
			double r2 = this.radialFunction(theta2);
			gl.glVertex2d(0,0);
			gl.glVertex2d(Math.cos(theta1)*r1/Constants.N, Math.sin(theta1)*r1/Constants.N);			
			gl.glVertex2d(Math.cos(theta2)*r2/Constants.N, Math.sin(theta2)*r2/Constants.N);			
		}

		gl.glEnd();
	}
}
