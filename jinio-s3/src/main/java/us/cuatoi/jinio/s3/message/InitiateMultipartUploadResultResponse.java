package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

public class InitiateMultipartUploadResultResponse extends GenericXmlResponse{
    @Key("Bucket")
    private String bucket;
    @Key("Key")
    private String key;
    @Key("UploadId")
    private String uploadId;

    public InitiateMultipartUploadResultResponse() {
        super.name = "InitiateMultipartUploadResult";
        super.namespaceDictionary.set("","http://s3.amazonaws.com/doc/2006-03-01/");
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
