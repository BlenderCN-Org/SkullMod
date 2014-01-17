package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.dataStructures.GFSInternalFileReference;
import skullMod.gfsEdit.utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainWindow extends JFrame{
    public static final String APPLICATION  = "GFS edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "1.2";
    public static final String DATE         = "2013-12-26";
    public static final String GAME         = "Skullgirls (PC)";

    public static JFrame window; //Contains a reference to the top window (TODO beautify this)

    //UI elements
    private JCheckBox includeDirectoryNameCheckbox,dropTargetCheckbox, dropTargetCheckboxCreateDirectoryWithFilename,
            dropTargetCheckboxAddFiles,dropTargetCheckboxOverwriteFiles;
    private JComboBox<File> fileList;
    private JButton packButton, unpackButton, selectDirectoryButton;
    private SelectDirectoryListener directoryListener;


    private JTextField outputNameTextField;
    private JRadioButton alignment4kbyte,alignmentNone;
    private JLabel selectDirectoryLabel;
    //Data elements
    private FileListTransferHandler fileListTransferHandler;
    private JList<GFSInternalFileReference> currentFileList;

    public MainWindow(){
        super(APPLICATION + " " + VERSION); //Set title

        window = this;

        /**Set icon*/
        try {
            InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("appIcon.png");
            this.setIconImage(ImageIO.read(io));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Couldn't load application icon","Error",JOptionPane.ERROR_MESSAGE);
        }

        //Going with nimbus, looks better (fonts) and is the same on all platforms but requires JDK 7
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Set look of the application to mimic the OS GUI
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
                System.err.println("Setting look and feel failed"); //This should happen silently
            }
        }

        /**Get default font and make a bold/italic copy of it*/
        Font defaultFont = UIManager.getDefaults().getFont("Label.font");
        Font boldFont = defaultFont.deriveFont(Font.BOLD);
        Font italicFont = defaultFont.deriveFont(Font.ITALIC);

        /**Main pane and sub panes*/
        JTabbedPane mainPane = new JTabbedPane();

        JPanel packPanel = new JPanel();
        JPanel unpackPanel = new JPanel();
        JPanel aboutPanel = new JPanel();

        packPanel.setLayout(new BoxLayout(packPanel,BoxLayout.Y_AXIS));
        unpackPanel.setLayout(new BoxLayout(unpackPanel,BoxLayout.Y_AXIS));
        aboutPanel.setLayout(new BoxLayout(aboutPanel,BoxLayout.Y_AXIS));

        JLabel aboutLabel1 = new JLabel("<html>Made by " + AUTHOR + "<br><br>Current version: " + VERSION + " " + DATE + "<br>Game: " + GAME + "<html>");
        JLabel aboutLabel2 = new JLabel("<html><br>Newest version at: www.github.com/0xFAIL (Click here)<html>");
        JLabel aboutLabel3 = new JLabel("<html><br>For the license see LICENSE.txt (BSD 2-Clause License)<br><br><br>Icon by junglemoonicons.weebly.com/icons.html<br>Icon license is CC Attribution Non-Commerical Share Alike<html>");

        aboutLabel1.setFont(boldFont);
        aboutLabel3.setFont(italicFont);

        aboutPanel.add(aboutLabel1);
        aboutPanel.add(aboutLabel2);
        aboutPanel.add(aboutLabel3);

        unpackPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        packPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        mainPane.add("Unpack",unpackPanel);
        mainPane.add("Pack",packPanel);
        mainPane.add("About",aboutPanel);
        this.add(mainPane);

        //Pack label
        JLabel packLabel = new JLabel("Pack .gfs");
        packLabel.setFont(boldFont);

        packPanel.add(packLabel);
        packPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //Directory input
        JPanel selectDirectoryPanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));

        selectDirectoryButton = new JButton("Select directory");
        selectDirectoryLabel = new JLabel("No directory selected");

        Utility.setAlignmentTopLeft(selectDirectoryPanel);
        selectDirectoryPanel.add(selectDirectoryButton);
        selectDirectoryPanel.add(selectDirectoryLabel);

        JLabel selectDirectoryHelpLabel = new JLabel("Hover over folder name to see the full path");
        selectDirectoryHelpLabel.setFont(italicFont);

        packPanel.add(selectDirectoryPanel);
        packPanel.add(selectDirectoryHelpLabel);
        packPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //Output name
        JPanel outputNamePanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));
        outputNamePanel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        outputNameTextField = new JTextField(25);

        JLabel outputNameLabel = new JLabel("Output filename: ");

        outputNamePanel.add(outputNameLabel);
        outputNamePanel.add(outputNameTextField);

        JLabel outputNameHelpLabel = new JLabel("<html>Leave empty to use selected directory name<br>Allowed letters: a-z A-Z 0-9 _ - .<br>Spaces are not allowed <html>");
        outputNameHelpLabel.setFont(italicFont);

        packPanel.add(outputNamePanel);
        packPanel.add(outputNameHelpLabel);
        packPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //Include directory name
        JPanel includeDirectoryPanel = new FixedSizeJPanel();
        includeDirectoryPanel.setLayout(new BoxLayout(includeDirectoryPanel,BoxLayout.Y_AXIS));
        JLabel includeDirectoryHelpLabel = new JLabel("<html>Check if you select the \"temp\" directory directly<br>" +
                "Uncheck if there is no temp directory (just loose files)</html>");
        includeDirectoryHelpLabel.setFont(italicFont);

        //Checkbox if the directory name should be included in the inernal name inside the file
        includeDirectoryNameCheckbox = new JCheckBox("Include directory name");

        includeDirectoryPanel.add(includeDirectoryNameCheckbox);
        includeDirectoryPanel.add(includeDirectoryHelpLabel);

        packPanel.add(includeDirectoryPanel);
        packPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //Alignment radio buttons
        JPanel alignmentPanel = new FixedSizeJPanel();
        alignmentPanel.setLayout(new BoxLayout(alignmentPanel, BoxLayout.Y_AXIS));

        JPanel alignmentGroupPanel = new FixedSizeJPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup alignmentGroup = new ButtonGroup();

        alignmentNone = new JRadioButton("None");
        alignment4kbyte = new JRadioButton("4 kbyte");
        alignmentNone.setSelected(true);

        alignmentGroup.add(alignmentNone);
        alignmentGroup.add(alignment4kbyte);

        JLabel alignmentLabel = new JLabel("Alignment: ");

        Utility.setAlignmentTopLeft(alignmentGroupPanel);
        alignmentGroupPanel.add(alignmentLabel);
        alignmentGroupPanel.add(alignmentNone);
        alignmentGroupPanel.add(alignment4kbyte);

        JLabel alignmentHelpLabel = new JLabel("Alignment is only needed when packing 'characters-art-pt.gfs\"");
        alignmentHelpLabel.setFont(italicFont);

        alignmentPanel.add(alignmentGroupPanel);
        alignmentPanel.add(alignmentHelpLabel);

        packPanel.add(alignmentPanel);
        packPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //"Pack" button
        JPanel packButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Utility.setAlignmentTopLeft(packButtonPanel);
        packButton = new JButton("Pack");

        packButtonPanel.add(packButton);

        packPanel.add(packButtonPanel);

        //*****UNPACK PANEL*****
        //Label
        JLabel unpackLabel = new JLabel("Unpack .gfs");
        unpackLabel.setFont(boldFont);

        unpackPanel.add(unpackLabel);
        unpackPanel.add(Utility.getFixedSizeHorizontalJSeparator());
        //Drag and Drop zone (Win + Linux http://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path)
        JPanel dropTarget = new FixedSizeJPanel();

        dropTarget.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Drag and drop"));
        Utility.setAlignmentTopLeft(dropTarget);
        JLabel dropTargetLabel = new JLabel("Drag .gsf file(s) here");

        JPanel dropTargetCheckboxPanel = new FixedSizeJPanel();
        dropTargetCheckboxPanel.setLayout(new BoxLayout(dropTargetCheckboxPanel,BoxLayout.Y_AXIS));
        Utility.setAlignmentTopLeft(dropTargetCheckboxPanel);
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
        unpackPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //List of recieved files
        JPanel fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel,BoxLayout.Y_AXIS));
        Utility.setAlignmentTopLeft(fileListPanel);

        JLabel fileListLabel = new JLabel("Valid files");


        fileList = new JComboBox<>();
        fileList.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        fileList.setMaximumSize(new Dimension(fileList.getMaximumSize().width,fileList.getPreferredSize().height));

        JLabel fileInternalListLabel = new JLabel("Internal files");
        JPanel fileInternalListPanel = new JPanel(new BorderLayout());
        Utility.setAlignmentTopLeft(fileInternalListPanel);


        currentFileList = new JList<>();
        JScrollPane fileInternalListScrollPane = new JScrollPane(currentFileList);
        fileInternalListPanel.add(fileInternalListScrollPane);

        fileListPanel.add(fileListLabel);
        fileListPanel.add(fileList);
        fileListPanel.add(fileInternalListLabel);
        fileListPanel.add(fileInternalListPanel);

        unpackPanel.add(fileListPanel);
        unpackPanel.add(Utility.getFixedSizeHorizontalJSeparator());

        //Unpack button
        JPanel unpackButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Utility.setAlignmentTopLeft(unpackButtonPanel);

        unpackButton = new JButton("Unpack");

        unpackButtonPanel.add(unpackButton);

        unpackPanel.add(unpackButtonPanel);

        //*****Add menuListeners and handlers
        packButton.addActionListener(new PackActionListener(this,selectDirectoryLabel,outputNameTextField,includeDirectoryNameCheckbox,alignment4kbyte));

        unpackButton.addActionListener(new UnpackActionListener(fileList,dropTargetCheckboxCreateDirectoryWithFilename,dropTargetCheckboxOverwriteFiles,this));
        directoryListener = new SelectDirectoryListener(this,selectDirectoryLabel);
        selectDirectoryButton.addActionListener(directoryListener);

        fileListTransferHandler = new FileListTransferHandler(fileList,dropTargetCheckboxAddFiles,dropTargetCheckbox,unpackButton);
        dropTarget.setTransferHandler(fileListTransferHandler);


        fileList.addItemListener(new FileItemListener(currentFileList));

        aboutLabel2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aboutLabel2.addMouseListener(new MouseURLAdapter("http://github.com/0xFAIL"));


        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);
        this.setResizable(false);

        //Issue a warning if Java is not required version
        if (Utility.JAVA_VERSION < 1.7) {
            JOptionPane.showMessageDialog(this, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 1.7 is required for this application to work properly!\nDrag and drop might not work.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
