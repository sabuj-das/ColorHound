/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.AppSettingsChangedEvent
 * Date     : May 28, 2013
 */

package com.gs.tools.colorhound.event;

import com.gs.tools.colorhound.ApplicationSettings;
import java.util.EventObject;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class AppSettingsChangedEvent extends EventObject 
        implements ApplicationEvent{

    private final ApplicationSettings oldValue;
	private final ApplicationSettings newValue;

    public AppSettingsChangedEvent(ApplicationSettings oldValue, 
            ApplicationSettings newValue, Object source) {
        super(source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public ApplicationSettings getOldValue() {
        return oldValue;
    }

    public ApplicationSettings getNewValue() {
        return newValue;
    }

    
}