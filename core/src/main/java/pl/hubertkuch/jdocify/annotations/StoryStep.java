package pl.hubertkuch.jdocify.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a single step in a @DocumentedStory.
 * A step can be either a narrative text block or a reference to a documented code element.
 * This annotation is not meant to be used directly, but rather within the 'steps' array of a @DocumentedStory.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StoryStep {
    /**
     * A block of narrative text. Use this for explanations in the story. If set, element should not be set.
     */
    String narrative() default "";

    /**
     * The class to be documented in this step. If set, narrative should not be set.
     */
    Class<?> element() default void.class;

    /**
     * (Optional) Specific method names within the element class to highlight.
     * This is only applicable when 'element' is also set.
     */
    String[] methods() default {};
}
