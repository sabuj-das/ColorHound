/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.AppSettingsChangedListener
 * Date     : May 28, 2013
 */

package com.gs.tools.colorhound.event;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public interface AppSettingsChangedListener extends BaseEventListener{
    void appSettingsChanged(AppSettingsChangedEvent event);
}
