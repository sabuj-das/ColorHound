/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.event.ColorGrabListener
 * Date     : May 23, 2013
 */

package com.gs.tools.colorhound.event;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public interface ColorGrabListener extends BaseEventListener{

    void colorGrabbed(ColorGrabEvent event);
    
}
