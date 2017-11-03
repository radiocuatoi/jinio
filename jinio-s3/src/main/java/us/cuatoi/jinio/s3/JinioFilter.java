package us.cuatoi.jinio.s3;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

/**
 * A filter to handle S3 request.
 * It will pass the request back to the chain if it can not be handled by Jinio
 */
public abstract class JinioFilter implements Filter {
    public static final String DEFAULT_REGION = "us-east-1";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String region = DEFAULT_REGION;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        printDebugInformation(request);
        if (isS3Request(request)) {
            new JinioHandler(this, request, response).handle();
        } else {
            chain.doFilter(request, servletResponse);
        }
    }


    private void printDebugInformation(HttpServletRequest request) {
        logger.info("");
        logger.info("-----------------DEBUG-----------------------");
        logger.info(request.getMethod() + " " + request.getRequestURI());
        logger.info("request.pathInfo=" + request.getPathInfo());
        logger.info("request.servletPath=" + request.getServletPath());
        logger.info("request.requestURL=" + request.getRequestURL());
        request.getParameterMap().forEach((key, value) -> {
            logger.info("request.parameters." + key + "=" + StringUtils.join(value, ','));

        });
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            logger.info("request.headers." + key + "=" + request.getHeader(key));
        }
        logger.info("-----------END DEBUG-------------------------");
        logger.info("");
    }

    private boolean isS3Request(HttpServletRequest request) {
        String sha256 = request.getHeader("x-amz-content-sha256");
        String authorization = request.getHeader("Authorization");
        return !isAnyBlank(sha256, authorization);
    }

    @Override
    public void destroy() {

    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public abstract String getSecretKey(String accessKey);

    public abstract Path getDataPath();
}
