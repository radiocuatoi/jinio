package us.cuatoi.jinio.s3.operation.object;

import com.google.common.hash.Hashing;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.model.ObjectMetadata;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PutObjectOperation extends ObjectOperation {

    private final Path data;

    public PutObjectOperation(JinioFilter context, String requestURI, Path data) {
        super(context,requestURI);
        this.data = data;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        //Write file content. Need to handle part upload later.
        Files.copy(data, objectPath);

        ObjectMetadata metadata = getSavedMetadata();
        String ETag = com.google.common.io.Files.asByteSource(data.toFile()).hash(Hashing.md5()).toString();
        metadata.setETag(ETag);
        metadata.setContentType(request.getContentType());
        //May need to check this on multi part case
        metadata.setContentLength(Files.size(data));
        recordHeaders(metadata.getAttributes());
        saveMetadata(metadata);

        //write response
        setCommonHeaders();
        response.setHeader("ETag", ETag);
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("PUT Object:" + objectPath);
        logger.info("PUT Object:" + metadata.toString());
        return true;
    }

}
