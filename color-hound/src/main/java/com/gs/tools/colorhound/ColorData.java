/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gs.tools.colorhound;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Sabuj das
 */
public class ColorData  implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8851654449004277988L;
    private String tagName;
    private String tagShortName;
    private String colorCode;
    private transient Color color;

    public ColorData() {
    }

    public ColorData(String tagName, String tagShortName, String colorCode, Color color) {
        this.tagName = tagName;
        this.tagShortName = tagShortName;
        this.colorCode = colorCode;
        this.color = color;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagShortName() {
        return tagShortName;
    }

    public void setTagShortName(String tagShortName) {
        this.tagShortName = tagShortName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.tagName != null ? this.tagName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColorData other = (ColorData) obj;
        if ((this.tagName == null) ? (other.tagName != null) : !this.tagName.equals(other.tagName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ColorData{" + "tagName=" + tagName + ", colorCode=" + colorCode + '}';
    }
    
    
}
