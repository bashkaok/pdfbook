package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMetaFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        book.setGUID(uuid);
        assertEquals(uuid, book.getGUID().orElseThrow());

        LocalDate date_created = LocalDate.of(2025, 11, 1);
        book.setDateCreated(date_created);
        assertEquals(date_created, book.getDateCreated().orElseThrow());

        book.addGenre("sf");
        book.addGenre("sf_history");
        assertEquals(List.of("sf", "sf_history"), book.getGenres());

        book.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        assertEquals(1, book.getAuthors().size());
        assertEquals("J.S. Bach", book.getAuthors().getFirst().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"), book.getAuthors().getFirst().getGUID().orElseThrow());

        book.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        assertEquals(2, book.getAuthors().size());
        assertEquals("L.V. Beethoven", book.getAuthors().getLast().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"), book.getAuthors().getLast().getGUID().orElseThrow());

        book.setSheets("C-moll", "piano", "BWV 855", "Busoni");
        assertEquals("C-moll", book.getSheets().getKey());
        assertEquals("piano", book.getSheets().getInstruments());
        assertEquals("BWV 855", book.getSheets().getCatalogNumber());
        assertEquals("Busoni", book.getSheets().getArrangedBy());

        WorkStruct work = book.addWork();
        work.setTitle("Work #1");
        work.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2600600c"));
        work.setDateCreated(LocalDate.of(2025, 11, 1));
        work.addGenre("music_sheets");
        work.addGenre("music_barocca");
        work.setSheets("F-dur", "piano", "BWV 785", "author");
        work.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        assertEquals(1, book.getWorks().size());
        WorkStruct a = book.getWorks().getFirst();
        assertEquals("Work #1", a.getTitle());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef2600600c"), a.getGUID().orElseThrow());
        assertEquals(LocalDate.of(2025, 11, 1), a.getDateCreated().orElseThrow());
        assertEquals("music_sheets", a.getGenres().getFirst());
        assertEquals("music_barocca", a.getGenres().getLast());
        assertEquals(1, a.getAuthors().size());
        assertEquals("J.S. Bach", a.getAuthors().getFirst().getName());
        assertEquals(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"), a.getAuthors().getFirst().getGUID().orElseThrow());
        assertEquals("F-dur", a.getSheets().getKey());
        assertEquals("author", a.getSheets().getArrangedBy());
    }

    @Test
    void worksTest() {
        BookXMPSchema b = new BookXMPSchema(XMPMetaFactory.create());
        b.setTitle("Works test");
        b.addWork().setTitle("Work #1");
        b.addWork().setTitle("Work #2");
        assertEquals(2, b.getWorks().size());
        b.getWorks().forEach(System.out::println);
        System.out.println(b.getMetadata().dumpObject());

    }
}