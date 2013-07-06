package skullMod.data;

/**
 * A reference of a file inside another file
 */
public class InternalFileReference {
    public final String path;
    public final String name;
    public final int length;
    public final int offset;
    public final String originalFileName;

    public InternalFileReference(int length, int offset){ this(null,null,length,offset,null); }
    public InternalFileReference(String name, int length, int offset){ this(null,name,length,offset,null); }
    public InternalFileReference(String name, int length, int offset, String originalFileName){ this(null,name,length,offset,originalFileName); }

    public InternalFileReference(String path, String name, int length, int offset, String originalFileName){
        if(length < 0){ throw new IllegalArgumentException("length is smaller than 0!"); }
        if(offset < 0){ throw new IllegalArgumentException("offset is smaller than 0!"); }
        this.path = path.replaceAll("/","\\")+ "\\";
        this.name = name;
        this.length = length;
        this.offset = offset;
        this.originalFileName = originalFileName;
    }
}
