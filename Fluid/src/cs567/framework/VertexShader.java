package cs567.framework;

import javax.media.opengl.GL2;

public class VertexShader extends Shader {

	public VertexShader(GL2 glContext, String srcFile)
			throws GlslException {
		super(GL2.GL_VERTEX_SHADER, glContext, srcFile);
	}

}
