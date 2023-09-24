// trying to recreate renderer using functions to make it more comprehensible bc impossible to keep track of anything in the other one

package Renderer;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Point;


public class CleanRenderer {
    public static void main(String[] args) {
        start();
        loadShape();
    }



    public static void start() {

        // JFrame is actual window, JPanel is area where actual stuff gets rendered
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());



        // not really sure why this needs an anonymous class rn but ill leave it
        JPanel renderPanel = new JPanel() {
            private static Point p = new Point(0, 0);
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; //WHAT DOES THIS LINE MEAN <- cast g to graphics2d 
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                
            }
        };




        








        // set display window settings, has to go after above because can't use before declaration
        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }

    
    
    // public static void paintComponent(Graphics g) {
    //     Graphics2D g2 = (Graphics2D) g;
    //     g2.setColor(Color.BLACK);
    //     g2.fillRect(0, 0, 10, 10);
    // }
    
    public static void drawPanel() {
    
    }


    public static List<Triangle> loadShape() {
        List<Triangle> tris = new ArrayList<>();
        tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100, 100), new Vertex(-100, 100, -100), Color.RED));
        tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100, 100), new Vertex(100, -100, -100), Color.WHITE));
        tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100, -100, -100), new Vertex(100, 100, 100), Color.GREEN));
        tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100, -100, -100), new Vertex(-100, -100, 100), Color.ORANGE));

        return tris;
    }
}










class Triangle {
    Vertex v1, v2, v3;
    Color color;
    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}

class Vertex {
    double x, y, z;

    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void Print(double x, double y, double z){
        System.out.println(x + y + z);
    }
}

class Matrix3 {
    double[] values;
    Matrix3(double[] values) {
        this.values = values;
    }
    Matrix3 multiply(Matrix3 other) {
        double[] result = new double[9];
        for(int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] += this.values[row * 3 + i] *other.values[i * 3 + col];
                }
            }
        }
        return new Matrix3(result);
    }
    Vertex transform(Vertex in) {
        return new Vertex(
            in.x * values[0] + in.y * values[1] + in.z * values[2],
            in.x * values[3] + in.y * values[4] + in.z * values[5],
            in.x * values[6] + in.y * values[7] + in.z * values[8]
        );
    }
}

