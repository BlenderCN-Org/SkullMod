package skullMod.gfsEdit.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

import skullMod.gfsEdit.dataStructures.DataStreamIn;
import skullMod.gfsEdit.dataStructures.DataStreamOut;
import skullMod.gfsEdit.dataStructures.GFSExternalFileReference;
import skullMod.gfsEdit.dataStructures.GFSInternalFileReference;
import skullMod.gfsEdit.utility.Utility;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;

/**
 * GFS file format
 */
public class GFS {
    public static final String MAGIC_STRING = "Reverge Package File";
    public static final String KNOWN_VERSION_NUMBER = "1.1";

    //Assumes correct file is choosen, currently doesn't check for errors thouroughly
    public static GFSInternalFileReference[] getReferencesGFS(DataStreamIn data) throws IOException {

        int dataOffset = data.s.readInt();
        System.out.println("Offset to dataStructures portion of GFS: " + dataOffset);

        if(readPascalString(data).equals(MAGIC_STRING)){  //Read magic string
            System.out.println("Found Magic string");
        }else{
            throw new IllegalArgumentException("Magic string not found");
        }

        if(readPascalString(data).equals(KNOWN_VERSION_NUMBER)){
            System.out.println("Found fitting version number");
        }else{
            throw new IllegalArgumentException("Version number is unknown");
        }

        int nOfFiles = (int) data.s.readLong(); //Archive won't have that many files
        System.out.println("Number of files: " + nOfFiles);

        //Length of one entry:
        //Pascal String
        //Length of file (long)
        //Byte alignment (int)

        //Running offset
        int fileOffset = dataOffset;
        GFSInternalFileReference[] result = new GFSInternalFileReference[nOfFiles];

        for(int i = 0;i < nOfFiles;i++){
            File file = new File(readPascalString(data));
            int fileLength = (int) data.s.readLong();
            int alignment = data.s.readInt(); //Alignment in bytes

            if(i == 0){
                if(dataOffset % alignment != 0){
                    fileOffset += alignment - dataOffset % alignment;
                }
                System.out.println("Header ends at" + fileOffset);
            }

            //Create new InternalFileReference
            result[i] = new GFSInternalFileReference(file.getParent(),file.getName(),fileLength,fileOffset,null,alignment);
            fileOffset += fileLength; //The new fileOffset after the current file
            if(fileOffset % alignment != 0){
                fileOffset += alignment - fileOffset % alignment;
            }

        }

        if(fileOffset != data.fileLength){
            throw new IllegalArgumentException("The accumulated file length does not match with the real file length: Calc: " + fileOffset + " Actual Length: " + data.fileLength);
        }else{
            System.out.println("Calculated file length and actual file length match");
        }

        Utility.getMemoryUsage();
        return result;
    }
    private static String byteArray2String(byte[] data){
        StringBuilder sb = new StringBuilder(data.length);
        for (byte character : data) {
            if (character < 0) throw new IllegalArgumentException();
            sb.append((char) character);
        }
        return sb.toString();
    }

    private static String readPascalString(DataStreamIn data) throws IOException{
        int length = (int) data.s.readLong();
        return byteArray2String(readBytes(data,length));
    }

    private static byte[] readBytes(DataStreamIn data, int nOfBytes) throws IOException{
        byte[] result = new byte[nOfBytes];
        for(int i = 0;i < nOfBytes;i++){
            result[i] = data.s.readByte();
        }
        return result;
    }

    /**
     * @param gfsFile File to unpack
     * @param newFolder Create a new folder with the filename
     * @param overwrite Overwrite files
     */
    public static void unpack(File gfsFile, boolean newFolder, boolean overwrite) throws IOException{
        GFSInternalFileReference[] files = getInternalFileReferences(gfsFile);

        String outputDirectory;
        if(newFolder){
            outputDirectory = FilenameUtils.removeExtension(gfsFile.getAbsolutePath()) + File.separator;
        }else{
            outputDirectory = gfsFile.getParent() + File.separator;
        }

        int headerOffset = 0;
        long inputFileSize = 0;
        long offset = 0;

        DataStreamIn data = new DataStreamIn(gfsFile.getAbsolutePath());

        inputFileSize = data.fileLength;

        headerOffset = files[0].offset + files[0].offset % files[0].alignment; //Includes enforced alignment TODO headerOffset includes padding make two variables out of it
        offset += headerOffset;
        data.s.skipBytes(headerOffset); //Skip to alignment after the header if ther is any alignment

        for(int i = 0;i < files.length;i++){
            boolean noWriting = false;

            String basePath = outputDirectory;
            File currentDirectory = new File(basePath + files[i].path);
            String filePath = basePath + files[i].path + files[i].name;
            File currentFile = new File(filePath);

            if(currentFile.isDirectory()){
                throw new IllegalArgumentException("File can not be written, a folder is in place and won't be overwritten:\n" + currentFile.getAbsolutePath());
            }

            if(currentFile.exists() && !overwrite){
                noWriting = true; //Skip file if it exists
            }

            System.out.println(basePath + files[i].path + files[i].name);
            if(noWriting){
                data.s.skip(files[i].length);
                offset += files[i].length;

                long alignmentSkip = offset % files[i].alignment;
                data.s.skip(alignmentSkip);
                offset += alignmentSkip;
                System.out.println("Skipping file: " + files[i].name);
            }else{
                System.out.println("Writing file: " + files[i].name);
                currentDirectory.mkdirs();
                DataStreamOut dataOut = new DataStreamOut(basePath + files[i].path + files[i].name);

                for(int j = 0;j < files[i].length;j++){
                    dataOut.s.writeByte(data.s.readByte());
                }
                offset += files[i].length;

                long alignmentSkip = offset % files[i].alignment;
                data.s.skip(alignmentSkip);
                offset += alignmentSkip;

                System.out.println("Skipped " + alignmentSkip + " bytes (" + files[i].name + ")");

                dataOut.s.flush();
                dataOut.close();
            }
        }

        if(offset == inputFileSize){
            System.out.println("Everyting went fine");

        }else{
            throw new IllegalArgumentException("Data was extracted, but file length does not match with calculated file length");
        }
    }

    public static GFSInternalFileReference[] getInternalFileReferences(File gfsFile) throws FileNotFoundException{
        if(gfsFile == null){ throw new IllegalArgumentException("No file given"); }
        if(!gfsFile.exists()){ throw new FileNotFoundException("File does not exist"); }
        if(!gfsFile.isFile()){ throw new IllegalArgumentException("Not a file"); }

        String inputFile = FilenameUtils.normalize(gfsFile.getAbsolutePath());

        DataStreamIn data = new DataStreamIn(inputFile);

        GFSInternalFileReference[] files = null;

        try {
             files = GFS.getReferencesGFS(data);
        } catch (IOException e) {
        } finally {
            data.close();
            return files;
        }
    }

    public static void pack(File directory, String outputName, boolean includeDirectoryName, boolean align4k) {
        int alignment;
        if(align4k){
            alignment = 4096;
        }else{
            alignment = 1;
        }


        String outFilePath;

        if(outputName == null || outputName.equals("")){
            outFilePath = directory.getAbsolutePath() + ".gfs";
        }else{
            outFilePath = directory.getParentFile().getAbsolutePath() + File.separator + outputName;
        }

        LinkedList<GFSExternalFileReference> referencesList = new LinkedList<>();

        //TODO Make another walk method without the last argument
        walk(directory.getAbsolutePath(),referencesList,alignment,includeDirectoryName,directory.getAbsolutePath() + File.separator);
        GFSExternalFileReference[] references = referencesList.toArray(new GFSExternalFileReference[0]);

        byte[] header = { 0,0,0,0, 0,0,0,0x14, 0x52,0x65,0x76,0x65,0x72,0x67,0x65,0x20,0x50,0x61,0x63,0x6b,0x61,0x67,0x65,0x20,0x46,0x69,0x6c,0x65,
                0,0,0,0, 0,0,0,3,0x31,0x2e,0x31}; //Hardcoded header, if you're lazy and you know it clap your hands

        int offset = 4+header.length+8; //Offset for dataStructures, 51

        for(int i = 0;i < references.length;i++){
            offset += 8; //The string size long
            offset += references[i].internalPath.length();
            offset += 8; //The file size long
            offset += 4; //The alignment
        }
        try {
            DataStreamOut output = new DataStreamOut(outFilePath);
            output.s.writeInt(offset);
            output.writeBytes(header);
            output.s.writeLong(references.length);

            for(int i = 0;i < references.length;i++){
                output.s.writeLong(references[i].internalPath.length());
                output.s.writeBytes(references[i].internalPath);
                output.s.writeLong(references[i].lengthAndPadding);
                output.s.writeInt(alignment);
            }

            if(offset % alignment != 0){ output.writeBytes(new byte[alignment - offset % alignment]); }

            for(int i = 0;i < references.length;i++){
                DataStreamIn input = new DataStreamIn(references[i].absolutePath);
                for(int j = 0;j < references[i].length;j++){
                    output.s.writeByte(input.s.readByte());
                }
                if(references[i].padding != 0){ output.writeBytes(new byte[references[i].padding]);} //Padd stuff
            }
            output.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"A file previously in the list couldn't be found!","Error",JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Generic input/output exception","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    //http://stackoverflow.com/questions/2056221/recursively-list-files-in-java
    public static void walk( String basePath,LinkedList<GFSExternalFileReference> references,int alignment,boolean addBaseDirectory, String originalPath) {
        File root = new File( basePath );

        File[] list = root.listFiles();

        if (list == null){
            return;
        }

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(),references,alignment,addBaseDirectory,originalPath);
            }
            else {
                String absPath = f.getAbsolutePath();
                String internalPath = absPath.substring(originalPath.length(),absPath.length()).replaceAll("\\\\","/");

                if(addBaseDirectory){
                    internalPath = new File(originalPath).getName() + '\\' + internalPath;
                }

                references.add(new GFSExternalFileReference(absPath,internalPath,f.getName(),f.length(),alignment));
            }
        }
    }

    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        try (RandomAccessFile f = new RandomAccessFile(file, "r")) {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return dataStructures
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
    }
}
