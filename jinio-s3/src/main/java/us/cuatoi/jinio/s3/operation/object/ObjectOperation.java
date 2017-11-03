package us.cuatoi.jinio.s3.operation.object;

import io.minio.ErrorCode;
import io.minio.messages.Bucket;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.operation.Operation;
import us.cuatoi.jinio.s3.operation.bucket.BucketOperation;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ObjectOperation extends BucketOperation {

    protected String bucketName;
    protected String objectName;

    public ObjectOperation(String requestURI) {
        String[] paths = StringUtils.split(requestURI, '/');
        if (paths.length < 2) {
            throw new IllegalArgumentException("Invalid object path");
        }
        bucketName = paths[0];
        objectName = paths[1];
        for (int i = 2; i < paths.length; i++) {
            objectName += "/" + paths[i];
        }
    }

    protected void verifyObjectExists() {
        Path objectPath = context.getDataPath().resolve(bucketName).resolve(objectName);
        if (!Files.exists(objectPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_OBJECT).setBucketName(bucketName).setObjectName(objectName);
        }
    }
}
