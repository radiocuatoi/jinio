package us.cuatoi.jinio.s3.exception;

import io.minio.ErrorCode;

/**
 * Wrapper for all Jinio exception. Which is based on Minio java client
 */
public class JinioException extends RuntimeException {
    private final ErrorCode code;

    public JinioException() {
        this(ErrorCode.INTERNAL_ERROR);
    }

    public JinioException(ErrorCode code) {
        super(code.message());
        this.code = code;
    }


    public ErrorCode getCode() {
        return code;
    }
}
