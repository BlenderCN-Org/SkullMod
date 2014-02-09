package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.LVL.LVL_File;
import skullMod.lvlEdit.dataStructures.SGA.SGA_File;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Animation;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Element;
import skullMod.lvlEdit.dataStructures.SGI.SGI_File;
import skullMod.lvlEdit.dataStructures.SGM.SGM_File;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;
import skullMod.lvlEdit.utility.AutoReentrantLock;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

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

    public static final String lvlExtension = ".lvl";
    public static final String ddsExtension = ".dds";
    public static final String sgiExtension = ".sgi.msb";
    public static final String sgmExtension = ".sgm.msb";
    public static final String sgaExtension = ".sga.msb";
    public static final String sgsExtension = ".sgs.msb";

    public static void main(String[] args){
        //WIN new Level("C:\\levels\\temp\\levels","class_notes_3d");
        //UNIX new Level("/home/netbook/Working_files/Skullgirls_extracted/levels", "class_notes_3d");
        new Level("C:\\levels\\temp\\levels","class_notes_3d");
    }

    public Level(String mainDirectory, String lvlName) throws IllegalArgumentException{
        super(null);

        //Set up paths

        String texturePath = mainDirectory + File.separator + "textures";
        String lvlFilePath = mainDirectory + File.separator + lvlName + lvlExtension;
        String lvlDirectoryPath = mainDirectory + File.separator + lvlName;
        String sgiFilePath = lvlDirectoryPath + File.separator + "background" + sgiExtension;

        //Check files and directories
        if(! new File(mainDirectory).isDirectory()){
            throw new IllegalArgumentException("Given directory for levels does not exist");
        }
        if(! new File(texturePath).isDirectory()){
            throw new IllegalArgumentException("No texture directory");
        }
        if(! new File(lvlFilePath).isFile()){
            throw new IllegalArgumentException("No .lvl file in directory");
        }
        if(! new File(lvlDirectoryPath).isDirectory()){
            throw new IllegalArgumentException("No directory for the given level");
        }
        if(! new File(sgiFilePath).isFile()){
            throw new IllegalArgumentException("No background.sgi.msb file found");
        }

        //Read lvl
        LVL_File lvlData = new LVL_File(LVL_File.prepareLVL(lvlFilePath));
        //Read sgi
        SGI_File sgiData;
        try {
            DataStreamIn dsi = new DataStreamIn(sgiFilePath);
            //TODO change to DataInputStream directly
            sgiData = new SGI_File(dsi);
            dsi.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("IO error");
            //TODO fitting catch
        }

        HashMap<String, SGM_File> models = new HashMap<>(); //Key is the model name found in the sgi file
        HashMap<String, HashMap<String, SGA_File>> animations = new HashMap<>();

        for(SGI_Element model : sgiData.elements){
            //Read sgm file
            String pathToCurrentModel = lvlDirectoryPath + File.separator + model.modelFileName + sgmExtension;

            try {
                DataStreamIn dsi = new DataStreamIn(pathToCurrentModel);
                models.put(model.elementName, new SGM_File(dsi.s));
                dsi.close();
            } catch (IOException e) {
                //TODO proper exception
                throw new IllegalArgumentException("Couldn't read file x" );
            }

            HashMap<String, SGA_File> currentAnimations = new HashMap<>();
            for(SGI_Animation animation : model.animations){
                //Read sga files

                String pathToCurrentAnimation = lvlDirectoryPath + File.separator + animation.animationFileName + sgaExtension;
                try {
                    DataStreamIn dsi = new DataStreamIn(pathToCurrentAnimation);
                    currentAnimations.put(animation.animationName, new SGA_File(dsi.s));
                    dsi.close();
                } catch (IOException e) {
                    //TODO proper exception
                    throw new IllegalArgumentException("Couldn't read file x" );
                }

                //TODO more verification
            }

            //TODO check for dds files and music files

            animations.put(model.elementName, currentAnimations);
        }

        this.stageSettings = new StageSettings(this, lvlData);
        this.lighting = new Lighting(this, lvlData);
        this.music = new Music(this, lvlData);

        this.models = new Models(this, sgiData, models, animations);


    }

    public Level(){
        super(null); //Root node
        stageSettings = new StageSettings(this);
        music = new Music(this);
        lighting = new Lighting(this);
        models = new Models(this);
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

    public String toString(){
        return "Level";
    }
}
