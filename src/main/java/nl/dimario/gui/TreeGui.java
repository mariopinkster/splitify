package nl.dimario.gui;

import java.awt.*;
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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.dimario.model.Analyzer;
import nl.dimario.model.Mapper;
import nl.dimario.model.Renderer;
import nl.dimario.model.SplitInfo;

public class TreeGui extends JFrame implements ItemListener {

    private static final int DISPLAYLENGTH = 52;
    private static final String SEPARATECHILDREN = "childnodes in separae files";
    private static final String ADDDEFCON = "add definition/config nodes";

    private String fullFileName;

    JPanel pnlLeft;
    JPanel pnlRight;

    private JTree tree;
    JTextPane preview;
    private JLabel inputFileName;
    private JCheckBox separateChildren;
    private JCheckBox defcon;
    private JTextField dirsegment;


    private Renderer renderer;

    public TreeGui() {
        this.renderer = new Renderer();
    }

    private void makeSplitPanels() {
        pnlLeft = new JPanel();
        pnlLeft.setLayout( new BorderLayout());
        pnlRight = new JPanel();
        pnlRight.setLayout( new BorderLayout());
        JSplitPane splitski = new JSplitPane( SwingConstants.VERTICAL, pnlLeft, pnlRight);
        this.add( splitski, BorderLayout.CENTER);
    }

    private void makeTree() {

        tree = new JTree() {
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                if (value != null && value instanceof DefaultMutableTreeNode) {
                    Object uo = ((DefaultMutableTreeNode)value).getUserObject();
                    if( uo instanceof SplitInfo) {
                        SplitInfo si = (SplitInfo) uo;
                        return si.getDisplayLabel();
                    }
                }
                return super.convertValueToText(value,selected,expanded,leaf,row,hasFocus);
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
        preview.setText( "(preview)");
        pnlRight.add( new JScrollPane(preview), BorderLayout.CENTER);
    }

    private void makeOptions() {

        JPanel pnlOptions = new JPanel();
        pnlOptions.setLayout( new BoxLayout( pnlOptions,BoxLayout.Y_AXIS));
        pnlOptions.add( new JLabel( "options"));

        separateChildren = new JCheckBox(SEPARATECHILDREN);
        separateChildren.addItemListener( this);
        pnlOptions.add(separateChildren);

        defcon = new JCheckBox( ADDDEFCON);
        defcon.addItemListener(this);
        pnlOptions.add( defcon);

        dirsegment = new JTextField();
        dirsegment.addFocusListener(new FocusListener() {

             @Override
             public void focusGained(FocusEvent e) {
             }

             @Override
             public void focusLost(FocusEvent e) {
                setModelFromDisplay();
             }
        });
        pnlOptions.add(dirsegment);

        pnlRight.add(pnlOptions, BorderLayout.NORTH);
    }

    private void makeSaveButtons() {
        JPanel  pnlSave = new JPanel();
        pnlSave.add( new JLabel( "save buttons"));
        pnlRight.add( pnlSave, BorderLayout.SOUTH);
    }

    private void makeMisc() {

        BasicLabelUI helperUI = new BasicLabelUI() {
            @Override
            protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR) {
                return StringUtils.reverse(super.layoutCL(label, fontMetrics, StringUtils.reverse(text), icon, viewR, iconR, textR));
            }
        };
        inputFileName = new JLabel();
        inputFileName.setUI( helperUI);
        add(inputFileName, BorderLayout.NORTH);

        // On close save the current main window size and position
        final TreeGui guiFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent event) {
                GuiSettings settings = new GuiSettings();
                settings.saveWindowDimension( guiFrame);
            }
        });
    }

    private void buildGui() {

        this.setTitle("Splitify");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        makeSplitPanels();
        makeTree();
        makePreview();
        makeOptions();
        makeSaveButtons();
        makeMisc();

        this.pack();
    }

    private void loadTree() throws IOException {

        ObjectMapper mapper = Mapper.getMapper();
        ObjectNode rootNode = (ObjectNode) mapper.readTree(
                new File( fullFileName));

        Analyzer analyzer = new Analyzer();
        DefaultMutableTreeNode jroot = analyzer.makeJtree( rootNode);
        ((DefaultTreeModel)tree.getModel()).setRoot( jroot);
    }

    private void setModelFromDisplay() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if( treeNode == null) {
            return;
        }
        Object uo = treeNode.getUserObject();
        if( ! (uo instanceof SplitInfo)) {
            return;
        }
        SplitInfo splitInfo = (SplitInfo) treeNode.getUserObject();
        String newDirsegment = dirsegment.getText();
        splitInfo.setDirSegment( newDirsegment);
        splitInfo.setStopSplit( separateChildren.isSelected());
        splitInfo.setAddDefCon( defcon.isSelected());
        String content = renderer.preview( splitInfo);
        preview.setText( content);
    }

    private void setDisplayFromModel() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if( treeNode == null) {
            return;
        }
        Object uo = treeNode.getUserObject();
        if( ! (uo instanceof SplitInfo)) {
            return;
        }
        SplitInfo splitInfo = (SplitInfo) treeNode.getUserObject();
        String content = renderer.preview( splitInfo);
        preview.setText( content);
        defcon.setSelected( splitInfo.isAddDefCon());
        separateChildren.setSelected( splitInfo.isStopSplit());
        dirsegment.setText( splitInfo.getDirSegment());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object o = e.getItem();
        if( o instanceof JCheckBox) {
            setModelFromDisplay();
        }
    }

    public static void main(String args[]) {

        try {
            TreeGui gui = new TreeGui();
            gui.buildGui();
            if( args.length > 0) {
                File inputFile = new File( args[0]);
                gui.fullFileName = inputFile.getAbsolutePath();
                gui.inputFileName.setText( gui.fullFileName);
                gui.loadTree();
            }
            GuiSettings settings = new GuiSettings();
            settings.loadWindowDimension( gui);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.setVisible(true);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
