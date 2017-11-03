package us.cuatoi.jinio.s3.message;

import com.google.api.client.util.Key;

public class InitiatorResponse  extends GenericXmlResponse  {
    @Key("ID")
    private String id;
    @Key("DisplayName")
    private String displayName;

    public InitiatorResponse()  {
        super();
        this.name = "Initiator";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
