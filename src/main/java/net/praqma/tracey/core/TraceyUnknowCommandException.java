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
public class TraceyUnknowCommandException extends TraceyCLIException {

    static String[] cmds = { "say", "listen" };

    public TraceyUnknowCommandException(String cmd, Throwable cause) {
        super(generateMessage(cmd), cause);
    }
    
    private static String generateMessage(String cmd) {
        return String.format("Illegal command specified: %s. Must be one of: [%s]", cmd, String.join(" ", cmds));
    }

}
