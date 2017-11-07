package us.cuatoi.jinio.s3.operation.object;

import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static us.cuatoi.jinio.s3.JinioConfiguration.JINIO;

public class DeleteObjectOperation extends ObjectOperation {

    public DeleteObjectOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        verifyObjectExists();
        Files.deleteIfExists(objectPath);
        Files.deleteIfExists(objectMetadataPath);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        logger.info("DELETE Object:" + objectPath.toString());
        return true;
    }

}
