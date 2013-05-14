package cs567.smoke;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.Screenshot;


/**
 * 
 * CS567: Assignment #4 "Interactive Smoke Control" main class. 
 * 
 * Modify as needed.
 * 
 * @author Doug James, April 2007
 */
public class smokeWithColor implements GLEventListener
{
	/** Frame dimensions. */
	static final int dim = 600; 

	/** Reference to current FrameExporter, or null if no frames being
	 * dumped. */
	FrameExporter frameExporter;

	int N_STEPS_PER_FRAME = 1;

	/** Size of time step (in seconds). */
	public float dt = Constants.dt;

	/** Size of grid. */
	private int n = Constants.n;

	/** Main window frame. */
	JFrame frame = null;

	private int width, height;


	/** Toggle to advance simulation. */
	boolean simulate      = false;
	boolean veldisplay    = false;
	boolean forcedisplay  = false;

	/** Draws wireframe if true, and pixel blocks if false. */
	boolean drawWireframe = false;

	FluidSolver fs;
	FluidSolver fsR;
	FluidSolver fsG;
	FluidSolver fsB;
	
	ArrayList <RigidBody> rbs;

	MyMouseHandler mouseHandler;

	SmokeControlForces control;
	SmokeControlForces controlR;
	SmokeControlForces controlG;
	SmokeControlForces controlB;

	SmokeKeyframe[]    keyframes;
	SmokeKeyframe[]    keyframesR;
	SmokeKeyframe[]    keyframesG;
	SmokeKeyframe[]    keyframesB;
	
	ArrayList <Vector3f> colorCollection;
	Vector3f currentColor;
	int currentColorIndex;
	
	BufferedImage backGroundimage;

	/** 
	 * Main constructor. Call start() to begin simulation. 
	 * 
	 * @param imageKeyframes Image keyframes to use as smoke targets.
	 */
	smokeWithColor(String[] imageKeyframes) throws IOException {
		if(imageKeyframes==null)      throw new NullPointerException("imageKeyframes was null");
		if(imageKeyframes.length < 1) throw new NullPointerException("No image keyframes");

		fs = new FluidSolver(); /// <<-- PARAMETERS IN "Constants" CLASS
		fsR = new FluidSolver(); /// <<-- PARAMETERS IN "Constants" CLASS
		fsG = new FluidSolver(); /// <<-- PARAMETERS IN "Constants" CLASS
		fsB = new FluidSolver(); /// <<-- PARAMETERS IN "Constants" CLASS


		control = new SmokeControlForces();
		controlR = new SmokeControlForces();
		controlG = new SmokeControlForces();
		controlB = new SmokeControlForces();

		loadKeyframes(imageKeyframes);
		control.setKeyframe(keyframes[0]);
		controlR.setKeyframe(keyframesR[0]);
		controlG.setKeyframe(keyframesG[0]);
		controlB.setKeyframe(keyframesB[0]);

		fs.setSmokeControl(control);
		fsR.setSmokeControl(controlR);
		fsG.setSmokeControl(controlG);
		fsB.setSmokeControl(controlB);

		fs.setNumerofFrame(N_STEPS_PER_FRAME);
		fsR.setNumerofFrame(N_STEPS_PER_FRAME);
		fsG.setNumerofFrame(N_STEPS_PER_FRAME);
		fsB.setNumerofFrame(N_STEPS_PER_FRAME);

		
		// Set up colors
		currentColorIndex = 0;
		colorCollection = new ArrayList <Vector3f>();
		colorCollection.add(new Vector3f(1f, 1f, 1f));
		colorCollection.add(new Vector3f(186f/256f, 85f/256f, 211f/256f));
		colorCollection.add(new Vector3f(255f/256f, 250f/256f, 205f/256f));
		colorCollection.add(new Vector3f(240f/256f, 255f/256f, 240f/256f));
		colorCollection.add(new Vector3f(240f/256f, 255f/256f, 255f/256f));
		colorCollection.add(new Vector3f(255f/256f, 240f/256f, 245f/256f));
		colorCollection.add(new Vector3f(132f/256f, 112f/256f, 255f/256f));
		colorCollection.add(new Vector3f(0f/256f, 255f/256f, 255f/256f));
		colorCollection.add(new Vector3f(127f/256f, 255f/256f, 0f/256f));
		colorCollection.add(new Vector3f(173f/256f, 255f/256f, 47f/256f));
		colorCollection.add(new Vector3f(255f/256f, 255f/256f, 255f/256f));
		colorCollection.add(new Vector3f(255f/256f, 0f/256f, 0f/256f));
		currentColor = new Vector3f(1f,1f,1f);


//		RigidCircle rb1 = new RigidCircle(new Point2d(50,90), new Vector2d(0.0,0.0), 0, 0, 1.5, 5);
//		RigidEllipse rb2 = new RigidEllipse(new Point2d(40,70), new Vector2d(0.0,0.0), 0, 0, 10, 6, 3);
//		RigidDisk rb2 = new RigidDisk(new Point2d(50,90), new Vector2d(0.0,0.0), 0, 0, 1.5, 5);
//		RigidEllipse2 rb2 = new RigidEllipse2(new Point2d(50,90), new Vector2d(0.0,0.0), 0, 0, 1.5, 2.0, 1);
//		RigidHalfEllipse rb2 = new RigidHalfEllipse(new Point2d(50,90), new Vector2d(0.0,0.0), -1.5, 0, 2, 8, 4);

//		rbs = new ArrayList<RigidBody>();
//		rbs.add(rb1);
//		rbs.add(rb2);
//		fs.addRigidBodies(rbs);	
		
		// SET UP BACKGROUND
		File file = new File("images/flower2.jpg");		
		backGroundimage = ImageIO.read(file);
	}

	/** Loads/Constructs "keyframes" */
	private void loadKeyframes(String[] imageFilenames) throws IOException
	{
		keyframes = new SmokeKeyframe[imageFilenames.length];
		keyframesR = new SmokeKeyframe[imageFilenames.length]; 
		keyframesG = new SmokeKeyframe[imageFilenames.length];
		keyframesB = new SmokeKeyframe[imageFilenames.length];
		for(int k=0; k<imageFilenames.length; k++) {
			keyframes[k] = new SmokeKeyframe(imageFilenames[k]);
			keyframesR[k] = new SmokeKeyframe(imageFilenames[k], 0); //RED
			keyframesG[k] = new SmokeKeyframe(imageFilenames[k], 1); //GREEN
			keyframesB[k] = new SmokeKeyframe(imageFilenames[k], 2); //BLUE
		}	
	}

	/**
	 * Builds/shows window, and starts simulator.
	 */
	public void start()
	{
		if(frame != null) return;

		frame = new JFrame("Colorful Smokes!");
		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		mouseHandler = new MyMouseHandler();
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseHandler);

		canvas.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				dispatchKey(e.getKeyChar(), e);
			}
		});

		frame.add(canvas);

		final Animator animator = new Animator(canvas);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});

		frame.pack();
		frame.setSize(dim, dim);
		frame.setLocation(200, 0);
		frame.setVisible(true);
		animator.start();
	}



	private OrthoMap orthoMap;

	/** Maps mouse event into computational cell using OrthoMap. */
	public Point2d getPoint2d(MouseEvent e) {
		return orthoMap.getPoint2d(e);
	}

	/** GLEventListener implementation: Initializes JOGL renderer. */
	public void init(GLAutoDrawable drawable) 
	{
		// DEBUG PIPELINE (can use to provide GL error feedback... disable for speed)
		//drawable.setGL(new DebugGL(drawable.getGL()));

		GL2 gl = drawable.getGL().getGL2();
		System.err.println("INIT GL IS: " + gl.getClass().getName());

		gl.setSwapInterval(1);

		gl.glLineWidth(1);

	}

	/** GLEventListener implementation */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

	/** GLEventListener implementation */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
	{
		System.out.println("width="+width+", height="+height);
		height = Math.max(height, 1); // avoid height=0;

		this.width  = width;
		this.height = height;

		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0,0,width,height);	

		/// SETUP ORTHOGRAPHIC PROJECTION AND MAPPING INTO UNIT CELL:
		gl.glMatrixMode(GL2.GL_PROJECTION);	
		gl.glLoadIdentity();			
		orthoMap = new OrthoMap(width, height);//Hide grungy details in OrthoMap
		orthoMap.apply_glOrtho(gl);

		/// GET READY TO DRAW:
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}


	/** 
	 * Main event loop: OpenGL display + simulation
	 * advance. GLEventListener implementation.
	 */
	public void display(GLAutoDrawable drawable) 
	{
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0,0,0,0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		{/// DRAW COMPUTATIONAL CELL BOUNDARY:
			gl.glBegin(GL.GL_LINE_STRIP);
			if(simulate)
				gl.glColor3f(0,0,0);
			else 
				gl.glColor3f(1,0,0);
			gl.glVertex2d(0,0);	gl.glVertex2d(1,0);	gl.glVertex2d(1,1);	gl.glVertex2d(0,1);	gl.glVertex2d(0,0);
			gl.glEnd();
		}

		if (drawWireframe)  gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

		simulateAndDisplayScene(gl);/// <<<-- MAIN CALL

		if(frameExporter != null)  frameExporter.writeFrame();
	}

	/** Simulate then display particle system and any builder
	 * adornments. */
	void simulateAndDisplayScene(GL2 gl)
	{
		if(simulate) {/// TAKE dt step
			for(int s=0; s<N_STEPS_PER_FRAME; s++) {
				fs.velocitySolver();/// <<<-- with control forces
				fs.densitySolver();
				
				fsR.velocitySolver();/// <<<-- with control forces
				fsR.densitySolver();
				fsG.velocitySolver();/// <<<-- with control forces
				fsG.densitySolver();
				fsB.velocitySolver();/// <<<-- with control forces
				fsB.densitySolver();
			}
		}



		////////////////////
		// DRAW 
		////////////////////
		if(false) {/// DRAW POINTS:
			gl.glPointSize(Math.min(width,height)/n-1);
			gl.glBegin(gl.GL_POINTS);
			/// DON'T DRAW 0, n+1 border:
			for(int i=1; i<=n; i++) {
				float x = (i - 0.5f)/(float)n;

				for(int j=1; j<=n; j++) {
					float y = (j - 0.5f)/(float)n;

					float d = 2*fs.getDensity(i,j);
					if(d < 0) d=0;
					gl.glColor3f(d, d, d);

					gl.glVertex2f(x, y);
				}
			}
			gl.glEnd();
		}
		
		int width = backGroundimage.getWidth();
		int height = backGroundimage.getHeight();

		if(true) {//DRAW RASTER:

			int N = n + 2;
			float h = 1.f/(float)n;
			for(int row=0; row<N-1; row++) {
				gl.glBegin(gl.GL_QUAD_STRIP);

				float y = row * h;

				for(int i=0; i<=n+1; i++) {
					
					int xx = (int) (row * 1.0 /N * width);
					int yy = (int) (i * 1.0 /N * height);
					int pixel = backGroundimage.getRGB(xx, yy);
					
					/* getRGB() returns an ARGB-packed int */
//					int alpha = (pixel >> 24) & 0xff;
//					int red   = (pixel >> 16) & 0xff;
//					int green = (pixel >> 8)  & 0xff;
//					int blue  = (pixel >> 0)  & 0xff;
					
					float x = (i - 0.5f) * h;

					float d = getDrawDensityRGB(i,row, 0);
					//gl.glColor3f(d * currentColor.x, d * currentColor.y, d * currentColor.z);
					//gl.glColor3f(d * red / 256f, d * green / 256f, d * blue / 256f);
					gl.glColor3f(getDrawDensityRGB(i, row, 0), getDrawDensityRGB(i, row, 1), getDrawDensityRGB(i, row, 2));
					gl.glVertex2f(x, y);

					d = getDrawDensityRGB(i,row+1, 0);
					//gl.glColor3f(d * currentColor.x, d * currentColor.y, d * currentColor.z);
					//gl.glColor3f(d * red / 256f, d * green / 256f, d * blue / 256f);
					gl.glColor3f(getDrawDensityRGB(i, row+1, 0), getDrawDensityRGB(i, row+1, 1), getDrawDensityRGB(i, row+1, 2));
					gl.glVertex2f(x, y+h);
				}
				gl.glEnd();
			}

		}

		if(rbs!=null){
			for (RigidBody rb: rbs){
				rb.display(gl);
			}
		}
		if(veldisplay){
			/// DON'T DRAW 0, n+1 border:
			for(int i=1; i<=n; i++) {
				gl.glBegin(gl.GL_LINES);
				gl.glLineWidth(0.05f);
				gl.glColor3f(1.0f, 0.0f, 0.0f);
				float x = (i + 0.5f)/(float)n;

				for(int j=1; j<=n; j++) {
					float y = (j + 0.5f)/(float)n;
					Vector2f temp = new Vector2f(fs.u[Constants.I(i,j)], fs.v[Constants.I(i,j)]);
					//System.out.println(temp);
					temp.scale(0.1f);
					gl.glVertex2f(x, y);
					float u = temp.x;
					float v = temp.y;
					gl.glVertex2f(x+u, y+v);

				}
				gl.glEnd();
			}
		}
		if(forcedisplay){
			/// DON'T DRAW 0, n+1 border:
			for(int i=1; i<=n; i++) {
				gl.glBegin(gl.GL_LINES);
				gl.glLineWidth(0.1f);
				gl.glColor3f(0.0f, 1.0f, 0.0f);
				float x = (i - 0.5f)/(float)n;

				for(int j=1; j<=n; j++) {
					float y = (j - 0.5f)/(float)n;
					Vector2f temp = new Vector2f(fs.fx[Constants.I(i,j)], fs.fy[Constants.I(i,j)]);
					gl.glVertex2f(x, y);
					float u = 0.1f*temp.x;
					float v = 0.1f*temp.y;
					gl.glVertex2f(x+u, y+v);

				}
				gl.glEnd();
			}
		}
	}

	private float getDrawDensity(int i, int j) { 
		float d = 2*fs.getDensity(i,j);///arbitrary scaling
		if(d < 0) d=0;
		return d;
	}

	private float getDrawDensityRGB(int i, int j, int rgb) { 
		float d = 0;
		if (rgb == 0){
			d = 2*fsR.getDensity(i,j);///arbitrary scaling
		}
		else if (rgb == 1){
			d = 2*fsG.getDensity(i,j);///arbitrary scaling
		}
		else if (rgb == 2){
			d = 2*fsB.getDensity(i,j);///arbitrary scaling
		}
		else{
			System.out.println("Wrong index!!!");
		}
		
		if(d < 0) d=0;
		return d;
	}
	
	/** 
	 * Modify this class to improve mouse interaction.
	 */ 
	class MyMouseHandler implements MouseListener, MouseMotionListener
	{
		MyMouseHandler() { }

		// Methods required for the implementation of MouseListener
		public void mouseEntered (MouseEvent e) { }
		public void mouseExited  (MouseEvent e) { }
		public void mouseReleased(MouseEvent e) { }
		public void mouseClicked (MouseEvent e) { }
		public void mouseMoved   (MouseEvent e) { }


		int      button;
		Point2d  pOld = new Point2d();
		Point2d  p    = new Point2d();
		int      i, j; // of p

		public void mousePressed(MouseEvent e)
		{
			// save button event
			button = e.getButton();
			//System.out.println("button = "+button);

			updateLocation(e);
		}

		public void mouseDragged(MouseEvent e)
		{
			updateLocation(e);

			/// ADD DENSITY
			if (button == 1) {
				float c  = Math.max(100, 2*n);
				float cb = c * 0.1f;
				fs.dOld[fs.I(i, j)]   = c;
				fs.dOld[fs.I(i+1, j)] = cb; 
				fs.dOld[fs.I(i-1, j)] = cb; 
				fs.dOld[fs.I(i, j-1)] = cb; 
				fs.dOld[fs.I(i, j+1)] = cb; 
				
				fsR.dOld[fs.I(i, j)]   = c;
				fsR.dOld[fs.I(i+1, j)] = cb; 
				fsR.dOld[fs.I(i-1, j)] = cb; 
				fsR.dOld[fs.I(i, j-1)] = cb; 
				fsR.dOld[fs.I(i, j+1)] = cb;
				
				fsG.dOld[fs.I(i, j)]   = c;
				fsG.dOld[fs.I(i+1, j)] = cb; 
				fsG.dOld[fs.I(i-1, j)] = cb; 
				fsG.dOld[fs.I(i, j-1)] = cb; 
				fsG.dOld[fs.I(i, j+1)] = cb;
				
				fsB.dOld[fs.I(i, j)]   = c;
				fsB.dOld[fs.I(i+1, j)] = cb; 
				fsB.dOld[fs.I(i-1, j)] = cb; 
				fsB.dOld[fs.I(i, j-1)] = cb; 
				fsB.dOld[fs.I(i, j+1)] = cb;
			}

			/// ADD VELOCITY/FORCE (should scale by DT)
			if (button == 3) {
				fs.uOld[fs.I(i, j)] = (float)(p.x - pOld.x) * 500000;
				fs.vOld[fs.I(i, j)] = (float)(p.y - pOld.y) * 500000;
				
				fsR.uOld[fs.I(i, j)] = (float)(p.x - pOld.x) * 500000;
				fsR.vOld[fs.I(i, j)] = (float)(p.y - pOld.y) * 500000;
				
				fsG.uOld[fs.I(i, j)] = (float)(p.x - pOld.x) * 500000;
				fsG.vOld[fs.I(i, j)] = (float)(p.y - pOld.y) * 500000;

				fsB.uOld[fs.I(i, j)] = (float)(p.x - pOld.x) * 500000;
				fsB.vOld[fs.I(i, j)] = (float)(p.y - pOld.y) * 500000;
			}
		}

		private void updateLocation(MouseEvent e)
		{
			pOld.set(p);
			p = orthoMap.getPoint2d(e);
			i = (int) (p.x * n) + 1;
			j = (int) (p.y * n) + 1;
			if(i<1) i=1;	    if(j<1) j=1;
			if(i>n) i=n;	    if(j>n) j=n;
		}
	}


	/**
	 * Handles keyboard events, e.g., spacebar toggles
	 * simulation/pausing, and escape resets the current Task.
	 */
	public void dispatchKey(char key, KeyEvent e)
	{
		//System.out.println("CHAR="+key+", keyCode="+e.getKeyCode()+", e="+e);
		if(key == ' ') {//SPACEBAR --> TOGGLE SIMULATE
			simulate = !simulate;
		}
		else if (key == 'r') {//RESET
			System.out.println("RESET!");
			simulate = false;
			frameExporter = null;
			fs.reset();
			fsR.reset();
			fsG.reset();
			fsB.reset();
		}
		else if (key == 'w') {//RESET
			System.out.println("WIREFRAME");
			drawWireframe = !drawWireframe;
		}
		else if (key == 'v') {// velocity field display
			veldisplay = !veldisplay;
			if (veldisplay){
				System.out.println("VELOCITY FIELD ON");
			}
			else {
				System.out.println("VELOCITY FIELD OFF");
			}
			forcedisplay = false;
		}
		else if (key == 'f') {// force field display
			forcedisplay = !forcedisplay;
			if (forcedisplay){
				System.out.println("FORCE FIELD ON");
			}
			else{
				System.out.println("FORCE FIELD OFF");
			}
			veldisplay = false;
		}
		else if (e.toString().contains("Escape")) {
			System.out.println("ESCAPE");
		}
		else if (key == 'e') {//toggle exporter
			frameExporter = ((frameExporter==null) ? (new FrameExporter()) : null);
			System.out.println("'e' : frameExporter = "+frameExporter);
		}
		else if (key == 'q') {//change colors
			if (currentColorIndex < colorCollection.size()-1){
				currentColorIndex += 1;
				currentColor = colorCollection.get(currentColorIndex);
			}
			else{
				currentColorIndex = 0;
				currentColor = colorCollection.get(currentColorIndex);
			}
		}
		else {

			for(int k=0; k<keyframes.length; k++) {
				if(key == (new String(""+(k+1))).charAt(0)) {
					//System.out.println("KEY: "+key);
					control.setKeyframe(keyframes[k]); 
					controlR.setKeyframe(keyframesR[k]); 
					controlG.setKeyframe(keyframesG[k]); 
					controlB.setKeyframe(keyframesB[k]); 
				}
			}
		}

	}

	/** 
	 * ID of latest FrameExporter 
	 */
	private static int exportId = -1;

	/**
	 * Code to dump frames---very useful for slow/large runs. 
	 */
	private class FrameExporter
	{
		private int nFrames  = 0;

		FrameExporter()  { 
			exportId += 1;
		}

		void writeFrame()
		{ 
			long   timeNS   = -System.nanoTime();
			String number   = Utils.getPaddedNumber(nFrames, 5, "0");
			String filename = "frames/export"+exportId+"-"+number+".png";/// BUG: DIRECTORY MUST EXIST!

			try{  
				java.io.File   file     = new java.io.File(filename);
				if(file.exists()) System.out.println("WARNING: OVERWRITING PREVIOUS FILE: "+filename);

				/// WRITE IMAGE: ( :P Screenshot asks for width/height --> cache in GLEventListener.reshape() impl)
				Screenshot.writeToFile(file, width, height);

				timeNS += System.nanoTime();
				System.out.println((timeNS/1000000)+"ms:  Wrote image: "+filename);

			}catch(Exception e) { 
				e.printStackTrace();
				System.out.println("OOPS: "+e); 
			} 

			nFrames += 1;
		}
	}


	/**
	 * ### Ready. Set. Starting Smoking! ... Control! Control! ###
	 */
	public static void main(String[] args) 
	{
		try{
			System.out.println("Smoke! \nUSAGE: args[k] = k'th keyframe image filename");
			String[] imageKeyframes = {
					"images/cornellLogo.png", 
					"images/yinYang2.png",
					"images/Cornell-logo1.jpg",
			};
			if(args.length > 0)  imageKeyframes = args;
			System.out.println("Images: "+(Arrays.asList(imageKeyframes)));

			smokeWithColor sim = new smokeWithColor(imageKeyframes);
			sim.start();

		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("OOPS: "+e);
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}
