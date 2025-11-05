package pl.hubertkuch.jdocify.description;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.hubertkuch.jdocify.annotations.DocumentedMethod;
import java.lang.reflect.Method;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationDescriptionStrategyTest {

    private AnnotationDescriptionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AnnotationDescriptionStrategy();
    }

    static class SampleClass {
        @DocumentedMethod(description = "This is a documented method.")
        public void documentedMethod() {
        }

        public void undocumentedMethod() {
        }
    }

    @Test
    void getDescription_shouldReturnDescriptionFromAnnotation() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("documentedMethod");
        Optional<String> description = strategy.getDescription(method);
        assertTrue(description.isPresent());
        assertEquals("This is a documented method.", description.get());
    }

    @Test
    void getDescription_shouldReturnEmptyForMethodWithoutAnnotation() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("undocumentedMethod");
        Optional<String> description = strategy.getDescription(method);
        assertFalse(description.isPresent());
    }
}
