package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.LVL.LVL_File;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Music extends NodeAdapter{
    private LeafContentNode<String> musicIntro;
    private LeafContentNode<Boolean> interruptIntro;
    private LeafContentNode<String> musicLoop;
    private LeafContentNode<String> musicOutro;

    private static final String defaultSong = "blankmusic";

    public static enum MUSIC_TYPES{ INTRO,LOOP,OUTRO }

    public Music(TreeNode parent, LVL_File lvl){
        super(parent);

        //TODO unify
        this.musicIntro = new LeafContentNode<>(this,"Music intro", defaultSong);
        this.interruptIntro = new LeafContentNode<>(this,"Interrupt intro", false);
        this.musicLoop = new LeafContentNode<>(this,"Music loop", defaultSong);
        this.musicOutro = new LeafContentNode<>(this,"Music outro", defaultSong);


        setMusic(MUSIC_TYPES.INTRO, lvl.musicIntro);
        setInterruptIntro(lvl.musicInterruptIntro);
        setMusic(MUSIC_TYPES.LOOP, lvl.musicLoop);
        setMusic(MUSIC_TYPES.OUTRO, lvl.musicOutro);
    }

    public Music(TreeNode parent, String musicIntro, String musicLoop, boolean interruptIntro, String musicOutro){
        super(parent);

        //TODO unify with other constructor
        this.musicIntro = new LeafContentNode<>(this,"Music intro", defaultSong);
        this.interruptIntro = new LeafContentNode<>(this,"Interrupt intro", false);
        this.musicLoop = new LeafContentNode<>(this,"Music loop", defaultSong);
        this.musicOutro = new LeafContentNode<>(this,"Music outro", defaultSong);


        setMusic(MUSIC_TYPES.INTRO, musicIntro);
        setInterruptIntro(interruptIntro);
        setMusic(MUSIC_TYPES.LOOP, musicLoop);
        setMusic(MUSIC_TYPES.OUTRO, musicOutro);
    }

    public Music(TreeNode parent){
        super(parent);
        musicIntro = new LeafContentNode<>(this,"Music intro", defaultSong);
        interruptIntro = new LeafContentNode<>(this,"Interrupt intro", false);
        musicLoop = new LeafContentNode<>(this,"Music loop", defaultSong);
        musicOutro = new LeafContentNode<>(this,"Music outro", defaultSong);
    }

    public String getMusic(MUSIC_TYPES type){
        switch(type){
            case INTRO:
                return musicIntro.getContent();
            case LOOP:
                return musicLoop.getContent();
            case OUTRO:
                return musicOutro.getContent();
            default:
                throw new IllegalArgumentException("Unknown enum"); //TODO can this even happen?
        }
    }

    public void setMusic(MUSIC_TYPES type, String song){
        //TODO verfiy music

        switch(type){
            case INTRO:
                musicIntro.setContent(song);
                break;
            case LOOP:
                musicLoop.setContent(song);
                break;
            case OUTRO:
                musicOutro.setContent(song);
                break;
            default:
                throw new IllegalArgumentException("Unknown enum");
        }
    }

    public void setInterruptIntro(boolean interrupt){
        interruptIntro.setContent(interrupt);
    }
    public boolean getInterruptIntro(){
        return interruptIntro.getContent();
    }

    public int getChildCount() { return 4; }

    public Enumeration<TreeNode> children() {
        ArrayList<TreeNode> list = new ArrayList<>(getChildCount());

        list.add(musicIntro);
        list.add(interruptIntro);
        list.add(musicLoop);
        list.add(musicOutro);

        return Collections.enumeration(list);
    }

    public String toString(){
        return "Music";
    }
}
