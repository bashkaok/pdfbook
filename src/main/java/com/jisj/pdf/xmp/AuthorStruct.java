package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.properties.XMPProperty;

import java.util.Optional;
import java.util.UUID;

public class AuthorStruct extends ChildXMPStruct {
    /**
     * Author structure namespace value
     */
    public static final String NS = "http://www.jisj.com/ns/book/author";
    /**
     * Author structure namespace prefix value
     */
    public static final String PREFIX = "author";

    /**
     * Author name property name
     */
    public static final String NAME = "Name";
    /**
     * Author library GUID property name
     */
    public static final String GUID = "GUID";

    /**
     * Create Author structure access object
     * @param metadata metadata object
     * @param schemaNS schema namespace
     * @param structPath path | name of the structure in parent node
     */
    public AuthorStruct(XMPMeta metadata, String schemaNS, String structPath) {
        super(metadata, NS, PREFIX, schemaNS, structPath);
    }

    /**
     * Sets the author name
     *
     * @param value author name
     */
    public void setName(String value) {
        setStructField(NAME, value);
    }

    /**
     * Give the author Name property value (string)
     *
     * @return The property value | null
     */
    public String getName() {
        return getStructField(getSchemaNS(), getStructPath(), NS, NAME)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Sets GUID property value
     *
     * @param uuid identifier value
     */
    public void setGUID(UUID uuid) {
        setStructField(GUID, uuid.toString());
    }

    /**
     * Gives the author GUID
     *
     * @return GUID if exists
     */
    public Optional<UUID> getGUID() {
        return getStructField(GUID)
                .map(XMPProperty::getValue)
                .map(UUID::fromString);
    }


}
