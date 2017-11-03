package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.message.LocationConstraintResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static us.cuatoi.jinio.s3.JinioFilter.DEFAULT_REGION;

public class GetBucketUploadsOperation extends BucketOperation {

    public GetBucketUploadsOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {

        return true;
    }

}
