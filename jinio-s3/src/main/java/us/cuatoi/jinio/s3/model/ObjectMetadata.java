package us.cuatoi.jinio.s3.model;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class ObjectMetadata {
    private String ETag;
    private String contentType;
    private long contentLength;
    private Map<String, String> attributes = new HashMap<>();

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
