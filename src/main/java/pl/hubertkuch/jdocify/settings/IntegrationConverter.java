package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Converter;
import pl.hubertkuch.jdocify.integrations.Integration;

import java.lang.reflect.Method;

public class IntegrationConverter implements Converter<Class<? extends Integration>> {

    @Override
    public Class<? extends Integration> convert(Method method, String text) {
        try {
            return (Class<? extends Integration>) Class.forName(text);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find integration class: " + text, e);
        }
    }
}
