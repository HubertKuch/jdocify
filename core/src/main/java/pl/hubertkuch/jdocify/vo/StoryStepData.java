package pl.hubertkuch.jdocify.vo;

import java.util.List;
import java.util.Optional;

public record StoryStepData(
        Optional<String> narrative,
        Optional<ClassData> classData,
        List<MethodData> methodDataList
) {}
