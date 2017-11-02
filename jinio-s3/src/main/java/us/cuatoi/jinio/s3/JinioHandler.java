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
import us.cuatoi.jinio.s3.operation.bucket.CreateBucketOperation;
import us.cuatoi.jinio.s3.operation.bucket.GetBucketLocationOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
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
    public boolean handle() throws MalformedURLException {
        String requestId = UUID.randomUUID().toString();
        String serverId = "jinio";
        try {
            new AWS4VerifierForAuthorizationHeader(context, request).verifyHeaders();
            byte[] data = readAndVerifyContent();

            String requestURI = request.getRequestURI();
            if (equalsAnyIgnoreCase(method, "put") && countMatches(requestURI, '/') == 1) {
                //PUT Bucket
                return new CreateBucketOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else if (equalsAnyIgnoreCase(method, "get")
                    && countMatches(requestURI, '/') == 1
                    && request.getParameter("location") != null) {
                //GET Bucket location
                return new GetBucketLocationOperation()
                        .setRequest(request).setResponse(response).setContext(context)
                        .setRequestId(requestId).setServerId(serverId)
                        .execute();
            } else {
                throw new JinioException(ErrorCode.NOT_IMPLEMENTED);
            }
        } catch (Exception e) {
            ErrorCode code = ErrorCode.INTERNAL_ERROR;
            String bucketName =null;
            if (e instanceof JinioException) {
                JinioException je = (JinioException) e;
                code = je.getCode();
                bucketName = je.getBucketName();
            }
            logger.warn("Error while serving request ", e);
            return new ErrorResponseWriter(response)
                    .setRequestId(requestId).setServerId(serverId)
                    .setResource(request.getRequestURI())
                    .setBucketName(bucketName)
                    .setError(code)
                    .write();
        }
    }

    private byte[] readAndVerifyContent() throws IOException {
        byte[] data = IOUtils.toByteArray(request.getInputStream());
        String providedSha256 = request.getHeader("x-amz-content-sha256");
        String computedSha256 = data.length > 0 ? Hashing.sha256().hashBytes(data).toString() : AWS4SignerBase.EMPTY_BODY_SHA256;
        if (!equalsIgnoreCase(computedSha256, providedSha256)) {
            throw new JinioException(ErrorCode.BAD_DIGEST);
        }
        String providedMd5 = request.getHeader("Content-MD5");
        if (data.length > 0 && isNotBlank(providedMd5)) {
            String computedMd5 = Hashing.md5().hashBytes(data).toString();
            if (!equalsIgnoreCase(providedMd5, computedMd5)) {
                throw new JinioException(ErrorCode.INVALID_DIGEST);
            }
        }
        return data;
    }
}
