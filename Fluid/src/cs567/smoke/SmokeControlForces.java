package cs567.smoke;


/**
 * Control forces based on [Fattal and Lischinski 2004].
 * 
 * @author Doug James, April 2007.
 */
public class SmokeControlForces
{
	int         n, size;

	/** Temporary blurring field */
	float[]     rhoBlur;

	/** Current active keyframe. */
	SmokeKeyframe keyframe = null;

	/** Builds forces for the specified solver. */
	SmokeControlForces() //FluidSolver solver)
	{
		n    = Constants.n;
		size = Constants.size; 

		Blur.init(n, size);

		rhoBlur = new float[size];
	}

	/** Specifies active/current key frame. */
	public void setKeyframe(SmokeKeyframe K)
	{
		keyframe = K;
		System.out.println("SmokeControlForces: keyframe = "+K);
	}


	/** 
	 * Smoke control driving force. 
	 */
	public void getDrivingForce(float[] fx, float[] fy, float[] rho)
	{
		if(rho.length != fx.length) throw new IllegalArgumentException("density.length != fx.length");

		/// ZERO (fx,fy):
		for(int i=0; i<size; i++)   fx[i] = fy[i] = 0.f;

		/// ZERO IF NO KEYFRAME:
		if(keyframe==null) return;

		SmokeKeyframe currentKeyframe = keyframe; /// cache reference in case it changes!

		/// BLUR DENSITY:
		Blur.blur(rho, rhoBlur);

		/// COMPUTE FORCE: 
		for(int i=1; i<=n; i++) {
			for(int j=1; j<=n; j++) {
				int ij = I(i,j);
				fx[ij] = Constants.V_f * rhoBlur[ij] *  currentKeyframe.fdx[ij] ;
				fy[ij] = Constants.V_f * rhoBlur[ij] * currentKeyframe.fdy[ij] ;
			}
		}
	}

	/** Evalutes gathering rate: rate = V_g * G = V_g * DIV ( D GRAD
	 * (rho-rhoGoal) ), where the diffusion coefficient is D =
	 * rho*rhoGoalBlur. */
	public void getGatheringRate(float[] rate, float[] rho)
	{
		/// ZERO rate (SLOTH: only need to zero boundary):
		for(int i=0; i<size; i++)   rate[i] = 0; 

		/// ZERO IF NO KEYFRAME:
		if(keyframe==null) return;

		SmokeKeyframe currentKeyframe = keyframe; /// cache reference in case it changes!

		float[] rhoGoal     = currentKeyframe.rhoGoal;
		float[] rhoGoalBlur = currentKeyframe.rhoGoalBlur;

		for(int i=1; i<=n; i++) {
			for(int j=1; j<=n; j++) {

				// GRAD(rho - rhoGoal)  (ON EDGES, x,y,X,Y):
				float g_x = ( (rho[I(i,j)] - rhoGoal[I(i,j)])   -  (rho[I(i-1,j)] - rhoGoal[I(i-1,j)]) ) / 1f ;
				float g_X = ( (rho[I(i+1,j)] - rhoGoal[I(i+1,j)])   -  (rho[I(i,j)] - rhoGoal[I(i,j)]) ) / 1f ;
				float g_y = ( (rho[I(i,j)] - rhoGoal[I(i,j)])   -  (rho[I(i,j-1)] - rhoGoal[I(i,j-1)]) ) / 1f ;
				float g_Y = ( (rho[I(i,j+1)] - rhoGoal[I(i,j+1)])   -  (rho[I(i,j)] - rhoGoal[I(i,j)]) ) / 1f ;

//				// DIFFUSION COEFF (AVERAGE ON EDGES, x,y,X,Y):
//				float D_x = ( (rho[I(i,j)] * rhoGoalBlur[I(i,j)]) + (rho[I(i-1,j)] * rhoGoalBlur[I(i-1,j)]) ) / 2f ;
//				float D_X = ( (rho[I(i+1,j)] * rhoGoalBlur[I(i+1,j)]) + (rho[I(i,j)] * rhoGoalBlur[I(i,j)]) ) / 2f ;
//				float D_y = ( (rho[I(i,j)] * rhoGoalBlur[I(i,j)])   +  (rho[I(i,j-1)] * rhoGoalBlur[I(i,j-1)]) ) / 2f ;
//				float D_Y = ( (rho[I(i,j+1)] * rhoGoalBlur[I(i,j+1)])   + (rho[I(i,j)] * rhoGoalBlur[I(i,j)]) ) / 2f ;

				float D_x = ( (rho[I(i,j)]   + rho[I(i-1,j)] ) /2f ) * ( ( rhoGoalBlur[I(i,j)]    + rhoGoalBlur[I(i-1,j)]) / 2f ) ;
				float D_X = ( (rho[I(i+1,j)] + rho[I(i,j)] ) /2f   ) * ( ( rhoGoalBlur[I(i+1,j)]  + rhoGoalBlur[I(i,j)])   / 2f ) ;
				float D_y = ( (rho[I(i,j)]   + rho[I(i,j-1)] ) /2f ) * ( ( rhoGoalBlur[I(i,j)]    + rhoGoalBlur[I(i,j-1)]) / 2f ) ;
				float D_Y = ( (rho[I(i,j+1)] + rho[I(i,j)]   ) /2f ) * ( (rhoGoalBlur[I(i,j+1)]   + rhoGoalBlur[I(i,j)])   / 2f ) ;
				
				// DIV (D GRAD(rho-rhoGoal)) (USING EDGE-BASED H=D GRAD(...)):
				// rate[I(i,j)] = Constants.V_g * (  (D_X * g_X - D_x * g_x ) /1f + (D_Y * g_Y - D_y * g_y ) /1f );
				
				// FIRST TERM: find V dot GRAD (F)
				float V_x = (g_x + g_X)/2f;
				float V_y = (g_y + g_Y)/2f;
				float dx_F = (D_X - D_x)/1f;
				float dy_F = (D_Y - D_y)/1f;
				
				float first_term = V_x*dx_F+V_y*dy_F;
				
				// SECOND TERM: find F * GRAD V
				float F = rho[I(i,j)]*rhoGoalBlur[I(i,j)];
				float dxVx = (g_X - g_x)/1f;
				float dyVy = (g_Y - g_y)/1f;
				
				float second_term = F*(dxVx + dyVy);
				
				rate[I(i,j)] = Constants.V_g * (first_term + second_term);
			}
		}
	}

	/** Normalizes density sum to match keyframe density sum. To avoid
	 * sudden changes in brightness, a gain is used to make the change
	 * gradual. */
	public void normalizeDensity(float[] rho)//...)
	{
		/// IGNORE IF NO KEYFRAME:
		if(keyframe==null) return;

		float rhoGoalSum  = this.keyframe.rhoGoalSum;
		float rhoSum = Utils.sum(rho);
		for (int i = 0; i<size; i++){
			if(rho[i]>0.01){
//				rho[i] *= 1f+(rhoGoalSum -rhoSum)/rhoSum;  //More natural approach but seems to not give the 
				rho[i] += (this.keyframe.rhoGoal[i] -rho[i])/1000;  //A little hacky but it gets the job done
			}
		}
	}


	private final int I(int i, int j) { return Constants.I(i,j); }

}
