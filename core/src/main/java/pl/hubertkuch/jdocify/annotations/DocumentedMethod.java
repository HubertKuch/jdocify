package pl.hubertkuch.jdocify.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method-level annotation to explicitly include a method in the documentation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DocumentedMethod {
    /**
     * (Optional) A custom name for the method.
     */
    String name() default "";

    /**
     * (Optional) A detailed description of the method's functionality.
     */
    String description() default "";
}
