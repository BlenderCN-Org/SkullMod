package skullMod.sprConv.dataStructures.SPR;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public final class ProcessSPR {
    /** Contains static methods only (Utility class) */
    private ProcessSPR(){}

    /** Extension of a sprite file */
    public final static String sprExtension = ".spr.msb";

    /**
     * Read a spr/dds file, output all animations
     * Contains no unknown metadata of the spr file
     *
     * @param sprFilename
     * @return All animations with each of their individual frames
     * @throws IllegalArgumentException If there are any problems processing the spr or dds file
     */
    public static HashMap<String, BufferedImage[]> convertSPR(String sprFilename) throws IllegalArgumentException{
        //TODO CHECK IF IT ENDS WITH THE REQUIRED ENDING
        String ddsFilename = sprFilename.substring(0, sprFilename.length() - sprExtension.length()) + ".dds";
        System.out.println("FileName: " + ddsFilename);

        File file = new File(ddsFilename);

        BufferedImage image = null;
        try{
            image = ImageIO.read(file);
            System.out.println(image);
            if(image == null){ throw new RuntimeException("Could not read file: " + file.getAbsolutePath()); }

        }catch(FileNotFoundException fnfe){
            System.out.println("File not found");
        }catch(IOException ioe){
            throw new IllegalArgumentException(ioe);
        }

        DataStreamIn dsi = null;

        SPR_File spr_file = null;
        try{
            dsi = new DataStreamIn(sprFilename);
            spr_file = new SPR_File(dsi.s);
        }catch(FileNotFoundException fnfe){
            throw new IllegalArgumentException(fnfe);
        }catch(IOException ioe){
            throw new IllegalArgumentException(ioe);
        }finally{
            if(dsi != null){ dsi.close(); }
        }
        //Output for animations
        HashMap<String, BufferedImage[]> animations = new HashMap<>();

        SPR_Entry[] entries = spr_file.entries;

        int blockWidth = (int) spr_file.blockWidth; //Downcast
        int blockHeight = (int) spr_file.blockHeight; //Downcast

        //Go through each animation
        for(int currentAnimationNumber = 0;currentAnimationNumber < spr_file.animations.length;currentAnimationNumber++){
            SPR_Animation currentAnimation = spr_file.animations[currentAnimationNumber];
            System.out.println("Animation name: " + currentAnimation.animationName);
            int nOfFrames = currentAnimation.nOfFrames;
            int frameOffset = currentAnimation.frameOffset;

            //Create store for frames of animation
            BufferedImage[] animationImages = new BufferedImage[nOfFrames];

            //Go through each frame of the current animation
            for(int frameNumber = 0;frameNumber < nOfFrames;frameNumber++){
                int currentFrameNumber = frameNumber + frameOffset;
                SPR_Frame currentFrame = spr_file.frames[currentFrameNumber];

                int blockOffset = currentFrame.blockOffset;
                int nOfBlocks = currentFrame.nOfBlocks;
                //Determine size by evaluating maximum x/y coordinate
                Dimension imageSize = getMaxBounds(entries, blockOffset, nOfBlocks, blockWidth, blockHeight);

                //Create image
                BufferedImage frameImage = new BufferedImage((int) imageSize.getWidth(), (int) imageSize.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                //Descramble blocks
                for(int blockNumber = blockOffset;blockNumber < blockOffset + nOfBlocks;blockNumber++){
                    copyRect(image,frameImage,entries[blockNumber].tile_u & 0xFF, entries[blockNumber].tile_v & 0xFF, entries[blockNumber].tile_x & 0xFF, entries[blockNumber].tile_y & 0xFF, blockWidth, blockHeight);
                }
                //Add frame to animation
                animationImages[frameNumber] = frameImage;
            }
            //Put animation into result HashMap
            animations.put(currentAnimation.animationName, animationImages);
        }
        return animations;
    }

    /**
     * Get the maximum bounds of the given frame
     * Inside the spr file there were no given dimensions so we have to calculate them
     *
     * @param entries Contains all SPR_Entry references
     * @param blockOffset blockOffset of entries
     * @param nOfBlocks Number of blocks for this frame
     * @param blockWidth Width of a single block
     * @param blockHeight Height of a single block
     * @return
     */
    private static Dimension getMaxBounds(SPR_Entry[] entries, int blockOffset, int nOfBlocks, int blockWidth, int blockHeight) {
        int xMax = 0, yMax = 0;
        for(int i = blockOffset;i < blockOffset+nOfBlocks;i++){
            //Tile numbers start from 0, to make the start from one 1 is added
            xMax = Math.max(xMax, (entries[i].tile_x+1)*blockWidth);
            yMax = Math.max(yMax, (entries[i].tile_y+1)*blockHeight);
        }
        return new Dimension(xMax,yMax);
    }

    /**
     * Copy given block from the source image to the target image
     *
     * @param source Source image
     * @param target Target image
     * @param xSource X block coordinate for source image
     * @param ySource Y block coordinate for source image
     * @param xDest X block coordinates for target image
     * @param yDest Y block coordinates for target image
     * @param blockWidth Width of a single block
     * @param blockHeight Height of a single block
     */
    public static void copyRect(BufferedImage source, BufferedImage target, int xSource, int ySource, int xDest, int yDest, int blockWidth, int blockHeight){
        System.out.println(xSource + " " + ySource + " " + xDest + " " + yDest + " ");
        for(int y = 0;y < blockHeight;y++){
            for(int x = 0;x < blockWidth;x++){

                target.setRGB(xDest*blockWidth + x, yDest*blockHeight + y,source.getRGB(xSource*blockWidth + x,ySource*blockHeight + y));
            }
        }
    }
}
