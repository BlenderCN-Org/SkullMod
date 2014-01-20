package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.SPR.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Is a model/view/control giant
 */
public class SPR_JTree extends JTree{
    private final Component parent;
    private SPR_File sprFile;
    private HashMap<String, BufferedImage[]> animations;
    private final DrawPanel drawPanel;

    private final LinkedList<TreeModelListener> listeners = new LinkedList<>();


    public SPR_JTree(Component parent, SPR_File sprFile, HashMap<String, BufferedImage[]> animations, DrawPanel drawPanel){
        this.parent = parent;
        this.sprFile = sprFile;
        this.animations = animations;

        this.drawPanel = drawPanel;

        this.setModel(new DataModel());
        this.addTreeSelectionListener(new SPR_frame_TreeSelectionListener());
    }

    public void setModel(SPR_File sprFile, HashMap<String, BufferedImage[]> animations){
        this.sprFile = sprFile;
        this.animations = animations;

        this.setModel(new DataModel());
    }

    private class DataModel implements TreeModel {
        public Object getRoot() {
            return sprFile;
        }
        public Object getChild(Object parent, int index) {
            //TODO switch case this
            if(parent instanceof SPR_File){
                if(index == 0){ return new StringNode(sprFile, "Scene name", sprFile.sceneName, 0); }
                if(index == 1){ return new StringNode(sprFile, "Unknown", Integer.toString(sprFile.unknown1), 1); }
                if(index == 2){ return new NamedArrayNode("Frames", sprFile.frames); }
                if(index == 3){ return new NamedArrayNode("Animations", sprFile.animations); }
            }

            if(parent instanceof SPR_Frame){
                SPR_Frame frame = (SPR_Frame) parent;
                if(index == 0){ return new StringNode(frame, "Unknown1", Integer.toString(frame.unknown1), 0); }
                if(index == 1){ return new StringNode(frame, "Unknown2", Float.toString(frame.unknown2), 1); }
                if(index == 2){ return new StringNode(frame, "Unknown3", Float.toString(frame.unknown3), 2); }
            }
            if(parent instanceof SPR_Animation){
                SPR_Animation animation = (SPR_Animation) parent;
                if(index == 0){ return new StringNode(animation, "Unknown1", Integer.toString(animation.unknown1), 0); }
                if(index == 1){ return new StringNode(animation, "Unknown2", Integer.toString(animation.unknown2), 1); }
            }
            if(parent instanceof NamedArrayNode){
                NamedArrayNode node = (NamedArrayNode) parent;
                return node.getChild(index);
            }
            throw new IllegalArgumentException("Unknown node type or index");
        }
        public int getChildCount(Object parent) {
            if(parent == null){ throw new IllegalArgumentException("Given node is null"); } //TODO can this happen?
            //TODO No hardcoding..., or maybe?
            if(parent instanceof SPR_File){ return 4; } //sceneName, unknown1 , frames, animations
            if(parent instanceof SPR_Frame){ return 3; } //All unknowns for now
            if(parent instanceof SPR_Animation){ return 2; }
            if(parent instanceof NamedArrayNode){ return ((NamedArrayNode) parent).getChildCount(); }
            if(parent instanceof StringNode){ return 0;}
            throw new IllegalArgumentException("Unknown node type");
        }
        public boolean isLeaf(Object node) {
            if(node instanceof SPR_File) { return false; }
            if(node instanceof SPR_Frame){ return false; }
            if(node instanceof SPR_Animation) { return false;}
            if(node instanceof NamedArrayNode) {
                NamedArrayNode namedArrayNode = (NamedArrayNode) node;
                if(namedArrayNode.getChildCount() > 0){ return false; }else{ return true; }
            }
            if(node instanceof StringNode){ return true;  }
            throw new IllegalArgumentException("Unknown node type");
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
            throw new NotImplementedException(); //TODO implement when necessary
        }
        public int getIndexOfChild(Object parent, Object child) {
            if(parent instanceof SPR_File){
                if(child instanceof StringNode){ return ((StringNode) child).getIndex(); }
                if(child instanceof NamedArrayNode && ((NamedArrayNode) child).name.equals("Frames")){ return 2;}
                if(child instanceof NamedArrayNode && ((NamedArrayNode) child).name.equals("Animations")){ return 3;}
            }
            if(parent instanceof SPR_Frame){
                return ((StringNode) child).getIndex();
            }

            if(parent instanceof SPR_Animation){
                return ((StringNode) child).getIndex();
            }

            if(parent instanceof NamedArrayNode){
                return ((NamedArrayNode) parent).findChildIndex(child);
            }

            throw new IllegalArgumentException("Given parent/child is invalid");
        }

        public void addTreeModelListener(TreeModelListener l) { listeners.add(l); }
        public void removeTreeModelListener(TreeModelListener l) { listeners.remove(l); }
    }


    private class SPR_frame_TreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            Object source = SPR_JTree.this.getLastSelectedPathComponent();
            if(source instanceof SPR_Frame){
                SPR_Frame frame = (SPR_Frame) source;

                int frameNumber = frame.frameNumber;
                String animationName = null;
                int frameOffset = 0;
                for(SPR_Animation animation : sprFile.animations){
                    if(frameNumber >= animation.frameOffset && frameNumber <= animation.frameOffset + animation.nOfFrames){
                        animationName = animation.animationName;
                        frameOffset = animation.frameOffset;
                    }

                }

                drawPanel.setImage(SPR_JTree.this.animations.get(animationName)[frameNumber - frameOffset]);
            }else{
                drawPanel.removeImage();
            }
        }
    }
}
