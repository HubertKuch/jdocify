package pl.hubertkuch.jdocify.generator;

import de.kherud.llama.ModelParameters;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.ai.AiDocGenerator;
import pl.hubertkuch.jdocify.annotations.Documented;
import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;
import pl.hubertkuch.jdocify.description.AiDescriptionStrategy;
import pl.hubertkuch.jdocify.description.AnnotationDescriptionStrategy;
import pl.hubertkuch.jdocify.description.DescriptionStrategy;
import pl.hubertkuch.jdocify.description.JavaDocDescriptionStrategy;
import pl.hubertkuch.jdocify.parser.JavaDocParser;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.writer.DocumentationWriter;

public class DocumentationGenerator {

    private static final Logger log = LoggerFactory.getLogger(DocumentationGenerator.class);

    private final TemplateEngine templateEngine;
    private final DocumentationWriter documentationWriter;

    public DocumentationGenerator() {
        this.templateEngine = new TemplateEngine();
        this.documentationWriter = new DocumentationWriter();
    }

    public static void main(String[] args) throws IOException {
        var packageToScan = System.getProperty("jdocify.scanPackage");
        if (packageToScan == null || packageToScan.isEmpty()) {
            log.error("Error: The package to scan was not specified.");
            log.error(
                    "Please configure the 'jdocify.scanPackage' system property in your build"
                            + " configuration.");
            System.exit(1);
        }

        log.info("Scanning for @Documented classes in package: {}", packageToScan);
        var reflections = new Reflections(packageToScan, Scanners.TypesAnnotated);
        var documentedClasses = reflections.getTypesAnnotatedWith(Documented.class);

        if (documentedClasses.isEmpty()) {
            log.info("No classes found with the @Documented annotation.");
        } else {
            log.info("Found {} documented class(es):", documentedClasses.size());
            new DocumentationGenerator().generate(documentedClasses);
        }
    }

    public void generate(Set<Class<?>> classes) throws IOException {
        for (var clazz : classes) {
            log.info("Generating documentation for class: {}", clazz.getName());
            var javaDocParser = new JavaDocParser(getFilePath(clazz));
            var aiDocGenerator = initAiDocGenerator();
            var descriptionStrategies =
                    List.of(
                            new AnnotationDescriptionStrategy(),
                            new JavaDocDescriptionStrategy(javaDocParser),
                            new AiDescriptionStrategy(aiDocGenerator));
            var classData = processClass(clazz, javaDocParser, descriptionStrategies);
            var renderedTemplate =
                    templateEngine.render(
                            templateEngine.getTemplate("class.md.template"), classData);
            documentationWriter.write(clazz.getSimpleName(), renderedTemplate);
        }
    }

    private Map<String, String> processClass(
            Class<?> clazz,
            JavaDocParser javaDocParser,
            List<DescriptionStrategy> descriptionStrategies)
            throws IOException {
        var documentedAnnotation = clazz.getAnnotation(Documented.class);
        var classDescription = documentedAnnotation.description();
        if (classDescription.isEmpty()) {
            classDescription = javaDocParser.getClassJavaDoc(clazz.getSimpleName()).orElse("");
        }

        var data = new HashMap<String, String>();
        data.put(
                "class.name",
                documentedAnnotation.name().isEmpty()
                        ? clazz.getSimpleName()
                        : documentedAnnotation.name());
        data.put("class.description", classDescription);
        data.put("fields", processFields(clazz, javaDocParser));
        data.put("constructors", processConstructors(clazz, javaDocParser));
        data.put("methods", processMethods(clazz, javaDocParser, descriptionStrategies));

        return data;
    }

    private String processFields(Class<?> clazz, JavaDocParser javaDocParser) throws IOException {
        var fieldsBuilder = new StringBuilder();
        var fieldTemplate = templateEngine.getTemplate("field.md.template");
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(DocumentedExcluded.class)) {
                continue;
            }
            var fieldData = new HashMap<String, String>();
            fieldData.put("field.name", field.getName());
            fieldData.put("field.type", field.getType().getSimpleName());
            fieldsBuilder.append(templateEngine.render(fieldTemplate, fieldData));
        }

        return fieldsBuilder.toString();
    }

    private String processConstructors(Class<?> clazz, JavaDocParser javaDocParser)
            throws IOException {
        var constructorsBuilder = new StringBuilder();
        var constructorTemplate = templateEngine.getTemplate("constructor.md.template");
        for (var constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(DocumentedExcluded.class)) {
                continue;
            }
            var constructorData = new HashMap<String, String>();
            constructorData.put("constructor.signature", getConstructorSignature(constructor));
            constructorsBuilder.append(templateEngine.render(constructorTemplate, constructorData));
        }

        return constructorsBuilder.toString();
    }

    private String processMethods(
            Class<?> clazz,
            JavaDocParser javaDocParser,
            List<DescriptionStrategy> descriptionStrategies)
            throws IOException {
        var methodsBuilder = new StringBuilder();
        var methodTemplate = templateEngine.getTemplate("method.md.template");
        for (var method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(DocumentedExcluded.class)) {
                continue;
            }

            var description =
                    descriptionStrategies.stream()
                            .map(strategy -> strategy.getDescription(method))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .findFirst()
                            .orElse("");

            var methodData = new HashMap<String, String>();
            methodData.put("method.name", method.getName());
            methodData.put("method.signature", getMethodSignature(method));
            methodData.put("method.description", description);
            methodsBuilder.append(templateEngine.render(methodTemplate, methodData));
        }

        return methodsBuilder.toString();
    }

    private AiDocGenerator initAiDocGenerator() {
        var modelPath = System.getProperty("jdocify.modelPath");
        if (modelPath != null && !modelPath.isEmpty()) {
            log.info("Initializing AiDocGenerator with model path: {}", modelPath);
            var modelParameters = new ModelParameters().setModel(modelPath);

            return new AiDocGenerator(modelParameters);
        }
        log.warn("AiDocGenerator not initialized. 'jdocify.modelPath' system property is not set.");

        return null;
    }

    private String getMethodSignature(Method method) {
        return getExecutableSignature(method) + ": " + method.getReturnType().getSimpleName();
    }

    private String getConstructorSignature(Constructor<?> constructor) {
        return getExecutableSignature(constructor);
    }

    private String getExecutableSignature(Executable executable) {
        return Modifier.toString(executable.getModifiers())
                + " "
                + executable.getName()
                + "("
                + Arrays.stream(executable.getParameters())
                        .map(p -> p.getType().getSimpleName() + " " + p.getName())
                        .collect(Collectors.joining(", "))
                + ")";
    }

    private String getFilePath(Class<?> clazz) {
        return "src"
                + File.separator
                + "main"
                + File.separator
                + "java"
                + File.separator
                + clazz.getName().replace(".", File.separator)
                + ".java";
    }
}
