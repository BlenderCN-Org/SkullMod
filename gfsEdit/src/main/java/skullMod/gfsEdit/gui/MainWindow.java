package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.Old;
import skullMod.gfsEdit.dataStructures.GFSInternalFileReference;
import skullMod.gfsEdit.processing.GFS;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainWindow extends JFrame{
    public static final String APPLICATION  = "GFS edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.6";
    public static final String DATE         = "2013-08-10";

    public static final String GAME         = "Skullgirls PC";

    //UI elements
    private JCheckBox includeDirectoryNameCheckbox,dropTargetCheckbox, dropTargetCheckboxCreateDirectoryWithFilename,
            dropTargetCheckboxAddFiles,dropTargetCheckboxOverwriteFiles;
    private JComboBox<File> fileList;
    private JButton packButton, unpackButton, selectDirectoryButton;
    private SelectDirectoryListener directoryListener;

    //Data elements
    private FileListTransferHandler fileListTransferHandler;
    private JList<GFSInternalFileReference> currentFileList;

    public MainWindow(){
        super(APPLICATION + " " + VERSION);

        //Set icon
        try {
            InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("appIcon.png");
            this.setIconImage(ImageIO.read(io));
        } catch (IOException e) {
            System.err.println("Couldn't load application icon");
        }

        /*Set look of the application to mimic the OS GUI*/
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Setting look and feel failed");
        }

        //Main pane and sub panes
        JTabbedPane mainPane = new JTabbedPane();

        JPanel packPanel = new JPanel();
        JPanel unpackPanel = new JPanel();
        JPanel aboutPanel = new JPanel();

        packPanel.setLayout(new BoxLayout(packPanel,BoxLayout.Y_AXIS));
        unpackPanel.setLayout(new BoxLayout(unpackPanel,BoxLayout.Y_AXIS));
        aboutPanel.setLayout(new BoxLayout(aboutPanel,BoxLayout.Y_AXIS));

        aboutPanel.add(new JLabel("<html>Made by " + AUTHOR + "<br><br>Current version: " + VERSION + " " + DATE +
                "<br>Newest version at: www.github.com/0xFAIL<br><br>Tested on Win 7 64-bit with 32-bit Java<br><br>" +
                "Game: " + GAME + "<br><br><br>Icon by junglemoonicons.weebly.com/icons.html<br>Icon license is CC Attribution Non-Commerical Share Alike </html>"));

        unpackPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        packPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        mainPane.add("Unpack",unpackPanel);
        mainPane.add("Pack (in development)",packPanel);
        mainPane.add("About",aboutPanel);
        this.add(mainPane);

        //Get default font and make a bold/italic copy of it
        Font defaultFont = UIManager.getDefaults().getFont("Label.font");
        Font boldFont = new Font(defaultFont.getFontName(),Font.BOLD,defaultFont.getSize());
        Font italicFont = new Font(defaultFont.getFontName(),Font.ITALIC,defaultFont.getSize());

        //Pack label
        JLabel packLabel = new JLabel("Pack .gfs");
        packLabel.setFont(boldFont);

        packPanel.add(packLabel);
        packPanel.add(getFixedSizeHorizontalJSeparator());

        //Directory input
        JPanel selectDirectoryPanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));

        selectDirectoryButton = new JButton("Select directory");
        JLabel selectDirectoryLabel = new JLabel("No directory selected");

        setAlignmentTopLeft(selectDirectoryPanel);
        selectDirectoryPanel.add(selectDirectoryButton);
        selectDirectoryPanel.add(selectDirectoryLabel);

        JLabel selectDirectoryHelpLabel = new JLabel("Hover over folder name to see the full path");
        selectDirectoryHelpLabel.setFont(italicFont);

        packPanel.add(selectDirectoryPanel);
        packPanel.add(selectDirectoryHelpLabel);
        packPanel.add(getFixedSizeHorizontalJSeparator());

        //Output name
        JPanel outputNamePanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));
        outputNamePanel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JTextField outputNameTextField = new JTextField(25);

        JLabel outputNameLabel = new JLabel("Output filename: ");

        outputNamePanel.add(outputNameLabel);
        outputNamePanel.add(outputNameTextField);

        JLabel outputNameHelpLabel = new JLabel("Leave empty to use selected directory name");
        outputNameHelpLabel.setFont(italicFont);

        packPanel.add(outputNamePanel);
        packPanel.add(outputNameHelpLabel);
        packPanel.add(getFixedSizeHorizontalJSeparator());

        //Include directory name
        JPanel includeDirectoryPanel = new FixedSizeJPanel();
        includeDirectoryPanel.setLayout(new BoxLayout(includeDirectoryPanel,BoxLayout.Y_AXIS));
        JLabel includeDirectoryHelpLabel = new JLabel("<html>Check if you select the \"temp\" directory directly<br>" +
                "Uncheck if there is no temp directory (just loose files)</html>");
        includeDirectoryHelpLabel.setFont(italicFont);

        //FILL
        includeDirectoryNameCheckbox = new JCheckBox("Include directory name");

        includeDirectoryPanel.add(includeDirectoryNameCheckbox);
        includeDirectoryPanel.add(includeDirectoryHelpLabel);

        packPanel.add(includeDirectoryPanel);
        packPanel.add(getFixedSizeHorizontalJSeparator());

        //Alignment radio buttons
        JPanel alignmentPanel = new FixedSizeJPanel();
        alignmentPanel.setLayout(new BoxLayout(alignmentPanel, BoxLayout.Y_AXIS));

        JPanel alignmentGroupPanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup alignmentGroup = new ButtonGroup();

        JRadioButton alignmentNone = new JRadioButton("None");
        JRadioButton alignment4kbyte = new JRadioButton("4 kbyte");
        alignmentNone.setSelected(true);

        alignmentGroup.add(alignmentNone);
        alignmentGroup.add(alignment4kbyte);

        JLabel alignmentLabel = new JLabel("Alignment: ");

        setAlignmentTopLeft(alignmentGroupPanel);
        alignmentGroupPanel.add(alignmentLabel);
        alignmentGroupPanel.add(alignmentNone);
        alignmentGroupPanel.add(alignment4kbyte);

        JLabel alignmentHelpLabel = new JLabel("Alignment is only needed when packing \"characters-art-pt.gfs\"");
        alignmentHelpLabel.setFont(italicFont);

        alignmentPanel.add(alignmentGroupPanel);
        alignmentPanel.add(alignmentHelpLabel);

        packPanel.add(alignmentPanel);
        packPanel.add(getFixedSizeHorizontalJSeparator());

        //"Pack" button
        JPanel packButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setAlignmentTopLeft(packButtonPanel);
        packButton = new JButton("Pack .gfs");

        packButtonPanel.add(packButton);

        packPanel.add(packButtonPanel);

        //*****UNPACK PANEL*****
        //Label
        JLabel unpackLabel = new JLabel("Unpack .gfs");
        unpackLabel.setFont(boldFont);

        unpackPanel.add(unpackLabel);
        unpackPanel.add(getFixedSizeHorizontalJSeparator());
        //Drag and Drop zone (Win + Linux http://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path)
        JPanel dropTarget = new FixedSizeJPanel();

        dropTarget.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Drag and drop"));
        setAlignmentTopLeft(dropTarget);
        JLabel dropTargetLabel = new JLabel("Drag .gsf file(s) here");

        JPanel dropTargetCheckboxPanel = new FixedSizeJPanel();
        dropTargetCheckboxPanel.setLayout(new BoxLayout(dropTargetCheckboxPanel,BoxLayout.Y_AXIS));
        setAlignmentTopLeft(dropTargetCheckboxPanel);
        //FILL
        dropTargetCheckbox = new JCheckBox("Unpack all files immediatly after dropping");
        dropTargetCheckboxCreateDirectoryWithFilename = new JCheckBox("Create directory with filename");
        dropTargetCheckboxAddFiles = new JCheckBox("Add files (uncheck to replace) after drag and drop");
        dropTargetCheckboxOverwriteFiles = new JCheckBox("Overwrite files");

        dropTargetCheckboxCreateDirectoryWithFilename.setSelected(true);
        dropTargetCheckboxOverwriteFiles.setSelected(true);

        dropTargetCheckboxPanel.add(dropTargetCheckbox);
        dropTargetCheckboxPanel.add(dropTargetCheckboxCreateDirectoryWithFilename);
        dropTargetCheckboxPanel.add(dropTargetCheckboxAddFiles);
        dropTargetCheckboxPanel.add(dropTargetCheckboxOverwriteFiles);

        dropTarget.add(dropTargetLabel);

        unpackPanel.add(dropTarget);
        unpackPanel.add(dropTargetCheckboxPanel);
        unpackPanel.add(getFixedSizeHorizontalJSeparator());

        //List of recieved files
        JPanel fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel,BoxLayout.Y_AXIS));
        setAlignmentTopLeft(fileListPanel);

        JLabel fileListLabel = new JLabel("Valid files");


        fileList = new JComboBox<>();
        fileList.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        fileList.setMaximumSize(new Dimension(fileList.getMaximumSize().width,fileList.getPreferredSize().height));

        JLabel fileInternalListLabel = new JLabel("Internal files");
        JPanel fileInternalListPanel = new JPanel(new BorderLayout());
        setAlignmentTopLeft(fileInternalListPanel);


        currentFileList = new JList<>();
        JScrollPane fileInternalListScrollPane = new JScrollPane(currentFileList);
        fileInternalListPanel.add(fileInternalListScrollPane);

        fileListPanel.add(fileListLabel);
        fileListPanel.add(fileList);
        fileListPanel.add(fileInternalListLabel);
        fileListPanel.add(fileInternalListPanel);

        unpackPanel.add(fileListPanel);
        unpackPanel.add(getFixedSizeHorizontalJSeparator());

        //Unpack button
        JPanel unpackButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setAlignmentTopLeft(unpackButtonPanel);

        unpackButton = new JButton("Unpack .gfs file(s)");

        unpackButtonPanel.add(unpackButton);

        unpackPanel.add(unpackButtonPanel);

        //*****Add listeners and handlers
        packButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"PACK");
            }
        });

        unpackButton.addActionListener(new UnpackActionListener(fileList,dropTargetCheckboxCreateDirectoryWithFilename,dropTargetCheckboxOverwriteFiles,this));
        directoryListener = new SelectDirectoryListener(this,selectDirectoryLabel);
        selectDirectoryButton.addActionListener(directoryListener);

        fileListTransferHandler = new FileListTransferHandler(fileList,dropTargetCheckboxAddFiles,dropTargetCheckbox,unpackButton);
        dropTarget.setTransferHandler(fileListTransferHandler);


        fileList.addItemListener(new FileItemListener(currentFileList));

        //*****DISABLE non functional stuff
        disableAllComponents(packPanel);

        //*****Misc stuff and layout of the JFrame*****
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);
        this.setResizable(false);
    }

    private void disableAllComponents(JComponent component) {
        Component[] com = component.getComponents();

        for (int a = 0; a < com.length; a++) {
            try{
                disableAllComponents((JComponent) com[a]);
            }catch(ClassCastException cce){}
            com[a].setEnabled(false);
        }
    }

    public static void setAlignmentTopLeft(JComponent c){
        c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        c.setAlignmentY(JComponent.TOP_ALIGNMENT);
    }
    public static void setPreferredHeightToMaxHeight(Component c){
        c.setMaximumSize(new Dimension(c.getMaximumSize().width,c.getPreferredSize().height));
    }
    public static JSeparator getFixedSizeHorizontalJSeparator(){
        JSeparator result = new JSeparator(JSeparator.HORIZONTAL);
        setAlignmentTopLeft(result);
        setPreferredHeightToMaxHeight(result);
        return result;
    }
}
