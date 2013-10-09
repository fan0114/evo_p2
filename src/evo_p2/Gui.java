package evo_p2;

/**
 *
 * @author fan0114
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Surface extends JPanel {

    public double[][] linenums;

    public Surface(double[][] linenums) {
        this.linenums = linenums;
    }

    private void doDrawing(Graphics g) throws FileNotFoundException, IOException {
        int scale = 300;


        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < linenums.length; i++) {
            g2d.drawRect((int) (linenums[i][0] * scale+scale), (int) (scale*1 - linenums[i][1] * scale), 1, 1);
            g2d.setColor(Color.GREEN);
            g2d.drawRect((int) (linenums[i][0] * scale+scale), (int) (scale*1 - linenums[i][2] * scale), 1, 1);
            g2d.setColor(Color.BLUE);
            g2d.drawRect((int) (linenums[i][0] * scale+scale), (int) (scale*1 - linenums[i][3] * scale), 1, 1);
            g2d.setColor(Color.RED);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        try {
            doDrawing(g);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Surface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Surface.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}

public class Gui extends JFrame {

    static double[][] linenums;
    static Evo_p2 instance;
    static JPanel panel;

    public void updateLines() throws FileNotFoundException, IOException, Exception {
        linenums = instance.run2();
    }

    public Gui() throws FileNotFoundException, IOException, InterruptedException, Exception {

        final JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout(10, 10));


        JButton myButton = new JButton("Add Component ");
        myButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parentPanel.remove(panel);
                    updateLines();
                    panel = new Surface(linenums);
                    panel.setPreferredSize(new Dimension(800, 600));
                    parentPanel.add(panel, BorderLayout.CENTER);
                    parentPanel.revalidate();
                    parentPanel.repaint();
                    pack();
                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        updateLines();
        panel = new Surface(linenums);
        panel.setPreferredSize(new Dimension(800, 600));

        setTitle(Evo_p2.filename);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(800, 800);
        setLocationRelativeTo(null);
        parentPanel.add(panel, BorderLayout.CENTER);
        parentPanel.add(myButton, BorderLayout.SOUTH);
        add(parentPanel);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        instance = new Evo_p2();
        instance.init();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Gui lines;

                    lines = new Gui();
                    lines.setVisible(true);

                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
