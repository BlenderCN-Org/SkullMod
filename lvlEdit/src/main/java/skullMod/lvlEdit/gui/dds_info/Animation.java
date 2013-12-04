package skullMod.lvlEdit.gui.dds_info;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An animation of a background element
 */
public class Animation implements Serializable {
    private static long serialVersionUID = 1; //TODO correct?

    private String animationName;
    private ArrayList<InfoRectangle> frames;

    public Animation(String animationName, InfoRectangle[] frames){
        this.frames = new ArrayList<>();
        this.setAnimationName(animationName);
        this.setFrames(frames);
    }

    public void setAnimationName(String newAnimationName){
        if(newAnimationName == null){ throw new IllegalArgumentException("animationName is null"); }
        this.animationName = newAnimationName;
    }
    public void setFrames(InfoRectangle[] frames){
        if(frames == null){ throw new IllegalArgumentException("frames is null"); }

        //Check contents
        for(InfoRectangle frame : frames){
            if(frame == null){ throw new IllegalArgumentException("Given frames array contains a null entry"); }
        }

        //Add contents
        this.frames.clear();
        for(int i = 0;i < frames.length;i++){
            InfoRectangle currentFrame = new InfoRectangle(frames[i]); //TODO Cloning, is implementing clonable better?
            currentFrame.setName(animationName + ":" + i);

            this.frames.add(currentFrame);
        }

    }

    public String getAnimationName(){
        return animationName;
    }

    public ArrayList<InfoRectangle> getFrames(){
        return frames;
    }

    public InfoRectangle[] getFramesArray(){
        return frames.toArray(new InfoRectangle[0]);
    }

    public String toString(){
        return getAnimationName();
    }
}
