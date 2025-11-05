package pl.hubertkuch.jdocify.generator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.hubertkuch.jdocify.filter.MemberFilter;
import pl.hubertkuch.jdocify.resources.SampleClassForFilterTest;
import pl.hubertkuch.jdocify.settings.Settings;
import pl.hubertkuch.jdocify.vo.ConstructorData;
import pl.hubertkuch.jdocify.vo.FieldData;
import pl.hubertkuch.jdocify.vo.MethodData;

import pl.hubertkuch.jdocify.writer.DocumentationWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DocumentationGeneratorTest {

    private DocumentationGenerator documentationGenerator;

    @BeforeEach
    public void setUp() {
        Settings.initialize();
        documentationGenerator = new DocumentationGenerator();
    }

    @AfterEach
    public void tearDown() {
        Settings.reset();
    }

    @Test
    public void testDefaultMemberFilter() throws Exception {
        // given
        Class<?> clazz = SampleClassForFilterTest.class;

        // when
        List<FieldData> fields = processFields(clazz);
        List<ConstructorData> constructors = processConstructors(clazz);
        List<MethodData> methods = processMethods(clazz);

        // then
        assertEquals(1, fields.size());
        assertEquals("publicField", fields.get(0).name());

        assertEquals(1, constructors.size());

        assertEquals(1, methods.size());
        assertEquals("publicMethod", methods.get(0).name());
    }

    @Test
    public void testCustomMemberFilter() throws Exception {
        // given
        Class<?> clazz = SampleClassForFilterTest.class;
        MemberFilter customFilter = new MemberFilter() {
            @Override
            public boolean filterField(Field field) {
                return field.getName().equals("privateField");
            }

            @Override
            public boolean filterConstructor(Constructor<?> constructor) {
                return constructor.getParameterCount() == 1;
            }

            @Override
            public boolean filterMethod(Method method) {
                return method.getName().equals("privateMethod");
            }
        };
        Settings.setMemberFilter(customFilter);
        documentationGenerator = new DocumentationGenerator();

        // when
        List<FieldData> fields = processFields(clazz);
        List<ConstructorData> constructors = processConstructors(clazz);
        List<MethodData> methods = processMethods(clazz);

        // then
        assertEquals(1, fields.size());
        assertEquals("privateField", fields.get(0).name());

        assertEquals(1, constructors.size());

        assertEquals(1, methods.size());
        assertEquals("privateMethod", methods.get(0).name());
    }

    private List<FieldData> processFields(Class<?> clazz) throws Exception {
        Method method = DocumentationGenerator.class.getDeclaredMethod("processFields", Class.class);
        method.setAccessible(true);
        return (List<FieldData>) method.invoke(documentationGenerator, clazz);
    }

    private List<ConstructorData> processConstructors(Class<?> clazz) throws Exception {
        Method method = DocumentationGenerator.class.getDeclaredMethod("processConstructors", Class.class);
        method.setAccessible(true);
        return (List<ConstructorData>) method.invoke(documentationGenerator, clazz);
    }

    private List<MethodData> processMethods(Class<?> clazz) throws Exception {
        Method method = DocumentationGenerator.class.getDeclaredMethod("processMethods", Class.class, List.class);
        method.setAccessible(true);
        return (List<MethodData>) method.invoke(documentationGenerator, clazz, Collections.emptyList());
    }

    @Test
    public void testCustomSourcePath() throws Exception {
        // given
        System.setProperty("jdocify.source.paths", "src/test/custom_src");
        Settings.reset(); // force reload of settings
        DocumentationWriter mockWriter = mock(DocumentationWriter.class);
        Settings.setDocumentationWriter(mockWriter);
        documentationGenerator = new DocumentationGenerator();
        Set<Class<?>> classes = Set.of(com.example.MyCustomClass.class);

        // when
        documentationGenerator.generate(classes);

        // then
        verify(mockWriter).write(anyString(), anyString());
    }
}
