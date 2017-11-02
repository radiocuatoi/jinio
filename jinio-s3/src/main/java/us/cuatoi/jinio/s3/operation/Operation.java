package us.cuatoi.jinio.s3.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.JinioFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Operation {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected String requestId;
    protected String serverId;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected JinioFilter context;

    public String getRequestId() {
        return requestId;
    }

    public Operation setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public Operation setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public JinioFilter getContext() {
        return context;
    }

    public Operation setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public Operation setResponse(HttpServletResponse response) {
        this.response = response;
        return this;
    }

    public Operation setContext(JinioFilter context) {
        this.context = context;
        return this;
    }

    public abstract boolean execute() throws IOException;

    protected void setCommonHeaders() {
        response.setHeader("x-amz-request-id", requestId);
        response.setHeader("x-amz-id-2x-amz-id-2x-amz-id-2x-amz-id-2", requestId);
        response.setHeader("Server", serverId);
    }
}
