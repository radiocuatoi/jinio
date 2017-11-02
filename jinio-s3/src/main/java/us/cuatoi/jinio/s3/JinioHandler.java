package us.cuatoi.jinio.s3;

import io.minio.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.auth.AWS4VerifierForAuthorizationHeader;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.message.ErrorResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Jinio handle to detect request type and forward them to the respective handler.
 */
public class JinioHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JinioFilter context;

    public JinioHandler(JinioFilter jinioFilter, HttpServletRequest request, HttpServletResponse response) {
        context = jinioFilter;
        this.request = request;
        this.response = response;
    }

    public boolean handle() throws MalformedURLException {
        String requestId = UUID.randomUUID().toString();
        String serverId = "jinio";
        try {
            new AWS4VerifierForAuthorizationHeader(context,request).verifyHeaders();

        } catch (Exception e) {
            ErrorCode code = ErrorCode.INTERNAL_ERROR;
            if (e instanceof JinioException) code = ((JinioException) e).getCode();
            logger.warn("Error while serving request ", e);
            return new ErrorResponseWriter(response)
                    .setRequestId(requestId).setServerId(serverId)
                    .setResource(request.getRequestURI())
                    .setError(code)
                    .write();
        }
        throw new UnsupportedOperationException();
    }
}
