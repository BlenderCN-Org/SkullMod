package skullMod.sprConv.utility;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Misc
 */
public class Utility {
    public static double getVersion () {
        String version = System.getProperty("java.version");
        return Double.parseDouble (version.substring (0, 3));
    }


    /**
     * Big endian
     *
     * @param dis
     * @return
     * @throws java.io.IOException
     */
    public static String readLongPascalString(DataInputStream dis) throws IOException {
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        long stringLength = dis.readLong();
        if(stringLength < 1 || stringLength > Integer.MAX_VALUE){ throw new IllegalArgumentException("Given stream position results in invalid data: " + stringLength); }
        byte[] data = new byte[(int) stringLength]; //No string is longer than Integer.MAX_VALUE
        dis.read(data);

        return IOUtils.toString(data, "ASCII");
    }
}
