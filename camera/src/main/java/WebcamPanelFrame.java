import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

import com.github.sarxos.webcam.*;


/**
 * Example of how WebcamPanel can be used in SWT using SWT_AWT bridge.
 *
 * @author Dimitrios Chondrokoukis
 */
public class WebcamPanelFrame extends JFrame implements AutoCloseable, WebcamListener, KeyListener, WindowListener {


    private Webcam webcam;
    private WebcamPanel panel;
    public boolean running;
    public static enum Color {
        Red, Orange, Yellow, White, Blue, Green
    }
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
    public Color[] frontFaces;

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


        this.add(panel);
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
        System.out.println("------------ Running ------------");
        ArrayList<Color> colors = new ArrayList<Color>();
        for(int i=0;i<squareCenters.length;i++){
            colors.add(colorAround(img, squareCenters[i]));
        }

        for(int i=0;i<3;i++){
            System.out.print("{");
            for(int j=0;j<3;j++){
                System.out.print(colors.get((3*i)+j));
                if(j != 2){
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }


    }

    public Color colorAround(BufferedImage img, Point p){
        Raster rast = img.getRaster();
        int centerx = (rast.getWidth() / 2) + p.x*BOX_SIZE;
        int centery = (rast.getHeight() / 2) - p.y*BOX_SIZE;

        int r = averageOver(rast, centerx, centery,0, 10); // rast.getSample(centerx,centery,0);
        int g = averageOver(rast, centerx, centery,1, 10);// rast.getSample(centerx,centery,1);
        int b = averageOver(rast, centerx, centery,2, 10);// rast.getSample(centerx,centery,2);

        Triple rgb = new Triple(r,g,b);
        return fromTriple(rgb);
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

    public Color fromTriple(Triple t){
        Color c = Color.Red;
        double dist = Double.MAX_VALUE;
        if(t.distance(Triple.redTriple) < dist){
            dist = t.distance(Triple.redTriple);
            c = Color.Red;
        }
        if(t.distance(Triple.greenTriple) < dist){
            dist = t.distance(Triple.greenTriple);
            c = Color.Green;
        }
        if(t.distance(Triple.whiteTripple) < dist){
            dist = t.distance(Triple.whiteTripple);
            c = Color.White;
        }
        if(t.distance(Triple.orangeTripple) < dist){
            dist = t.distance(Triple.orangeTripple);
            c = Color.Orange;
        }
        if(t.distance(Triple.yellowTripple) < dist){
            dist = t.distance(Triple.yellowTripple);
            c = Color.Yellow;
        }
        if(t.distance(Triple.blueTriple) < dist){
            dist = t.distance(Triple.blueTriple);
            c = Color.Blue;
        }
        System.out.println(t + " ==> " + c);
        return c;
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