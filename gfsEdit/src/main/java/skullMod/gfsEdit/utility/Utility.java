package skullMod.gfsEdit.utility;

import java.text.NumberFormat;

/**
 * Statistics
 */
public class Utility {
    public static double JAVA_VERSION = getVersion ();

    public static void getMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        System.out.print(sb.toString());
    }

    static double getVersion () {
        String version = System.getProperty("java.version");
        return Double.parseDouble (version.substring (0, 3));
    }
}
