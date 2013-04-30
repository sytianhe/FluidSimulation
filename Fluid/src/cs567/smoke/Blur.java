package cs567.smoke;

/** 
 * Blur operator. Starter code is based on a simple local iteration,
 * and is slow for large kernel support. Implement a better blurring
 * operator!
 *
 * @author Doug James, April 2007.
 */
class Blur
{
    /** TWEAKABLE iterative-blur parameter: Self-weighting parameter affecting
     * the rate of local averaging (1=no blurring; 0.9=moderate
     * blurring). */
    public static final float alpha = 0.90f;//// <<< TWEAKABLE

    private static int   n, N;
    private static int   size;
    private static float[] blurTmp;

    /** Must call to setup Blur operator--bad design. */
    static void init(int n, int size) //, float alpha) 
    {
	Blur.n     = n;
	Blur.size  = size;
	//Blur.alpha = alpha;
	N = n + 2;

	blurTmp = new float[size];
    }

    /** 
     * Blurs f into fBlur.
     */
    static void blur(float[] f, float[] fBlur) 
    {
	if(f    ==null) throw new NullPointerException("f was null");
	if(fBlur==null) throw new NullPointerException("fBlur was null");
	if(n==0) throw new RuntimeException("Must call init() first.");

	/// COPY f --> fBlur:
	for(int i=0; i<size; i++)  fBlur[i] = f[i];	

	for(int iter=0; iter<n/2; iter++)   blurTwice(fBlur);
    }

    // util method for indexing 1d arrays
    private static final int I(int i, int j){ return Constants.I(i,j); }

    /** Blurs f twice: first into a temporary array, and then it blurs
     * that back into f. */
    private static void blurTwice(float[] f) 
    {
	if(n==0) throw new RuntimeException("Must call init() first.");

	float[] g = blurTmp;

	/// COPY (FOR BOUNDARY): (SLOTH)
	//for(int i=0; i<size; i++)  g[i] = f[i];

	float beta = 0.25f*(1-alpha);

	/// BLUR: f --> g:
	for(int i=1; i<=n; i++)  for(int j=1; j<=n; j++) 
	    g[I(i,j)] = alpha*f[I(i,j)] + beta*( f[I(i-1,j)] + f[I(i+1,j)] + f[I(i,j-1)] + f[I(i,j+1)] );	

	/// BLUR: g --> f:
	for(int i=1; i<=n; i++)  for(int j=1; j<=n; j++) 
	    f[I(i,j)] = alpha*g[I(i,j)] + beta*( g[I(i-1,j)] + g[I(i+1,j)] + g[I(i,j-1)] + g[I(i,j+1)] );
    }

}
