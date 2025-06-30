package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.jisj.pdf.Utils;

import java.util.*;

public class BookXMPSchema extends BaseXMPStructure {
    public static final String NS = "http://www.jisj.com/ns/book/";
    public static final String PREFIX = "book";

    public static final String TITLE = "Title";
    public static final String UUID = "UUID";
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
     * Sets UUID property value
     *
     * @param uuid identifier value
     */
    public void setUUID(UUID uuid) {
        setProperty(UUID, uuid);
    }

    public Optional<UUID> getUUID() {
        return Optional.ofNullable(getGUIDPropertyValue(UUID));
    }

    /**
     * Sets the date of book created
     *
     * @param calendar calendar of book created
     */
    public void setDateCreated(Calendar calendar) {
        setProperty(DATE_CREATED, calendar);
    }

    /**
     * Gives the book created date
     *
     * @return created date
     */
    public Optional<Calendar> getDateCreated() {
        return Optional.ofNullable(getCalendarPropertyValue(DATE_CREATED));
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
        author.setUUID(authorUUID);
    }

    public List<AuthorStruct> getAuthors() {
        return getArrayStruct(getNS(), AUTHORS, p -> new AuthorStruct(getMetadata(), getNS(), p));
    }

    /**
     * Sets the music sheets metadata to XMP book(work)
     *
     * @param key        music work key
     * @param instrument sheets instrument
     */
    public void setSheets(String key, String instrument, String catalogNumber, String arrangedBy) {
        MusicStruct musicStruct = new MusicStruct(getMetadata(), getNS(), SHEETS);
        musicStruct.setKey(key);
        musicStruct.setInstrument(instrument);
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
        return getArrayStruct(getNS(), WORKS, p-> new WorkStruct(getMetadata(),getNS(),p));
    }
}
