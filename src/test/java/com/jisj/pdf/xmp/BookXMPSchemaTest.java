package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMetaFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookXMPSchemaTest {
    private static BookXMPSchema book;

    @BeforeAll
    static void setUp() {
        book = new BookXMPSchema(XMPMetaFactory.create());
    }

    @AfterAll
    static void results() {
        System.out.println(book.getMetadata().dumpObject());
    }

    @Test
    void createXMPScheme() {
        final String title = "Test book title";
        book.setTitle(title);
        assertEquals(title, book.getTitle());

        final UUID uuid = UUID.fromString("b47665da-6c75-4632-952d-a2ef2619600c");
        book.setUUID(uuid);
        assertEquals(uuid, book.getUUID().orElseThrow());

        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        book.setDateCreated(date_created);
        assertEquals(date_created.getTimeInMillis(),
                book.getDateCreated().orElseThrow().getTimeInMillis());

        book.addGenre("sf");
        book.addGenre("sf_history");
        assertEquals(List.of("sf", "sf_history"), book.getGenres());

        book.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        assertEquals(1, book.getAuthors().size());
        assertEquals("J.S. Bach", book.getAuthors().getFirst().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"), book.getAuthors().getFirst().getUUID().orElseThrow());

        book.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        assertEquals(2, book.getAuthors().size());
        assertEquals("L.V. Beethoven", book.getAuthors().getLast().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"), book.getAuthors().getLast().getUUID().orElseThrow());

        book.setSheets("C-moll", "piano", "BWV 855", "Busoni");
        assertEquals("C-moll", book.getSheets().getKey());
        assertEquals("piano", book.getSheets().getInstrument());
        assertEquals("BWV 855", book.getSheets().getCatalogNumber());
        assertEquals("Busoni", book.getSheets().getArrangedBy());

        WorkStruct work = book.addWork();
        work.setTitle("Work #1");
        work.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2600600c"));
        work.setDateCreated(new Calendar.Builder().setDate(2025, 11, 1).build());
        work.addGenre("music_sheets");
        work.addGenre("music_barocca");
        work.setSheets("F-dur", "piano", "BWV 785", "author");
        work.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        assertEquals(1, book.getWorks().size());
        WorkStruct a = book.getWorks().getFirst();
        assertEquals("Work #1", a.getTitle());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef2600600c"), a.getUUID().orElseThrow());
        assertEquals(new Calendar.Builder().setDate(2025, 11, 1).build().getTimeInMillis(),
                a.getDateCreated().orElseThrow().getTimeInMillis());
        assertEquals("music_sheets", a.getGenres().getFirst());
        assertEquals("music_barocca", a.getGenres().getLast());
        assertEquals(1, a.getAuthors().size());
        assertEquals("J.S. Bach", a.getAuthors().getFirst().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"), a.getAuthors().getFirst().getUUID().orElseThrow());
        assertEquals("F-dur", a.getSheets().getKey());
        assertEquals("author", a.getSheets().getArrangedBy());
    }
}