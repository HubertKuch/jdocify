package pl.hubertkuch.jdocify.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaDocParser {

    private final com.github.javaparser.ast.CompilationUnit compilationUnit;

    public JavaDocParser(String filePath) throws IOException {
        this.compilationUnit = StaticJavaParser.parse(Paths.get(filePath));
    }

    public Optional<String> getClassJavaDoc(String className) {
        return compilationUnit
                .getClassByName(className)
                .flatMap(ClassOrInterfaceDeclaration::getJavadocComment)
                .map(comment -> parseJavaDoc(comment.parse()));
    }

    public Optional<String> getMethodJavaDoc(String methodName) {
        return compilationUnit.findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getNameAsString().equals(methodName))
                .findFirst()
                .flatMap(MethodDeclaration::getJavadocComment)
                .map(comment -> parseJavaDoc(comment.parse()));
    }

    public Optional<String> getFieldJavaDoc(String fieldName) {
        return compilationUnit.findAll(FieldDeclaration.class).stream()
                .filter(field -> field.getVariable(0).getNameAsString().equals(fieldName))
                .findFirst()
                .flatMap(FieldDeclaration::getJavadocComment)
                .map(comment -> parseJavaDoc(comment.parse()));
    }

    private String parseJavaDoc(Javadoc javadoc) {
        var description = javadoc.getDescription().toText();
        var blockTags =
                javadoc.getBlockTags().stream()
                        .map(this::formatBlockTag)
                        .collect(Collectors.joining("\n"));

        return description + "\n" + blockTags;
    }

    private String formatBlockTag(JavadocBlockTag blockTag) {
        return "**" + blockTag.getTagName() + "**: " + blockTag.getContent().toText();
    }
}