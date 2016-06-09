/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.praqma.tracey.Tracey;

/**
 *
 * @author Mads
 */
public class TraceyIllegalEventException extends TraceyCLIException {

    static List<Tracey.Eiffel> vals = Arrays.asList(Tracey.Eiffel.values());
    static List<String> names = new ArrayList<>(TraceyIllegalEventException.vals.size());
    static {
        TraceyIllegalEventException.vals.stream().forEach((val) -> {
                TraceyIllegalEventException.names.add(val.name());
        });
    }

    public TraceyIllegalEventException(String eventType, Throwable source) {
        super(createMessage(eventType), source);
    }

    private static final String createMessage(String eventType) {
        return String.format("Illegal event type declared. Declared value was %s, must be one of: [%s]", eventType, String.join(" ", names));
    }
}
