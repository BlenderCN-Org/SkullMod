package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.SPR.*;
import skullMod.sprConv.dataStructures.jTreeNodes.NamedArrayNode;
import skullMod.sprConv.dataStructures.jTreeNodes.StringNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private final JFrame window;

    private final LinkedList<TreeModelListener> listeners = new LinkedList<>();


    public SPR_JTree(Component parent, SPR_File sprFile, HashMap<String, BufferedImage[]> animations, DrawPanel drawPanel, JFrame window){
        this.parent = parent;
        this.sprFile = sprFile;
        this.animations = animations;

        this.drawPanel = drawPanel;
        this.window = window;

        this.setModel(new DataModel());
        this.addTreeSelectionListener(new SPR_frame_TreeSelectionListener());
        this.setCellRenderer(new SPR_TreeCellRenderer());
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
                if(index == 1){ return new StringNode(frame, "Image center x", Float.toString(frame.xImageCenter), 1); }
                if(index == 2){ return new StringNode(frame, "Image center y", Float.toString(frame.yImageCenter), 2); }
            }
            if(parent instanceof SPR_Animation){
                SPR_Animation animation = (SPR_Animation) parent;
                if(index == 0){ return new StringNode(animation, "Unknown1", Integer.toString(animation.unknown1), 0); }
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
            if(parent instanceof SPR_Animation){ return 1; }
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
        private AnimationThread animationThread;
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

            if(source instanceof SPR_Animation){
                SPR_Animation animation = (SPR_Animation) source;

                if(animationThread != null){
                    animationThread.setThreadInactive();
                }

                animationThread = new AnimationThread(animation.animationName,  animation.nOfFrames);
                animationThread.start();
            }else{
                if(animationThread != null){
                    animationThread.setThreadInactive();
                    animationThread = null; //TODO do this?
                }
            }
        }

        //TODO exit thread on dispose through endThread listener or hooks

        private class AnimationThread extends Thread{
            private String animationName;
            private int frameOffset;
            private int nOfFrames;

            private boolean isActive = true;

            private int currentFrame = 0;

            public AnimationThread(String animationName, int nOfFrames){
                this.animationName = animationName;
                this.nOfFrames = nOfFrames;

                window.addWindowListener(new ThreadWindowAdapater());
            }
            //TODO shut down jframe, listener for thread ending (GUI support threads) or shutdown hook, more thread safety
            public void run(){
                while(this.isThreadActive()){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                                drawPanel.setImage(animations.get(animationName)[currentFrame]);

                                currentFrame++;
                                if(currentFrame > nOfFrames-1){ currentFrame = 0; }
                            }
                        }
                    );
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }

            public void setThreadInactive(){
                isActive = false;
            }

            public boolean isThreadActive(){
                return isActive;
            }


            private class ThreadWindowAdapater extends WindowAdapter {
                public void windowClosing(WindowEvent e){
                    setThreadInactive();
                }
            }
        }
    }

    /**
     * By: SanderB
     * Arrow: http://vector4free.com/vector/56-free-arrow-symbols-icons/
     * License: http://creativecommons.org/licenses/by/3.0/
     *
     * Font used: Selznick Remix NF (look terms up)
     */
    private class SPR_TreeCellRenderer extends DefaultTreeCellRenderer{

        private final ImageIcon leafIcon;
        private final ImageIcon directoryIcon;
        private final ImageIcon frameIcon;
        private final ImageIcon animationIcon;

        public SPR_TreeCellRenderer(){
            leafIcon = new ImageIcon(SPR_TreeCellRenderer.class.getResource("/leafIcon.png"));
            directoryIcon = new ImageIcon(SPR_TreeCellRenderer.class.getResource("/directoryIcon.png"));
            frameIcon = new ImageIcon(SPR_TreeCellRenderer.class.getResource("/frameIcon.png"));
            animationIcon = new ImageIcon(SPR_TreeCellRenderer.class.getResource("/animationIcon.png"));

            this.setLeafIcon(leafIcon);
            this.setOpenIcon(directoryIcon);
            this.setClosedIcon(directoryIcon);
        }
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            Object node = value;
            // check whatever you need to on the node user object
            if(node instanceof SPR_Frame) { setIcon(frameIcon); }
            if(node instanceof SPR_Animation){ setIcon(animationIcon); }
            return this;
        }
    }
}
