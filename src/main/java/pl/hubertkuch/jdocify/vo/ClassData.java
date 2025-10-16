package pl.hubertkuch.jdocify.vo;

import java.util.List;

public record ClassData(
        String name, String description, List<FieldData> fields, List<ConstructorData> constructors, List<MethodData> methods) {}
