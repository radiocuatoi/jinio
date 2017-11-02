package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

public class ListAllMyBucketsResponse extends GenericXmlResponse {
    @Key("Owner")
    private OwnerResponse owner;
    @Key("Buckets")
    private BucketsResponse buckets;

    public ListAllMyBucketsResponse() {
        this.name = "ListAllMyBucketsResult";
    }

    public OwnerResponse getOwner() {
        return owner;
    }

    public void setOwner(OwnerResponse owner) {
        this.owner = owner;
    }

    public BucketsResponse getBuckets() {
        return buckets;
    }

    public void setBuckets(BucketsResponse buckets) {
        this.buckets = buckets;
    }
}
