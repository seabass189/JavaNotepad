package proj5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class FindDlg {

    public static final int OPEN_OPTION = 0;
    public static final int CANCEL_OPTION = 1;
    protected int dialogResultValue = CANCEL_OPTION;

    private JDialog dlg;
    private JTextArea jtxt;
    private JTextField input;
    private JCheckBox matchCase;
    private JRadioButton up, down;

    private String search;
    private String searchTerm = "";
    private int lastIndex = 0;
    private boolean carPosMoved;

    public FindDlg() {
        search = "";
        searchTerm = "";
        lastIndex = 0;
        carPosMoved = false;
        input = new JTextField(15);
    }

    public void showDialog(JFrame parent, JTextArea jtextArea) {
        jtxt = jtextArea;
        Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        dlg = new JDialog(frame, "Find", false);
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(2, 1));

        JPanel dirPanel = new JPanel();
        Border dirBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Direction");
        dirPanel.setLayout(new BorderLayout());
        dirPanel.setBorder(dirBorder);
        up = new JRadioButton("Up");
        up.setMnemonic('U');
        down = new JRadioButton("Down", true);
        down.setMnemonic('D');

        JPanel dirButtonPanel = new JPanel();
        ButtonGroup butGroup = new ButtonGroup();
        butGroup.add(up);
        butGroup.add(down);
        dirButtonPanel.add(up);
        dirButtonPanel.add(down);
        dirPanel.add(dirButtonPanel, BorderLayout.CENTER);

        JLabel findWhat = new JLabel("Find what:");
        findWhat.setDisplayedMnemonic('n');
        JPanel topPan = new JPanel();
        topPan.add(findWhat);
        topPan.add(input);

        matchCase = new JCheckBox("Match Case");
        matchCase.setDisplayedMnemonicIndex(6);
        JPanel botPan = new JPanel();
        botPan.add(matchCase);
        botPan.add(dirPanel);

        Action findAction = new DialogFindAction();
        Action cancelAction = new DialogCancelAction();
        JButton findNext = new JButton(findAction);
        findNext.setText("Find Next");
        JButton cancel = new JButton(cancelAction);
        cancel.setText("Cancel");
        findNext.setMnemonic('F');
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 1));
        buttons.add(findNext);
        buttons.add(cancel);
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 10, 75, 10));

        pan.add(topPan);
        pan.add(botPan);

        dlg.add(pan, BorderLayout.CENTER);
        dlg.add(buttons, BorderLayout.EAST);

        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    protected class DialogFindAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            findNext();
        }
    }

    protected class DialogCancelAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            searchTerm = input.getText();
            dialogResultValue = CANCEL_OPTION;
            dlg.setVisible(false);
        }
    }

    public void setCaretPos(boolean movedCaret) {
        carPosMoved = movedCaret;
    }

    public void setInputBox() {
        input.setText(searchTerm);
    }

    public void findNext() {
        int pos = jtxt.getCaretPosition();
        search = input.getText();
        String text = jtxt.getText();
        if (!matchCase.isSelected()) {
            search = search.toLowerCase();
            text = text.toLowerCase();
        }
        if (!input.getText().equals("")) {
            if (down.isSelected()) {
                if (carPosMoved) {
                    lastIndex = pos;
                }
                int index = text.indexOf(search, lastIndex);
                if (index != -1) {
                    Highlighter highlighter = jtxt.getHighlighter();
                    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                    highlighter.removeAllHighlights();
                    try {
                        highlighter.addHighlight(index, index + search.length(), painter);
                        lastIndex = index + search.length();
                        carPosMoved = false;
                    } catch (Exception ex) {
                        System.out.println("Bad Highlight Location");
                    }
                } else {
                    JOptionPane.showMessageDialog(dlg,
                            "Cannot find \"" + search + "\"",
                            "JNotepad",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (carPosMoved) {
                    lastIndex = pos;
                }
                int index = text.lastIndexOf(search, lastIndex);
                if (index != -1) {
                    Highlighter highlighter = jtxt.getHighlighter();
                    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                    highlighter.removeAllHighlights();
                    try {
                        highlighter.addHighlight(index, index + search.length(), painter);
                        lastIndex = index - 1;
                        carPosMoved = false;
                    } catch (Exception ex) {
                        System.out.println("Bad Highlight Location");
                    }
                } else {
                    JOptionPane.showMessageDialog(dlg,
                            "Cannot find \"" + search + "\"",
                            "JNotepad",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
