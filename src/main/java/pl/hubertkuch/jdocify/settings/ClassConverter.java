package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Converter;
import java.lang.reflect.Method;

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
