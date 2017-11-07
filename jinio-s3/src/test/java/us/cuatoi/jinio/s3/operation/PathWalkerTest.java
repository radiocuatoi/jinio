package us.cuatoi.jinio.s3.operation;

import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PathWalkerTest {
    private String separator = FileSystems.getDefault().getSeparator();

    @Test
    public void testWalkWithoutParameter() throws Exception {
        PathWalker walker = new PathWalker().walk(Paths.get(""));
        verify(walker);
    }

    @Test
    public void testWalkWithPrefix() throws Exception {
        PathWalker walker = new PathWalker().setPrefix("src").walk(Paths.get(""));
        verify(walker);
    }

    @Test
    public void testWalkWithDelimiter() throws Exception {

        PathWalker walker = new PathWalker().setDelimiter(separator).walk(Paths.get(""));
        verify(walker);
    }

    @Test
    public void testWalkWithPrefixAndDelimiter() throws Exception {
        String prefix = Paths.get("src", "main", "java", "us", "cuatoi", "jinio", "s3").toString() + separator;
        PathWalker walker = new PathWalker().setPrefix(prefix).setDelimiter(separator).walk(Paths.get(""));
        verify(walker);
    }

    @Test
    public void testTruncatedWalkWithPrefixAndDelimiter() throws Exception {
        String prefix = Paths.get("src", "main", "java", "us", "cuatoi", "jinio", "s3").toString() + separator;
        PathWalker walker = new PathWalker().setPrefix(prefix).setDelimiter(separator)
                .setMax(2)
                .walk(Paths.get(""));
        verify(walker);
    }

    @Test
    public void testTruncatedWalkWithPrefixAndDelimiterFromMarker() throws Exception {
        String prefix = Paths.get("src", "main", "java", "us", "cuatoi", "jinio", "s3").toString() + separator;
        String marker = Paths.get("src", "main", "java", "us", "cuatoi", "jinio", "s3", "JinioConfiguration.java").toString();
        PathWalker walker = new PathWalker().setPrefix(prefix).setDelimiter(separator)
                .setMarker(marker)
                .setMax(2)
                .walk(Paths.get(""));
        verify(walker);
    }

    private void verify(PathWalker walker) {
        System.out.println("----PATH---------");
        walker.getPaths().forEach(System.out::println);
        System.out.println("----PREFIXES-----");
        walker.getCommonPrefixes().forEach(System.out::println);
        System.out.println("-TRUNCATED=" + walker.isTruncated());
        System.out.println("-----------------");

        assertTrue("Paths must not null", walker.getPaths() != null);
        assertTrue("Prefixes must not null", walker.getCommonPrefixes() != null);
        assertTrue("Must not return more values than max", walker.getPaths().size() <= walker.getMax());
        if (isNotBlank(walker.getPrefix())) {
            String prefix = walker.getPrefix();
            for (Path path : walker.getPaths()) {
                assertTrue("Must have prefix", startsWith(path.toString(), prefix));
            }
        }
        if (isNotBlank(walker.getDelimiter())) {
            String delimiter = walker.getDelimiter();
            for (Path path : walker.getPaths()) {
                int prefixLength = length(walker.getPrefix());
                assertTrue("Must not contains delimiter", indexOf(path.toString(), delimiter, prefixLength) < 0);
            }
        }
        if (isNotBlank(walker.getMarker())) {
            String marker = walker.getMarker();
            for (Path path : walker.getPaths()) {
                assertTrue("Must return path greater than marker.", compare(path.toString(), marker) > 0);
            }
        }
    }


}
