package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;
import com.google.api.client.xml.GenericXml;
import com.google.api.client.xml.XmlNamespaceDictionary;

public class LocationConstraintResponse extends GenericXml {
    @Key(value = "text()")
    private String region;

    public LocationConstraintResponse() {
        super.namespaceDictionary = new XmlNamespaceDictionary();
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
