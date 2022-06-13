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
                .warning(Span.multipleLine(1, 0, 6, 0), "This is yakou lang source code")
                .label(Span.multipleLine(1, 0, 3, 8), "They link together at compile time").color(Attribute.RED_TEXT()).build()
                .label(Span.multipleLine(2, 0,5, 0), "IDK why I mark this section lol").color(Attribute.BRIGHT_MAGENTA_TEXT()).build()
                .label(Span.singleLine(1, 6, 11), "The name of class").color(Attribute.BRIGHT_CYAN_TEXT()).build()
                .label(Span.singleLine(3, 0, 4), "Implementation of class `Yakou`").color(Attribute.BRIGHT_CYAN_TEXT()).build()
                .label(Span.multipleLine(4, 0, 6, 0), "Just some redundant empty lines").build()
                .build()
                .print(System.out);
    }
}
