package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.jisj.pdf.Utils;

import java.util.Optional;

import static com.adobe.internal.xmp.XMPPathFactory.composeStructFieldPath;

public class ChildXMPStruct extends BaseXMPStructure {
    private final String schemaNS;
    private final String structPath;

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
        this.structPath = structPath;
    }

    public String getSchemaNS() {
        return schemaNS;
    }

    public String getStructPath() {
        return structPath;
    }

    public void setStructField(String fieldName, String value) {
        setStructField(getSchemaNS(), getStructPath(), getNS(), fieldName, value);
    }

    public Optional<XMPProperty> getStructField(String fieldName) {
        return getStructField(getSchemaNS(), getStructPath(), getNS(), fieldName);
    }

    public boolean doesStructFieldExist(String fieldName) {
        return getMetadata().doesStructFieldExist(getSchemaNS(), getStructPath(), getNS(), fieldName);
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
                    .setStructField(getSchemaNS(), getStructPath(), fieldNS, fieldName, "", Utils.newOptions(propertyOptions));
            return getStructFieldPath(fieldNS, fieldName);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStructFieldPath(String fieldNS, String fieldName) {
        try {
            return getStructPath() + composeStructFieldPath(fieldNS, fieldName);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ChildXMPStruct{" +
                "schemaNS='" + schemaNS + '\'' +
                ", structPath='" + structPath + '\'' +
                "} " + super.toString();
    }
}
