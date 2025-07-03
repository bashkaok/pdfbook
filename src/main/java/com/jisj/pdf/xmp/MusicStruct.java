package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.properties.XMPProperty;

import java.util.Map;

/**
 * Music sheets property structure
 */
public class MusicStruct extends ChildXMPStruct {
    /**
     * Music structure namespace value
     */
    public static final String NS = "http://www.jisj.com/ns/book/musicsheets";
    /**
     * Music structure namespace prefix value
     */
    public static final String PREFIX = "sheets";
    /**
     * Music sheets key sign property name
     */
    public static final String KEY = "Key";
    /**
     * Musical instrument property name
     */
    public static final String INSTRUMENTS = "Instruments";
    /**
     * Music work catalog number property name
     */
    public static final String CATALOG_NUMBER = "CatalogNumber";
    /**
     * Transcription or arrangement author name property name
     */
    public static final String TRANSCRIPTION_BY = "Transcription";


    /**
     * Creates access object to music sheets metadata
     *
     * @param metadata   XMP object
     * @param schemaNS   parent node name space
     * @param structPath this structure name in parent node
     */
    public MusicStruct(XMPMeta metadata, String schemaNS, String structPath) {
        super(metadata, NS, PREFIX, schemaNS, structPath);
    }

    /**
     * Sets the key of music work, ex:F-dur
     *
     * @param value key
     */
    public void setKey(String value) {
        setStructField(KEY, value);
    }

    /**
     * Give the music work key
     *
     * @return music work key | empty {@code String}
     */
    public String getKey() {
        return getStructField(KEY)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Sets the music sheets instrument titles
     * <p>The instruments should be divided by comma or semicolon</p>
     *
     * @param instrument name
     */
    public void setInstruments(String instrument) {
        setStructField(INSTRUMENTS, instrument);
    }

    /**
     * Gives the music sheets instrument
     *
     * @return instrument name | empty String
     */
    public String getInstruments() {
        return getStructField(INSTRUMENTS)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Sets the composer catalog piece number
     *
     * @param number catalog number
     */
    public void setCatalogNumber(String number) {
        setStructField(CATALOG_NUMBER, number);
    }

    /**
     * Gives the composer catalog piece number
     *
     * @return piece number | empty String
     */
    public String getCatalogNumber() {
        return getStructField(CATALOG_NUMBER)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Sets the arrangement composer name
     *
     * @param composer arrangement composer name
     */
    public void setArrangedBy(String composer) {
        setStructField(TRANSCRIPTION_BY, composer);
    }

    /**
     * Gives the arrangement composer name
     *
     * @return composer name | empty {@code String}
     */
    public String getArrangedBy() {
        return getStructField(TRANSCRIPTION_BY)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Saves the structure to map propertyName:value
     *
     * @return map with propertyName:value
     */
    public Map<String, String> toMap() {
        return Map.of(KEY, getKey(),
                INSTRUMENTS, getInstruments(),
                CATALOG_NUMBER, getCatalogNumber(),
                TRANSCRIPTION_BY, getArrangedBy());
    }

    @Override
    public String toString() {
        return "MusicType{" + KEY + "=" + getKey() +
                "," + INSTRUMENTS + "=" + getInstruments() +
                "," + CATALOG_NUMBER + "=" + getCatalogNumber() +
                "," + TRANSCRIPTION_BY + "=" + getArrangedBy() +
                "}";
    }
}
