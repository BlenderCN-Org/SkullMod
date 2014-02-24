package skullMod.lvlEdit.gui.menuListeners;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Call URL if a the mouse is clicked
 */
public class MouseURLAdapter extends MouseAdapter{
    private final String URL;
    public MouseURLAdapter(String URL){
        if(URL == null){ throw new IllegalArgumentException("Given URL is null"); }
        this.URL = URL;
    }
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() > 0) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    URI uri = new URI(URL);
                    desktop.browse(uri);
                } catch (IOException | URISyntaxException uriException) {
                    //Might happen, just ignore it
                }
            }
        }
    }
}
