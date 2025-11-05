package pl.hubertkuch.jdocify.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

public class MethodBodyExtractor {

    private static final Path SOURCE_ROOT = Paths.get("src", "main", "java");

    /**
     * Extracts the body of a method as a String using source code parsing.
     *
     * @param method The java.lang.reflect.Method object to analyze.
     * @return The method's body as a String, or an empty string if not found.
     */
    public static String getBody(Method method) {
        try {
            File sourceFile = findSourceFile(method.getDeclaringClass());
            var compilationUnit = StaticJavaParser.parse(sourceFile);

            AtomicReference<String> methodBody = new AtomicReference<>("");

            new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodDeclaration md, Void arg) {
                    super.visit(md, arg);
                    if (md.getNameAsString().equals(method.getName()) &&
                            md.getParameters().size() == method.getParameterCount()) {

                        md.getBody().ifPresent(body -> methodBody.set(body.toString()));
                    }
                }
            }.visit(compilationUnit, null);

            return methodBody.get();

        } catch (FileNotFoundException e) {
            System.err.println("Source file not found for class: " + method.getDeclaringClass().getName());
            return "";
        } catch (Exception e) {
            System.err.println("Error parsing source file: " + e.getMessage());
            return "";
        }
    }

    /**
     * Locates the .java source file for a given Class object.
     */
    private static File findSourceFile(Class<?> clazz) {
        String classPath = clazz.getName().replace('.', File.separatorChar);
        Path filePath = SOURCE_ROOT.resolve(classPath + ".java");
        return filePath.toFile();
    }
}
