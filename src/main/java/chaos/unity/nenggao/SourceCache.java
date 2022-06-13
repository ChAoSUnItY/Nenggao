package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SourceCache {
    static final SourceCache INSTANCE = new SourceCache();

    final @NotNull Map<@NotNull File, @NotNull Source> sources = new HashMap<>();

    @SuppressWarnings("")
    public @NotNull Source getOrAdd(@NotNull File sourceFile) {
        return Objects.requireNonNull(sources.computeIfAbsent(sourceFile, file -> Source.fromFile(sourceFile)));
    }
}
