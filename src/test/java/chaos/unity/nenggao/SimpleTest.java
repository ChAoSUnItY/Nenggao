package chaos.unity.nenggao;

import org.junit.jupiter.api.Test;

import java.io.File;

public class SimpleTest {
    @Test
    public void testSimpleErrorRendering() {
        File testFile = new File(getClass().getResource("/test.yk").getFile());
        Source source = Source.fromFile(testFile);
        ReportBuilder.sourceFile(testFile).error(Span.singleLine(1, 1, 1), "Test").print(System.out);
    }
}
