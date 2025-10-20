package pl.hubertkuch.jdocify.filter;

import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * The default implementation of the {@link MemberFilter} interface.
 * This filter includes all public members that are not explicitly excluded with the {@link DocumentedExcluded} annotation.
 */
public class DefaultMemberFilter implements MemberFilter {

    @Override
    public boolean filterField(Field field) {
        return !field.isAnnotationPresent(DocumentedExcluded.class) && !Modifier.isPrivate(field.getModifiers());
    }

    @Override
    public boolean filterConstructor(Constructor<?> constructor) {
        return !constructor.isAnnotationPresent(DocumentedExcluded.class) && !Modifier.isPrivate(constructor.getModifiers());
    }

    @Override
    public boolean filterMethod(Method method) {
        return !method.isAnnotationPresent(DocumentedExcluded.class) && !Modifier.isPrivate(method.getModifiers());
    }
}
