package camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.*;

import util.Color.ColorName;

import com.github.sarxos.webcam.*;

import cube.Cubie;
import cube.Cubie.Side;

public class WebcamPanelFrame extends JFrame implements AutoCloseable, WebcamListener, KeyListener, WindowListener {
	
    private Webcam webcam;
    private WebcamPanel panel;
    private JLabel instructionsLabel;
    public boolean running;
    public int BOX_SIZE = 50;
    public Point[] squareCenters = {
            new Point(-1,1),
            new Point(0,1),
            new Point(1,1),
            new Point(-1,0),
            new Point(0,0),
            new Point(1,0),
            new Point(-1,-1),
            new Point(0,-1),
            new Point(1,-1)
    };
    public ColorName[] frontFaces;
    
    Triple triples[][][] = new Triple[6][3][3];
    private int currFace = 0;
    private String result;
    
    private String[] instructionStrs = {
    		"Show the red face with the yellow face on top. Press space to continue.",
    		"Show the green face with the yellow face on top. Press space to continue.",
    		"Show the orange face with the yellow face on top. Press space to continue.",
    		"Show the blue face with the yellow face on top. Press space to continue.",
    		"Show the yellow face with the green face on top. Press space to continue.",
    		"Show the white face with the blue face on top. Press space to continue."};
    
    public WebcamPanelFrame()  {
        Dimension size = WebcamResolution.QVGA.getSize();

        setTitle("Scan");
        setSize(size.width * 2, size.height * 2);

        webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.addWebcamListener(this);

        webcam.setImageTransformer(new WebcamImageTransformer() {
            @Override
            public BufferedImage transform(BufferedImage image) {
                int w = image.getWidth();
                int h = image.getHeight();

                BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = modified.createGraphics();

                g.drawImage(image, w, 0, -w, h, null); // Flip the image
                g.transform(new AffineTransform());
                modified.flush();
                return modified;
            }
        });
        panel = new WebcamPanel(webcam, size, false);
        panel.setFPSDisplayed(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        instructionsLabel = new JLabel(instructionStrs[0]);
        instructionsLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        instructionsLabel.setOpaque(true);
        instructionsLabel.setBackground(Color.black);
        instructionsLabel.setForeground(Color.white);
        instructionsLabel.setBorder(BorderFactory.createLineBorder(Color.black, 10));
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(instructionsLabel, BorderLayout.NORTH);
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.addKeyListener(this);
        this.addWindowListener(this);
        panel.start();
    }


    @Override
    public void close(){
        webcam.close();
    }
    public Webcam getWebcam(){
        return webcam;
    }


    public static void drawSquare(int x, int y, int side, int c, BufferedImage img){
        int half = side / 2;
        for(int i=x - half;i<x+half;i++){
            img.setRGB(i,y+half, c);
        }
        for(int i=x - half;i<x+half;i++){
            img.setRGB(i,y-half, c);
        }
        for(int i=y-half;i<y+half;i++){
            img.setRGB(x+half,i,c);
        }
        for(int i=y-half;i<y+half;i++){
            img.setRGB(x-half,i,c);
        }
    }

    @Override
    public void webcamOpen(WebcamEvent webcamEvent) {

    }

    @Override
    public void webcamClosed(WebcamEvent webcamEvent) {
        System.out.println(webcamEvent.toString());
        System.out.println("Webcam closed");
    }

    @Override
    public void webcamDisposed(WebcamEvent webcamEvent) {
        System.out.println("Webcam closed");
    }

    @Override
    public void webcamImageObtained(WebcamEvent webcamEvent) {
        BufferedImage img = webcamEvent.getImage();

        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;

        for(Point p: squareCenters){
            drawSquare(centerX + BOX_SIZE*p.x, centerY + BOX_SIZE*p.y,BOX_SIZE, 0, img);
        }

        if(running){
            captureAndProcess(img);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE && !running) {
            running = true;
        }
    }
    public void captureAndProcess(BufferedImage img){
    	running = false;
    	
    	for(int i = 0; i < squareCenters.length; ++i)
    		triples[currFace][i/3][i%3] = tripleAround(img, squareCenters[i]);
    	
    	// Save center color as reference
    	Triple.COLOR_TRIPLES[currFace] = triples[currFace][1][1];
    	
    	// Print all 9 triples for this face
    	for(int i=0;i<3;i++){
            System.out.print("{");
            for(int j=0;j<3;j++){
                System.out.print(triples[currFace][i][j]);
                if(j != 2){
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }
    	
    	// Move onto the next face
    	++currFace;
    	    	
    	// If have done all 6 faces
    	if(currFace == 6) {
	    	// Convert all triples to closest colors
			Cubie.Side[][][] colors = convertTriplesToColors();
			
			// Print center references
	    	System.out.println("Center references");
	    	for(int i = 0; i < 6; ++i)
	    		System.out.println(Triple.COLOR_TRIPLES[i]);
	    	
	    	// Print min saturation of centers (excluding white)
	    	System.out.println();
	    	System.out.println("Min sat: " + Triple.getMinSaturation());
			
			StringBuilder sb = new StringBuilder();
			
			// Top face
			for(int y = 2; y >= 0; --y)
				for(int x = 2; x >= 0; --x)
					sb.append(colors[4][x][y]);
			
			// Right face
			for(int y = 0; y < 3; ++y)
				for(int x = 2; x >= 0; --x)
					sb.append(colors[1][y][x]);
				
			// Front face
			for(int y = 0; y < 3; ++y)
				for(int x = 2; x >= 0; --x)
					sb.append(colors[0][y][x]);
			
			// Bottom face
			for(int y = 0; y < 3; ++y)
				for(int x = 0; x < 3; ++x)
					sb.append(colors[5][x][y]);
			
			// Left face
			for(int y = 0; y < 3; ++y)
				for(int x = 2; x >= 0; --x)
					sb.append(colors[3][y][x]);
			
			// Back face
			for(int y = 0; y < 3; ++y)
				for(int x = 2; x >= 0; --x)
					sb.append(colors[2][y][x]);
			
			result = sb.toString();    		
			
			// Done with the frame
			this.setVisible(false);
			this.close();
			this.dispose();
    	}
    	// Otherwise, set instructions label properly
    	else
    		instructionsLabel.setText(instructionStrs[currFace]);
    }
    
    // Converts all triples to their closest colors
    public Cubie.Side[][][] convertTriplesToColors() {    	
    	Cubie.Side colors[][][] = new Cubie.Side[6][3][3];
    	for(int face = 0; face < 6; ++face) {
    		for(int i = 0; i < 3; ++i) {
    			System.out.print("{");
    			for(int j = 0; j < 3; ++j) {
    				// If it's the center, set it correctly
    				if(i == 1 && j == 1) {
    					colors[face][i][j] = Triple.getColorFromIndex(face);
    				}
    				// Otherwise, convert the triple to the closest color
    				else {
    					final Cubie.Side col = fromTriple(triples[face][i][j]);
    					colors[face][i][j] = col;
    				}
    				System.out.print(colors[face][i][j]);
    				if(j != 2)
    					System.out.print(", ");
    			}
    			System.out.println("}");
    		}
    		System.out.println();
    		System.out.println();
    	}
    	
    	return colors;
    }
    
    // Returns HSV triple around a given point
    public Triple tripleAround(BufferedImage img, Point p){
	    Raster rast = img.getRaster();
	    int centerx = (rast.getWidth() / 2) + p.x*BOX_SIZE;
	    int centery = (rast.getHeight() / 2) - p.y*BOX_SIZE;
	
	    int r = averageOver(rast, centerx, centery,0, 15); // rast.getSample(centerx,centery,0);
	    int g = averageOver(rast, centerx, centery,1, 15);// rast.getSample(centerx,centery,1);
	    int b = averageOver(rast, centerx, centery,2, 15);// rast.getSample(centerx,centery,2);
	    
	    float hsbvals[] = new float[3];
	    java.awt.Color.RGBtoHSB(r, g, b, hsbvals);
	    
	    // Change hue to angle representation
	    hsbvals[0] = 360 * (hsbvals[0] - ((float)Math.floor(hsbvals[0])));
	    
	    System.out.println("hsbvals: " + Arrays.toString(hsbvals));
	
	    return new Triple(Math.round(hsbvals[0]), Math.round(hsbvals[1] * 100), Math.round(hsbvals[2] * 100));	
	}

    public int averageOver(Raster rast, int x, int y, int b, int width){
        ArrayList<Integer> samples = new ArrayList<>();
        int halfWidth = width/ 2;
        for(int i = x - halfWidth; i<x + halfWidth;i++){
            for(int j=y - halfWidth;j<y + halfWidth;j++){
                samples.add(rast.getSample(i,j,b));
            }
        }

        Collections.sort(samples);
        int middle = samples.size() / 2;

        return samples.get(middle);
    }

    // Given a triple, returns the closest color
    public Cubie.Side fromTriple(Triple t){
    	// If value is less than 88% of the minimum value, assume bottom (white)
    	final int minSat = Triple.getMinSaturation();
    	if(t.y < minSat * 0.6)
    		return Side.BOTTOM;
        
    	// Otherwise, find the closest hue
        Cubie.Side side = null;
        double dist = Double.MAX_VALUE;
        for(int i = 0; i < 6; ++i) {
        	final double thisDist = t.distance(Triple.COLOR_TRIPLES[i]);
        	if(thisDist < dist) {
        		dist = thisDist;
        		side = Triple.getColorFromIndex(i);
        	}
        }
        
        return side;
    }
    
    public String getResult() {
    	return result;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        webcam.close();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        webcam.close();
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}