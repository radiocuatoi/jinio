package us.cuatoi.jinio.server;

import org.springframework.stereotype.Component;
import us.cuatoi.jinio.s3.JinioFilter;

@Component
public class SpringJinioFilter extends JinioFilter {
    @Override
    public String getSecretKey(String accessKey) {
        return "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG";
    }
}
