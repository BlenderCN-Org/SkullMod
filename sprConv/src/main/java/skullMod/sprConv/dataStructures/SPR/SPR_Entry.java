package skullMod.sprConv.dataStructures.SPR;

import java.io.Serializable;

//This class assumes the standard layout found in all spr files
public class SPR_Entry implements Serializable {
    //TODO add unsigned byte as class
    byte tile_x;
    byte tile_y;
    byte tile_u;
    byte tile_v;
}
