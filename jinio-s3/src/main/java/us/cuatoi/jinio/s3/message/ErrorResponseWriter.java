package us.cuatoi.jinio.s3.message;

import io.minio.ErrorCode;
import io.minio.messages.ErrorResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * DTO class to return error message
 */
public class ErrorResponseWriter {
    public final HttpServletResponse response;
    private String bucketName = null;
    private String objectName = null;
    private ErrorCode error = ErrorCode.INTERNAL_ERROR;
    private String resource;
    private String requestId;
    private String serverId;

    public ErrorResponseWriter(HttpServletResponse response) {
        this.response = response;
    }

    public ErrorResponseWriter setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public ErrorResponseWriter setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public ErrorResponseWriter setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public ErrorResponseWriter setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public ErrorResponseWriter setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public ErrorResponseWriter setError(ErrorCode error) {
        this.error = error;
        return this;
    }

    public boolean write(){
        try {
            ErrorResponse er = new ErrorResponse(error, bucketName, objectName, resource, requestId, serverId);
            response.setStatus(500);
            response.setContentType("application/xml; charset=utf-8");
            response.setHeader("x-amz-request-id", requestId);
            response.setHeader("x-amz-version-id", "1.0");
            response.getWriter().write(er.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
