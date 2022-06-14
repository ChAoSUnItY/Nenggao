package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Source {
    public final @NotNull List<@NotNull Line> lines;

    public Source(@NotNull List<@NotNull Line> lines) {
        this.lines = lines;
    }

    /**
     *
     * @param startLine starts from 1, pass 0 would throw exception
     * @param endLine
     * @return lines segment
     * @throws IllegalArgumentException
     */
    public @NotNull List<@NotNull Line> slice(int startLine, int endLine) throws IllegalArgumentException {
        if (startLine <= 0) {
            throw new IllegalArgumentException("Unable to retrieve lines segment from source, start line index must larger than 0.");
        }

        return lines.subList(startLine - 1 , endLine);
    }

    public static @Nullable Source fromFile(File file) {
        Source source;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            source = fromStrings(lines);
        } catch (IOException e) {
            e.printStackTrace();
            source = null;
        }

        return source;
    }

    public static @NotNull Source fromString(@NotNull String line) {
        return new Source(Arrays.asList(new Line(1, 0, line.length(), line)));
    }

    public static @NotNull Source fromStrings(@NotNull List<@NotNull String> lines) {
        AtomicInteger offset = new AtomicInteger();

        return new Source(IntStream.range(0, lines.size()).mapToObj(i -> {
            Line l = new Line(i + 1, offset.get(), lines.get(i).length() + 1, lines.get(i) + "\n");
            offset.addAndGet(l.len);
            return l;
        }).collect(Collectors.toList()));
    }
}
