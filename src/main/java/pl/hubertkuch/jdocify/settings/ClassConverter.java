package pl.hubertkuch.jdocify.settings;

import java.lang.reflect.Method;
import org.aeonbits.owner.Converter;

public class ClassConverter implements Converter<Class<?>> {

    @Override
    public Class<?> convert(Method method, String text) {
        try {
            return Class.forName(text);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find or load class: " + text, e);
        }
    }
}
