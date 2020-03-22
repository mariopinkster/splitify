package nl.dimario.gui;

/**
 *     This file is part of Splitify
 *
 *     Splitify is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, version 3 of the License.
 *
 *     Splitify is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */

import nl.dimario.model.SplitInfo;
import nl.dimario.model.WizardUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class NodeSettingsWizard extends JDialog {

    private SplitInfo root;

    private JPanel pnlDialog;
    private JPanel pnlButton;
    private JRadioButton radioByDepth;
    private JComboBox whatDepth;
    private JRadioButton radioByType;
    private JComboBox whatType;

    public NodeSettingsWizard(SplitInfo root) {
        this.root = root;
        buildGui();
    }

    private void buildGui() {
        this.setTitle("Settings Wizard");
        setSize(300, 200);
        makePanels();
        makeButtons();
        makeWizardControls();
        this.pack();
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
        final NodeSettingsWizard dialog = this;

        JButton doit = new JButton("do it");
        doit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySettings();
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
        radioByDepth = new JRadioButton("stop at depth");
        radioByType = new JRadioButton( "stop at node type");

        ButtonGroup radioWhat = new ButtonGroup();
        radioWhat.add( radioByDepth);
        radioWhat.add( radioByType);

        whatDepth = new JComboBox();
        whatDepth.addItem( "2");
        whatDepth.addItem( "3");
        whatDepth.addItem( "4");
        whatDepth.addItem( "5");
        whatDepth.addItem( "6");
        whatDepth.addItem( "7");
        whatDepth.addItem( "8");

        whatType = new JComboBox();
        WizardUtil.setNodeTypeValues( this.root, whatType);

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

    private void applySettings() {
        if( radioByDepth.isSelected()) {
            int depth = Integer.parseInt( (String)whatDepth.getSelectedItem());
            WizardUtil.setSeparateChildNodes( this.root, false);
            WizardUtil.setSeparateChildNodesByLevel( this.root, 0, depth);
        } else if( radioByType.isSelected()) {
            String nodeType = (String) whatType.getSelectedItem();
            WizardUtil.setSeparateChildNodes( this.root, false);
            WizardUtil.setSeparateChildNodesByType( this.root, nodeType);
        }
    }
}