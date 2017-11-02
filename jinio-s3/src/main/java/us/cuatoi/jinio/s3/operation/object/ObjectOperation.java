package us.cuatoi.jinio.s3.operation.object;

import org.apache.commons.lang3.StringUtils;
import us.cuatoi.jinio.s3.operation.Operation;

public abstract class ObjectOperation extends Operation {

    private String bucketName;
    private String objectName;

    public ObjectOperation(String requestURI) {
        String[] paths = StringUtils.split(requestURI, '/');
        if (paths.length < 2) {
            throw new IllegalArgumentException("Invalid object path");
        }
        bucketName = paths[0];
        objectName = paths[1];
        for (int i = 2; i < paths.length; i++) {
            objectName += "/" + paths[i];
        }
    }
}
