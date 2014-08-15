package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.utility.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ModalProgressBarDialog extends JDialog{
    private final JProgressBar progressBar;
    private final JLabel progressLabel;
    private final JLabel currentTaskLabel;
    private final JButton cancelButton;

    private final SwingWorker<Object, String> thread;

    //TODO is the progress worker param the best way?
    public ModalProgressBarDialog(Frame owner, String title, ProgressWorker thread){
        super(owner, title, true);

        //Set attributes
        this.thread = thread;

        //Add components
        this.currentTaskLabel = new JLabel("Loading...");
        this.progressLabel = new JLabel("Loading...");
        this.progressBar = new JProgressBar();
        this.cancelButton = new JButton("Cancel");

        this.cancelButton.addActionListener(new CancelActionListener());

        this.cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.currentTaskLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(new Utility.BorderJPanel(progressBar, 10, 10, 5, 10));
        add(new Utility.BorderJPanel(progressLabel, 5, 10, 5, 10));
        add(new Utility.BorderJPanel(currentTaskLabel, 5, 10 , 5, 10));
        add(new Utility.BorderJPanel(cancelButton, 5, 10, 10, 10));


        thread.addPropertyChangeListener(new ProgressPropertyChangeListener());

        //TODO why here?, does not work after it being visible
        thread.execute();

        this.setMinimumSize(new Dimension(200, 100));

        //Config Dialog
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);
        addWindowListener(new CancelWindowAdapter());

        this.setLocationRelativeTo(owner);

        this.pack();
        this.setVisible(true);
    }

    //TODO throw exception if maxProgress is not set before any other property
    private class ProgressPropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            switch (name) {
                case "progress":
                    int progress = (int) evt.getNewValue();
                    progressLabel.setText(progress + " of " + progressBar.getMaximum());
                    progressBar.setValue(progress);
                    repaint();
                    break;
                case "state":
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    switch (state) {
                        case DONE:
                            ModalProgressBarDialog.this.setVisible(false);
                            ModalProgressBarDialog.this.dispose();
                            break;
                    }
                    break;
                case "currentTask":
                    String currentTask = (String) evt.getNewValue();
                    currentTaskLabel.setText(currentTask);
                    break;
                case "setMaxProgress":
                    int maxProgress = (int) evt.getNewValue();
                    progressBar.setMaximum(maxProgress);
                    break;
                case "setIndeterminate":
                    progressBar.setIndeterminate(true);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event");
            }
        }
    }

    private class CancelWindowAdapter extends WindowAdapter{
        public void windowClosing(WindowEvent we){
            if(thread != null){ thread.cancel(false); }
            ModalProgressBarDialog.this.setVisible(false);
            ModalProgressBarDialog.this.dispose();
        }
    }

    private class CancelActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(thread != null){ thread.cancel(false); }
            ModalProgressBarDialog.this.setVisible(false);
            ModalProgressBarDialog.this.dispose();
        }
    }

    public static abstract class ProgressWorker extends SwingWorker<Object, String>{
        abstract protected Object doInBackground();

        //This runs on the EDT thread
        protected void process(List<String> chunks) {
            for(String currentTask : chunks){
                firePropertyChange("currentTask", "", currentTask);
            }
        }
    }
}
