package skullMod.gfsEdit.data;

/**
 * Reference to an external file with all metadata already attached, does not account for changes
 * DON'T change the files after the creation of an object of this class!
 * Alignment currently only exists with 1 byte and 4k byte boundaries in the files
 */
public class GFSExternalFileReference {
    public final String absolutePath;
    public final String internalPath;
    public final String name;
    public final long length;
    public final int alignment;
    public final int padding;
    public final int lengthAndPadding;

    public GFSExternalFileReference(String absolutePath, String internalPath, String name, long length, int alignment){
        if(length < 0){ throw new IllegalArgumentException("length is smaller than 0!"); }
        this.absolutePath = absolutePath;
        this.internalPath = internalPath;
        this.name = name;
        this.length = length;
        this.alignment = alignment;

        if(length % alignment != 0){
            this.padding = (int) (alignment - length % alignment);
        }else{
            this.padding = 0;
        }

        this.lengthAndPadding = (int) length + padding;

        System.out.println(internalPath);
    }
}
