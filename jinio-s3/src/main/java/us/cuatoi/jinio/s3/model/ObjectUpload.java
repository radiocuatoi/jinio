package us.cuatoi.jinio.s3.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUpload {
    private String uploadId;
    private Map<String, String> attributes = new HashMap<>();
    private List<ObjectUploadPart> parts = new ArrayList<>();

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<ObjectUploadPart> getParts() {
        return parts;
    }

    public void setParts(List<ObjectUploadPart> parts) {
        this.parts = parts;
    }
}
