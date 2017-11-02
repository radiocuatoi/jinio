package us.cuatoi.jinio.s3.operation.object;

import java.io.IOException;

public class PutObjectOperation extends ObjectOperation {
    public PutObjectOperation(String requestURI) {
        super(requestURI);
    }

    @Override
    public boolean execute() throws IOException {
        return false;
    }
}
