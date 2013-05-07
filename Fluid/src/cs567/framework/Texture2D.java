package cs567.framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Texture2D extends TextureTwoDim {
	public Texture2D(GL2 gl)
	{
		super(gl, GL2.GL_TEXTURE_2D, GL2.GL_RGBA);
	}
	
	public Texture2D(GL2 gl, int internalFormat)
	{
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
	}
	
	public Texture2D(GL2 gl, String filename) throws IOException
	{
		this(gl, filename, GL2.GL_RGBA);
	}
	
	public Texture2D(GL2 gl, String filename, int internalFormat) throws IOException
	{		
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
		File file = new File(filename);		
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
		if (data.getMustFlipVertically())
		{
			BufferedImage image = ImageIO.read(file);
			ImageUtil.flipImageVertically(image);
			data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);			
		}
		setImage(data);		
	}
	
	public Texture2D(GL2 gl, File file) throws IOException
	{
		this(gl, file, GL2.GL_RGBA);		
	}
	
	public Texture2D(GL2 gl, File file, int internalFormat) throws IOException
	{
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
		if (data.getMustFlipVertically())
		{
			BufferedImage image = ImageIO.read(file);
			ImageUtil.flipImageVertically(image);
			data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
		}
		setImage(data);
	}
	
	@Override
	public void allocate(int width, int height, int format, int type)
	{
		super.allocate(width, height, format, type);		
	}
}
