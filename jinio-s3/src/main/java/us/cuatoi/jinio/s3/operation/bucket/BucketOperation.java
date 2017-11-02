package us.cuatoi.jinio.s3.operation.bucket;

import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.operation.Operation;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BucketOperation extends Operation {

    protected String getBucketName() {
        String bucketName = StringUtils.replace(request.getRequestURI(), "/", "");
        checkBucketName(bucketName);
        return bucketName;
    }

    protected void checkBucketName(String name) {
        if (name == null) {
            throw new JinioException(ErrorCode.INVALID_BUCKET_NAME);
        }

        // Bucket names cannot be no less than 3 and no more than 63 characters long.
        if (name.length() < 3 || name.length() > 63) {
            String message = "bucket name must be at least 3 and no more than 63 characters long";
            throw new JinioException(ErrorCode.INVALID_BUCKET_NAME, message);
        }
        // Successive periods in bucket names are not allowed.
        if (name.matches("\\.\\.")) {
            String message = "bucket name cannot contain successive periods. For more information refer "
                    + "http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html";
            throw new JinioException(ErrorCode.INVALID_BUCKET_NAME, message);
        }
        // Bucket names should be dns compatible.
        if (!name.matches("^[a-z0-9][a-z0-9\\.\\-]+[a-z0-9]$")) {
            String message = "bucket name does not follow Amazon S3 standards. For more information refer "
                    + "http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html";
            throw new JinioException(ErrorCode.INVALID_BUCKET_NAME, message);
        }
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
