package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

public class PrefixResponse extends GenericXmlResponse {
    @Key("Prefix")
    private String prefix;

    public PrefixResponse() {
        super.name="Prefix";
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
