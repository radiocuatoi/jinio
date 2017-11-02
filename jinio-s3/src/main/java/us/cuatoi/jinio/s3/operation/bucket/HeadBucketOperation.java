package us.cuatoi.jinio.s3.operation.bucket;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeadBucketOperation extends BucketOperation {

    @Override
    public boolean execute() throws IOException {
        String bucketName = getBucketName();
        verifyBucketExists(bucketName);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("HEAD Bucket:" + bucketName);
        return true;
    }

}
