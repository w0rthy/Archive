package archiveinterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

//JFrames, 8 pixels on the left, 8 on the right; 31 on the top, 8 on the bottom, and 24 pixels for the taskbar

public class ViewImageFrame extends javax.swing.JFrame {

    BufferedImage img;
    
    public ViewImageFrame(String title, BufferedImage bi) {
        initComponents();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(Color.BLACK);
        setTitle(title);
        
        img = bi;
        reSize();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void paint(Graphics g){
        double wr = (double)(getWidth()-16)/img.getWidth();
        double hr = (double)(getHeight()-39)/img.getHeight();
        double ratio = wr<hr?wr:hr;
        
        g.drawImage(img, 8, 31, (int)(img.getWidth()*ratio), (int)(img.getHeight()*ratio), null);
        
        g.dispose();
    }
    
    //Sizes the windows to the image
    private void reSize() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        double wr = (double)img.getWidth()/(d.width-16);
        double hr = (double)img.getHeight()/(d.height-39-24);
        double ratio = wr>hr?wr:hr;
        
        
        if(ratio>1d){
            //Resize image and try again
            int w = (int)(img.getWidth()/ratio);
            int h = (int)(img.getHeight()/ratio);
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            bi.getGraphics().drawImage(img, 0, 0, w, h, null);
            img = bi;
            
            ratio = 1d;
        }
        
        setSize((int)(img.getWidth())+16,(int)(img.getHeight())+39);
    }
    
    public void updateImage(String title, BufferedImage bi){
        setTitle(title);
        img = bi;
        reSize();
        repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
