package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SourceCache {
    public static final SourceCache INSTANCE = new SourceCache();

    private final @NotNull Map<@NotNull File, @NotNull Source> sources = new HashMap<>();

    public @NotNull Source getOrAdd(@NotNull File sourceFile) {
        return Objects.requireNonNull(sources.computeIfAbsent(sourceFile, file -> Source.fromFile(sourceFile)));
    }
}
