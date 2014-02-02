package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class Music extends NodeAdapter{
    private String musicIntro;
    private boolean interruptIntro;
    private String musicLoop;
    private String musicOutro;

    private static final String defaultSong = "blankmusic";

    public static enum MUSIC_TYPES{ INTRO,LOOP,OUTRO }

    public Music(String musicIntro, String musicLoop, boolean interruptIntro, String musicOutro){
        setMusic(MUSIC_TYPES.INTRO, musicIntro);
        setMusic(MUSIC_TYPES.LOOP, musicLoop);
        setMusic(MUSIC_TYPES.OUTRO, musicOutro);
    }

    public Music(){
        musicIntro = defaultSong;
        interruptIntro = false;
        musicLoop = defaultSong;
        musicOutro = defaultSong;
    }

    public String getMusic(MUSIC_TYPES type){
        switch(type){
            case INTRO:
                return musicIntro;
            case LOOP:
                return musicLoop;
            case OUTRO:
                return musicOutro;
            default:
                throw new IllegalArgumentException("Unknown enum");
        }
    }

    public void setMusic(MUSIC_TYPES type, String song){
        //TODO verfiy music

        switch(type){
            case INTRO:
                musicIntro = song;
                break;
            case LOOP:
                musicLoop = song;
                break;
            case OUTRO:
                musicOutro = song;
                break;
            default:
                throw new IllegalArgumentException("Unknown enum");
        }
    }


    public void setInterruptIntro(boolean interrupt){
        this.interruptIntro = interrupt;
    }
    public boolean getInterruptIntro(){
        return interruptIntro;
    }



    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    public int getChildCount() { return 4; }

    public int getIndex(TreeNode node) {


        return -1;
    }


    public Enumeration children() {
        return null;
    }

}
