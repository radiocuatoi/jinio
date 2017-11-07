package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.message.LocationConstraintResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static us.cuatoi.jinio.s3.JinioFilter.DEFAULT_REGION;

public class GetBucketLocationOperation extends BucketOperation {

    public GetBucketLocationOperation(JinioFilter context, String requestURI) {
        super(context,requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);

        LocationConstraintResponse lcr = new LocationConstraintResponse();
        if (!equalsIgnoreCase(context.getRegion(), DEFAULT_REGION)) {
            lcr.setRegion(context.getRegion());
        }
        String content = lcr.toString();

        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(content);
        response.setContentType(JinioConfiguration.CONTENT_TYPE);
        logger.info("GET Bucket location:" + bucketName);
        logger.info("GET Bucket location:" + content);
        return true;
    }

}
