package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeadBucketOperation extends BucketOperation {

    public HeadBucketOperation(JinioFilter context, String requestURI) {
        super(context,requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("HEAD Bucket:" + bucketName);
        return true;
    }

}
