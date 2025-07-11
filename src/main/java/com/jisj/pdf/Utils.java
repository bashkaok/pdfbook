package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.options.PropertyOptions;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Static methods
 */
public class Utils {
    private Utils() {
    }

    /**
     * Convert the calendar date to string ISO-8601 date
     *
     * @param calendar {@code Calendar} date
     * @return format {@code String} of "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
     */
    public static String formatCalendarToISO8601(Calendar calendar) {
        // Use 'XXX' for ISO 8601 offset (e.g., +01:00, Z for UTC)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(calendar.getTimeZone());
        return sdf.format(calendar.getTime());
    }

    /**
     * Converts the ISO-8601 string gata format to {@code Calendar}
     *
     * @param stringISO8601 string gata
     * @return {@code Calendar} object
     */
    public static Calendar fromISO8601ToCalendar(String stringISO8601) {
        OffsetDateTime odt = OffsetDateTime.parse(stringISO8601);
        return GregorianCalendar.from(odt.toZonedDateTime());
    }

    /**
     * Creates the new {@link PropertyOptions} object with specified options
     *
     * @param options property options list
     * @return new object
     */
    public static PropertyOptions newOptions(int... options) {
        if (options.length == 0)
            throw new IllegalArgumentException("Unexpected options count = 0");
        if (options.length == 1) {
            try {
                return new PropertyOptions(options[0]);
            } catch (XMPException e) {
                throw new RuntimeException(e);
            }
        }
        throw new UnsupportedOperationException("Unexpected options count > 1");
    }
}
