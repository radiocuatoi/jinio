package us.cuatoi.jinio.s3.operation.object;

import io.minio.ErrorCode;
import io.minio.messages.Bucket;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.operation.Operation;
import us.cuatoi.jinio.s3.operation.bucket.BucketOperation;

import java.nio.file.Files;
import java.nio.file.Path;

import static us.cuatoi.jinio.s3.JinioConfiguration.JINIO;

public abstract class ObjectOperation extends BucketOperation {

    protected String objectName;
    protected Path objectPath;
    protected Path objectMetadataPath;

    public ObjectOperation(JinioFilter context, String requestURI) {
        super(context, StringUtils.substring(requestURI, 0, StringUtils.indexOf(requestURI, '/', 1)));
        String[] paths = StringUtils.split(requestURI, '/');
        if (paths.length < 2) {
            throw new IllegalArgumentException("Invalid object path");
        }
        bucketName = paths[0];
        objectName = paths[1];
        for (int i = 2; i < paths.length; i++) {
            objectName += "/" + paths[i];
        }
        objectPath = context.getDataPath().resolve(bucketName).resolve(objectName);
        objectMetadataPath = bucketMetadataPath.resolve(objectName + ".json");
    }

    protected void verifyObjectExists() {
        if (!Files.exists(objectPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_OBJECT).setBucketName(bucketName).setObjectName(objectName);
        }
    }
}
