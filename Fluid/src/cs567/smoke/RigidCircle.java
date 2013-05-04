package cs567.smoke;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RigidCircle extends RigidBody
{
	/** Radius */
	double 	 radius = 0;
	
	public RigidCircle(Point2d centerMass, Vector2d LinVel, double AngVel, double r, double d) {
		super(centerMass, LinVel, AngVel, d);
		radius = r;
		mass = Math.PI*r*r*d;

	}

	/** Return the w ratio for cell (i,j) */
	public double wRatio (int row, int column){
		
		int numSample = 3;
		int counter = 0;
		double step = 1.0/Constants.N;
		
		for (int i=0; i<numSample; i++){
			for(int j=0; j<numSample; j++){
				double xCoord = row*step + (i+0.5)*step/numSample;
				double yCoord = column*step + (j+0.5)*step/numSample;
				if (Math.pow(xCoord - x.x, 2.0) + Math.pow(yCoord - x.y, 2.0) < radius*radius){
					counter +=1;
				}
			}
		}
		
		return counter * 1.0/(numSample*numSample);
	}
}