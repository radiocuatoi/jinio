package us.cuatoi.jinio.s3.message;

import com.google.api.client.xml.GenericXml;
import com.google.api.client.xml.XmlNamespaceDictionary;

public class GenericXmlResponse extends GenericXml {
    public GenericXmlResponse() {
        super.namespaceDictionary = new XmlNamespaceDictionary();
        super.namespaceDictionary.set("s3", "http://s3.amazonaws.com/doc/2006-03-01/");
        super.namespaceDictionary.set("", "");
    }
}
