package us.cuatoi.jinio.s3.operation.object;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.model.ObjectMetadata;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

public class PutObjectOperation extends ObjectOperation {

    private final Path data;

    public PutObjectOperation(JinioFilter context, String requestURI, Path data) {
        super(context,requestURI);
        this.data = data;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute() throws IOException {
        verifyBucketExists(bucketName);
        //Write file content. Need to handle part upload later.
        Files.copy(data, objectPath);

        //We need to handle object metadata.
        Files.createDirectories(objectMetadataPath.getParent());
        ObjectMetadata metadata = new ObjectMetadata();
        if (Files.exists(objectMetadataPath)) {
            try (BufferedReader br = Files.newBufferedReader(objectMetadataPath)) {
                metadata = new Gson().fromJson(br, ObjectMetadata.class);
            }
        }
        String ETag = com.google.common.io.Files.asByteSource(data.toFile()).hash(Hashing.md5()).toString();
        metadata.setETag(ETag);
        metadata.setContentType(request.getContentType());
        metadata.setContentLength(Files.size(data));//May need to check this on multi part case
        Enumeration<String> headerNames = request.getHeaderNames();
        String[] recordedHeaders = new String[]{"Cache-Control", "Content-Disposition",
                "Content-Encoding", "Content-Length", "Content-MD5", "Content-Type", "Expires"};
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (StringUtils.equalsAnyIgnoreCase(header, recordedHeaders)) {
                metadata.getAttributes().put(header, request.getHeader(header));
            } else if (StringUtils.startsWith(header, "x-amz-")) {
                metadata.getAttributes().put(header, request.getHeader(header));
            }
        }
        try (BufferedWriter bw = Files.newBufferedWriter(objectMetadataPath)) {
            new Gson().toJson(metadata, bw);
        }

        //write response
        setCommonHeaders();
        response.setHeader("ETag", ETag);
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("PUT Object:" + objectPath);
        logger.info("PUT Object:" + metadata.toString());
        return false;
    }
}
