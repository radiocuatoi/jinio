package us.cuatoi.jinio.s3.operation;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;


public class PathStreamerTest {
    @Test
    public void testStream() throws Exception {
        Path path = Paths.get("..").toAbsolutePath().normalize();
        System.out.println("---------------------------");
        new PathStreamer(path).stream().getStream().forEach((p) -> {
            System.out.println(p);
        });
        System.out.println("---------------------------");
        new PathStreamer(path).setPrefix("jinio-s3").stream().getStream().forEach((p) -> {
            System.out.println(p);
        });
        System.out.println("---------------------------");
        new PathStreamer(path).setDelimiter(".").stream().getStream().forEach((p) -> {
            System.out.println(p);
        });
    }
}
