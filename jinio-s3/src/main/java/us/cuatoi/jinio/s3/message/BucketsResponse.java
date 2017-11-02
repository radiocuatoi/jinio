package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

import java.util.LinkedList;
import java.util.List;

public class BucketsResponse extends GenericXmlResponse {
    @Key("Bucket")
    private List<BucketResponse> bucketList = new LinkedList<>();


    public BucketsResponse()  {
        super.name = "Buckets";
    }

    public List<BucketResponse> getBucketList() {
        return bucketList;
    }

    public void setBucketList(List<BucketResponse> bucketList) {
        this.bucketList = bucketList;
    }
}
