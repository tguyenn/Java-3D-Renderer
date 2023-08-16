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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.util.Arrays;
import java.awt.Point;


public class Panel {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // why don't we need to create a new Container object with the new keyword here?
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // panel that displays render results
        JPanel renderPanel = new JPanel() {
            private static Point p = new Point(0, 0);
            public void paintComponent(Graphics g) {
                
                Graphics2D g2 = (Graphics2D) g; //WHAT DOES THIS LINE MEAN <- cast g to graphics2d 
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                List<Triangle> tris = new ArrayList<>();
                tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100, 100), new Vertex(-100, 100, -100), Color.RED));
                tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100, 100), new Vertex(100, -100, -100), Color.WHITE));
                tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100, -100, -100), new Vertex(100, 100, 100), Color.GREEN));
                tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100, -100, -100), new Vertex(-100, -100, 100), Color.ORANGE));
                

                // following code creates a still image of the tetrahedron

                // g2.translate(getWidth() / 2, getHeight() / 2);
                // for(Triangle t: tris){
                //     Path2D path = new Path2D.Double();
                //     g2.setColor(t.color); // sets the color of lines being drawn to whatever color is assigned to it
                //     path.moveTo(t.v1.x, t.v1.y);
                //     path.lineTo(t.v2.x, t.v2.y);
                    
                //     path.lineTo(t.v3.x, t.v3.y);
                     
                //     path.closePath();
                //     g2.draw(path);
                // }
                
             

                // the following code creates a rotatable tetrahedron

                int x = (int)p.getX();
                int y = (int)p.getY();

                pane.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        double yi = 180.0 / pane.getHeight();
                        double xi = 180.0 / pane.getWidth();
                        int x = (int) (e.getX() * xi);
                        int y = -(int) (e.getY() * yi);
                        p.setLocation(x, y);
                        System.out.println(" x: " + p.getX() + "," + p.getY());
                        pane.repaint();
                    
                    
                    }
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        // empty bc we dont want default inherited abstract method 
                    }
                });
                
                double heading = Math.toRadians(x);
                    Matrix3 headingTransform = new Matrix3(new double[]{
                        Math.cos(heading), 0, -Math.sin(heading),
                        0, 1, 0,
                        Math.sin(heading), 0, Math.cos(heading)
                    });
                double pitch = Math.toRadians(y);
                    Matrix3 pitchTransform = new Matrix3(new double[]{
                        1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)
                    });
                // merge transformation matrices
                Matrix3 transform = headingTransform.multiply(pitchTransform);

                g2.translate(getWidth() /2, getHeight() / 2);

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                
                for(Triangle t: tris) {
                    g2.setColor(t.color);
                    Vertex v1 = transform.transform(t.v1);
                    v1.x += getWidth();
                    v1.y += getHeight();
                    Vertex v2 = transform.transform(t.v2);
                    v2.x += getWidth();
                    v2.y += getHeight();
                    Vertex v3 = transform.transform(t.v3);
                    v3.x += getWidth();
                    v3.y += getHeight();

                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxY = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    for (int y2 = minY; y2 <= maxY; y2++) {
                        for (int x2 = minX; x2 <= maxX; x2++) {
                            Vertex p = new Vertex (x, y , 0);
                            boolean V1 = sameSide(v1,v2,v3,p);
                            boolean V2 = sameSide(v2,v3,v1,p);
                            boolean V3 = sameSide(v3,v1,v2,p);
                            if (V3 && V2 && V1) {
                                img.setRGB(x2, y2, t.color.getRGB());
                            }
                        }
                    }

                    // Path2D path = new Path2D.Double();
                    // path.moveTo(v1.x, v1.y);
                    // path.lineTo(v2.x, v2.y);
                    // path.lineTo(v3.x, v3.y);
                    // path.closePath();
                    // g2.draw(path);
                    g2.drawImage(img, 0, 0, null);
                }
                // g2.drawImage(img, 0, 0, null);
            }
        }; // WHAT IS THIS WHY DOES IT NEED A SEMICOLON <- this is an anonymous class
        pane.add(renderPanel, BorderLayout.CENTER); 
        frame.setSize(600, 600); // why do i have to put the frame settings outside of the scope where it's declared?
        frame.setVisible(true);

    }

    static boolean sameSide(Vertex A, Vertex B, Vertex C, Vertex p) {
        Vertex V1V2 = new Vertex(B.x - A.x,B.y - A.y, B.z - A.z);
        Vertex V1V3 = new Vertex(C.x - A.x,C.y - A.y,C.z - A.z);
        Vertex V1P = new Vertex(p.x - A.x,p.y - A.y,p.z - A.z);

        double V1V2CrossV1V3 = V1V2.x * V1V3.y - V1V3.x * V1V2.y;
        double V1V2CrossP = V1V2.x * V1P.y - V1P.x * V1V2.y;
        return (V1V2CrossV1V3 * V1V2CrossP) >= 0;
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


