package nl.dimario.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class SettingsWizard extends JDialog {

    JPanel pnlDialog;
    JPanel pnlButton;


    public SettingsWizard() {
        buildGui();
    }

    private void buildGui() {
        this.setTitle("Settings Wizard");
        setSize( 300, 200);
        makePanels();
        makeButtons();
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

            JButton close = new JButton("close");
            close.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.processWindowEvent(
                            new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                }
            });

            buttons.add( close);
            pnlButton.add( buttons, BorderLayout.SOUTH);
        }
}