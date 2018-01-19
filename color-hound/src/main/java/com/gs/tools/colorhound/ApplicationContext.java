/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound;

import com.gs.tools.colorhound.ui.ColorPaletteManager;
import com.gs.tools.colorhound.ui.ColorPanel;
import com.gs.tools.colorhound.util.GraphicsUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Sabuj
 */
public class ApplicationContext {
    private static ApplicationContext context;
    private static final String SETTINGS_FILE = ".settings";
    private static final String DATA_FILE = ".data";
    
    private ApplicationSettings applicationSettings;
    private List<ColorPalette> colorPalettes;
    
    private ApplicationContext() {
        applicationSettings = new ApplicationSettings();
        colorPalettes = new ArrayList<ColorPalette>();
    }

    public static ApplicationContext getContext() {
        if(null != context)
            return context;
        synchronized(ApplicationContext.class){
            if(null == context){
                context = new ApplicationContext();
            }
        }
        return context;
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public List<ColorPalette> getColorPalettes() {
        return colorPalettes;
    }

    
    public void load(){
        boolean emptyData = false;
        boolean emptySetting = false;
        File settingDir = new File(applicationSettings.getAppSettingsPath());
        if(!settingDir.exists()){
            settingDir.mkdirs();
            emptySetting = true;
        } 
        File settingFile = new File(applicationSettings.getAppSettingsPath()
                + File.separator + SETTINGS_FILE);
        if(!settingFile.exists()){
            emptySetting = true;
        }
        File dataDir = new File(applicationSettings.getAppDataPath());
        if(!dataDir.exists()){
            dataDir.mkdirs();
            emptyData = true;
        }
        File dataFile = new File(applicationSettings.getAppDataPath() 
                + File.separator + DATA_FILE);
        if(!dataFile.exists()){
            emptyData = true;
        }
        if(emptyData && emptySetting)
            return;
        
        
        if(!emptyData){
            ObjectInputStream dataInputStream = null;
            try{
                dataInputStream = new ObjectInputStream(
                        new FileInputStream(dataFile));
                Object object = dataInputStream.readObject();
                if(null != object){
                    colorPalettes = (List<ColorPalette>) object;
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            } finally {
                if(null != dataInputStream)
                    try {
                    dataInputStream.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
        
        if(!emptySetting){
            ObjectInputStream dataInputStream = null;
            try{
                dataInputStream = new ObjectInputStream(
                        new FileInputStream(settingFile));
                Object object = dataInputStream.readObject();
                if(null != object){
                    applicationSettings = (ApplicationSettings) object;
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            } finally {
                if(null != dataInputStream)
                    try {
                    dataInputStream.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }
    
    public void save(){
        File dir = new File(applicationSettings.getAppDataPath());
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dataFile = new File(applicationSettings.getAppDataPath() 
                + File.separator + DATA_FILE);
        
        ColorPaletteManager cpm = ColorPaletteManager.getInstance();
        Set<String> palNames = cpm.getAllPaletteNames();
        if(null != palNames){
            colorPalettes = new ArrayList<ColorPalette>();
            for (String name : palNames) {
                ColorPalette cp = new ColorPalette();
                cp.setName(name);
                if(null != cpm.getAllColorPanels(name)){
                    for(ColorPanel panel : cpm.getAllColorPanels(name)){
                        if(panel.isColorGrabbed())
                            cp.add(GraphicsUtil.encodeColor(panel.getSelectedColor()));
                    }
                }
                colorPalettes.add(cp);
            }
        }
        ObjectOutputStream dataOutputStream = null;
        try{
            dataOutputStream = new ObjectOutputStream(
                    new FileOutputStream(dataFile, false));
            dataOutputStream.writeObject(colorPalettes);
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(null != dataOutputStream)
                try {
                dataOutputStream.close();
            } catch (IOException ex) {
                // ignore
            }
        }
        
        File settingFile = new File(applicationSettings.getAppSettingsPath()
                + File.separator + SETTINGS_FILE);
        
        ObjectOutputStream settingOutputStream = null;
        try{
            settingOutputStream = new ObjectOutputStream(
                    new FileOutputStream(settingFile, false));
            settingOutputStream.writeObject(applicationSettings);
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(null != settingOutputStream)
                try {
                settingOutputStream.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    
    
}
