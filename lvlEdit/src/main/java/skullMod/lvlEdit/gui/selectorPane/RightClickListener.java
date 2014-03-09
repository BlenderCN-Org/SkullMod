package skullMod.lvlEdit.gui.selectorPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Animation;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;
import skullMod.lvlEdit.dataStructures.completeLevel.Model;
import skullMod.lvlEdit.dataStructures.completeLevel.SkullmodJTree;
import skullMod.lvlEdit.dataStructures.inputDialogs.Dimension2DDialog;
import skullMod.lvlEdit.dataStructures.inputDialogs.FloatInput;
import skullMod.lvlEdit.dataStructures.inputDialogs.IntegerInput;
import skullMod.lvlEdit.dataStructures.inputDialogs.StringInput;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.utility.Dimension2D;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class RightClickListener extends MouseAdapter {
    private final JTree tree;
    private JFrame parent;

    public RightClickListener(JTree tree, JFrame parent){
        this.tree = tree;
        this.parent = parent;
    }
    public void mouseClicked(MouseEvent e) {
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

        if (selPath == null) {
            return; //Cancel processing if no element was selected while clicking
        }

        if(SwingUtilities.isLeftMouseButton(e)){
            int row = tree.getRowForLocation(e.getX(), e.getY());
            System.out.println("ROW:" +row);
            //int row = tree.getClosestRowForLocation(e.getX(), e.getY());
            if(row == -1){
                tree.setSelectionRow(-1);
            }else{
                tree.setSelectionRow(row);

                TreeNode node = (TreeNode) selPath.getLastPathComponent();
                System.out.println(node.getChildCount());

                if(node instanceof Model){
                    Model m = (Model) node;

                    Level l = (Level) tree.getModel().getRoot();
                    CentralDataObject.ddsPanel.changeImage(l.getSaveDirectory() + File.separator + "textures" + File.separator + m.getTextureFileName() + Level.ddsExtension);
                }
            }
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            if(row == -1){
                tree.setSelectionRow(-1);
            }else{
                tree.setSelectionRow(row);


                TreeNode node = (TreeNode) selPath.getLastPathComponent();

                PopupMenu popupMenu = new PopupMenu(node);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());


            }

        }
    }
    private class PopupMenu extends JPopupMenu{
        private JMenuItem edit;
        private JMenuItem delete;

        public boolean isMenuAvailable;

        public PopupMenu(TreeNode node){


            EditActionListener editActionListener = new EditActionListener(node);
            DeleteActionListener deleteActionListener = new DeleteActionListener(node);

            if(editActionListener.isDataEditable){
                edit = new JMenuItem("Edit value");
                edit.addActionListener(editActionListener);
                add(edit);
                isMenuAvailable = true;
            }
            if(deleteActionListener.isDeleteable){
                delete = new JMenuItem("Delete");
                delete.addActionListener(deleteActionListener);
                add(delete);
                isMenuAvailable = true;
            }
        }

    }

    private class DeleteActionListener implements ActionListener{
        private TreeNode clickedNode;
        public final boolean isDeleteable;
        //TODO Check for last animation
        public DeleteActionListener(TreeNode node){
            this.clickedNode = node;
            if(node instanceof Model || node instanceof Animation){
                if(node instanceof Animation){ ((SkullmodJTree.SkullmodTreeModel) tree.getModel()).removeAnimation((Animation) node);}
                if(node instanceof Model){ ((SkullmodJTree.SkullmodTreeModel)tree.getModel()).removeModel((Model) node);}
                isDeleteable = true;

            }else{
                isDeleteable = false;
            }
        }

        public void actionPerformed(ActionEvent e) {
            SkullmodJTree.SkullmodTreeModel model = (SkullmodJTree.SkullmodTreeModel) tree.getModel();

            TreeNode parentNode = clickedNode.getParent();
            //TODO telling the root isn't nice but a workaround
            System.out.println("Deleting node");

            model.reload(parentNode);
        }
    }

    private class EditActionListener implements ActionListener{
        TreeNode clickedNode;

        public final boolean isDataEditable;


        public EditActionListener(TreeNode node){
            if(node instanceof LeafContentNode){
                clickedNode = node;

                Object nodeContent = ((LeafContentNode) node).getContent();

                if( nodeContent instanceof Float || nodeContent instanceof Integer || nodeContent instanceof String || nodeContent instanceof Boolean){
                    isDataEditable = true;
                }else{
                    isDataEditable = false;
                }
            }else{
                isDataEditable = false;
            }
        }
        public void actionPerformed(ActionEvent e) {
                LeafContentNode nodeType = (LeafContentNode) clickedNode;
                Object nodeContent = nodeType.getContent();

                if(nodeContent instanceof Dimension2D){
                    Dimension2D oldValue = (Dimension2D) nodeContent;

                    Dimension2DDialog input = new Dimension2DDialog(parent, "Test","More text");
                    if(input.isValid()){
                        //nodeType.setContent(new Dimension2D<Integer>(,input.getXCoordinate(), input.getYCoordinate()));
                    }
                }


                if(nodeContent instanceof Boolean){
                    Boolean oldValue = (Boolean) nodeContent;


                    nodeType.setContent(!oldValue);
                }

                if(nodeContent instanceof Float){
                    Float oldValue = (Float) nodeContent;

                    FloatInput input = new FloatInput(parent, "Input float", "Input float");
                    System.out.println("DONE");
                    if(input.isValid){
                        nodeType.setContent(input.getFloat()) ;
                    }

                }

                if(nodeContent instanceof Integer){
                    Integer oldValue = (Integer) nodeContent;

                    IntegerInput input = new IntegerInput(parent, "Input integer","Input Integer");
                    if(input.isValid){

                        nodeType.setContent(input.getInt());
                    }
                }

                if(nodeContent instanceof String){
                    String oldValue = (String) nodeContent;

                    StringInput input = new StringInput(parent, "Input String", "Input String");
                    if(input.isValid){
                        nodeType.setContent(input.getString());
                    }
                }
            }
    }
}