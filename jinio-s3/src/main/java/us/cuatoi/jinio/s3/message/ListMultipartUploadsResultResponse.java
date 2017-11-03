package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

import java.util.List;

public class ListMultipartUploadsResultResponse extends GenericXmlResponse {
    @Key("Upload")
    List<UploadResponse> uploads;
    @Key("Bucket")
    private String bucketName;
    @Key("KeyMarker")
    private String keyMarker;
    @Key("UploadIdMarker")
    private String uploadIdMarker;
    @Key("NextKeyMarker")
    private String nextKeyMarker;
    @Key("NextUploadIdMarker")
    private String nextUploadIdMarker;
    @Key("MaxUploads")
    private int maxUploads;
    @Key("IsTruncated")
    private boolean isTruncated;

    public ListMultipartUploadsResultResponse() {
        super.name = "ListMultipartUploadsResult";
    }

    public List<UploadResponse> getUploads() {
        return uploads;
    }

    public void setUploads(List<UploadResponse> uploads) {
        this.uploads = uploads;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKeyMarker() {
        return keyMarker;
    }

    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    public String getNextKeyMarker() {
        return nextKeyMarker;
    }

    public void setNextKeyMarker(String nextKeyMarker) {
        this.nextKeyMarker = nextKeyMarker;
    }

    public String getNextUploadIdMarker() {
        return nextUploadIdMarker;
    }

    public void setNextUploadIdMarker(String nextUploadIdMarker) {
        this.nextUploadIdMarker = nextUploadIdMarker;
    }

    public int getMaxUploads() {
        return maxUploads;
    }

    public void setMaxUploads(int maxUploads) {
        this.maxUploads = maxUploads;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean truncated) {
        isTruncated = truncated;
    }
}
