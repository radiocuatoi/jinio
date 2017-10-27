package us.cuatoi.jinio.server;

import io.minio.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.cuatoi.jinio.server.message.ErrorResponseWriter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

@Component
public class JinioFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        performFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, chain);
    }

    private void performFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        printDebugInformation(request);
        if (notValidS3Request(request)) {
            chain.doFilter(request, response);
            return;
        }
        String requestId = UUID.randomUUID().toString();
        String serverId = "jinio";

        //verify authorization header
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        if (code != null) {
            new ErrorResponseWriter(response)
                    .setRequestId(requestId).setServerId(serverId)
                    .setResource(request.getRequestURI())
                    .setError(code)
                    .write();
            return;
        }


        new ErrorResponseWriter(response)
                .setRequestId(requestId).setServerId(serverId)
                .setResource(request.getRequestURI())
                .write();
    }

    private boolean notValidS3Request(HttpServletRequest request) {
        String sha256 = request.getHeader("x-amz-content-sha256");
        String authorization = request.getHeader("Authorization");
        return isAnyBlank(sha256, authorization);
    }

    private void printDebugInformation(HttpServletRequest request) {
        logger.info("request.pathInfo=" + request.getPathInfo());
        logger.info("request.requestURI=" + request.getRequestURI());
        logger.info("request.requestURL=" + request.getRequestURL());
        logger.info("request.method=" + request.getMethod());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            logger.info("request.headers." + key + "=" + request.getHeader(key));
        }
    }

    @Override
    public void destroy() {

    }
}
