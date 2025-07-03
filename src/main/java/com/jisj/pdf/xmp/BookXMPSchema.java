package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.jisj.pdf.Utils;

import java.time.LocalDate;
import java.util.*;

public class BookXMPSchema extends BaseXMPStructure {
    /**
     * Book structure namespace
     */
    public static final String NS = "http://www.jisj.com/ns/book/";
    /**
     * Book structure namespace prefix
     */

    public static final String PREFIX = "book";

    public static final String TITLE = "Title";
    /**
     * Book GUID from library
     */
    public static final String GUID = "GUID";
    public static final String DATE_CREATED = "DateCreated";
    public static final String GENRES = "Genres";
    public static final String AUTHORS = "Authors";
    public static final String SHEETS = "MusicSheets";
    public static final String WORKS = "Works";

    /**
     * Book XMP Schema constructor
     *
     * @param metadata metadata object
     */
    public BookXMPSchema(XMPMeta metadata) {
        super(metadata, NS, PREFIX);
    }

    /**
     * Sets the book title
     *
     * @param value book title
     */
    public void setTitle(String value) {
        setProperty(TITLE, value);
    }

    /**
     * Give the Title property value (string)
     *
     * @return The property value | empty string
     */
    public String getTitle() {
        return getStringPropertyValue(TITLE);
    }

    /**
     * Sets GUID property value
     *
     * @param uuid identifier value
     */
    public void setGUID(UUID uuid) {
        setProperty(GUID, uuid);
    }

    /**
     * Gives the GUID property value
     * @return GUID value if exists
     */
    public Optional<UUID> getGUID() {
        return Optional.ofNullable(getGUIDPropertyValue(GUID));
    }

    /**
     * Sets the date of book created
     *
     * @param date of book created
     */
    public void setDateCreated(LocalDate date) {
        setProperty(DATE_CREATED, date.toString());
    }

    /**
     * Gives the book created date
     *
     * @return created date
     */
    public Optional<LocalDate> getDateCreated() {
        return getProperty(getNS(), DATE_CREATED)
                .map(XMPProperty::getValue)
                .map(LocalDate::parse);
    }

    /**
     * Adds a genre to XMP
     *
     * @param genre genre string value
     */
    public void addGenre(String genre) {
        try {
            getMetadata().appendArrayItem(getNS(), GENRES, Utils.newOptions(PropertyOptions.ARRAY),
                    genre, Utils.newOptions(PropertyOptions.NO_OPTIONS));
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getGenres() {
        return getArray(getNS(), GENRES);
    }

    /**
     * Adds the author to XMP
     *
     * @param authorName author name
     * @param authorUUID author UUID
     */
    public void addAuthor(String authorName, UUID authorUUID) {
        AuthorStruct author = new AuthorStruct(getMetadata(), getNS(), appendArrayStructItem(getNS(), AUTHORS));
        author.setName(authorName);
        author.setGUID(authorUUID);
    }

    public List<AuthorStruct> getAuthors() {
        return getArrayStruct(getNS(), AUTHORS, p -> new AuthorStruct(getMetadata(), getNS(), p));
    }

    /**
     * Sets the music sheets metadata to XMP book(work)
     *
     * @param key           music work key
     * @param instrument    sheets instrument
     * @param catalogNumber composer catalog number
     * @param arrangedBy    transcription/arrangement author
     */
    public void setSheets(String key, String instrument, String catalogNumber, String arrangedBy) {
        MusicStruct musicStruct = new MusicStruct(getMetadata(), getNS(), SHEETS);
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
        return new MusicStruct(getMetadata(), getNS(), SHEETS);
    }

    /**
     * Adds the book as work item
     *
     * @return access object to new Work element in array
     */
    public WorkStruct addWork() {
        return new WorkStruct(getMetadata(), getNS(), appendArrayStructItem(getNS(), WORKS));
    }

    /**
     * Gives the works list
     *
     * @return works list | empty list if the works are not defined
     */
    public List<WorkStruct> getWorks() {
        return getArrayStruct(getNS(), WORKS, p -> new WorkStruct(getMetadata(), getNS(), p));
    }
}
