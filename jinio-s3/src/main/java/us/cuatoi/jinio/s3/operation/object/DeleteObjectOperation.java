package us.cuatoi.jinio.s3.operation.object;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteObjectOperation extends ObjectOperation {

    public DeleteObjectOperation(String requestURI) {
        super(requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        verifyObjectExists();
        Path objectPath = context.getDataPath().resolve(bucketName).resolve(objectName);
        Files.deleteIfExists(objectPath);
        Path metadataPath = context.getDataPath().resolve(".metadata").resolve(bucketName).resolve(objectName);
        Files.deleteIfExists(metadataPath);
        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        logger.info("DELETE Object:" + objectPath.toString());
        return false;
    }

}
