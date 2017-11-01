package io.minio;

import org.junit.Test;
import us.cuatoi.jinio.s3.auth.AWS4SignerBase;
import us.cuatoi.jinio.s3.util.BinaryUtils;

import static org.junit.Assert.assertEquals;

public class HashTest {
    @Test
    public void testCompareHashing() throws Exception {
        String string = "PUT\n" +
                "/minio-java-test-3jprk42\n" +
                "\n" +
                "host:localhost:8080\n" +
                "x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" +
                "x-amz-date:20171101T055341Z\n" +
                "\n" +
                "host;x-amz-content-sha256;x-amz-date\n" +
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        String hash1 = BinaryUtils.toHex(AWS4SignerBase.hash(string));
        String hash2 = Digest.sha256Hash(string);
        System.out.println(hash1+"="+hash2);
        assertEquals("Should have same hash",hash1,hash2);
    }
}
