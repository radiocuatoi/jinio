package us.cuatoi.jinio.s3.operation.object;

import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.message.InitiateMultipartUploadResultResponse;
import us.cuatoi.jinio.s3.model.ObjectMetadata;
import us.cuatoi.jinio.s3.model.ObjectUpload;
import us.cuatoi.jinio.s3.model.ObjectUploads;

import java.io.IOException;
import java.util.UUID;

public class InitiateMultipartUploadOperation extends ObjectOperation {

    public InitiateMultipartUploadOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        String uploadId = UUID.randomUUID().toString();

        ObjectUpload upload = new ObjectUpload();
        upload.setUploadId(uploadId);
        recordHeaders(upload.getAttributes());

        ObjectUploads uploads = getUploads();
        uploads.getUploads().add(upload);

        saveUploads(uploads);
        InitiateMultipartUploadResultResponse result = new InitiateMultipartUploadResultResponse();
        result.setBucket(bucketName);
        result.setKey(objectName);
        result.setUploadId(uploadId);
        String content = result.toString();

        setCommonHeaders();
        response.setContentType(JinioConfiguration.CONTENT_TYPE);
        response.getWriter().write(content);
        logger.info("POST Object uploads:" + objectPath);
        logger.info("POST Object uploads:uploads=" + uploads);
        logger.info("POST Object uploads:result=" + content);
        return true;
    }


}
