import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;

    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.height = 3;
        range.width = 3;
        range.x = -2;
        range.y = -1.5;
    }

    @Override
    public int numIterations(double x, double y) {

        int count = 0;

        double real = x;
        double imagine = y;
        double z_multiplyZ = 0;

        while (count<MAX_ITERATIONS&&z_multiplyZ<4){
            count++;
            double newReal= real*real-imagine*imagine+x;
            double newImagine = 2*real*imagine+y;
            z_multiplyZ=newReal*newReal+newImagine*newImagine;
            real=newReal;
            imagine=newImagine;
        }
        if(MAX_ITERATIONS==count){return -1;}
        else{return count;}
    }

    @Override
    public String toString() {
        return "Mandelbrot";
    }
}
