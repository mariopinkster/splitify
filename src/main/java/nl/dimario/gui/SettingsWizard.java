package nl.dimario.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class SettingsWizard extends JDialog {

    JPanel pnlDialog;
    JPanel pnlButton;
    JRadioButton radioByDepth;
    JComboBox whatDepth;
    JRadioButton radioByType;
    JComboBox whatType;

    public SettingsWizard() {
        buildGui();
    }

    private void buildGui() {
        this.setTitle("Settings Wizard");
        setSize(300, 200);
        makePanels();
        makeButtons();
        makeWizardControls();
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

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
        final SettingsWizard dialog = this;

        JButton doit = new JButton("do it");
        doit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JButton close = new JButton("close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.processWindowEvent(
                        new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });

        buttons.add(doit);
        buttons.add(new Label("   "));
        buttons.add(close);
        pnlButton.add(buttons, BorderLayout.SOUTH);
    }

    private void makeWizardControls() {

        JLabel text = new JLabel( "Set separate file control by:");
        radioByDepth = new JRadioButton("depth");
        radioByType = new JRadioButton( "node type");

        ButtonGroup radioWhat = new ButtonGroup();
        radioWhat.add( radioByDepth);
        radioWhat.add( radioByType);

        whatDepth = new JComboBox();
        whatDepth.addItem( "2");
        whatDepth.addItem( "3");
        whatDepth.addItem( "4");
        whatDepth.addItem( "5");

        whatType = new JComboBox();
        whatType.addItem( "document handle");
        whatType.addItem( "other");

        JPanel pnlRadio = new JPanel();
        pnlRadio.setLayout( new GridLayout(2,2));
        pnlRadio.add( radioByDepth);
        pnlRadio.add( whatDepth);
        pnlRadio.add( radioByType);
        pnlRadio.add( whatType);

        JPanel pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout( pnlOptions, BoxLayout.Y_AXIS));
        pnlOptions.add( text);
        pnlOptions.add( pnlRadio);

        pnlDialog.add( pnlOptions, BorderLayout.CENTER);
    }
}