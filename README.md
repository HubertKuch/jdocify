# JDocify: A Modern Documentation Framework for Java

## Vision

JDocify is a lightweight, annotation-driven documentation framework for Java applications. It aims to simplify the process of generating beautiful, comprehensive, and easy-to-navigate documentation directly from your codebase. The core idea is to use a set of intuitive annotations to control what gets documented and how, reducing the need for external configuration files.

## Core Annotations

Here is the list of annotations that will form the core of JDocify:

*   `@Documented`: A class-level annotation that marks a class, interface, or enum to be included in the documentation.
    *   **Parameters:**
        *   `name`: (Optional) A custom name for the component in the documentation.
        *   `description`: (Optional) A brief description of the component.

*   `@DocumentedMethod`: A method-level annotation to explicitly include a method in the documentation. This can be used to document methods in classes not marked with `@Documented` or to provide more specific details.
    *   **Parameters:**
        *   `name`: (Optional) A custom name for the method.
        *   `description`: (Optional) A detailed description of the method's functionality.

*   `@DocumentedExcluded`: An annotation that can be applied to any element (class, method, field) to explicitly exclude it from the generated documentation. This is useful for hiding internal or utility components.

*   `@DocumentedStory`: An annotation that can be placed on a package or a dedicated class to combine multiple documented components into a single Markdown file. This is ideal for documenting complex workflows or features.
    *   **Parameters:**
        *   `name`: The name of the generated Markdown file (e.g., "WebSocket-Creation-Workflow").
        *   `steps`: An array of `@StoryStep` annotations describing the story's content.

## Reflection-Based Documentation

JDocify will use reflection to automatically document elements within a `@Documented` class. This includes:

*   **Fields:** Public fields will be automatically included.
*   **Methods:** Public methods will be automatically included. Details like parameters and return types will be extracted using reflection.
*   **Constructors:** Public constructors will be documented.

This approach minimizes the number of annotations needed, keeping your code clean. You only need to use annotations when you want to override the default behavior (e.g., provide a custom description) or exclude something from the documentation.

## Advanced Features

### JavaDoc Integration
JDocify will automatically parse and incorporate your existing JavaDoc comments. This means that the descriptions for your classes, methods, and fields can be sourced directly from the JavaDocs, reducing the need to add `description` parameters to the annotations unless you want to override the JavaDoc content.

### `@see` Tag Handling
To create a rich, interconnected documentation experience, JDocify renders Javadoc block tags, including `@see`, in a structured format. For example, a `@see` tag is rendered as **see**: content. Automatic hyperlinking to other documented elements is a goal for future versions.

### AI-Powered Documentation (Optional)
For methods that lack JavaDoc comments, JDocify can use a Large Language Model (LLM) to automatically generate a description. By analyzing the method's name, parameters, and return type, the LLM can infer its purpose and generate a human-readable description. For example, a method `public User getUserById(UUID id)` could have a description automatically generated as: "Gets the user by their ID, taking a UUID as a parameter." This feature will be optional and can be enabled in the project's configuration.

### Custom Member Filtering

JDocify provides a `MemberFilter` interface that allows you to programmatically control which class members (fields, constructors, and methods) are included in the documentation. This provides a powerful mechanism for implementing custom filtering logic beyond the default behavior.

By default, JDocify uses the `DefaultMemberFilter`, which includes all public members that are not annotated with `@DocumentedExcluded`.

To use a custom filter, you need to:
1.  Create a class that implements the `pl.hubertkuch.jdocify.filter.MemberFilter` interface.
2.  Implement the `filterField`, `filterConstructor`, and `filterMethod` methods to define your filtering logic.
3.  Set your custom filter in the `Settings` before running the documentation generator.

**Example of a custom filter that includes only private members:**

```java
import pl.hubertkuch.jdocify.filter.MemberFilter;
import pl.hubertkuch.jdocify.settings.Settings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PrivateMemberFilter implements MemberFilter {

    @Override
    public boolean filterField(Field field) {
        return Modifier.isPrivate(field.getModifiers());
    }

    @Override
    public boolean filterConstructor(Constructor<?> constructor) {
        return Modifier.isPrivate(constructor.getModifiers());
    }

    @Override
    public boolean filterMethod(Method method) {
        return Modifier.isPrivate(method.getModifiers());
    }
}

// To use this filter:
Settings.setMemberFilter(new PrivateMemberFilter());
```

## Usage Example

### Basic Usage

```java
@Documented(name = "User Service", description = "Handles all user-related operations.")
public class UserService {

    // This field will be automatically documented by reflection.
    public final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Finds a user by their unique ID.
     * @see User
     */
    @DocumentedMethod(description = "Finds a user by their unique ID.")
    public User findUserById(Long id) {
        // ... implementation
    }

    public User createUser(String username, String email) {
        // ... implementation
    }

    @DocumentedExcluded
    public void internalHelperMethod() {
        // This method will not appear in the documentation.
    }
}
```

### Story Usage

```java
// On a package-info.java file or a dedicated empty class
@DocumentedStory(
    name = "User-Authentication-Flow",
    steps = {
        @StoryStep(narrative = "The authentication process begins with the `AuthService`. A user provides their credentials, which are then validated."),
        @StoryStep(element = AuthService.class, methods = {"authenticate"}),
        @StoryStep(narrative = "If authentication is successful, a `User` object is retrieved and a JWT token is generated by the `JwtProvider`."),
        @StoryStep(element = User.class),
        @StoryStep(element = JwtProvider.class, methods = {"generateToken"}),
        @StoryStep(narrative = "This token is then returned to the client for subsequent requests.")
    }
)
package com.example.auth;
```

## Roadmap & Future Ideas

*   **Documentation Generator:** The core of the project will be a tool that scans the compiled bytecode for these annotations and generates the documentation.
*   **Integration with Build Tools:** Seamless integration with Gradle.
*   **Project-Level Configuration:** Support for a `config.properties` file to define global settings like output directories, project-wide excludes, and AI feature toggles.
*   **Documentation Validator:** A built-in linter to check for common documentation issues like missing JavaDocs, broken `@see` links, or invalid story elements.
*   **Custom Markdown Templates:** Allow users to provide their own templates to control the structure and layout of the generated Markdown files.
*   **Output Formats:** While initially targeting Markdown, the architecture should allow for extension to other formats in the future.
*   **Automated Diagram Generation:** JDocify aims to analyze the relationships between your `@Documented` classes (e.g., field types, method parameters, return types) to automatically generate class diagrams. These diagrams would be embedded into the documentation using a syntax compatible with modern Markdown renderers like VitePress (e.g., Mermaid.js). This provides a powerful visual aid for understanding your application's architecture directly within the documentation.
