package skullMod.lvlEdit.gui.rightPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;

public class RightCenterPane extends JPanel {
    private CardLayout layout;
    public RightCenterPane(){
        layout = new CardLayout();
        this.setLayout(layout);

        this.add(SCENE.name(), new SceneJTable());
        this.add(MODEL.name(), new ImageJTable());
        this.add(ANIMATION.name(), new AnimationJTable());
        this.add(SHAPE.name(), new ShapeJTable());

        CentralDataObject.modeList.addItemListener(new ModeListItemListener());
    }

    private class ModeListItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                String item = (String) itemEvent.getItem();
                showCard(item);
            }
        }
    }

    public void showCard(String cardName){
        if(cardName == null){ throw new IllegalArgumentException("Given cardName is null"); }
        //FIXME make this dynamic
        if(cardName.equals(SCENE.toString()) || cardName.equals(MODEL.toString()) || cardName.equals(ANIMATION.toString()) || cardName.equals(SHAPE.toString())){
            layout.show(this, cardName);
        }else{
            throw new IllegalArgumentException("Invalid cardName identifier: " + cardName);
        }
    }
}
