package skullMod.lvlEdit.utility;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import skullMod.lvlEdit.dataStructures.DataStreamOut;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

public class Utility {
    public static double JAVA_VERSION = getVersion ();

    public static void getMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("Free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("Allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("Max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        System.out.println(sb.toString());
    }

    public static double getVersion () {
        String version = System.getProperty("java.version");
        return Double.parseDouble (version.substring (0, 3));
    }

    public static void disableAllComponents(JComponent component) {
        Component[] com = component.getComponents();

        for (int a = 0; a < com.length; a++) {
            try {
                disableAllComponents((JComponent) com[a]);
            } catch (ClassCastException cce) {
            }
            com[a].setEnabled(false);
        }
    }

    public static void setAlignmentTopLeft(JComponent c) {
        c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        c.setAlignmentY(JComponent.TOP_ALIGNMENT);
    }

    public static void setPreferredHeightToMaxHeight(Component c) {
        c.setMaximumSize(new Dimension(c.getMaximumSize().width, c.getPreferredSize().height));
    }

    public static JSeparator getFixedSizeHorizontalJSeparator() {
        JSeparator result = new JSeparator(JSeparator.HORIZONTAL);
        setAlignmentTopLeft(result);
        setPreferredHeightToMaxHeight(result);
        return result;
    }

    public static String byteToBinString(byte b){
        return String.format("%8s", Integer.toBinaryString(convertToUnsignedByte(b))).replace(' ', '0');
    }

    public static int convertToUnsignedByte(byte b){ return  b &0xFF;}

    //TODO checks
    public static float[] readFloatArray(DataInputStream dis, float[] array) throws IOException {
        for(int i = 0;i < array.length;i++){
            array[i] = dis.readFloat();
        }
        return array;
    }

    /**
     * Big endian
     *
     * @param dis
     * @return
     * @throws java.io.IOException
     */
    public static String readLongPascalString(DataInputStream dis) throws IOException{
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        long stringLength = dis.readLong();
        if(stringLength < 1 || stringLength > Integer.MAX_VALUE){ throw new IllegalArgumentException("Given stream position results in invalid data: " + stringLength); }
        byte[] data = new byte[(int) stringLength]; //No string is longer than Integer.MAX_VALUE
        dis.read(data);

        return IOUtils.toString(data, "ASCII");
    }


    public static void writeLongPascalString(DataStreamOut dso, String input) throws IOException{
        if(dso == null){ throw new IllegalArgumentException("Given stream is null"); }
        if(input == null){throw new IllegalArgumentException("Given input is null"); }
        if(input.length() == 0){ throw new IllegalArgumentException("Given input has a length of zero"); }

        //FIXME validate input to be ascii

        long stringLength = input.length();

        dso.s.writeLong(stringLength);
        dso.s.write(input.getBytes(Charsets.US_ASCII));
    }

    public static String[] readLongPascalStringArray(DataInputStream dis, String[] array) throws IOException{
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        for(int i = 0;i < array.length;i++){
            array[i] = readLongPascalString(dis);
        }
        return array;
    }

    public static byte[] readByteArray(DataInputStream dis, byte[] array) throws IOException{
        dis.read(array);
        return array;
    }

    public static short[] readShortArray(DataInputStream dis, short[] array) throws IOException{
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        for(int i = 0;i < array.length;i++){
            array[i] = dis.readShort();
        }
        return array;
    }
}
