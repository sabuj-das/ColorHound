/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Sabuj
 */
public class ApplicationSettings implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3122703035986274992L;
	private boolean doNotShowExitDialog = true;
    private boolean closeToHide = true;
    private boolean hideWhenMinimized = true;
    private boolean alwaysOnTop = true;
    private String appDataPath ;
    private String appSettingsPath ;
    
    public ApplicationSettings() {
        appDataPath = System.getProperty("user.home")
            + File.separator + ".colorhound"
            + File.separator + "app-data";
        appSettingsPath = System.getProperty("user.home")
            + File.separator + ".colorhound";
    }

    public boolean isDoNotShowExitDialog() {
        return doNotShowExitDialog;
    }

    public void setDoNotShowExitDialog(boolean doNotShowExitDialog) {
        this.doNotShowExitDialog = doNotShowExitDialog;
    }

    public String getAppDataPath() {
        return appDataPath;
    }

    public void setAppDataPath(String appDataPath) {
        this.appDataPath = appDataPath;
    }

    public String getAppSettingsPath() {
        return appSettingsPath;
    }

    public void setAppSettingsPath(String appSettingsPath) {
        this.appSettingsPath = appSettingsPath;
    }

    public boolean isCloseToHide() {
        return closeToHide;
    }

    public void setCloseToHide(boolean closeToHide) {
        this.closeToHide = closeToHide;
    }

    public boolean isHideWhenMinimized() {
        return hideWhenMinimized;
    }

    public void setHideWhenMinimized(boolean hideWhenMinimized) {
        this.hideWhenMinimized = hideWhenMinimized;
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }
    
    public void copySettings(ApplicationSettings settings){
        if(null == settings)
            return;
        doNotShowExitDialog = settings.isDoNotShowExitDialog();
        closeToHide = settings.isCloseToHide();
        hideWhenMinimized = settings.isHideWhenMinimized();
        alwaysOnTop = settings.isAlwaysOnTop();
        appDataPath = settings.getAppDataPath();
        appSettingsPath = settings.getAppSettingsPath();
    }
}
