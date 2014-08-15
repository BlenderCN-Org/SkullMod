package skullMod.lvlEdit.openGL;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.GLProfile;
import java.io.File;
import java.io.IOException;

public class DDS_Texture {
    public final Texture texture;
    public DDS_Texture(GLProfile glp, String pathToDDS){

        TextureData textureData = null;
        try {
            textureData = TextureIO.newTextureData(glp, new File(pathToDDS), false, TextureIO.DDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        texture = TextureIO.newTexture(textureData);

    }
}
