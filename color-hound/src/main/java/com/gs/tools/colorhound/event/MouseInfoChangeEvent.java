/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.event;

import java.awt.Point;
import java.util.EventObject;

/**
 *
 * @author Sabuj
 */
public class MouseInfoChangeEvent  extends EventObject 
        implements ApplicationEvent{
    private Point oldLocation, newLocation;

    public MouseInfoChangeEvent(Point oldLocation, Point newLocation, Object source) {
        super(source);
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public Point getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(Point oldLocation) {
        this.oldLocation = oldLocation;
    }

    public Point getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(Point newLocation) {
        this.newLocation = newLocation;
    }
    
}
