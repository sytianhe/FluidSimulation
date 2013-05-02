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

		float something = 0;// ;)
		for(int i=1; i<=n; i++) {
			for(int j=1; j<=n; j++) {

				// GRAD(rho - rhoGoal)  (ON EDGES, x,y,X,Y):
				float g_x = something;///FIX ME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				float g_X = something;///FIX ME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				float g_y = something;///FIX ME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				float g_Y = something;///FIX ME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

				// DIFFUSION COEFF (AVERAGE ON EDGES, x,y,X,Y):
				float D_x = something;/// FIX! 
				float D_X = something;/// FIX! 
				float D_y = something;/// FIX! 
				float D_Y = something;/// FIX! 

				// DIV H = DIV (D GRAD(rho-rhoGoal)) (USING EDGE-BASED H=D GRAD(...)):
				rate[I(i,j)] = Constants.V_g * something;///!!!!!!!!!!!!!!!!!!!!!!!
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

		/// DO SOMETHING HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}


	private final int I(int i, int j) { return Constants.I(i,j); }

}
