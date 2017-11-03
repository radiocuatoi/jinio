package us.cuatoi.jinio.s3.exception;

import io.minio.ErrorCode;

/**
 * Wrapper for all Jinio exception. Which is based on Minio java client
 */
public class JinioException extends RuntimeException {
    private final ErrorCode code;
    private String bucketName;
    private String objectName;

    public JinioException() {
        this(ErrorCode.INTERNAL_ERROR);
    }

    public JinioException(ErrorCode code) {
        this(code,code.message());
    }

    public JinioException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }


    public ErrorCode getCode() {
        return code;
    }

    public JinioException setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public JinioException setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }
}
