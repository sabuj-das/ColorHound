/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.event;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Sabuj
 */
public class ExternalEventListener implements AWTEventListener {

    private ApplicationEventManager eventManager = ApplicationEventManager.getInstance();

    @Override
    public void eventDispatched(AWTEvent event) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        if (null == location) {
            return;
        }
        if (event instanceof KeyEvent) {
            KeyEvent evt = (KeyEvent) event;
            if (KeyEvent.CTRL_MASK == evt.getModifiers()
                    && KeyEvent.VK_G == evt.getKeyCode()) {
                try {
                    Robot robot = new Robot();
                    Color color = robot.getPixelColor(
                            (int) location.getX(), (int) location.getY());
                    ColorGrabEvent cge = new ColorGrabEvent(null, color, this);
                    eventManager.fireEvent(cge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (KeyEvent.VK_0 == evt.getKeyCode()){
                try {
                    Robot robot = new Robot();
                    Color color = robot.getPixelColor(
                            (int) location.getX(), (int) location.getY());
                    ColorDetectEvent cge = new ColorDetectEvent(null, color, this);
                    eventManager.fireEvent(cge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(event instanceof MouseEvent){
            if (null != location) {
                try {
                    Robot robot = new Robot();
                    Color color = robot.getPixelColor(
                            (int) location.getX(), (int) location.getY());
                    ColorDetectEvent cge = new ColorDetectEvent(null, color, this);
                    eventManager.fireEvent(cge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        else {
            System.out.println("Unknown event: "+event.getClass().getCanonicalName());

        }
    }
}
