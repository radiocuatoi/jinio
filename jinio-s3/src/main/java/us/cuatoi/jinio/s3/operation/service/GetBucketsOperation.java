package us.cuatoi.jinio.s3.operation.service;


import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.message.BucketResponse;
import us.cuatoi.jinio.s3.message.BucketsResponse;
import us.cuatoi.jinio.s3.message.ListAllMyBucketsResponse;
import us.cuatoi.jinio.s3.message.OwnerResponse;
import us.cuatoi.jinio.s3.operation.Operation;
import us.cuatoi.jinio.s3.operation.Verifier;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import static io.minio.DateFormat.EXPIRATION_DATE_FORMAT;

public class GetBucketsOperation extends Operation {

    public GetBucketsOperation(JinioFilter context) {
        super(context);
    }

    @Override
    public boolean execute() throws IOException {
        OwnerResponse o = new OwnerResponse();
        o.setId("jinio");
        o.setDisplayName("Jinio");
        BucketsResponse b = new BucketsResponse();
        Files.list(context.getDataPath()).forEach((p) -> {
            try {
                Verifier.verifyBucketName(p.getFileName().toString());
                BasicFileAttributes attribute = Files.readAttributes(p, BasicFileAttributes.class);
                BucketResponse br = new BucketResponse();
                br.setName(p.getFileName().toString());
                br.setCreationDate(EXPIRATION_DATE_FORMAT.print(attribute.creationTime().toMillis()));
                b.getBucketList().add(br);
            } catch (JinioException ex) {
                logger.debug("Ignored:" + p.getFileName().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ListAllMyBucketsResponse r = new ListAllMyBucketsResponse();
        r.setOwner(o);
        r.setBuckets(b);
        String content = r.toString();

        setCommonHeaders();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(content);
        response.setContentType("application/xml; charset=utf-8");
        logger.info("GET Buckets:" + content);
        return true;
    }

}
