/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.ColorGrabEvent
 * Date     : May 23, 2013
 */

package com.gs.tools.colorhound.event;

import java.awt.Color;
import java.util.EventObject;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public final class ColorGrabEvent extends EventObject 
        implements ApplicationEvent{

    private Color oldValue;
	private Color newValue;

    public ColorGrabEvent(Color oldValue, Color newValue, Object source) {
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
