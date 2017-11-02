package us.cuatoi.jinio.s3;

import io.minio.MinioClient;
import io.minio.messages.Bucket;

import java.util.List;

public class MinioPlay {
    public static void main(String[] args) throws Exception{
        MinioClient mc = new MinioClient("https://play.minio.io:9000",
                "Q3AM3UQ867SPQQA43P2F",
                "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3T",
                "us-west-1");
        List<Bucket> buckets = mc.listBuckets();
    }
}
