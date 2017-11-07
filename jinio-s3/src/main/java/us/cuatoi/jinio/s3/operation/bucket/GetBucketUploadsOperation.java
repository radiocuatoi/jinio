package us.cuatoi.jinio.s3.operation.bucket;

import io.minio.ErrorCode;
import io.minio.messages.Upload;
import org.slf4j.LoggerFactory;
import us.cuatoi.jinio.s3.JinioConfiguration;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.message.*;
import us.cuatoi.jinio.s3.operation.PathWalker;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static io.minio.DateFormat.EXPIRATION_DATE_FORMAT;
import static org.apache.commons.lang3.StringUtils.*;
import static us.cuatoi.jinio.s3.JinioFilter.DEFAULT_REGION;

public class GetBucketUploadsOperation extends BucketOperation {
    public GetBucketUploadsOperation(JinioFilter context, String requestURI) {
        super(context, requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        //Encoding parameter
        String encodingType = request.getParameter("encoding-type");
        //Filtering parameter
        String delimiter = request.getParameter("delimiter");
        String prefix = request.getParameter("prefix");
        //Paging parameter
        int maxUploads = getIntParameter();
        String keyMarker = request.getParameter("key-marker");
        String uploadIdMarker = request.getParameter("upload-id-marker");

        PathWalker walker = new PathWalker(bucketUploadPath)
                .setPrefix(prefix).setDelimiter(delimiter)
                .setMarker(keyMarker).setMax(maxUploads)
                .walk(bucketUploadPath);
        writeLog(walker);

        ListMultipartUploadsResultResponse result = new ListMultipartUploadsResultResponse();
        result.setBucketName(bucketName);
        result.setMaxUploads(maxUploads);
        result.setKeyMarker(keyMarker);
        result.setUploadIdMarker(uploadIdMarker);

        if (isNotBlank(walker.getNextMarker())) {
            result.setNextKeyMarker(walker.getNextMarker());
            result.setNextUploadIdMarker(UUID.randomUUID().toString());//TODO: fix this
            result.setTruncated(walker.isTruncated());
        }

        for (String cp : walker.getCommonPrefixes()) {
            PrefixResponse pr = new PrefixResponse();
            pr.setPrefix(cp);
            result.getCommonPrefixes().add(pr);
        }

        for (Path p : walker.getPaths()) {
            BasicFileAttributes attribute = Files.readAttributes(p, BasicFileAttributes.class);
            OwnerResponse or = new OwnerResponse();
            or.setId("jinio");
            or.setDisplayName("Jinio");

            InitiatorResponse ir = new InitiatorResponse();
            ir.setDisplayName("Jinio");
            ir.setId("jinio");

            UploadResponse ur = new UploadResponse();
            ur.setObjectName(p.toString());
            ur.setUploadId(UUID.randomUUID().toString());
            ur.setInitiated(EXPIRATION_DATE_FORMAT.print(attribute.creationTime().toMillis()));
            ur.setInitiator(ir);
            ur.setOwner(or);
            ur.setStorageClass("STANDARD");
            result.getUploads().add(ur);
        }

        String content = result.toString();
        setCommonHeaders();
        response.setContentType(JinioConfiguration.CONTENT_TYPE);
        response.getWriter().write(content);
        logger.info("GET Bucket uploads:" + bucketName);
        logger.info("GET Bucket uploads:encodingType=" + encodingType);
        logger.info("GET Bucket uploads:delimiter=" + delimiter);
        logger.info("GET Bucket uploads:prefix=" + prefix);
        logger.info("GET Bucket uploads:maxUploads=" + maxUploads);
        logger.info("GET Bucket uploads:keyMarker=" + keyMarker);
        logger.info("GET Bucket uploads:uploadIdMarker=" + uploadIdMarker);
        logger.info("GET Bucket uploads:" + content);
        return true;
    }

    private void writeLog(PathWalker walker) {
        logger.info("walker.truncated:" + walker.isTruncated());
        for (Path p : walker.getPaths()) {
            logger.info("walker.path:" + p.toString());
        }
        for (String cp : walker.getCommonPrefixes()) {
            logger.info("walker.path:" + cp);
        }
    }

    private int getIntParameter() {
        try {
            return Integer.parseInt(request.getParameter("max-uploads"));
        } catch (Exception ex) {
            throw new JinioException(ErrorCode.UNEXPECTED_CONTENT).setBucketName(bucketName);
        }
    }


}
