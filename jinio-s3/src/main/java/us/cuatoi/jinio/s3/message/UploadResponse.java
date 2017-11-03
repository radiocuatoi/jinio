package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;
import io.minio.messages.Initiator;
import io.minio.messages.Owner;

public class UploadResponse extends GenericXmlResponse {
    @Key("Key")
    private String objectName;
    @Key("UploadId")
    private String uploadId;
    @Key("Initiator")
    private InitiatorResponse initiator;
    @Key("Owner")
    private OwnerResponse owner;
    @Key("StorageClass")
    private String storageClass;
    @Key("Initiated")
    private String initiated;

    public UploadResponse() {
        super.name = "Upload";
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public InitiatorResponse getInitiator() {
        return initiator;
    }

    public void setInitiator(InitiatorResponse initiator) {
        this.initiator = initiator;
    }

    public OwnerResponse getOwner() {
        return owner;
    }

    public void setOwner(OwnerResponse owner) {
        this.owner = owner;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getInitiated() {
        return initiated;
    }

    public void setInitiated(String initiated) {
        this.initiated = initiated;
    }
}
