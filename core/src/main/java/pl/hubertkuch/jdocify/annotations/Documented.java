package pl.hubertkuch.jdocify.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class-level annotation that marks a class, interface, or enum to be included in the documentation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Documented {
    /**
     * (Optional) A custom name for the component in the documentation.
     */
    String name() default "";

    /**
     * (Optional) A brief description of the component.
     */
    String description() default "";
}
