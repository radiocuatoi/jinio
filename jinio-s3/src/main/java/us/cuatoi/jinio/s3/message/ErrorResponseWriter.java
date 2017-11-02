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

    public boolean write() {
        try {
            ErrorResponse er = new ErrorResponse(error, bucketName, objectName, resource, requestId, serverId);
            response.setStatus(getStatus());
            response.setContentType("application/xml; charset=utf-8");
            response.setHeader("x-amz-request-id", requestId);
            response.setHeader("x-amz-version-id", "1.0");
            response.getWriter().write(er.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getStatus() {
        switch (error) {
            case PERMANENT_REDIRECT:
                return 301;
            case REDIRECT:
            case TEMPORARY_REDIRECT:
                return 307;
            case AMBIGUOUS_GRANT_BY_EMAIL_ADDRESS:
            case BAD_DIGEST:
            case CREDENTIALS_NOT_SUPPORTED:
            case ENTITY_TOO_SMALL:
            case ENTITY_TOO_LARGE:
            case EXPIRED_TOKEN:
            case ILLEGAL_VERSIONING_CONFIGURATION_EXCEPTION:
            case INCOMPLETE_BODY:
            case INCORRECT_NUMBER_OF_FILES_IN_POST_REQUEST:
            case INLINE_DATA_TOO_LARGE:
            case INVALID_ARGUMENT:
            case INVALID_BUCKET_NAME:
            case INVALID_DIGEST:
            case INVALID_ENCRYPTION_ALGORITHM_ERROR:
            case INVALID_LOCATION_CONSTRAINT:
            case INVALID_PART:
            case INVALID_PART_ORDER:
            case INVALID_POLICY_DOCUMENT:
            case INVALID_REQUEST:
            case INVALID_SOAP_REQUEST:
            case INVALID_SECURITY:
            case INVALID_STORAGE_CLASS:
            case INVALID_TARGET_BUCKET_FOR_LOGGING:
            case INVALID_TOKEN:
            case INVALID_URI:
            case KEY_TOO_LONG:
            case MALFORMED_ACL_ERROR:
            case MALFORMED_POST_REQUEST:
            case MALFORMED_XML:
            case MAX_MESSAGE_LENGTH_EXCEEDED:
            case MAX_POST_PRE_DATA_LENGTH_EXCEEDED_ERROR:
            case METADATA_TOO_LARGE:
            case MISSING_REQUEST_BODY_ERROR:
            case MISSING_SECURITY_ELEMENT:
            case MISSING_SECURITY_HEADER:
            case NO_LOGGING_STATUS_FOR_KEY:
            case REQUEST_IS_NOT_MULTI_PART_CONTENT:
            case REQUEST_TIMEOUT:
            case REQUEST_TORRENT_OF_BUCKET_ERROR:
            case TOKEN_REFRESH_REQUIRED:
            case TOO_MANY_BUCKETS:
            case UNEXPECTED_CONTENT:
            case UNRESOLVABLE_GRANT_BY_EMAIL_ADDRESS:
            case USER_KEY_MUST_BE_SPECIFIED:
                return 400;
            case ACCESS_DENIED:
            case ACCOUNT_PROBLEM:
            case CROSS_LOCATION_LOGGING_PROHIBITED:
            case INVALID_ACCESS_KEY_ID:
            case INVALID_OBJECT_STATE:
            case INVALID_PAYER:
            case NOT_SIGNED_UP:
            case REQUEST_TIME_TOO_SKEWED:
            case SIGNATURE_DOES_NOT_MATCH:
            case SERVICE_UNAVAILABLE:
            case SLOW_DOWN:
                return 403;
            case NO_SUCH_BUCKET:
            case NO_SUCH_KEY:
            case NO_SUCH_LIFECYCLE_CONFIGURATION:
            case NO_SUCH_UPLOAD:
            case NO_SUCH_VERSION:
            case NO_SUCH_BUCKET_POLICY:
                return 404;
            case METHOD_NOT_ALLOWED:
                return 405;
            case BUCKET_ALREADY_EXISTS:
            case BUCKET_ALREADY_OWNED_BY_YOU:
            case BUCKET_NOT_EMPTY:
            case INVALID_BUCKET_STATE:
            case OPERATION_ABORTED:
            case RESTORE_ALREADY_IN_PROGRESS:
                return 409;
            case MISSING_CONTENT_LENGTH:
                return 411;
            case PRECONDITION_FAILED:
                return 412;
            case INVALID_RANGE:
                return 416;
            case NOT_IMPLEMENTED:
                return 501;
            default:
                return 500;
        }
    }
}
