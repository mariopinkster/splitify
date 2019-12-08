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
import nl.dimario.model.SplitInfo;

public class TreeGui extends JFrame {

    private static final int DISPLAYLENGTH = 52;
    private String fullFileName;
    private JTree tree;
    private JLabel lblInput;
    private JLabel lblStatus;

    private void displayInputFile() {
        String display = fullFileName;
        if( display.length() > DISPLAYLENGTH) {
            int from = display.length() - DISPLAYLENGTH;
            display = "..." +  display.substring( from);
        }
        lblInput.setText(display);
    }

    private void buildGui() {

        tree = new JTree() {
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                if (value != null && value instanceof DefaultMutableTreeNode) {
                    Object uo = ((DefaultMutableTreeNode)value).getUserObject();
                    if( uo instanceof SplitInfo) {
                        SplitInfo si = (SplitInfo) uo;
                        return String.format( "%s = %s", si.getNodeSegment(), si.getNodeType());
                    }
                }
                return super.convertValueToText(value,selected,expanded,leaf,row,hasFocus);
            }
        };
        add(new JScrollPane(tree));
        lblStatus = new JLabel();
        add( lblStatus, BorderLayout.SOUTH);
        lblInput = new JLabel();
        add( lblInput, BorderLayout.NORTH);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object uo = selectedNode.getUserObject();
                if( uo instanceof SplitInfo) {
                    // Ga dingen doen met deze splitinfo
                }
            }
        });
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final TreeGui guiFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent event) {
                GuiSettings settings = new GuiSettings();
                settings.saveWindowDimension( guiFrame);
            }
        });
        this.setTitle("Splitify");
        this.pack();
    }

    private ObjectMapper configureMapper() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.configure( YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        yamlFactory.configure( YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
        ObjectMapper mapper = new ObjectMapper( yamlFactory);
        mapper.configure( SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

    private void loadTree() throws IOException {

        ObjectMapper om = configureMapper();
        ObjectNode rootNode = (ObjectNode) om.readTree(
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
