/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.ApplicationEventManager
 * Date     : May 23, 2013
 */

package com.gs.tools.colorhound.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class ApplicationEventManager {

    private static ApplicationEventManager instance;
    private Map<Class, List<BaseEventListener>> allListeners;
    
    private ApplicationEventManager() {
        this.allListeners = new HashMap<Class, List<BaseEventListener>>();
    }

    public static ApplicationEventManager getInstance() {
        if(null != instance)
            return instance;
        synchronized(ApplicationEventManager.class){
            if(null == instance){
                instance = new ApplicationEventManager();
            }
        }
        return instance;
    }

    public synchronized void registerListener(final Class eventType, 
            final BaseEventListener listener){
        if(null == allListeners.get(eventType)){
            allListeners.put(eventType, new ArrayList<BaseEventListener>());
        }
        allListeners.get(eventType).add(listener);
    }
    
    public synchronized void unregisterListener(final Class eventType){
        allListeners.remove(eventType);
    }
    
    public synchronized void fireEvent(ApplicationEvent event) {
		if(null == event)
			return;
		List<BaseEventListener> listeners = allListeners.get(event.getClass());
        if(null != listeners){
            for (BaseEventListener listener : listeners) {
                if(event.getClass() == ColorGrabEvent.class){
                    fireColorGrabEvent((ColorGrabEvent)event, 
                            (ColorGrabListener)listener);
                }
                if(event.getClass() == ColorDetectEvent.class){
                    fireColorDetectEvent((ColorDetectEvent)event, 
                            (ColorDetectListener)listener);
                }
                if(event.getClass() == ColorPanelSelectedEvent.class){
                    fireColorPanelSelectedEvent((ColorPanelSelectedEvent)event, 
                            (ColorPanelSelectedEventListener)listener);
                }
                if(event.getClass() == AppSettingsChangedEvent.class){
                    fireAppSettingsChangedEvent((AppSettingsChangedEvent)event, 
                            (AppSettingsChangedListener)listener);
                }
                if(event.getClass() == MouseInfoChangeEvent.class){
                    fireMouseInfoChangeEvent((MouseInfoChangeEvent)event, 
                            (MouseInfoChangedListener)listener);
                }
            }
        }
	}
    
    private synchronized void fireColorGrabEvent(ColorGrabEvent event, ColorGrabListener listener){
        listener.colorGrabbed(event);
    }

    private void fireColorDetectEvent(ColorDetectEvent event, ColorDetectListener listener) {
        listener.colorDetected(event);
    }

    private void fireColorPanelSelectedEvent(ColorPanelSelectedEvent event, ColorPanelSelectedEventListener listener) {
        listener.colorPanelSelected(event);
    }

    private void fireAppSettingsChangedEvent(AppSettingsChangedEvent event, AppSettingsChangedListener listener) {
        listener.appSettingsChanged(event);
    }

    private void fireMouseInfoChangeEvent(MouseInfoChangeEvent mouseInfoChangeEvent, MouseInfoChangedListener mouseInfoChangedListener) {
        mouseInfoChangedListener.mouseInfoChanged(mouseInfoChangeEvent);
    }
 }
