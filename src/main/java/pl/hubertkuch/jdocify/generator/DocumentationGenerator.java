package pl.hubertkuch.jdocify.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.ai.AiDocGenerator;
import pl.hubertkuch.jdocify.ai.ModelManager;
import pl.hubertkuch.jdocify.annotations.Documented;
import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;
import pl.hubertkuch.jdocify.annotations.DocumentedStory;
import pl.hubertkuch.jdocify.description.AiDescriptionStrategy;
import pl.hubertkuch.jdocify.description.AnnotationDescriptionStrategy;
import pl.hubertkuch.jdocify.description.DescriptionStrategy;
import pl.hubertkuch.jdocify.description.JavaDocDescriptionStrategy;
import pl.hubertkuch.jdocify.naming.FileNamer;
import pl.hubertkuch.jdocify.parser.JavaDocParser;
import pl.hubertkuch.jdocify.renderer.DefaultMarkdownRenderer;
import pl.hubertkuch.jdocify.settings.Settings;
import pl.hubertkuch.jdocify.template.DefaultTemplateEngine;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.ConstructorData;
import pl.hubertkuch.jdocify.vo.FieldData;
import pl.hubertkuch.jdocify.vo.MethodData;
import pl.hubertkuch.jdocify.vo.StoryData;
import pl.hubertkuch.jdocify.vo.StoryStepData;
import pl.hubertkuch.jdocify.writer.DefaultDocumentationWriter;
import pl.hubertkuch.jdocify.writer.DocumentationWriter;

public class DocumentationGenerator {

    private static final Logger log = LoggerFactory.getLogger(DocumentationGenerator.class);

    private final TemplateEngine templateEngine;
    private final DocumentationWriter documentationWriter;
    private final ModelManager modelManager;
    private final FileNamer fileNamer;

    public DocumentationGenerator() {
        this.modelManager = new ModelManager();

        this.templateEngine = Settings.templateEngine();
        this.documentationWriter = Settings.documentationWriter();
        this.fileNamer = Settings.fileNamer();
    }

    public static void main(String[] args) throws IOException {
        var packageToScan = Settings.get().getScanPackage();
        if (packageToScan == null || packageToScan.isEmpty()) {
            log.error("Error: The package to scan was not specified.");
            log.error("Please configure the 'jdocify.scanPackage' in your config.properties file or" + " as a system property.");

            return;
        }

        log.info("Scanning for @Documented and @DocumentedStory classes in package: {}", packageToScan);
        var reflections = new Reflections(packageToScan, Scanners.TypesAnnotated);

        var documentedClasses = reflections.getTypesAnnotatedWith(Documented.class);
        if (documentedClasses.isEmpty()) {
            log.info("No classes found with the @Documented annotation.");
        } else {
            log.info("Found {} documented class(es):", documentedClasses.size());
            new DocumentationGenerator().generate(documentedClasses);
        }

        var documentedStoryClasses = reflections.getTypesAnnotatedWith(DocumentedStory.class);
        if (documentedStoryClasses.isEmpty()) {
            log.info("No classes found with the @DocumentedStory annotation.");
        } else {
            log.info("Found {} documented story class(es):", documentedStoryClasses.size());
            new DocumentationGenerator().generateStories(documentedStoryClasses);
        }

        System.exit(0);
    }

    public void generate(Set<Class<?>> classes) throws IOException {
        var markdownRenderer = new DefaultMarkdownRenderer(templateEngine);
        Optional<AiDocGenerator> aiDocGeneratorOptional = modelManager.initAiDocGenerator();

        try {
            for (var clazz : classes) {
                log.info("Generating documentation for class: {}", clazz.getName());

                var javaDocParser = new JavaDocParser(getFilePath(clazz));
                var classData = processClass(clazz, javaDocParser, getDescriptionStrategies(javaDocParser, aiDocGeneratorOptional));
                var renderedTemplate = markdownRenderer.render(classData);

                documentationWriter.write(fileNamer.name(clazz), renderedTemplate);
            }
        } finally {
            aiDocGeneratorOptional.ifPresent(AiDocGenerator::close);
        }
    }

    public void generateStories(Set<Class<?>> storyClasses) throws IOException {
        var markdownRenderer = new DefaultMarkdownRenderer(templateEngine);
        Optional<AiDocGenerator> aiDocGeneratorOptional = modelManager.initAiDocGenerator();

        try {
            for (var storyClass : storyClasses) {
                log.info("Generating documentation for story: {}", storyClass.getName());

                var documentedStoryAnnotation = storyClass.getAnnotation(DocumentedStory.class);
                if (documentedStoryAnnotation == null) {
                    log.warn("Class {} is in storyClasses but does not have @DocumentedStory annotation.", storyClass.getName());
                    continue;
                }

                var storyData = processStory(documentedStoryAnnotation, aiDocGeneratorOptional);
                var renderedTemplate = markdownRenderer.render(storyData);

                documentationWriter.write(fileNamer.name(documentedStoryAnnotation), renderedTemplate);
            }
        } finally {
            aiDocGeneratorOptional.ifPresent(AiDocGenerator::close);
        }
    }

    private StoryData processStory(DocumentedStory documentedStoryAnnotation, Optional<AiDocGenerator> aiDocGeneratorOptional) throws IOException {

        var storyName = documentedStoryAnnotation.name();
        List<StoryStepData> storyStepsData = new ArrayList<>();

        for (var storyStepAnnotation : documentedStoryAnnotation.steps()) {
            if (! storyStepAnnotation.narrative().isEmpty()) {
                // It's a narrative step
                storyStepsData.add(new StoryStepData(Optional.of(storyStepAnnotation.narrative()), Optional.empty(), Collections.emptyList()));
            } else if (! storyStepAnnotation.element().equals(void.class)) {
                // It's an element step
                Class<?> elementClass = storyStepAnnotation.element();
                var javaDocParser = new JavaDocParser(getFilePath(elementClass));
                var elementClassData = processClass(elementClass, javaDocParser, getDescriptionStrategies(javaDocParser, aiDocGeneratorOptional));

                List<MethodData> filteredMethodData = new ArrayList<>();
                if (storyStepAnnotation.methods().length > 0 && elementClassData.methods() != null) {
                    Set<String> methodNamesToInclude = new HashSet<>(Arrays.asList(storyStepAnnotation.methods()));
                    filteredMethodData = elementClassData
                            .methods()
                            .stream()
                            .filter(methodData -> methodNamesToInclude.contains(methodData.name()))
                            .toList();
                } else if (elementClassData.methods() != null) {
                    // If no specific methods are mentioned, include all methods
                    filteredMethodData = elementClassData.methods();
                }

                storyStepsData.add(new StoryStepData(Optional.empty(), Optional.of(elementClassData), filteredMethodData));
            }
        }
        return new StoryData(storyName, storyStepsData);
    }

    private ClassData processClass(Class<?> clazz, JavaDocParser javaDocParser, List<DescriptionStrategy> descriptionStrategies) {
        var documentedAnnotation = clazz.getAnnotation(Documented.class);
        var classDescription = documentedAnnotation.description();

        if (classDescription.isEmpty()) {
            classDescription = javaDocParser.getClassJavaDoc(clazz.getSimpleName()).orElse("");
        }

        var className = documentedAnnotation.name().isEmpty() ? clazz.getSimpleName() : documentedAnnotation.name();

        return new ClassData(className, classDescription, processFields(clazz), processConstructors(clazz), processMethods(clazz, descriptionStrategies));
    }

    private List<FieldData> processFields(Class<?> clazz) {
        return Arrays
                .stream(clazz.getDeclaredFields())
                .filter(field -> ! field.isAnnotationPresent(DocumentedExcluded.class))
                .filter(field -> ! Modifier.isPrivate(field.getModifiers()))

                .map(field -> new FieldData(field.getName(), field.getType().getSimpleName()))
                .toList();
    }

    private List<ConstructorData> processConstructors(Class<?> clazz) {
        var constructors = Arrays
                .stream(clazz.getDeclaredConstructors())
                .filter(constructor -> ! constructor.isAnnotationPresent(DocumentedExcluded.class))
                .filter(constructor -> ! Modifier.isPrivate(constructor.getModifiers()))

                .map(constructor -> new ConstructorData(getConstructorSignature(constructor)))
                .toList();
        log.info("Processed {} constructors for class {}: {}", constructors.size(), clazz.getSimpleName(), constructors);

        return constructors;
    }

    private List<MethodData> processMethods(Class<?> clazz, List<DescriptionStrategy> descriptionStrategies) {
        return Arrays
                .stream(clazz.getDeclaredMethods())
                .filter(method -> ! method.isAnnotationPresent(DocumentedExcluded.class))
                .filter(method -> ! Modifier.isPrivate(method.getModifiers()))
                .map(method -> {
                    var description = descriptionStrategies
                            .stream()
                            .map(strategy -> strategy.getDescription(method))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .findFirst()
                            .orElse("");

                    return new MethodData(method.getName(), getMethodSignature(method), description);
                })
                .toList();
    }

    private List<DescriptionStrategy> getDescriptionStrategies(JavaDocParser javaDocParser, Optional<AiDocGenerator> aiDocGenerator) {
        List<DescriptionStrategy> strategies = new ArrayList<>();

        strategies.add(new AnnotationDescriptionStrategy());
        strategies.add(new JavaDocDescriptionStrategy(javaDocParser));

        aiDocGenerator.ifPresent(docGenerator -> strategies.add(new AiDescriptionStrategy(docGenerator)));

        return strategies;
    }

    private String getMethodSignature(Method method) {
        return getExecutableSignature(method) + ": " + method.getReturnType().getSimpleName();
    }

    private String getConstructorSignature(Constructor<?> constructor) {
        return getExecutableSignature(constructor);
    }

    private String getExecutableSignature(Executable executable) {
        return Modifier.toString(executable.getModifiers()) + " " + executable.getName() + "(" + Arrays
                .stream(executable.getParameters())
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", ")) + ")";
    }

    private String getFilePath(Class<?> clazz) {
        return "src" + File.separator + "main" + File.separator + "java" + File.separator + clazz
                .getName()
                .replace(".", File.separator) + ".java";
    }
}
