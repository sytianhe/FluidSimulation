package cs567.framework;

import java.util.HashMap;

import javax.media.opengl.GL2;

public class Program {

private static final String SHADERS_BASE_DIR = "src//cs4620//shaders//";

private static Program current = null;

// ************* Static functions *************

	public static Boolean isAProgramInUse() {
		return current != null;
	}
	
	public static Program getCurrent() {
	    return current;
	}
	
	public static void unuseProgram(GL2 gl) {
		gl.glUseProgramObjectARB(0);    
	    current = null;
	}
	
// ************* Private variables *************
	
	private int id;
	private VertexShader vertexShader;
	private FragmentShader fragmentShader;
	private GL2 gl;
	
	private HashMap<String, Uniform> uniforms;
		
// ************* Public interface *************
	
	public Program(GL2 glContext, String vertexSrcFile, 
			String fragmentSrcFile) throws GlslException {
		this.vertexShader = null;
		this.fragmentShader = null;
		this.gl = glContext;
		
		this.id = gl.glCreateProgramObjectARB();
		
		// Attach shaders and link the program (may throw exception)
		buildProgram(SHADERS_BASE_DIR + vertexSrcFile, 
				SHADERS_BASE_DIR + fragmentSrcFile);
		
		// Create a hash map from all the 'active' uniform variables
		initializeUniforms();
	}
	
	public int getId() {
		return this.id;
	}
	
	public Boolean isUsed() {
		return current == this;
	}
	
	public void use() {
		this.gl.glUseProgramObjectARB(this.id);
	    current = this;
	}
	
	public void unuse() {
	    unuseProgram(this.gl);
	}
	
	public HashMap<String, Uniform> GetUniforms() {
		return this.uniforms;
	}
	
	public Uniform getUniform(String name) {
		return uniforms.get(name);
	}
	
// ************* Protected functions *************
	
	protected void finalize() {
		// Deallocate the GLSL resources
	}
	
	protected void buildProgram(String vertexSrcFile, String fragmentSrcFile) throws GlslException {
		
		this.vertexShader = new VertexShader(this.gl, vertexSrcFile);
		this.fragmentShader = new FragmentShader(this.gl, fragmentSrcFile);		
	    
	    // Attach the vertex shader
	    this.gl.glAttachShader(this.id, this.vertexShader.GetId());
	    
	    // Attach the fragment shader
	    this.gl.glAttachShader(this.id, this.fragmentShader.GetId());
	    
	    gl.glLinkProgramARB(this.id);
	    
	    // Check the linking status
		int[] linkCheck = new int[1];
		gl.glGetObjectParameterivARB(this.id,
				GL2.GL_OBJECT_LINK_STATUS_ARB, linkCheck, 0);
		
		if (linkCheck[0] == GL2.GL_FALSE) {
			throw new GlslException("Link error " + 
					Shader.getInfoLog(this.gl, this.id));
		}
	}
	
// ************* Private functions *************
	
	private void initializeUniforms() {		  
		this.uniforms = new HashMap<String, Uniform>();		   
	    
	    int[] uniformCount = new int[1];
	    this.gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_UNIFORMS, uniformCount, 0);
	    
	    System.out.print("GLSL uniforms: ");
		for(int uniform_index = 0; uniform_index < uniformCount[0]; 
			uniform_index++) {
			Uniform currUniform = new Uniform(this.gl, this, uniform_index);
									
			if ( !currUniform.getName().startsWith("gl_") ) {
				System.out.print(currUniform.getName() + " ");
				this.uniforms.put(currUniform.getName(), currUniform);								
			}
		}		
		System.out.println();
	} 
}
