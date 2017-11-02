package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

public class LocationConstraintResponse extends GenericXmlResponse {
    @Key(value = "text()")
    private String region;

    public LocationConstraintResponse() {
        super.name = "LocationConstraint";
        super.namespaceDictionary.set("", "http://s3.amazonaws.com/doc/2006-03-01/");
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
