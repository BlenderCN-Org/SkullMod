package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.dataStructures.DataStreamIn;
import skullMod.gfsEdit.processing.GFS;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.List;

class FileListTransferHandler extends TransferHandler {
    private JComboBox<File> guiElement;
    private JCheckBox integrateOrReplace,unpackImmediatlyCheckBox;
    private JButton unpackButton;

    public FileListTransferHandler(JComboBox<File> guiElement,JCheckBox integrateOrReplace,JCheckBox unpackImmediatlyCheckBox,JButton unpackButton) {
        this.guiElement = guiElement;
        this.integrateOrReplace = integrateOrReplace;
        this.unpackButton = unpackButton;
        this.unpackImmediatlyCheckBox = unpackImmediatlyCheckBox;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public boolean canImport(TransferSupport ts) {
        return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    public boolean importData(TransferSupport ts) {
        try {
            @SuppressWarnings("rawtypes")
            List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (data.size() < 1) { return false; }

            //Checkbox
            if(!integrateOrReplace.isSelected()){
                guiElement.removeAllItems();
            }

            for (Object item : data) {

                File file = (File) item;

                if(file.getName().endsWith(".gfs") && file.isFile()){
                    ComboBoxModel<File> internalList = guiElement.getModel();
                    int size = internalList.getSize();

                    boolean foundCurrentFile = false;

                    for(int i=0;i < size;i++){
                        File currentFile = internalList.getElementAt(i);
                        if(currentFile.getAbsolutePath().equals(file.getAbsolutePath())){
                           foundCurrentFile = true;
                        }
                    }

                    if(foundCurrentFile){
                        System.out.println("File already exists in list");
                    }else{
                        //Minimal file validation
                        if(file.length() > 32){ //Minimal size
                            //Check if magic string is present
                            DataStreamIn tempStream = new DataStreamIn(file.getAbsolutePath());
                            tempStream.s.skipBytes(12);

                            byte[] tempData = new byte[GFS.MAGIC_STRING.length()];
                            int returnValue = tempStream.s.read(tempData);
                            String str = new String(tempData, "UTF-8");

                            if(returnValue != -1 && str.equals(GFS.MAGIC_STRING)){
                                //After the file passed validation add it
                                System.out.println("File added: " + file.getAbsoluteFile());
                                guiElement.addItem(file);
                            }

                            tempStream.close();
                        }
                    }
                }else{
                    System.out.println("File not added: " + file.getAbsolutePath());
                }
            }
            if(unpackImmediatlyCheckBox.isSelected()){
                unpackButton.doClick();
            }
            return true;
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}