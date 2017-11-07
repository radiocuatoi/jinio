package us.cuatoi.jinio.s3.model;

public class ObjectUploadPart {
    int number;
    String eTag;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }
}
