package cs567.smoke;

import java.io.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.color.*;
import javax.imageio.*;

/** 
 * Smoke keyframe or "smokey frame" ;) based on an input image that is
 * resized to a square grid of resolution specified in Constants.
 * 
 * @author Doug James, April 2007.
*/
public class SmokeKeyframe
{
    /** Keyframe image loaded. */
    private String imageFilename;

    private int      N;
    private int      n;
    private int      size;

    /** Goal density of this keyframe. */
    float[]  rhoGoal;

    /** Blurred goal density. */
    float[]  rhoGoalBlur;

    /** Constant part of driving-force field:  <code>(fdx,fdy) = (GRAD rhoGoalBlur)/rhoGoalBlur.</code> */
    float[]  fdx, fdy;

    /** sum(rhoGoal) in case you need it ;) */
    float   rhoGoalSum;

    /**
     * Constructs a "smokey frame" from the specified image filename.
     * 
     * @param imageFilename Image in question. Resized, resampled, and
     * converted to grayscale image.
     */
    SmokeKeyframe(String imageFilename) throws IOException
    {
	this.imageFilename = imageFilename;

	n    = Constants.n;
	N    = n+2;
	size = N*N;/// NUMBER OF PIXELS

	/// LOAD & RESAMPLE IMAGE FOR DENSITY KEYFRAME (rhoGoal):
	rhoGoal = loadKeyframeDensity(imageFilename);

	/// BLUR rhoGoal:
	rhoGoalBlur = new float[size];
	Blur.blur(rhoGoal, rhoGoalBlur);

	/// CACHE CONSTANT DRIVING FORCE PART: fd = (GRAD rhoGoalBlur)/rhoGoalBlur:
	fdx = new float[size];
	fdy = new float[size];
	{/// TODO:
	    for(int i=1; i<=n; i++) {/// IGNORE EDGE FORCE (i=0,n+1)
		for(int j=1; j<=n; j++) {/// IGNORE EDGE FORCE (j=0,n+1)

		    // COMPUTE  (fdx,fdy) = GRAD_rhoGoalBlur / rhoGoalBlur   (Don't divide by zero!)
		    fdx[I(i,j)] = 0;/// FIX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		    fdy[I(i,j)] = 0;/// FIX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}
	    }
	}

	/// RECORD DENSITY SUM FOR SMOKE-MASS NORMALIZATION (in case you want it)
	rhoGoalSum = Utils.sum(rhoGoal);
	if(rhoGoalSum==0) throw new RuntimeException("Density keyframe had zero smoke mass... "+
						     "what kind of smoke keyframer are you!?!");
    }

    public String toString() { 
	return "SmokeKeyframe: "+imageFilename;
    }

    /** Returns keyframe density value at specified (i,j) location.  */
    public float getDensity(int i, int j) { return rhoGoal[I(i,j)]; }

    // util method for indexing 1d arrays
    private final int I(int i, int j){ return Constants.I(i,j); }


    /** Loads and resamples image to required image resolution
     * specified by Constants. */
    static float[] loadKeyframeDensity(String imageFilename) throws IOException
    {
	int N = Constants.N;
	float[] density = new float[Constants.size];

	/// LOAD IMAGE:
	File file = new File(imageFilename);
	if(!file.exists()) throw new FileNotFoundException("imageFilename="+imageFilename);
	BufferedImage image = ImageIO.read(file);

	/// RENDER TO KEYFRAME-RESOLUTION BufferedImage:
	Image renderImage = Toolkit.getDefaultToolkit().createImage
	    (new FilteredImageSource(image.getSource(), new AreaAveragingScaleFilter(N,N))); 
	BufferedImage keyframeImage = new BufferedImage(N,N,BufferedImage.TYPE_BYTE_GRAY); //INT_RGB);
	Graphics      gfx           = keyframeImage.getGraphics();
	gfx.drawImage(renderImage,0,0,null);
	gfx.dispose();
	//ImageIO.write(keyframeImage, "png", new File(imageFilename+".keyframe.png"));

	/// EXTRACT "density" DATA FROM keyframeImage:
	Raster raster = keyframeImage.getData();
	for(int i=0; i<N; i++) {
	    for(int j=0; j<N; j++) {
		//int fi = (N-1) - i;
		int fj = (N-1) - j;
		float gray = raster.getSampleFloat(i, fj, 0) / 255f;
		density[Constants.I(i,j)] = gray;
	    }
	}

	return density;
    }

    /** Test code. */
    public static void main(String[] args) 
    {
	try{
	    System.out.println("Usage: args[0]=imageFilename");
	    String imageFilename = args[0];
	    SmokeKeyframe key = new SmokeKeyframe(imageFilename);

	}catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("OOPS: "+e);
	}
    }

}