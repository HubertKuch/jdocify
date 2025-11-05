package pl.hubertkuch.jdocify.renderer;

import java.io.IOException;
import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.StoryData;

public interface MarkdownRenderer {
    String render(ClassData classData) throws IOException;

    String render(StoryData storyData) throws IOException;
}
