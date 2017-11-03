package us.cuatoi.jinio.s3.operation.bucket;

import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.operation.Operation;
import us.cuatoi.jinio.s3.operation.Verifier;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BucketOperation extends Operation {

    protected String bucketName;
    protected Path bucketPath;
    protected Path bucketMetadataPath;

    public BucketOperation(JinioFilter context, String requestURI) {
        super(context);
        bucketName = StringUtils.replace(requestURI, "/", "");
        bucketPath = context.getDataPath().resolve(bucketName);
        bucketMetadataPath = context.getDataPath().resolve(JinioConfiguration.JINIO).resolve(bucketName);
        Verifier.verifyBucketName(bucketName);
    }

    protected void verifyBucketExists(String bucketName) {
        if (!Files.exists(bucketPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_BUCKET).setBucketName(bucketName);
        }
        if (!Files.isDirectory(bucketPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_BUCKET).setBucketName(bucketName);
        }
    }
}
