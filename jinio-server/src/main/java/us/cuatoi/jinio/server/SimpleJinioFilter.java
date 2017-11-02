package us.cuatoi.jinio.server;

import org.springframework.stereotype.Component;
import us.cuatoi.jinio.s3.JinioFilter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class SimpleJinioFilter extends JinioFilter {
    public SimpleJinioFilter() {
        setRegion("us-central-1");
    }

    @Override
    public String getSecretKey(String accessKey) {
        return "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG";
    }

    @Override
    public Path getDataPath() {
        return Paths.get("data");
    }
}
