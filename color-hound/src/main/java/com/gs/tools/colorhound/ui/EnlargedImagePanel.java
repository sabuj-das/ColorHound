/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 *
 * @author Sabuj
 */
public class EnlargedImagePanel extends JPanel implements ChangeListener{

    public static final int WIDTH = 200;
    public static final int HEIGHT = 100;
    private final Color LINE_COLOR = Color.decode("0xFF988F");
    private Image image;
    private Dimension d = new Dimension(200, 100);
    private int ratio = 14;
    
    public EnlargedImagePanel() {
        setDoubleBuffered(true);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        setLayout(null);
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        if(null != image){
            g.drawImage(image, 0, 0, d.width, d.height, null);
        } else {
            super.paint(g);
        }
        g.setColor(LINE_COLOR);
        int x, y;
        int w = Math.max(2, ratio);
        int h = w;
        x = WIDTH/2 ;
        y = HEIGHT/2 - h/2;
        //BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        g.drawRect(x, y, h, h);
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() instanceof JSlider){
            JSlider slider = (JSlider) e.getSource();
            String name = slider.getAccessibleContext().getAccessibleName();
            if(ColorHoundBaseFrame.ENLARGE_RATIO_SLIDER.equals(name)){
                ratio = slider.getValue();
            }
        }
    }
    
    
    
}
