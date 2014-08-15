package skullMod.lvlEdit.dataStructures.completeLevel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class SkullmodJTree extends JTree{
    public SkullmodJTree(TreeNode root){
        super();
        this.setModel(new SkullmodTreeModel(root));
    }


    public static class SkullmodTreeModel extends DefaultTreeModel{
        public SkullmodTreeModel(TreeNode root){
            super(root,true);

        }


        public void removeModel(Model model) {
            try(AutoCloseable lock = ((Level) this.getRoot()).lock.autoLock()){
                ((Level) this.getRoot()).getModels().removeModel(model);

            } catch (Exception ignored) {}

        }

        public void removeAnimation(Animation animation){
            try(AutoCloseable lock = ((Level) this.getRoot()).lock.autoLock()){
                //((Animation) this.getRoot()).get FIXME not done yet

            }catch (Exception ignored){}
        }
    }


}
