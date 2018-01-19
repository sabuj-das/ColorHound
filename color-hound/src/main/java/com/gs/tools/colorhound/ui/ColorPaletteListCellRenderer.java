/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.ui.ColorPaletteListCellRenderer
 * Date     : May 30, 2013
 */

package com.gs.tools.colorhound.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class ColorPaletteListCellRenderer extends DefaultListCellRenderer{

        
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value != null){
            Color bgColor = Color.decode(value.toString());
            setText( value.toString() );
            Color fgColor = new Color(
                    Math.abs(bgColor.getRed()   - 255),
                    Math.abs(bgColor.getGreen() - 255),
                    Math.abs(bgColor.getBlue()  - 255)
                );
            setForeground( fgColor );
            setBackground( bgColor );
        }
        return this;
    }

    
    
}
