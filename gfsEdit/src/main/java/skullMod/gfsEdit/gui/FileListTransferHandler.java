package skullMod.gfsEdit.gui;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.List;

class FileListTransferHandler extends TransferHandler {
    private JComboBox<File> guiElement;
    private JCheckBox integrateOrReplace;

    public FileListTransferHandler(JComboBox<File> guiElement,JCheckBox integrateOrReplace) {
        this.guiElement = guiElement;
        this.integrateOrReplace = integrateOrReplace;
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
                        //TODO file validation
                        System.out.println("File added: " + file.getAbsoluteFile());
                        guiElement.addItem(file);
                    }
                }else{
                    System.out.println("File not added: " + file.getAbsolutePath());
                }
            }
            return true;

        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}