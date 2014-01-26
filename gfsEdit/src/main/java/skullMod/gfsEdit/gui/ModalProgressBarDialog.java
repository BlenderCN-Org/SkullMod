package skullMod.gfsEdit.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModalProgressBarDialog extends JDialog{
    private final JLabel progressLabel;
    private final JProgressBar progressBar;
    private final JButton cancelButton;

    private final SwingWorker<Object, String> thread;

    public ModalProgressBarDialog(Frame owner, String title, SwingWorker<Object, String> thread){
        super(owner, title, true);

        //Set attributes
        this.thread = thread;

        //Config Dialog
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);
        addWindowListener(new CancelWindowAdapter());

        //Add components
        this.progressLabel = new JLabel("TEXT");
        this.progressBar = new JProgressBar();
        this.cancelButton = new JButton("Cancel");

        this.cancelButton.addActionListener(new CancelActionListener());

        this.cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(progressBar);
        add(progressLabel);
        add(cancelButton);

        thread.addPropertyChangeListener(new ProgressPropertyChangeListener());

        thread.execute();

        pack();
        setVisible(true);


    }

    private class ProgressPropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent evt) {


            String name = evt.getPropertyName();
            if (name.equals("progress")) {
                int progress = (int) evt.getNewValue();
                progressBar.setValue(progress);
                repaint();
            } else if (name.equals("state")) {
                SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                switch (state) {
                    case DONE:
                        ModalProgressBarDialog.this.setVisible(false);
                        ModalProgressBarDialog.this.dispose();
                        break;
                }
            } else if(name.equals("currentTask")){
                String currentTask = (String) evt.getNewValue();
                progressLabel.setText(currentTask);
            } else{
                System.out.println("Different event: " + name);
            }
        }
    }

    private class CancelWindowAdapter extends WindowAdapter{
        public void windowClosing(WindowEvent we){
            if(thread != null){ thread.cancel(true); }
            ModalProgressBarDialog.this.setVisible(false);
            ModalProgressBarDialog.this.dispose();
        }
    }

    private class CancelActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(thread != null){ thread.cancel(true); }
            ModalProgressBarDialog.this.setVisible(false);
            ModalProgressBarDialog.this.dispose();
        }
    }
}
