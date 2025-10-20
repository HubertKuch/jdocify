package pl.hubertkuch.jdocify.resources;

import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;

public class SampleClassForFilterTest {

    public String publicField;

    private String privateField;

    @DocumentedExcluded
    public String excludedField;

    public SampleClassForFilterTest() {
    }

    private SampleClassForFilterTest(String privateConstructor) {
    }

    @DocumentedExcluded
    public SampleClassForFilterTest(String excludedConstructor, String anotherParam) {
    }

    public void publicMethod() {
    }

    private void privateMethod() {
    }

    @DocumentedExcluded
    public void excludedMethod() {
    }
}
