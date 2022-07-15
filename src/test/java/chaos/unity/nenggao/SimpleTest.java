package chaos.unity.nenggao;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SimpleTest {
    @Test
    public void testSimpleErrorRendering() {
        File testFile = new File(getClass().getResource("/test.yk").getFile());
        FileReportBuilder.sourceFile(testFile)
                .relativePath(testFile.getParentFile().toPath())
                .characterSet(CharacterSet.UNICODE)
                .warning(Span.multipleLine(1, 0, 6, 0), "Test1 %s", Ansi.colorize("Colored text", Attribute.YELLOW_TEXT()))
                .tag("E01")
                .label(Span.multipleLine(1, 0, 3, 8), "LOL").color(Attribute.RED_TEXT()).build()
                .label(Span.multipleLine(2, 0, 5, 0), "KEK").color(Attribute.BRIGHT_MAGENTA_TEXT()).build()
                .label(Span.singleLine(1, 6, 11), "L").color(Attribute.BRIGHT_CYAN_TEXT()).build()
                .label(Span.singleLine(3, 0, 4), "impl dude").color(Attribute.BRIGHT_CYAN_TEXT()).build()
                .label(Span.multipleLine(4, 0, 6, 0), "kek").build()
                .build()
                .print(System.out);
    }

    @Test
    public void testLongLineErrorRendering() {
        File testFile = new File(getClass().getResource("/long_line.yk").getFile());
        FileReportBuilder.sourceFile(testFile)
                .warning(Span.multipleLine(1, 0, 10, 0), "Warning No.1")
                .label(Span.singleLine(1, 0, 1), "Label No.1").color(Attribute.RED_TEXT()).build()
                .label(Span.singleLine(10, 0, 1), "Label No.2").color(Attribute.YELLOW_TEXT()).hint("Hint!").build()
                .build()
                .warning(Span.multipleLine(1, 0, 10, 0), "Warning No.2")
                .label(Span.multipleLine(1, 0, 10, 1), "Label").color(Attribute.RED_TEXT()).hint("Hint!").build()
                .build()
                .print(System.out);
    }
}
