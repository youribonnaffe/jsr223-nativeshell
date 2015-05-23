package jsr223.nativeshell;

public final class StringUtils {
    public static String toEmptyStringIfNull(Object value) {
        return value == null ? "" : value.toString();
    }
}
