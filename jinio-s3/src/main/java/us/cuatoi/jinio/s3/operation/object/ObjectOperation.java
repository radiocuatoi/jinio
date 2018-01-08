package us.cuatoi.jinio.s3.operation.object;

import com.google.gson.Gson;
import io.minio.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.JinioFilter;
import us.cuatoi.jinio.s3.exception.JinioException;
import us.cuatoi.jinio.s3.model.ObjectMetadata;
import us.cuatoi.jinio.s3.model.ObjectUploads;
import us.cuatoi.jinio.s3.operation.bucket.BucketOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

public abstract class ObjectOperation extends BucketOperation {

    protected String objectName;
    protected Path objectPath;
    protected Path objectMetadataPath;
    protected Path objectUploadPath;

    public ObjectOperation(JinioFilter context, String requestURI) {
        super(context, StringUtils.substring(requestURI, 0, StringUtils.indexOf(requestURI, '/', 1)));
        String[] paths = StringUtils.split(requestURI, '/');
        if (paths.length < 2) {
            throw new IllegalArgumentException("Invalid object path");
        }
        bucketName = paths[0];
        objectName = paths[1];
        for (int i = 2; i < paths.length; i++) {
            objectName += "/" + paths[i];
        }
        objectPath = context.getDataPath().resolve(bucketName).resolve(objectName);
        objectMetadataPath = bucketMetadataPath.resolve(objectName).resolve("fs.json");
        objectUploadPath = bucketUploadPath.resolve(objectName).resolve("uploads.json");
    }

    protected void verifyObjectExists() {
        if (!Files.exists(objectPath)) {
            throw new JinioException(ErrorCode.NO_SUCH_OBJECT).setBucketName(bucketName).setObjectName(objectName);
        }
    }

    protected ObjectUploads getUploads() throws IOException {
        return loadAsJson(objectUploadPath, ObjectUploads.class);
    }

    protected void saveUploads(ObjectUploads uploads) throws IOException {
        saveAsJson(uploads, objectUploadPath);
    }

    protected ObjectMetadata getSavedMetadata() throws IOException {
        return loadAsJson(objectMetadataPath, ObjectMetadata.class);
    }

    private <V> V loadAsJson(Path path, Class<V> objectClass) throws IOException {
        V metadata = null;
        try {
            metadata = objectClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Files.exists(path)) {
            try (BufferedReader br = Files.newBufferedReader(objectMetadataPath)) {
                metadata = new Gson().fromJson(br, objectClass);
            }
        }
        return metadata;
    }

    protected void saveMetadata(ObjectMetadata metadata) throws IOException {
        saveAsJson(metadata, objectMetadataPath);
    }

    private void saveAsJson(Object metadata, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            new Gson().toJson(metadata, bw);
        }
    }

    protected void recordHeaders(Map<String, String> attributes) {
        Enumeration<String> headerNames = request.getHeaderNames();
        String[] recordedHeaders = new String[]{"Cache-Control", "Content-Disposition",
                "Content-Encoding", "Content-Length", "Content-MD5", "Content-Type", "Expires"};
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (StringUtils.equalsAnyIgnoreCase(header, recordedHeaders)) {
                attributes.put(header, request.getHeader(header));
            } else if (StringUtils.startsWith(header, "x-amz-")) {
                attributes.put(header, request.getHeader(header));
            }
        }
    }
}
