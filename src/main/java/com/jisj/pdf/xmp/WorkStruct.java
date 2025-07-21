package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Wrapper class for Work XMP structure
 */
public class WorkStruct extends ChildXMPStruct {
    /**
     * Work structure namespace
     */
    public static final String NS = "http://www.jisj.com/ns/book/work";
    /**
     * Work structure namespace prefix
     */
    public static final String PREFIX = "work";
    /**
     * Work title field name
     */
    public static final String TITLE = "Title";
    /**
     * Work GUID field name
     */
    public static final String GUID = "GUID";
    /**
     * Work create date name
     */
    public static final String DATE_CREATED = "DateCreated";
    /**
     * Work genres section name
     */
    public static final String GENRES = "Genres";
    /**
     * Work authors section name
     */
    public static final String AUTHORS = "Authors";
    /**
     * Work music sheets section name
     */
    public static final String SHEETS = "MusicSheets";

    /**
     * Create Work structure access object
     *
     * @param metadata   metadata object
     * @param parentNS   parent name space
     * @param structPath path | name of the structure in parent node
     */
    public WorkStruct(XMPMeta metadata, String parentNS, String structPath) {
        super(metadata, NS, PREFIX, parentNS, structPath);
    }

    /**
     * Create Work structure access object
     *
     * @param parent   parent node
     * @param structPath path | name of the structure in parent node
     */
    public WorkStruct(BaseXMPStructure parent, String structPath) {
        this(parent.getMetadata(), parent.getNS(), structPath);
        setRoot(parent.getRoot());
    }

    /**
     * Sets the work title
     *
     * @param title work title
     * @param lang title language
     */
    public void setTitle(String title, String lang) {
        String titlePath = setStructureField(getNS(), TITLE, PropertyOptions.STRUCT);
        LocalizedText text = new LocalizedText(this, titlePath);
        text.setRoot(this.getRoot());
        text.setLang(lang);
        text.setContent(title);
    }

    /**
     * Give the Title property value (string)
     *
     * @return The property value | empty string
     */
    public LocalizedText getTitle() {
        return new LocalizedText(this, getStructFieldPath(NS, TITLE));
    }

    /**
     * Sets GUID property value
     *
     * @param uuid identifier value
     */
    public void setGUID(UUID uuid) {
        if (uuid == null)
            throw new IllegalArgumentException("Unexpected UUID value = null");
        setStructField(GUID, uuid.toString());
    }

    /**
     * Gives the GUID of the work
     *
     * @return GUID if exists
     */
    public Optional<UUID> getGUID() {
        return getStructField(GUID)
                .map(XMPProperty::getValue)
                .map(UUID::fromString);
    }

    /**
     * Sets the date of work created
     *
     * @param date of work created
     */
    public void setDateCreated(LocalDate date) {
        setStructField(DATE_CREATED, date.toString());
    }

    /**
     * Gives the work created date
     *
     * @return created date
     */
    public Optional<LocalDate> getDateCreated() {
        return getStructField(DATE_CREATED)
                .map(XMPProperty::getValue)
                .map(LocalDate::parse);
    }

    /**
     * Adds the genre to work XMP
     *
     * @param genre genre string value
     */
    public void addGenre(String genre) {
        if (!doesStructFieldExist(GENRES))
            setStructureField(getNS(), GENRES, PropertyOptions.ARRAY);
        String arrayPath = getStructFieldPath(getNS(), GENRES);
        appendArrayItem(getSchemaNS(), arrayPath, genre);
    }

    /**
     * Gives the genre list
     *
     * @return genre List | empty List
     */
    public List<String> getGenres() {
        return getArray(getSchemaNS(), getStructFieldPath(getNS(), GENRES));
    }

    /**
     * Adds the author to XMP
     *
     * @param authorName author name
     * @param authorUUID author UUID
     */
    public void addAuthor(String authorName, UUID authorUUID) {
        if (!doesStructFieldExist(AUTHORS))
            setStructureField(getNS(), AUTHORS, PropertyOptions.ARRAY_ORDERED);
        String arrayPath = getStructFieldPath(getNS(), AUTHORS);
        AuthorStruct author = new AuthorStruct(getMetadata(), getSchemaNS(),
                appendArrayStructItem(getSchemaNS(), arrayPath));
        author.setName(authorName);
        author.setGUID(authorUUID);
    }

    /**
     * Gives the authors of the work
     *
     * @return list with author wrapper objects
     */
    public List<AuthorStruct> getAuthors() {
        return getArrayStruct(getSchemaNS(), getStructFieldPath(getNS(), AUTHORS),
                p -> new AuthorStruct(getMetadata(), getSchemaNS(), p));
    }

    /**
     * Sets the music sheets metadata to XMP book(work)
     *
     * @param key           music work key
     * @param instrument    sheets instrument
     * @param catalogNumber composer works catalog
     * @param arrangedBy    author name of the transcription or arrangement
     */
    public void setSheets(String key, String instrument, String catalogNumber, String arrangedBy) {
        String sheetsPath = setStructureField(getNS(), SHEETS, PropertyOptions.STRUCT);
        MusicStruct musicStruct = new MusicStruct(getMetadata(), getSchemaNS(), sheetsPath);
        musicStruct.setKey(key);
        musicStruct.setInstruments(instrument);
        musicStruct.setCatalogNumber(catalogNumber);
        musicStruct.setArrangedBy(arrangedBy);
    }

    /**
     * Gives the MusicSheets property
     *
     * @return music sheets property
     */
    public MusicStruct getSheets() {
        return new MusicStruct(getMetadata(), getSchemaNS(), getStructFieldPath(getNS(), SHEETS));
    }

}
