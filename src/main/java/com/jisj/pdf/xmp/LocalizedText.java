package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.properties.XMPProperty;

public class LocalizedText extends BaseXMPStructure {
    /**
     * Lang marked structure namespace value
     */
    public static final String NS = "http://www.jisj.com/ns/book/localizedtext";
    /**
     * Lang marked structure namespace prefix value
     */
    public static final String PREFIX = "text";
    /**
     * Lang property name
     */
    public static final String LANG = "lang";
    /**
     * Title property name
     */
    public static final String CONTENT = "content";

    /**
     * Schema constructor
     *
     * @param metadata   metadata object
     * @param structPath path | name of the structure in parent node
     */
    public LocalizedText(XMPMeta metadata, String structPath) {
        super(metadata, NS, PREFIX);
        setStructName(structPath);
    }

    /**
     * Schema constructor
     *
     * @param parent     parent node
     * @param structPath path | name of the structure in parent node
     */
    public LocalizedText(BaseXMPStructure parent, String structPath) {
        this(parent.getMetadata(), structPath);
        setParent(parent);
        setRoot(parent.getRoot());
    }

    public void setLang(String lang) {
        setStructField(getRoot().getNS(), getStructName(), getNS(), LANG, lang);

    }

    public String getLang() {
        return getStructField(getRoot().getNS(), getStructName(), getNS(), LANG)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    public void setContent(String name) {
        setStructField(getRoot().getNS(), getStructName(), getNS(), CONTENT, name);
    }

    public String getContent() {
        return getStructField(getRoot().getNS(), getStructName(), getNS(), CONTENT)
                .map(XMPProperty::getValue)
                .orElse("");
    }
}
