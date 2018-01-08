package us.cuatoi.jinio.s3.operation.object;

import com.google.common.hash.Hashing;
import io.minio.ErrorCode;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.model.ObjectMetadata;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PutMultipartUploadOperation extends ObjectOperation {

    private final Path data;

    public PutMultipartUploadOperation(JinioFilter context, String requestURI, Path data) {
        super(context, requestURI);
        this.data = data;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        String uploadId = request.getParameter("uploadId");
        String partNumber = request.getParameter("partNumber");
        Path uploadPath = objectUploadPath.getParent().resolve(uploadId);
        if (!Files.exists(uploadPath)) {
            logger.info("Unknown upload:" + uploadId);
            throw new JinioException(ErrorCode.NO_SUCH_UPLOAD).setBucketName(bucketName).setObjectName(objectName);
        }
        Path uploadPartPath = uploadPath.resolve(partNumber);
        Files.copy(data, uploadPartPath);
        return true;
    }

}
