package chaos.unity.nenggao;

import com.diogonunes.jcolor.Attribute;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SimpleTest {
    @Test
    public void testSimpleErrorRendering() {
        File testFile = new File(getClass().getResource("/test.yk").getFile());
        Source source = Source.fromFile(testFile);
        FileReportBuilder.sourceFile(testFile)
                .relativePath(testFile.getParentFile().toPath())
                .characterSet(CharacterSet.UNICODE)
                .error(Span.multipleLine(1, 0, 3, 0), "Test1")
                .label(Span.singleLine(1, 0, 5), "This is `class` keyword")
                .color(Attribute.MAGENTA_TEXT())
                .build()
                .label(Span.singleLine(1, 6, 11), "This is an identifier")
                .color(Attribute.GREEN_TEXT())
                .build()
                .build()
                .error(Span.singleLine(3, 0, 0), "Test2")
                .label(Span.singleLine(3, 0, 4), "This is `impl` keyword")
                .color(Attribute.RED_TEXT())
                .build()
                .build()
                .print(System.out);
    }
}
