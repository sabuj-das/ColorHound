/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.ui;

import com.gs.tools.colorhound.ColorPalette;
import com.gs.tools.colorhound.event.ApplicationEventManager;
import com.gs.tools.colorhound.event.ColorDetectEvent;
import com.gs.tools.colorhound.event.ColorDetectListener;
import com.gs.tools.colorhound.event.ColorGrabEvent;
import com.gs.tools.colorhound.event.ColorGrabListener;
import com.gs.tools.colorhound.event.ColorPanelSelectedEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author Sabuj
 */
public class ColorPaletteManager implements ColorGrabListener, ColorDetectListener{
    
    private static ApplicationEventManager eventManager 
            = ApplicationEventManager.getInstance();
    private static ColorPaletteManager instance;
    private Map<String, List<ColorPanel>> colorPanelPaletteMap
            = new HashMap<String, List<ColorPanel>>();
    private Set<String> paletteNames = new LinkedHashSet<String>();
    private ColorPanel selectedPanel;
    
    private ColorPaletteManager() {
        eventManager.registerListener(ColorGrabEvent.class, this);
        eventManager.registerListener(ColorDetectEvent.class, this);
    }

    public static ColorPaletteManager getInstance() {
        if(null != instance)
            return instance;
        synchronized(ColorPaletteManager.class){
            if(null == instance){
                instance = new ColorPaletteManager();
            }
        }
        return instance;
    }

    public void setColorPanelPaletteMap(Map<String, List<ColorPanel>> colorPanelPaletteMap) {
        this.colorPanelPaletteMap = colorPanelPaletteMap;
    }

    public boolean isPaletteExists(String paletteName){
        return paletteNames.contains(paletteName);
    }
    
    public boolean addPalette(String paletteName){
        if(null != paletteName 
                && !"".equals(paletteName)
                && ! isPaletteExists(paletteName)){
            paletteNames.add(paletteName);
            colorPanelPaletteMap.put(paletteName, new ArrayList<ColorPanel>());
            return true;
        }
        return false;
    }
    
    public List<ColorPanel> getAllColorPanels(String paletteName){
        return colorPanelPaletteMap.get(paletteName);
    }
    
    public void addPanel(String paletteName, ColorPanel colorPanel){
        if(null == colorPanelPaletteMap.get(paletteName)){
            colorPanelPaletteMap.put(paletteName, new ArrayList<ColorPanel>());
        }
        colorPanelPaletteMap.get(paletteName).add(colorPanel);
    }
    
    public void selectPanel(String paletteName, ColorPanel colorPanel){
        final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
        for (ColorPanel p : colorPanels) {
            p.setSelected(false);
            p.updateUI();
        }
        selectedPanel = colorPanel;
        selectedPanel.setSelected(true);
    }
    
    public void makeColorPanelEditable(String paletteName, ColorPanel colorPanel){
        final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
    	for (ColorPanel p : colorPanels) {
            p.setSelected(false);
            p.updateUI();
        }
        selectedPanel = colorPanel;
        selectedPanel.setSelected(true);
        selectedPanel.setColorGrabbed(false);
    }
    
    public void makeSelectedColorPanelEditable(String paletteName){
    	final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
        for (ColorPanel p : colorPanels) {
            if(p != selectedPanel)
            	p.setSelected(false);
            p.updateUI();
        }
        selectedPanel.setSelected(true);
        selectedPanel.setColorGrabbed(false);
    }
    
    public void colorGrabbed(ColorGrabEvent event) {
        if(null != event && null != event.getNewValue()
                && null != selectedPanel && !selectedPanel.isColorGrabbed()){
            selectedPanel.setSelectedColor(event.getNewValue());
            selectedPanel.setColorGrabbed(true);
            selectedPanel.repaint();
            selectedPanel.updateUI();
            selectedPanel.setColorGrabbed(true);
            selectedPanel.getParentPanel().updateUI();
        }
    }

    public void colorDetected(ColorDetectEvent event) {
        if(null != event && null != event.getNewValue()
                && null != selectedPanel && !selectedPanel.isColorGrabbed()){
            selectedPanel.setSelectedColor(event.getNewValue());
            selectedPanel.repaint();
            selectedPanel.updateUI();
            selectedPanel.getParentPanel().updateUI();
        }
    }

    public void removeSelectedPanel(String paletteName) {
        final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
        if(selectedPanel != null){
            for (ColorPanel p : colorPanels) {
                if(p.equals(selectedPanel)){
                    colorPanels.remove(p);
                    break;
                } 
            }
        }
        selectedPanel = null;
        
    }

    public ColorPanel getSelectPanel() {
        return selectedPanel;
    }

    public void removeSelection() {
        ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(
                Boolean.FALSE, Boolean.FALSE, instance);
        event.setOldSelectedPanel(selectedPanel);
        event.setNewSelectedPanel(null);
        selectedPanel = null;
        eventManager.fireEvent(event);
    }

    public Set<String> getAllPaletteNames() {
        return paletteNames;
    }

    boolean isSelectedPanelEditable(String paletteName) {
        return (null != selectedPanel && !selectedPanel.isColorGrabbed());
    }

    public int getColorPanelCount(String paletteName) {
        return (null != colorPanelPaletteMap.get(paletteName)
                ? colorPanelPaletteMap.get(paletteName).size() : 0);
    }

    public void deletePalette(String paletteName) {
        paletteNames.remove(paletteName);
        colorPanelPaletteMap.remove(paletteName);
    }

    public void selectPanel(String paletteName, ColorPanel aThis, boolean b) {
        final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
        for (ColorPanel p : colorPanels) {
            p.setSelected(false);
            p.updateUI();
        }
        
        selectedPanel.setSelected(b);
        selectedPanel = null;
    }

    public void selectAll(String paletteName) {
//        final List<ColorPanel> colorPanels = colorPanelPaletteMap.get(paletteName);
//        for (ColorPanel p : colorPanels) {
//            p.setSelected(true);
//            ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(p.isSelected(), true, this);
//            ApplicationEventManager.getInstance().fireEvent(event);
//        }
    }

    
    
    
}
