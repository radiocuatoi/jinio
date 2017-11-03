package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.message.LocationConstraintResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;
import static us.cuatoi.jinio.s3.JinioFilter.DEFAULT_REGION;

public class GetBucketUploadsOperation extends BucketOperation {
    public GetBucketUploadsOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        //Encoding parameter
        String encodingType = request.getHeader("encoding-type");
        //Filtering parameter
        String delimiter = request.getHeader("delimiter");
        String prefix = request.getHeader("prefix");
        //Paging parameter
        String maxUploads = request.getHeader("max-uploads");
        String keyMarker = request.getHeader("key-marker");
        String uploadIdMarker = request.getHeader("upload-id-marker");


        return true;
    }



}
