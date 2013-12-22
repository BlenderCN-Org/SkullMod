package skullMod.lvlEdit.gui.leftPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.gui.modeChange.ModeChanger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TopSelectorPanel extends JPanel {
    private CenterSelectorPanel centerPanel;

    public TopSelectorPanel(CenterSelectorPanel centerPanel){

        this.centerPanel = centerPanel;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        CentralDataObject.modeList.addItemListener(new ModeListItemListener());

        this.add(new JLabel("Mode: "));
        this.add(CentralDataObject.modeList);
    }


    private class ModeListItemListener implements ItemListener{
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                String item = (String) itemEvent.getItem();

                centerPanel.showCard(item);
            }
        }
    }
}
