package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;
import skullMod.lvlEdit.utility.AutoReentrantLock;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * This class is checked by the reentrant lock, its children aren't.
 * Use the autoLock() method from the lock with a try-with resource statment for easy
 * and safe use, never use objects you got here, clone them if necessary.
 * Got a better idea? Contact me.
 */
public class Level extends NodeAdapter {
    public final AutoReentrantLock lock = new AutoReentrantLock(true);

    private StageSettings stageSettings;
    private Music music;
    private Lighting lighting; //Done
    private Models models;

    public Level(){
        stageSettings = new StageSettings();
        music = new Music();
        lighting = new Lighting(this);
        models = new Models();
    }

    public StageSettings getStageSettings(){
        if(lock.isHeldByCurrentThread()){
            return stageSettings;
        }else{
            throw new IllegalAccessError("Lock this object before using it");
        }
    }

    public Music getMusic(){
        if(lock.isHeldByCurrentThread()){
            return music;
        }else{
            throw new IllegalAccessError("Lock this object before using it");
        }
    }

    public Lighting getLighting(){
       if(lock.isHeldByCurrentThread()){
           return lighting;
       }else{
           throw new IllegalAccessError("Lock this object before using it");
       }

    }

    public Models getModels(){
        if(lock.isHeldByCurrentThread()){
            return models;
        }else{
            throw new IllegalAccessError("Lock this object before using it");
        }
    }

    //This bypasses the get methods because the EDT thread never writes
    //Also the content can be old or currently be written because
    //it is updated after being modified anyways
    public TreeNode getChildAt(int childIndex) {
        switch(childIndex){
            case 0:
                return stageSettings;
            case 1:
                return music;
            case 2:
                return lighting;
            case 3:
                return models;
            default:
                throw new IllegalArgumentException("Unknown child index");
        }
    }

    public int getChildCount() { return 4; }

    public int getIndex(TreeNode node) {
        if(node == stageSettings){ return 0; }
        if(node == music){ return 1; }
        if(node == lighting){ return 2; }
        if(node == models){ return 3; }

        return -1;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>(4);
        list.add(stageSettings);
        list.add(music);
        list.add(lighting);
        list.add(models);
        return null;
    }
}
