package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.jisj.pdf.Utils;

import java.util.Optional;

public class ChildXMPStruct extends BaseXMPStructure {
    private final String schemaNS;

    /**
     * Schema constructor
     *
     * @param metadata   metadata object
     * @param nameSpace  structure namespace URI
     * @param prefix     NS prefix
     * @param schemaNS   schema namespace URI
     * @param structPath path | name of the structure in parent node
     */
    public ChildXMPStruct(XMPMeta metadata, String nameSpace, String prefix, String schemaNS, String structPath) {
        super(metadata, nameSpace, prefix);
        this.schemaNS = schemaNS;
        setStructName(structPath);
    }

    public String getSchemaNS() {
        return schemaNS;
    }

    public void setStructField(String fieldName, String value) {
        setStructField(getSchemaNS(), getStructName(), getNS(), fieldName, value);
    }

    public Optional<XMPProperty> getStructField(String fieldName) {
        return getStructField(getSchemaNS(), getStructName(), getNS(), fieldName);
    }

    public boolean doesStructFieldExist(String fieldName) {
        return getMetadata().doesStructFieldExist(getSchemaNS(), getStructName(), getNS(), fieldName);
    }

    /**
     * Adds the structure field in current node
     * @param fieldNS field namespace
     * @param fieldName field name | a general path expression of structure
     * @param propertyOptions type of structure
     * @return a general path expression of the added structure
     */
    public String setStructureField(String fieldNS, String fieldName, int... propertyOptions) {
        try {
            getMetadata()
                    .setStructField(getSchemaNS(), getStructName(), fieldNS, fieldName, "", Utils.newOptions(propertyOptions));
            return getStructFieldPath(fieldNS, fieldName);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return "ChildXMPStruct{" +
                "schemaNS='" + schemaNS + '\'' +
                ", structPath='" + getStructName() + '\'' +
                "} " + super.toString();
    }
}
