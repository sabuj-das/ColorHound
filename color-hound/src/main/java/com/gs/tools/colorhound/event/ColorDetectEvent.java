/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.event;

import java.awt.Color;
import java.util.EventObject;

/**
 *
 * @author Sabuj
 */
public class ColorDetectEvent extends EventObject 
        implements ApplicationEvent{

    private Color oldValue;
	private Color newValue;

    public ColorDetectEvent(Color oldValue, Color newValue, Object source) {
        super(source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Color getOldValue() {
        return oldValue;
    }

    public void setOldValue(Color oldValue) {
        this.oldValue = oldValue;
    }

    public Color getNewValue() {
        return newValue;
    }

    public void setNewValue(Color newValue) {
        this.newValue = newValue;
    }


    
    
}
