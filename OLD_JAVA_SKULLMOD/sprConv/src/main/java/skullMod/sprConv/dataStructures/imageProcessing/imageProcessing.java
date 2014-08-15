package skullMod.sprConv.dataStructures.imageProcessing;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class ImageProcessing {
    private ImageProcessing(){}

    public static HashMap<String, BufferedImage> splitImage(File file){
        HashMap<String, BufferedImage> result = new HashMap<>();

        BufferedImage image = null;
        try{
            image = ImageIO.read(file);
            System.out.println(image);
            if(image == null){ throw new RuntimeException("Could not read file: " + file.getAbsolutePath()); }

        }catch(FileNotFoundException fnfe){
            throw new IllegalArgumentException(fnfe);
        }catch(IOException ioe){
            throw new IllegalArgumentException(ioe);
        }

        BufferedImage rImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        BufferedImage gImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        BufferedImage bImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        BufferedImage aImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);


        //FIXME This takes for ages
        for(int y=0;y < image.getHeight();y++){
            for(int x=0;x < image.getWidth();x++){

                int transparency = image.getTransparency();

                //abgr
                int color = image.getRGB(x,y);

                int rComplete = ((color & 0x000000ff) << 8) | ((color & 0x000000ff) << 16) | ((color & 0x000000ff)) | ((color & 0xFF000000));
                int gComplete = ((color & 0x0000ff00) << 8) | ((color & 0x0000ff00) >>  8) | ((color & 0x0000ff00)) | ((color & 0xFF000000));
                int bComplete = ((color & 0x00ff0000) >> 8) | ((color & 0x00ff0000) >> 16) | ((color & 0x00ff0000)) | ((color & 0xFF000000));
                int aComplete = 0xFF000000 | (transparency >> 8) | (transparency >> 16) | (transparency >> 24);


                rImage.setRGB(x,y,rComplete);
                gImage.setRGB(x,y,gComplete);
                bImage.setRGB(x,y,bComplete);
                aImage.setRGB(x,y,aComplete);
            }
        }

        result.put("r", rImage);
        result.put("g", gImage);
        result.put("b", bImage);
        result.put("a", aImage);

        return result;
    }
}
