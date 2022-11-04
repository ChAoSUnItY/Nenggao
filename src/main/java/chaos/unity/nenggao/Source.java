package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Source extends ArrayList<@NotNull Line> {
    public Source(@NotNull List<@NotNull Line> lines) {
        this.addAll(lines);
    }

    @Override
    public List<@NotNull Line> subList(int fromIndex, int toIndex) {
        if (fromIndex == 0) {
            throw new IllegalArgumentException("Line starts from 1, cannot subList from index 0.");
        }

        return super.subList(fromIndex - 1, toIndex);
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
        return new Source(Collections.singletonList(new Line(1, 0, line.length(), line)));
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
