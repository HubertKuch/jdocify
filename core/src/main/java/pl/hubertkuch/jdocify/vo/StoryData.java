package pl.hubertkuch.jdocify.vo;

import java.util.List;

public record StoryData(
        String name,
        List<StoryStepData> steps
) {}
