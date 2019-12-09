package nl.dimario.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import nl.dimario.model.Analyzer;
import nl.dimario.model.Mapper;
import nl.dimario.model.Renderer;
import nl.dimario.model.SplitInfo;

public class TreeGui extends JFrame {

    private static final int DISPLAYLENGTH = 52;
    private String fullFileName;
    private JTree tree;
    JTextPane preview;
    private JLabel lblInput;
    private JLabel lblStatus;
    private Renderer renderer;

    public TreeGui() {
        this.renderer = new Renderer();
    }

    private void displayInputFile() {
        String display = fullFileName;
        if( display.length() > DISPLAYLENGTH) {
            int from = display.length() - DISPLAYLENGTH;
            display = "..." +  display.substring( from);
        }
        lblInput.setText(display);
    }

    private void buildGui() {

        this.setTitle("Splitify");

        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout( new BorderLayout());
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout( new BorderLayout());
        JSplitPane splitski = new JSplitPane( SwingConstants.VERTICAL, pnlLeft, pnlRight);
        this.add( splitski, BorderLayout.CENTER);

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
        // TODO valueChanged() doet niet wat ik wil
        // TODO en render preview moet nog een boolean in voor wel/geen recursie
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object uo = selectedNode.getUserObject();
                if( uo instanceof SplitInfo) {
                    try {
                        String content = renderer.preview((SplitInfo) uo, true);
                        preview.setText( content);
                    } catch( Exception x) {
                        preview.setText( "ERROR: " + x.getMessage());
                    }
                }
            }
        });
        pnlLeft.add(new JScrollPane(tree), BorderLayout.CENTER);

        preview = new JTextPane();
        preview.setText( "(preview)");
        pnlRight.add( new JScrollPane(preview), BorderLayout.CENTER);

        lblStatus = new JLabel();
        add( lblStatus, BorderLayout.SOUTH);
        lblInput = new JLabel();
        add( lblInput, BorderLayout.NORTH);

        // On close save the current main window size and position
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final TreeGui guiFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent event) {
                GuiSettings settings = new GuiSettings();
                settings.saveWindowDimension( guiFrame);
            }
        });
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

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            TreeGui gui = new TreeGui();
            gui.buildGui();
            if( args.length > 0) {
                File inputFile = new File( args[0]);
                gui.fullFileName = inputFile.getAbsolutePath();
                gui.displayInputFile();
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
