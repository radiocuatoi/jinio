package us.cuatoi.jinio.s3.auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AWS4AuthorizationTest {
    @Test
    public void testParseHeader() throws Exception {
        String header = "AWS4-HMAC-SHA256 Credential=Q3AM3UQ867SPQQA43P2F/20171101/us-east-1/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=aa16cad8da33353b2312f92f7d24cc5bcf08595a822f673f9d4b4b67951b0599";
        AWS4Authorization authorization = new AWS4Authorization(header);
        assertEquals("Must be able to generate the same header", header, authorization.toString());
    }
}
