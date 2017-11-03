package us.cuatoi.jinio.s3;

import com.google.common.hash.Hashing;
import io.minio.ErrorCode;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.auth.AWS4SignerBase;
import us.cuatoi.jinio.s3.auth.AWS4VerifierForAuthorizationHeader;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.message.ErrorResponseWriter;
import us.cuatoi.jinio.s3.operation.bucket.*;
import us.cuatoi.jinio.s3.operation.object.DeleteObjectOperation;
import us.cuatoi.jinio.s3.operation.object.PutObjectOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Jinio handle to detect request type and forward them to the respective operation.
 */
public class JinioHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JinioFilter context;
    private final String method;

    public JinioHandler(JinioFilter jinioFilter, HttpServletRequest request, HttpServletResponse response) {
        context = jinioFilter;
        this.request = request;
        this.response = response;
        method = request.getMethod();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean handle() throws IOException {
        String requestId = UUID.randomUUID().toString();
        String serverId = "jinio";
        Path data = null;
        try {
            new AWS4VerifierForAuthorizationHeader(context, request).verifyHeaders();
            data = readAndVerifyContent();
            if (targetRoot() && isGet()) {
                //GET Buckets
                return new GetBucketsOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            }
            //Bucket operation
            if (targetBucket() && isHead()) {
                //HEAD Bucket
                return new HeadBucketOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else if (targetBucket() && isPut()) {
                //PUT Bucket
                return new PutBucketOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else if (targetBucket() && isGet() && oneParameter("location")) {
                //GET Bucket location
                return new GetBucketLocationOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else if (targetBucket() && isDelete()) {
                //DELETE Bucket
                return new DeleteBucketLocationOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            }
            //Object operation
            if (targetObject() && isPut()) {
                //PUT Object
                return new PutObjectOperation(request.getRequestURI(), data)
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else if (targetObject() && isDelete()) {
                //PUT Object
                return new DeleteObjectOperation(request.getRequestURI())
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            }
            throw new JinioException(ErrorCode.NOT_IMPLEMENTED);
        } catch (Exception e) {
            ErrorCode code = ErrorCode.INTERNAL_ERROR;
            String bucketName = null;
            String objectName = null;
            if (e instanceof JinioException) {
                JinioException je = (JinioException) e;
                code = je.getCode();
                bucketName = je.getBucketName();
                objectName = je.getObjectName();
            }
            logger.warn("Error while serving request ", e);
            return new ErrorResponseWriter(response)
                    .setRequestId(requestId).setServerId(serverId)
                    .setResource(request.getRequestURI())
                    .setBucketName(bucketName)
                    .setObjectName(objectName)
                    .setError(code)
                    .write();
        } finally {
            if (data != null) {
                Files.deleteIfExists(data);
            }
        }
    }

    private boolean targetObject() {
        return countMatches(request.getRequestURI(), '/') > 1;
    }

    private boolean isDelete() {
        return equalsIgnoreCase(method, "delete");
    }

    private boolean oneParameter(String name) {
        boolean parameterSizeCorrect = request.getParameterMap().size() == 1;
        boolean parameterExists = isBlank(name) || request.getParameter(name) != null;
        return parameterSizeCorrect && parameterExists;
    }

    private boolean isPut() {
        return equalsIgnoreCase(method, "put");
    }

    private boolean isHead() {
        return equalsIgnoreCase(method, "head");
    }

    private boolean targetBucket() {
        return !targetRoot() && countMatches(request.getRequestURI(), '/') == 1;
    }

    private boolean targetRoot() {
        return equalsIgnoreCase(request.getRequestURI(), "/");
    }

    private boolean isGet() {
        return equalsIgnoreCase(method, "get");
    }

    private boolean matchRequest(String method, int slashCount) {
        return matchRequest(method, slashCount, 0, null);
    }

    private boolean matchRequest(String method, int slashCount, int parameterCount, String parameterName) {
        boolean methodMatch = equalsIgnoreCase(this.method, method);
        boolean slashMatch = slashCount == 0 || countMatches(request.getRequestURI(), '/') == slashCount;
        boolean parameterCountMatch = parameterCount == 0 || request.getParameterMap().size() == parameterCount;
        boolean parameterMatch = isBlank(parameterName) || request.getParameter(parameterName) != null;
        return methodMatch && slashMatch && parameterCountMatch && parameterMatch;
    }

    private Path readAndVerifyContent() throws IOException {
        Path content = Files.createTempFile("request", ".dat");
        Files.copy(request.getInputStream(), content, StandardCopyOption.REPLACE_EXISTING);
        long contentLength = Files.size(content);

        String providedSha256 = this.request.getHeader("x-amz-content-sha256");
        String computedSha256 = contentLength > 0 ?
                com.google.common.io.Files.asByteSource(content.toFile()).hash(Hashing.sha256()).toString() :
                AWS4SignerBase.EMPTY_BODY_SHA256;
        if (!equalsIgnoreCase(computedSha256, providedSha256)) {
            throw new JinioException(ErrorCode.BAD_DIGEST);
        }
        String providedMd5 = this.request.getHeader("Content-MD5");
        if (contentLength > 0 && isNotBlank(providedMd5)) {
            String computedMd5 = com.google.common.io.Files.asByteSource(content.toFile()).hash(Hashing.md5()).toString();
            if (!equalsIgnoreCase(providedMd5, computedMd5)) {
                throw new JinioException(ErrorCode.INVALID_DIGEST);
            }
        }
        return content;
    }
}
