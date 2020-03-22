package nl.dimario.gui;

import nl.dimario.model.OutputOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OutputSettingsDialog extends JDialog {

    private OutputOptions options;
    private JPanel pnlDialog;
    private JPanel pnlButton;
    private JCheckBox addDefcon;

    public OutputSettingsDialog(OutputOptions options) {
        this.options = options;
        buildGui();
    }

    private void buildGui() {
        this.setTitle( "Output options");
        setSize( 400, 300);
        makePanels();
        makeButtons();
        makeControls();
    }

    private void makePanels() {
        pnlDialog = new JPanel();
        pnlDialog.setLayout(new BorderLayout());
        pnlDialog.setMinimumSize(new Dimension(180, 100));
        pnlButton = new JPanel();
        pnlButton.setLayout(new BorderLayout());
        this.add(pnlDialog, BorderLayout.CENTER);
        this.add(pnlButton, BorderLayout.SOUTH);
    }

    private void makeButtons() {

        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        final OutputSettingsDialog dialog = this;

        JButton close = new JButton("close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.processWindowEvent(
                        new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });
        pnlButtons.add(close);
        pnlButton.add(pnlButtons, BorderLayout.SOUTH);
    }

    private void makeControls() {

        ItemListener checkboxListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object o = e.getItem();
                if( o instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) o;
                    if( checkBox == addDefcon) {
                        options.setAddDefinitionsConfig( checkBox.isSelected());
                    }
                }
            }
        };

        JPanel pnlOptions = new JPanel();
        pnlOptions.setLayout( new GridLayout( 4, 1));
        addDefcon = new JCheckBox( "Add definition / config wrappers");
        addDefcon.setSelected(options.isAddDefinitionsConfig());
        addDefcon.addItemListener( checkboxListener);
        pnlOptions.add( addDefcon);

        pnlDialog.add( pnlOptions, BorderLayout.CENTER);
    }
}
