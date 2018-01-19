/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.tools.colorhound.event;

/**
 *
 * @author Sabuj
 */
public interface ColorDetectListener extends BaseEventListener{
    void colorDetected(ColorDetectEvent event);
}
