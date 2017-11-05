package us.cuatoi.jinio.s3.operation;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;


public class PathStreamerTest {
    @Test
    public void testStream() throws Exception {
        Path path = Paths.get("");
        System.out.println("---------------------------");
        new PathStreamer().from(path).getStream().forEach((p) -> {
            System.out.println(p);
        });
    }
}
