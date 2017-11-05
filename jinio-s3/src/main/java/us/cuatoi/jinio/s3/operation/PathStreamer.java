package us.cuatoi.jinio.s3.operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.substring;

public class PathStreamer {
    private String delimiter;
    private String prefix;

    private Stream<Path> stream;
    private List<String> commonPrefixes = new ArrayList<>();

    public PathStreamer() {
    }

    public PathStreamer setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public PathStreamer setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public PathStreamer from(Path p) {
        stream = streamFrom(p);
        return this;
    }

    private Stream<Path> streamFrom(Path p) {
        String path = p.toString();


        if (Files.isRegularFile(p)) {
            return Stream.of(p);
        }

        try {
            return Files.list(p).sorted().flatMap((subPath) -> streamFrom(subPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Stream<Path> getStream() {
        return stream;
    }

    public List<String> getCommonPrefixes() {
        return commonPrefixes;
    }
}
