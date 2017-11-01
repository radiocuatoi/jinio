package us.cuatoi.jinio.core.exception;

import io.minio.ErrorCode;

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
