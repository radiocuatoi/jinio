package us.cuatoi.jinio.s3.auth;

import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class AWS4VerifierForAuthorizationHeader {

    public static final int FIFTEEN_MINUTES = 15 * 60 * 1000;
    private final JinioFilter context;
    private final HttpServletRequest request;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AWS4VerifierForAuthorizationHeader(JinioFilter context, HttpServletRequest request) {
        this.context = context;
        this.request = request;
    }

    public void verifyHeaders() throws JinioException {
        try {
            URL url = new URL(request.getRequestURL().toString());
            String authorizationHeader = request.getHeader("Authorization");
            AWS4Authorization authorization = new AWS4Authorization(authorizationHeader);
            String bodyHash = request.getHeader("x-amz-content-sha256");
            if (isBlank(bodyHash)) {
                throw new JinioException(ErrorCode.BAD_DIGEST);
            }
            String method = request.getMethod();
            String serviceName = authorization.getServiceName();
            String regionName = authorization.getRegionName();
            AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(url, method, serviceName, regionName);
            String awsAccessKey = authorization.getAwsAccessKey();
            String awsSecretKey = context.getSecretKey(awsAccessKey);

            if (isBlank(awsSecretKey)) {
                throw new JinioException(ErrorCode.INVALID_ACCESS_KEY_ID);
            }
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
            if (Math.abs(date.getTime() - new Date().getTime()) > FIFTEEN_MINUTES) {
                throw new JinioException(ErrorCode.REQUEST_TIME_TOO_SKEWED);
            }
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
                throw new JinioException(ErrorCode.SIGNATURE_DOES_NOT_MATCH);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new JinioException(ErrorCode.AUTHORIZATION_HEADER_MALFORMED);
        }
    }
}
