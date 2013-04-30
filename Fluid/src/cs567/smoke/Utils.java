package cs567.smoke;
import javax.vecmath.*;

import java.io.*;
import javax.media.opengl.*;
//import com.sun.opengl.util.texture.spi.TGAImage;

import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;


/** 
 * Some utilities.
 * 
 * @author Doug James, January 2007
 */
public class Utils
{
    /**
     * sum += scale*v
     */
    public static void acc(Tuple2d sum, double scale, Tuple2d v)
    {
	sum.x += scale * v.x;
	sum.y += scale * v.y;
    }

    /** Returns  (sum_i v[i]).  */
    public static float sum(float[] v) {
	float sum = 0;
	for(int i=0; i<v.length; i++)  sum += v[i];
	return sum;
    }

//     static TGAImage loadImage(String tgaFilename) throws IOException
//     {
// 	TGAImage image = TGAImage.read(tgaFilename);
// 	System.out.println("image('"+tgaFilename+"'): height="+image.getHeight()+" width="+image.getWidth());

// 	if(image.getGLFormat() != GL.GL_BGR) 
// 	    throw new RuntimeException("Only BGR TGA formats accepted.");

// 	return image;
//     }

    /**
     * 
     * @param pad Pre-padding char.
     */
    public static String getPaddedNumber(int number, int length, String pad)  {
	return getPaddedString(""+number, length, pad, true);
    }

    /**
     * @param prePad Pre-pads if true, else post pads.
     */
    public static String getPaddedString(String s, int length, String pad, boolean prePad) 
    {
	if(pad.length() != 1) throw new IllegalArgumentException("pad must be a single character.");
	String result = s;
	result.trim();
	if(result.length() > length) 
	    throw new RuntimeException
		("input string "+s+" is already more than length="+length);

	int nPad = (length - result.length());
	for(int k=0; k<nPad; k++) {
	    //System.out.println("nPad="+nPad+", result="+result+", result.length()="+result.length());
	    if(prePad) 
		result = pad + result;
	    else
		result = result + pad;
	}

	return result;
    }


    public static void main(String[] args) 
    {
	try{

	}catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("OOPS: "+e);
	}
    }

}
