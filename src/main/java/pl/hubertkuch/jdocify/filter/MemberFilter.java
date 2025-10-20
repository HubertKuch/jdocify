package pl.hubertkuch.jdocify.filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * An interface for filtering class members (fields, constructors, and methods).
 * Implementations of this interface can be used to control which members are included in the generated documentation.
 */
public interface MemberFilter {

    /**
     * Filters a field.
     *
     * @param field The field to filter.
     * @return {@code true} if the field should be included, {@code false} otherwise.
     */
    boolean filterField(Field field);

    /**
     * Filters a constructor.
     *
     * @param constructor The constructor to filter.
     * @return {@code true} if the constructor should be included, {@code false} otherwise.
     */
    boolean filterConstructor(Constructor<?> constructor);

    /**
     * Filters a method.
     *
     * @param method The method to filter.
     * @return {@code true} if the method should be included, {@code false} otherwise.
     */
    boolean filterMethod(Method method);
}
