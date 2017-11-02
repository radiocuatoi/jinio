package us.cuatoi.jinio.s3.operation.bucket;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PutBucketOperation extends BucketOperation {


    @Override
    public boolean execute() throws IOException {
        String bucketName = getBucketName();
        Path bucketPath = context.getDataPath().resolve(bucketName);
        Files.createDirectories(bucketPath);
        logger.info("PUT Bucket:" + bucketName);
        logger.info("PUT Bucket:Created " + bucketPath.toString());
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }


}
