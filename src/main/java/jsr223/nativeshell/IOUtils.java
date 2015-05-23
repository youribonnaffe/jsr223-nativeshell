package jsr223.nativeshell;

import java.io.*;
import java.util.Scanner;

/**
 * Just to avoid external dependency on commons-io
 * Thanks to http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string#5445161
 */
public final class IOUtils {

    public static String toString(Reader reader) {
        Scanner s = new Scanner(reader).useDelimiter("\\A");
        return s.hasNext() ? s.next() : null;
    }

    public static String toString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : null;
    }

    public static void writeStringToFile(String string, File file) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        out.print(string);
        out.close();
    }

    public static void pipe(Reader from, Writer to) throws IOException {
            char[] buff = new char[1024];
            int n = from.read(buff);
            while (n != -1) {
                to.write(buff, 0, n);
                to.flush();
                n = from.read(buff);
            }
            from.close();
    }
}
