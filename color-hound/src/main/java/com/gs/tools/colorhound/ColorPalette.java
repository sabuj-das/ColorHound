/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sabuj
 */
public class ColorPalette implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8851654449004277988L;
    private String name;
    private List<ColorData> colorDataList;

    public ColorPalette() {
        colorDataList = new ArrayList<ColorData>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//
//    public List<String> getColorCodes() {
//        return colorCodes;
//    }
//
//    public void setColorCodes(List<String> colorCodes) {
//        this.colorCodes = colorCodes;
//    }
//
//    public List<Color> getColors() {
//        if (null != colorCodes) {
//            for (String code : colorCodes) {
//                Color c = Color.decode(code);
//                colors.add(c);
//            }
//        }
//        return colors;
//    }
    public List<ColorData> getColorDataList() {
        return colorDataList;
    }

    public void setColorDataList(List<ColorData> colorDataList) {
        this.colorDataList = colorDataList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final ColorPalette other = (ColorPalette) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ColorPalette{" + "name=" + name + '}';
    }

    public void add(String colorCode) {
        this.colorDataList.add(new ColorData(colorCode, colorCode, colorCode, Color.decode(colorCode)));
    }

}
