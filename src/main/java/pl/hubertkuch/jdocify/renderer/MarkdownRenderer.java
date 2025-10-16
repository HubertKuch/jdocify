package pl.hubertkuch.jdocify.renderer;

import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.StoryData;

import java.io.IOException;

public interface MarkdownRenderer {
    String render(ClassData classData) throws IOException;
    String render(StoryData storyData) throws IOException;
}
