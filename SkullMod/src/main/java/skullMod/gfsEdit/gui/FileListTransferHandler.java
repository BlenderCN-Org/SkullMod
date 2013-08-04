package skullMod.gfsEdit.gui;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

class FileListTransferHandler extends TransferHandler {
    private JComboBox<File> guiElement;

    public FileListTransferHandler(JComboBox<File> guiElement,JCheckBox replaceOrIntegrate) {
        this.guiElement = guiElement;
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

            for (Object item : data) {

                File file = (File) item;

                if(file.getName().endsWith(".gfs")){
                    System.out.println("File added: " + file.getAbsoluteFile());
                    guiElement.addItem(file);
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