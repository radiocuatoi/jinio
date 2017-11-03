package us.cuatoi.jinio.s3.operation.bucket;

import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.operation.Operation;
import us.cuatoi.jinio.s3.operation.Verifier;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BucketOperation extends Operation {

    protected String bucketName;

    public BucketOperation(JinioFilter context, String requestURI) {
        super(context);
        bucketName = StringUtils.replace(requestURI, "/", "");
        Verifier.verifyBucketName(bucketName);
    }

    protected void verifyBucketExists(String bucketName) {
        Path bucketPath = context.getDataPath().resolve(bucketName);
        if (!Files.exists(bucketPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_BUCKET).setBucketName(bucketName);
        }
        if (!Files.isDirectory(bucketPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_BUCKET).setBucketName(bucketName);
        }
    }
}
