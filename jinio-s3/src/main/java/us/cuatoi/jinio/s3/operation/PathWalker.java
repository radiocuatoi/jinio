package us.cuatoi.jinio.s3.operation;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * We need a way to speed up the process. We can't scan millions of file and folder in order to do this.
 * A database will speedup this process quickly but it will need time to get all the value.
 */
public class PathWalker {
    private final Path basePath;
    //input
    private String delimiter;
    private String prefix;
    private int max = 1000;
    private String marker;

    //output
    private List<Path> paths = new ArrayList<>();
    private Set<String> commonPrefixes = new HashSet<>();
    private boolean truncated;
    private String nextMarker;

    public PathWalker(Path basePath) {
        this.basePath = basePath;
    }

    public PathWalker setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public PathWalker setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public PathWalker setMax(int max) {
        this.max = max;
        return this;
    }

    public PathWalker setMarker(String marker) {
        this.marker = marker;
        return this;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public Set<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public PathWalker walk(Path p) {
        if (Files.isRegularFile(p)) {
            walkFile(p);
        } else {
            walkFolder(p);
        }
        return this;
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getMax() {
        return max;
    }

    public String getMarker() {
        return marker;
    }

    private void walkFolder(Path p) {
        String path = getRelativePath(p);
        if (isNotBlank(prefix) && startsWith(prefix, path)) {
            walkChild(p);
            return;
        }
        if (isNotBlank(prefix) && !startsWith(path, prefix)) {
            return;
        }

        if (shouldAddCommonPrefix(path)) {
            return;
        }
        walkChild(p);
    }

    private String getRelativePath(Path p) {
        return basePath.toAbsolutePath().relativize(p.toAbsolutePath()).toString();
    }

    private boolean shouldAddCommonPrefix(String path) {
        if (isNotBlank(delimiter)) {
            int prefixLength = length(prefix);
            int indexOfDelimiterAfterPrefix = indexOf(path, delimiter, prefixLength);
            if (indexOfDelimiterAfterPrefix >= 0) {
                commonPrefixes.add(substring(path, 0, indexOfDelimiterAfterPrefix + length(delimiter)));
                return true;
            }
        }
        return false;
    }

    private void walkChild(Path p) {
        try {
            Files.list(p).sorted().forEach((sp) -> walk(sp));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void walkFile(Path p) {
        String path = getRelativePath(p);
        if (isNotBlank(prefix) && !startsWith(path, prefix)) {
            return;
        }

        if (shouldAddCommonPrefix(path)) {
            return;
        }

        if (isNotBlank(marker) && compare(path, marker) <= 0) {
            return;
        }
        if (paths.size() < max) {
            paths.add(p);
        } else if (isBlank(nextMarker)) {
            nextMarker = path;
            truncated = true;
        }

    }
}
