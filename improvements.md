# JDocify Improvement Suggestions

Based on the analysis of the project, here are some areas for improvement to make the library more abstract and customizable, along with things to check:

### Areas for Improvement

1.  **Decouple component implementations from the `Settings` class:**
    *   **Observation:** The `Settings.initialize()` method currently hardcodes the creation of default implementations like `DefaultTemplateEngine`, `DefaultDocumentationWriter`, and `VitePressIntegration`. While the setters in the `Settings` class allow for programmatic overriding of these defaults, a more flexible approach would be to allow configuration through a properties file.
    *   **Suggestion:** Use a factory pattern to allow users to specify custom component implementations in the configuration file. This would work even for classes with constructors that require parameters.
        *   For each extensible component (like `TemplateEngine`), a corresponding factory interface (e.g., `TemplateEngineFactory`) would be introduced.
        *   This factory interface would have a `create(DocifySettings settings)` method, giving the factory access to the application's configuration.
        *   In the configuration file, the user would specify the factory class, like this:
            ```properties
            jdocify.template.engine.factory=com.example.MyCustomTemplateEngineFactory
            ```
        *   The user would then implement the factory to handle the creation of their custom component, including any complex initialization logic.
            ```java
            public class MyCustomTemplateEngineFactory implements TemplateEngineFactory {
                @Override
                public TemplateEngine create(DocifySettings settings) {
                    String templatePath = settings.get("my.custom.template.path");
                    return new MyCustomTemplateEngine(templatePath);
                }
            }
            ```

### What to Check

1.  **Plugin System:**
    *   For ultimate extensibility, consider implementing a plugin system. This could leverage Java's `ServiceLoader` mechanism, allowing third-party extensions to provide implementations for the various interfaces (`Integration`, `DescriptionStrategy`, etc.).

2.  **Integration Mechanism:**
    *   The `VitePressIntegration` is tightly coupled with the `Settings` class. It would be beneficial to design a more generic integration point. This could be a factory pattern or a registration mechanism where different integrations can be made available and chosen via configuration.

3.  **Reflection Library Configuration:**
    *   The `Reflections` library is a powerful tool with many configuration options. Exposing more of these options (e.g., different types of scanners) to the user through the settings file could enable more advanced use cases.

### Untested Areas

Based on a comparison of the production and test files, the following areas are not currently covered by tests:

*   **`pl.hubertkuch.jdocify.JDocify`**: The main entry point of the application is not tested. A test could be written to check that the `run` method correctly scans for classes and calls the `DocumentationGenerator`.
*   **`pl.hubertkuch.jdocify.ai.AiDocGenerator`**: This class is responsible for generating documentation using the AI model. It is a critical part of the AI feature and should be tested.
*   **`pl.hubertkuch.jdocify.ai.ModelManager`**: This class is only partially tested in an integration test. More unit tests could be added to cover the logic for downloading and managing the AI model.
*   **`pl.hubertkuch.jdocify.exceptions.DocGenerationException`**: A simple test could be written to check that the exception can be created.
*   **`pl.hubertkuch.jdocify.naming.DefaultFileNamer`**: This class is responsible for naming the output files. It should be tested to ensure that it generates the correct file names for classes and stories.
*   **`pl.hubertkuch.jdocify.renderer.DefaultMarkdownRenderer`**: This class is responsible for rendering the final Markdown documentation. It is a critical part of the documentation generation process and should be tested.
*   **`pl.hubertkuch.jdocify.settings.ClassConverter`**: This class is a converter for the `owner` library. It could be tested to ensure that it correctly converts class names to `Class` objects.
*   **`pl.hubertkuch.jdocify.utils.FancyFileDownloader`**: This utility class is used for downloading files. It should be tested to ensure that it can download files correctly and handles errors gracefully.
*   **`pl.hubertkuch.jdocify.utils.MethodBodyExtractor`**: This utility class is used for extracting the body of a method. It should be tested with different types of methods to ensure that it works correctly.
*   **`pl.hubertkuch.jdocify.writer.DefaultDocumentationWriter`**: This class is responsible for writing the documentation to files. It should be tested to ensure that it writes the files to the correct location with the correct content.
