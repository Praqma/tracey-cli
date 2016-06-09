/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.core;

/**
 *
 * @author Mads
 */
public class TraceyCLIException extends Exception {
    protected final String input;

    public TraceyCLIException(String eventType, Throwable source) {
        super(source);
        this.input = eventType;
    }
}
