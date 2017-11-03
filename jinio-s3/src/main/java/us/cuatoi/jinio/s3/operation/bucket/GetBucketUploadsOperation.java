package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.message.LocationConstraintResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWith;
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

        Stream<Path> prefixedPath = Files.list(bucketMetadataPath)
                .flatMap((p) -> {
                    String flatPath = p.relativize(context.getDataPath()).toString();
                    if (Files.isRegularFile(p)) {
                        if (startsWith(flatPath, prefix)) {
                            return Stream.of(p);
                        } else {
                            return Stream.empty();
                        }
                    }

                    if (startsWith(prefix, flatPath)) {
                        try {
                            return Files.list(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return Stream.empty();
                    }
                });

        return true;
    }

}
