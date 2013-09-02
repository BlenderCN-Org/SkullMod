package skullMod.lvlEdit.utility;

import javax.swing.*;
import java.awt.*;
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
}
