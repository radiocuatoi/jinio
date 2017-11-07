package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PutBucketOperation extends BucketOperation {

    public PutBucketOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        Files.createDirectories(bucketPath);
        Files.createDirectories(bucketMetadataPath);
        Files.createDirectories(bucketUploadPath);
        logger.info("PUT Bucket:" + bucketName);
        logger.info("PUT Bucket:Created " + bucketPath.toString());
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }


}
