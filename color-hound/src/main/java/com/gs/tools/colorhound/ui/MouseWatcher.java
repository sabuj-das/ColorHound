/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.ui;

import com.gs.tools.colorhound.event.ApplicationEventManager;
import com.gs.tools.colorhound.event.MouseInfoChangeEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.TimerTask;

/**
 *
 * @author Sabuj
 */
public class MouseWatcher extends TimerTask {

    static ApplicationEventManager eventManager = ApplicationEventManager.getInstance();

    @Override
    public void run() {
        try {
            Point location = MouseInfo.getPointerInfo().getLocation();
            if (null != location) {
                MouseInfoChangeEvent event = new MouseInfoChangeEvent(
                        location, location, this);
                eventManager.fireEvent(event);
            }
        } catch (Exception ex) {
        }

    }
}
