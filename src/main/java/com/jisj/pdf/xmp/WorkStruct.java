package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.jisj.pdf.Utils;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jisj.pdf.Utils.formatCalendarToISO8601;

public class WorkStruct extends ChildXMPStruct {
    public static final String NS = "http://www.jisj.com/ns/book/work";
    public static final String PREFIX = "work";

    public static final String TITLE = "Title";
    public static final String GUID = "UUID";
    public static final String DATE_CREATED = "DateCreated";
    public static final String GENRES = "Genres";
    public static final String AUTHORS = "Authors";
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
     * Sets the work title
     *
     * @param value work title
     */
    public void setTitle(String value) {
        setStructField(TITLE, value);
    }

    /**
     * Give the Title property value (string)
     *
     * @return The property value | empty string
     */
    public String getTitle() {
        return getStructField(TITLE)
                .map(XMPProperty::getValue)
                .orElse("");
    }

    /**
     * Sets UUID property value
     *
     * @param uuid identifier value
     */
    public void setUUID(UUID uuid) {
        setStructField(GUID, uuid.toString());
    }

    public Optional<UUID> getUUID() {
        return getStructField(GUID)
                .map(XMPProperty::getValue)
                .map(UUID::fromString);
    }

    /**
     * Sets the date of work created
     *
     * @param calendar calendar of work created
     */
    public void setDateCreated(Calendar calendar) {
        setStructField(DATE_CREATED, formatCalendarToISO8601(calendar));
    }

    /**
     * Gives the work created date
     *
     * @return created date
     */
    public Optional<Calendar> getDateCreated() {
        return getStructField(DATE_CREATED)
                .map(XMPProperty::getValue)
                .map(Utils::fromISO8601ToCalendar);
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
        author.setUUID(authorUUID);
    }

    public List<AuthorStruct> getAuthors() {
        return getArrayStruct(getSchemaNS(), getStructFieldPath(getNS(), AUTHORS),
                p -> new AuthorStruct(getMetadata(), getSchemaNS(), p));
    }

    /**
     * Sets the music sheets metadata to XMP book(work)
     *
     * @param key        music work key
     * @param instrument sheets instrument
     */
    public void setSheets(String key, String instrument, String catalogNumber, String arrangedBy) {
        String sheetsPath = setStructureField(getNS(), SHEETS, PropertyOptions.STRUCT);
        MusicStruct musicStruct = new MusicStruct(getMetadata(), getSchemaNS(), sheetsPath);
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
        return new MusicStruct(getMetadata(), getSchemaNS(), getStructFieldPath(getNS(), SHEETS));
    }

}
