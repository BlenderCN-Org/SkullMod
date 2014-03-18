package skullMod.lvlEdit.utility;

import javax.swing.*;
import java.awt.*;

public final class SwingUtility {
    private SwingUtility(){}

    public static void topLeftAlignAllComponents(JComponent component) {
        Component[] com = component.getComponents();

        for (int a = 0; a < com.length; a++) {
            try {
                topLeftAlignAllComponents((JComponent) com[a]);
            } catch (ClassCastException cce) {
            }
            setAlignmentTopLeft(com[a]);
        }
    }

    public static void setAlignmentTopLeft(Component c) {
        if(c instanceof JComponent){
            System.out.println("ALIGNMENT SET");
            JComponent jc = (JComponent) c;
            jc.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            jc.setAlignmentY(JComponent.TOP_ALIGNMENT);
        }

    }
}
