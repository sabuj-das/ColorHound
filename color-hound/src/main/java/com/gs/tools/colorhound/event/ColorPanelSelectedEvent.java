/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.ColorPanelSelectedEvent
 * Date     : May 27, 2013
 */

package com.gs.tools.colorhound.event;

import com.gs.tools.colorhound.ui.ColorPanel;
import java.awt.Color;
import java.util.EventObject;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class ColorPanelSelectedEvent extends EventObject 
        implements ApplicationEvent{
    private Boolean oldValue;
    private Boolean newValue;
    private Object source;
    private Color selectedColor;
    private ColorPanel oldSelectedPanel;
    private ColorPanel newSelectedPanel;
    
    public ColorPanelSelectedEvent(Boolean oldValue, Boolean newValue, Object source) {
        super(source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }
    

    public Boolean getOldValue() {
        return oldValue;
    }

    public void setOldValue(Boolean oldValue) {
        this.oldValue = oldValue;
    }

    public Boolean getNewValue() {
        return newValue;
    }

    public void setNewValue(Boolean newValue) {
        this.newValue = newValue;
    }

    public ColorPanel getOldSelectedPanel() {
        return oldSelectedPanel;
    }

    public void setOldSelectedPanel(ColorPanel oldSelectedPanel) {
        this.oldSelectedPanel = oldSelectedPanel;
    }

    public ColorPanel getNewSelectedPanel() {
        return newSelectedPanel;
    }

    public void setNewSelectedPanel(ColorPanel newSelectedPanel) {
        this.newSelectedPanel = newSelectedPanel;
    }
    
}
