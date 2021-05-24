import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class FractalExplorer {
    private int displaySize;
    private JImageDisplay imageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double range;
    JComboBox<FractalGenerator> fractalBox;
    JPanel fractalPanel;
    JPanel fractalPanel2;
    JLabel fractalLabel;
    JButton button;
    JButton saveButton;
    int countstring;

    public FractalExplorer(int displaySize) {

        this.displaySize = displaySize;
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double(0, 0, 0, 0);
        fractalGenerator.getInitialRange(this.range);
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Fractal Explorer");
        imageDisplay = new JImageDisplay(displaySize, displaySize);
        button = new JButton("Reset Display");
        saveButton = new JButton("Save Image");
        //button.setPreferredSize(new Dimension(200, 100));
        fractalBox = new JComboBox<FractalGenerator>();
        fractalPanel = new JPanel();
        fractalLabel = new JLabel("Fractal:");
        fractalPanel2 = new JPanel();
        fractalPanel.add(fractalLabel);
        fractalPanel.add(fractalBox);
        fractalPanel2.add(button);
        fractalPanel2.add(saveButton);
        fractalBox.addItem(new Mandelbrot());
        fractalBox.addItem(new Tricorn());
        fractalBox.addItem(new BurningShip());


        ActionHandler aHandler = new ActionHandler();
        MouseHandler mHandler = new MouseHandler();
        button.addActionListener(aHandler);
        saveButton.addActionListener(aHandler);
        imageDisplay.addMouseListener(mHandler);
        fractalBox.addActionListener(aHandler);

        frame.setLayout(new java.awt.BorderLayout());
        frame.add(imageDisplay, BorderLayout.CENTER);
        frame.add(fractalPanel2, BorderLayout.SOUTH);
        frame.add(fractalPanel,BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void enableUI(boolean val){
        button.setEnabled(val);
        saveButton.setEnabled(val);
        fractalBox.setEnabled(val);
    }

    public void drawFractal() {
        enableUI(false);
        countstring=displaySize;
        for(int i=0;i<displaySize;i++){
            FractalWorker worker = new FractalWorker(i);
            worker.execute();
        }
    }

    public class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == "Reset Display") {
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
            else if(e.getActionCommand()=="Save Image"){
                JFileChooser  fileChooser = new JFileChooser();
                fileChooser.showSaveDialog(fileChooser);
                JFileChooser chooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                chooser.setFileFilter(filter);
                chooser.setAcceptAllFileFilterUsed(false);
                int res = fileChooser.showSaveDialog(imageDisplay);

                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        javax.imageio.ImageIO.write(imageDisplay.getBufferedImage(),
                                "png", fileChooser.getSelectedFile());
                    } catch (NullPointerException | IOException e1) {
                        javax.swing.JOptionPane.showMessageDialog(imageDisplay,
                                e1.getMessage(), "Cannot Save Image",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    return;
                }
            }
            else if(e.getSource()==(Object) fractalBox){
                fractalGenerator = (FractalGenerator) fractalBox.getSelectedItem();
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
        }
    }

    public class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(countstring!=0){
                return;
            }
            double xCoord = FractalGenerator.getCoord(range.x,
                    range.x + range.width, displaySize, e.getX());
            double yCoord = FractalGenerator.getCoord(range.y,
                    range.y + range.width, displaySize, e.getY());
            fractalGenerator.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }

    public static void main(String[] args) {
        FractalExplorer fracExp = new FractalExplorer(700);
        fracExp.createAndShowGUI();
        fracExp.drawFractal();
    }


    public class FractalWorker extends SwingWorker<Object,Object>{

        int y=0;
        int[] rgb;

        public FractalWorker(int y) {
            this.y = y;
        }

        public Object doInBackground(){
            rgb = new int[displaySize];
            double yCoord = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, y);
            for (int i = 0; i < displaySize; i++) {
                    double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, i);
                    double count = fractalGenerator.numIterations(xCoord, yCoord);
                    if (count == -1) {
                        rgb[i]=0;
                    } else {
                        float hue = 0.1f + (float) count / 200f;
                        int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                        rgb[i]=rgbColor;
                }
            }
            return null;
        }


        public void done() {
            for(int i=0;i<displaySize;i++){
                imageDisplay.drawPixel(i,y,rgb[i]);
            }
            imageDisplay.repaint(0,y,displaySize,1);
            countstring--;
            if(countstring==0){
                enableUI(true);
            }
        }

    }

}