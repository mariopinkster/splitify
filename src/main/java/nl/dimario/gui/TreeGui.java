package nl.dimario.gui;
/**
 * This file is part of Splitify
 * <p>
 * Splitify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p>
 * Splitify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.dimario.model.Analyzer;
import nl.dimario.model.FileWriter;
import nl.dimario.model.Mapper;
import nl.dimario.model.Renderer;
import nl.dimario.model.SplitInfo;

public class TreeGui extends JFrame implements ItemListener {

    private String fullFileName;

    JPanel pnlLeft;
    JPanel pnlRight;

    JMenuBar menuBar;
    private JTree tree;
    JTextPane preview;
    private JLabel inputFileName;
    private JCheckBox separateChildren;
    private JCheckBox defcon;
    private JTextField dirsegment;
    private JLabel outputFileName;
    private BasicLabelUI cutoffLeft;

    private JButton buttonLoad;
    private AbstractAction fileLoad;
    private AbstractAction fileSaveOne;
    private AbstractAction fileSaveAll;
    private AbstractAction fileQuit;
    private AbstractAction settingsWizard;
    private Renderer renderer;
    private FileWriter fileWriter;
    private SplitInfo rootSplitInfo;

    private boolean updatingData = false;

    public TreeGui() {
        this.renderer = new Renderer();
        this.fileWriter = new FileWriter("./splitify-output");

        // this trick cuts off the label text at the left side if it is too long.
        this.cutoffLeft = new BasicLabelUI() {
            @Override
            protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR) {
                return StringUtils.reverse(super.layoutCL(label, fontMetrics, StringUtils.reverse(text), icon, viewR, iconR, textR));
            }
        };
    }

    private void makeActions() {

        final TreeGui treeGui = this;

        fileLoad = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    treeGui.selectNewInput();
            }
        };

        fileSaveOne = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SplitInfo splitInfo = getCurrentSplitInfo();
                if( splitInfo == null) {
                    return;
                }
                try {
                    fileWriter.writeOne(splitInfo, treeGui.renderer);
                } catch (IOException x) {
                    preview.setText( "ERROR: " + x.getMessage());
                }
            }
        };

        fileSaveAll = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rootSplitInfo == null) {
                    return;
                }
                try {
                    fileWriter.writeAll(rootSplitInfo, treeGui.renderer);
                } catch (IOException x) {
                    preview.setText("ERROR: " + x.getMessage());
                }
            }
        };

        fileQuit = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeGui.processWindowEvent(
                        new WindowEvent( treeGui, WindowEvent.WINDOW_CLOSING));
            }
        };

        settingsWizard  = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( treeGui.rootSplitInfo == null) {
                    return;
                }
                SettingsWizard dialog = new SettingsWizard( treeGui.rootSplitInfo);
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLocationRelativeTo( treeGui);
                dialog.setVisible(true);
            }
        };
    }

    private void makeMenu() {
        menuBar = new JMenuBar();
        JMenuItem item;

        JMenu fileMenu = new JMenu("File");

        item = new JMenuItem( "Load");
        item.addActionListener( fileLoad);
        fileMenu.add( item);

        item = new JMenuItem("Save all");
        item.addActionListener( fileSaveAll);
        fileMenu.add( item);

        item = new JMenuItem("Save current node");
        item.addActionListener( fileSaveOne);
        fileMenu.add( item);

        item =  new JMenuItem( "Exit");
        item.addActionListener( fileQuit);
        fileMenu.add( item);

        menuBar.add( fileMenu);

        JMenu settingsMenu  = new JMenu( "Settings");
        item = new JMenuItem( "Node settings wizard...");
        item.addActionListener( settingsWizard);
        settingsMenu.add( item);

        settingsMenu.add( new JMenuItem( "Output transformations..."));
        menuBar.add(settingsMenu);
        JMenu helpMenu = new JMenu( "Help");
        menuBar.add( helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void makeSplitPanels() {
        pnlLeft = new JPanel();
        pnlLeft.setLayout(new BorderLayout());
        pnlLeft.setMinimumSize(new Dimension(180, 100));
        pnlRight = new JPanel();
        pnlRight.setLayout(new BorderLayout());
        JSplitPane splitski = new JSplitPane(SwingConstants.VERTICAL, pnlLeft, pnlRight);
        this.add(splitski, BorderLayout.CENTER);
    }

    private void makeTree() {

        tree = new JTree() {
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                if (value != null && value instanceof DefaultMutableTreeNode) {
                    Object uo = ((DefaultMutableTreeNode) value).getUserObject();
                    if (uo instanceof SplitInfo) {
                        SplitInfo si = (SplitInfo) uo;
                        return si.getDisplayLabel();
                    }
                }
                return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
            }
        };
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                setDisplayFromModel();
            }
        });
        pnlLeft.add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private void makePreview() {
        preview = new JTextPane();
        preview.setText("(preview)");

        // NOTE : On Windows you may want to fiddle with this font and size.
        Font font = new Font("monospaced", Font.TRUETYPE_FONT, 14);
        preview.setFont(font);

        pnlRight.add(new JScrollPane(preview), BorderLayout.CENTER);
    }

    private void makeOptions() {

        JPanel pnlOptions = new JPanel();
        pnlOptions.setLayout(new BorderLayout());

        JPanel widgets = new JPanel();
        widgets.setLayout(new GridLayout(4, 2));

        widgets.add(new JLabel("childnodes in separate files"));
        separateChildren = new JCheckBox();
        separateChildren.addItemListener(this);
        widgets.add(separateChildren);

        widgets.add(new JLabel("add definition/config nodes"));
        defcon = new JCheckBox();
        defcon.addItemListener(this);
        widgets.add(defcon);

        widgets.add(new JLabel("file path segment"));
        dirsegment = new JTextField("(directory)", 15);
        dirsegment.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setModelFromDisplay();
            }
        });
        widgets.add(dirsegment);

        widgets.add(new JLabel("output file"));
        outputFileName = new JLabel("(filename goes here)");
        outputFileName.setUI(cutoffLeft);
        widgets.add(outputFileName);

        pnlOptions.add(widgets, BorderLayout.CENTER);

        pnlRight.add(pnlOptions, BorderLayout.NORTH);
    }

    private void makeButtons() {

        final TreeGui treeGui = this;
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton quitski = new JButton("exit");
        quitski.addActionListener( fileQuit);

        JButton saveThis = new JButton("this");
        saveThis.addActionListener( fileSaveOne);

        JButton saveAll = new JButton("all");
        saveAll.addActionListener( fileSaveAll);

        buttonLoad = new JButton( "load");
        buttonLoad.addActionListener( fileLoad);

        buttons.add(new JLabel("   "));
        buttons.add(new JLabel("save: "));
        buttons.add(saveThis);
        buttons.add(saveAll);
        buttons.add(new JLabel("   "));
        buttons.add( buttonLoad);
        buttons.add(new JLabel("   "));
        buttons.add(quitski);

        pnlRight.add(buttons, BorderLayout.SOUTH);
    }

    private void makeMisc() {

        inputFileName = new JLabel();
        inputFileName.setUI(cutoffLeft);
        add(inputFileName, BorderLayout.SOUTH);

        // On close save the current main window size and position
        final TreeGui guiFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                GuiSettings settings = new GuiSettings();
                settings.saveWindowDimension(guiFrame);
            }
        });
    }

    private void buildGui() {

        this.setTitle("Splitify");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout( new BorderLayout());

        makeActions();
        makeMenu();
        makeSplitPanels();
        makeTree();
        makePreview();
        makeOptions();
        makeButtons();
        makeMisc();

        this.pack();
    }

    private void selectNewInput() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select input file");
        if (fullFileName != null) {
            chooser.setSelectedFile(new File(fullFileName));
        }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if( chooser.showOpenDialog( buttonLoad) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            fullFileName = selectedFile.getAbsolutePath();
            loadTree();
        }
    }

    private void loadTree() {
        try {
            ObjectMapper mapper = Mapper.getMapper();
            ObjectNode rootNode = (ObjectNode) mapper.readTree(
                    new File(fullFileName));
            loadTreeFromNode(rootNode);
        } catch (Exception x) {
            preview.setText("ERROR: " + x.getMessage());
        }
    }

    private void loadEmptyTree() {
        String emptyFile = "/emptyFile:\n  no-data-loaded:empty\n";
        try {
            ObjectMapper mapper = Mapper.getMapper();
            ObjectNode rootNode = (ObjectNode) mapper.readTree(
                    emptyFile);
            loadTreeFromNode(rootNode);
        } catch (Exception x) {
            preview.setText("ERROR: " + x.getMessage());
        }
    }

    private void loadTreeFromNode(ObjectNode rootNode) {
        Analyzer analyzer = new Analyzer();
        DefaultMutableTreeNode jroot = analyzer.makeJtree(rootNode);
        rootSplitInfo = (SplitInfo) jroot.getUserObject();
        ((DefaultTreeModel) tree.getModel()).setRoot(jroot);
        TreePath pathToVisible = null;
        DefaultMutableTreeNode visible = (DefaultMutableTreeNode) jroot.getFirstChild();
        if (visible != null) {
            pathToVisible = new TreePath(visible.getPath());
            visible = (DefaultMutableTreeNode) visible.getFirstChild();
            if (visible != null) {
                pathToVisible = new TreePath(visible.getPath());
            }
        }
        tree.setSelectionPath(pathToVisible);
        tree.scrollPathToVisible(pathToVisible);
        tree.setRootVisible(false);
        setDisplayFromModel();
        this.fileWriter = new FileWriter(fullFileName);
    }


    private void setModelFromDisplay() {
        if (updatingData) {
            return;
        }
        updatingData = true;
        SplitInfo splitInfo = getCurrentSplitInfo();
        if (splitInfo == null) {
            updatingData = false;
            return;
        }
        String newDirsegment = dirsegment.getText();
        splitInfo.setDirSegment(newDirsegment);
        splitInfo.setSeparateChildNodes(separateChildren.isSelected());
        splitInfo.setAddDefCon(defcon.isSelected());
        String content = renderer.preview(splitInfo);
        preview.setText(content);
        preview.setCaretPosition(0);
        outputFileName.setText(splitInfo.getFilePath());
        updatingData = false;
    }

    private void setDisplayFromModel() {
        if (updatingData) {
            return;
        }
        updatingData = true;
        SplitInfo splitInfo = getCurrentSplitInfo();
        if (splitInfo == null) {
            updatingData = false;
            return;
        }
        String content = renderer.preview(splitInfo);
        preview.setText(content);
        preview.setCaretPosition(0);
        defcon.setSelected(splitInfo.isAddDefCon());
        separateChildren.setSelected(splitInfo.isSeparateChildNodes());
        dirsegment.setText(splitInfo.getDirSegment());
        outputFileName.setText(splitInfo.getFilePath());
        updatingData = false;
    }

    private SplitInfo getCurrentSplitInfo() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (treeNode == null) {
            return null;
        }
        Object uo = treeNode.getUserObject();
        if (!(uo instanceof SplitInfo)) {
            return null;
        }
        return (SplitInfo) uo;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object o = e.getItem();
        if (o instanceof JCheckBox) {
            setModelFromDisplay();
        }
    }

    public static void main(String args[]) {

        TreeGui gui = new TreeGui();
        gui.buildGui();
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            gui.fullFileName = inputFile.getAbsolutePath();
            gui.inputFileName.setText(gui.fullFileName);
            gui.loadTree();
        } else {
            gui.loadEmptyTree();
        }
        GuiSettings settings = new GuiSettings();
        settings.loadWindowDimension(gui);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui.setVisible(true);
            }
        });
    }
}
