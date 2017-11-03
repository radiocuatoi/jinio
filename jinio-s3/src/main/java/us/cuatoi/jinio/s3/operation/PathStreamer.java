package us.cuatoi.jinio.s3.operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.substring;

public class PathStreamer {
    private final Path bucketPath;
    private String delimiter;
    private String prefix;
    private Set<String> commonPrefixes = new HashSet<>();
    private Stream<Path> stream = Stream.empty();
    private boolean streamed = false;

    public PathStreamer(Path path) {
        this.bucketPath = path;
    }

    public PathStreamer setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public PathStreamer setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public PathStreamer stream() {
        if (isBlank(delimiter) && isBlank(prefix)) {
            //easiest condition
            stream = list(bucketPath).flatMap((p) -> Files.isRegularFile(p) ? Stream.of(p) : list(p));
        } else if (isBlank(delimiter) && isNotBlank(prefix)) {
            stream = list(bucketPath).flatMap((p) -> {
                String name = p.toString();
                if (Files.isRegularFile(p)) {
                    return startsWith(name, prefix) ? Stream.of(p) : Stream.empty();
                } else {
                    return startsWith(prefix, name) ? list(p) : Stream.empty();
                }
            });
        } else if (isNotBlank(delimiter) && isBlank(prefix)) {
            stream = list(bucketPath).flatMap((p) -> {
                String flatName = p.toString();
                if (!contains(flatName, delimiter)) {
                    return Files.isRegularFile(p) ? Stream.of(p) : list(p);
                } else {
                    String commonPrefix = substring(flatName, 0, indexOf(flatName, delimiter) + 1);
                    commonPrefixes.add(commonPrefix);
                    return Stream.empty();
                }
            });
        } else {
            stream = list(bucketPath);
        }
        streamed = true;
        return this;
    }

    public Set<String> getCommonPrefixes() {
        if (!streamed) {
            throw new IllegalStateException("Must run stream first");
        }
        return commonPrefixes;
    }

    public Stream<Path> getStream() {
        if (!streamed) {
            throw new IllegalStateException("Must run stream first");
        }
        return stream;
    }

    private Stream<Path> list(Path p) {
        try {
            return Files.list(p).sorted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
