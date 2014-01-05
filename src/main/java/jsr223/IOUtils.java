package jsr223;

import java.io.InputStream;
import java.io.Reader;
import java.util.Scanner;

/** Thanks to http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string#5445161 */
public class IOUtils {

    public static String toString(Reader reader) {
        Scanner s = new Scanner(reader).useDelimiter("\\A");
        return s.hasNext() ? s.next() : null;
    }

    public static String toString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : null;
    }
}
