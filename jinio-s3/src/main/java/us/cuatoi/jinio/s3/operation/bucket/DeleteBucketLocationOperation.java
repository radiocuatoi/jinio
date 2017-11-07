package us.cuatoi.jinio.s3.operation.bucket;

import us.cuatoi.jinio.s3.JinioFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteBucketLocationOperation extends BucketOperation {

    public DeleteBucketLocationOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        Path bucketPath = context.getDataPath().resolve(bucketName);
        Files.deleteIfExists(bucketPath);
        Files.deleteIfExists(bucketMetadataPath);
        Files.deleteIfExists(bucketUploadPath);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("DELETE Bucket:" + bucketName);
        logger.info("DELETE Bucket:Deleted " + bucketPath);
        logger.info("DELETE Bucket:Deleted " + bucketMetadataPath);
        logger.info("DELETE Bucket:Deleted " + bucketUploadPath);
        return true;
    }

}
