package pl.hubertkuch.jdocify.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that can be placed on a package or a dedicated class to combine multiple documented components into a single Markdown file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface DocumentedStory {
    /**
     * The name of the generated Markdown file (e.g., "WebSocket-Creation-Workflow").
     */
    String name();

    /**
     * An array of steps that make up the story.
     */
    StoryStep[] steps();
}
