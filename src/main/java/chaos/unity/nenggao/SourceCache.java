package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public final class SourceCache extends HashMap<@NotNull File, @NotNull Source> {
    public static final SourceCache INSTANCE = new SourceCache();

    public @NotNull Source getOrAdd(@NotNull File sourceFile) {
        return Objects.requireNonNull(computeIfAbsent(sourceFile, file -> Objects.requireNonNull(Source.fromFile(sourceFile))));
    }
}
