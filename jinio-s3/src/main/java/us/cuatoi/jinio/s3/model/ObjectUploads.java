package us.cuatoi.jinio.s3.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ObjectUploads {
    List<ObjectUpload> uploads = new ArrayList<>();

    public List<ObjectUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<ObjectUpload> uploads) {
        this.uploads = uploads;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
