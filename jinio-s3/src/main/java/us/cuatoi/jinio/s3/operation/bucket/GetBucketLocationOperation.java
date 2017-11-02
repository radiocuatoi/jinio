package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.message.LocationConstraintResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static us.cuatoi.jinio.s3.JinioFilter.DEFAULT_REGION;

public class GetBucketLocationOperation extends BucketOperation {

    @Override
    public boolean execute() throws IOException {
        String bucketName = getBucketName();
        verifyBucketExists(bucketName);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        LocationConstraintResponse response = new LocationConstraintResponse();
        if (!equalsIgnoreCase(context.getRegion(), DEFAULT_REGION)) {
            response.setRegion(context.getRegion());
        }
        this.response.getWriter().write(response.toString());
        logger.info("GET Bucket location:" + bucketName + "\n" + response.toString());
        return true;
    }

}
