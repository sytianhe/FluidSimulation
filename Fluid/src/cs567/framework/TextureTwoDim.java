package cs567.framework;

import java.nio.Buffer;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.TextureData;

public class TextureTwoDim extends Texture {
	public TextureTwoDim(GL2 gl, int target, int internalFormat)
	{
		super(gl, target, internalFormat);
		allocated = false;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setImage(int width, int height, int format, int type, Buffer buffer)
	{
		this.width = width;
		this.height = height;
		
		Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
		if (oldTexture != this)
			bind();
		
		gl.glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, buffer);
		
		if (oldTexture == null)
			unbind();
		else if (oldTexture != this)
			oldTexture.bind();
		
		allocated = true;
	}
	
	public void setImage(TextureData data)
	{
		setImage(data.getWidth(), data.getHeight(), data.getPixelFormat(), data.getPixelType(), data.getBuffer());
	}
	
	protected void allocate(int width, int height, int format, int type)
	{
		setImage(width, height, format, type, null);
	}
	
	public boolean isAllocated()
	{
		return allocated;
	}
	
	protected int width;
	protected int height;
	protected boolean allocated;
}
