package us.cuatoi.jinio.s3;

import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.auth.AWS4Authorization;
import us.cuatoi.jinio.s3.auth.AWS4SignerBase;
import us.cuatoi.jinio.s3.auth.AWS4SignerForAuthorizationHeader;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.message.ErrorResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;


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
            URL url = new URL(request.getRequestURL().toString());
            String authorizationHeader = request.getHeader("Authorization");
            AWS4Authorization authorization = new AWS4Authorization(authorizationHeader);
            String bodyHash = request.getHeader("x-amz-content-sha256");
            String method = request.getMethod();
            String serviceName = authorization.getServiceName();
            String regionName = authorization.getRegionName();
            AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(url, method, serviceName, regionName);
            String awsAccessKey = authorization.getAwsAccessKey();
            String awsSecretKey = context.getSecretKey(awsAccessKey);
            HashMap<String, String> headers = new HashMap<>();
            for (String header : authorization.getSignedHeaders()) {
                if (!equalsAnyIgnoreCase(header, "host")) {
                    headers.put(header, request.getHeader(header));
                }
            }
            HashMap<String, String> queryParams = null;
            if ("get".equalsIgnoreCase(method)) {
                HashMap<String, String> map = new HashMap<>();
                request.getParameterMap().forEach((k, v) -> {
                    map.put(k, v[0]);
                });
                queryParams = map;
            }
            String amzDateHeader = request.getHeader("x-amz-date");
            long dateHeader = request.getDateHeader("Date");
            Date date = isBlank(amzDateHeader) ? new Date(dateHeader) :
                    AWS4Authorization.utcDateFormat(AWS4SignerBase.ISO8601BasicFormat).parse(amzDateHeader);
            String computedHeader = signer.computeSignature(headers, queryParams, bodyHash, awsAccessKey, awsSecretKey, date);
            logger.debug("headers=" + headers);
            logger.debug("parameters=" + queryParams);
            logger.debug("bodyHash=" + bodyHash);
            logger.debug("amzDateHeader=" + amzDateHeader);
            logger.debug("dateHeader=" + dateHeader);
            logger.debug("date=" + date);
            logger.debug("url=" + url);
            logger.debug("url.getHost()=" + url.getHost());
            logger.debug("url.getPort()=" + url.getPort());
            logger.debug("a=" + authorizationHeader);
            logger.debug("c=" + computedHeader);
            if (!StringUtils.equals(authorizationHeader, computedHeader)) {
                throw new JinioException(ErrorCode.INVALID_ACCESS_KEY_ID);
            }

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
