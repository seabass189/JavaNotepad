package proj5;

/*
Name:       Hernandez, Sebastian
Project:    Proj5 JNotepad
Due:        12/7/18
Course:     CS-2450-01-F18

Description:
Project 5 implements the same functions as Notepad.
It has a menu that will allow user to open java type files.
File opener will change the text to the contents of the file selected 
 and will be implemented with a JTree.
Text area text color is white, the background is blue.
About will open a standard About dialog.
Exit will prompt the user with a dialog to confirm action.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import static java.awt.event.KeyEvent.VK_DELETE;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

public class JNotepad {

    JFrame jfrm;
    JTextArea jtxt;
    Font def = new Font("Courier New", Font.PLAIN, 12);
    UndoManager undo = new UndoManager();
    String docTitle = "Untitled";
    String fileType = "";
    String searchTerm = "";
    boolean saved = true;
    boolean wrapped = false;
    JFileChooser fc = new JFileChooser(".");
    FindDlg findDlg = new FindDlg();

    JNotepad() {
        // Create a new JFrame container.   
        jfrm = new JFrame(docTitle + " - JNotepad");
        jfrm.setLayout(new BorderLayout());
        jfrm.setSize(800, 600);
        jfrm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jfrm.setIconImage(new ImageIcon("JNotepad.png").getImage());

        jtxt = new JTextArea();
        jtxt.setFont(def);
        JScrollPane jscroll = new JScrollPane(jtxt);

        Document doc = jtxt.getDocument();

        jscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        jtxt.setForeground(Color.WHITE);
        jtxt.setBackground(Color.BLUE);

        // Create the menu bar. 
        JMenuBar jmb = new JMenuBar();

        // Create the File menu. 
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic('F');
        JMenuItem jmiNew = new JMenuItem("New", 'N');
        jmiNew.setAccelerator(KeyStroke.getKeyStroke("control N"));
        JMenuItem jmiOpen = new JMenuItem("Open...", 'O');
        jmiOpen.setAccelerator(KeyStroke.getKeyStroke("control O"));
        JMenuItem jmiSave = new JMenuItem("Save", 'S');
        jmiSave.setAccelerator(KeyStroke.getKeyStroke("control S"));
        JMenuItem jmiSaveAs = new JMenuItem("Save As...");
        jmiSaveAs.setDisplayedMnemonicIndex(5);
        JMenuItem jmiPageSet = new JMenuItem("Page Setup...", 'u');
        JMenuItem jmiPrint = new JMenuItem("Print...", 'P');
        jmiPrint.setAccelerator(KeyStroke.getKeyStroke("control P"));
        JMenuItem jmiExit = new JMenuItem("Exit", 'x');

        jmFile.add(jmiNew);
        jmFile.add(jmiOpen);
        jmFile.add(jmiSave);
        jmFile.add(jmiSaveAs);
        jmFile.addSeparator();
        jmFile.add(jmiPageSet);
        jmFile.add(jmiPrint);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
        jmb.add(jmFile);

        // Create the Edit menu. 
        JMenu jmEdit = new JMenu("Edit");
        jmEdit.setMnemonic('E');
        JMenuItem jmiUndo = new JMenuItem("Undo", 'U');
        JMenuItem jmiCut = new JMenuItem("Cut", 't');
        jmiCut.setAccelerator(KeyStroke.getKeyStroke("control X"));
        JMenuItem jmiCopy = new JMenuItem("Copy", 'C');
        jmiCopy.setAccelerator(KeyStroke.getKeyStroke("control C"));
        JMenuItem jmiPaste = new JMenuItem("Paste", 'P');
        jmiPaste.setAccelerator(KeyStroke.getKeyStroke("control V"));
        JMenuItem jmiDelete = new JMenuItem("Delete", 'l');
        jmiDelete.setAccelerator(KeyStroke.getKeyStroke(VK_DELETE, 0));
        JMenuItem jmiFind = new JMenuItem("Find...", 'F');
        jmiFind.setAccelerator(KeyStroke.getKeyStroke("control F"));
        JMenuItem jmiFindNext = new JMenuItem("Find Next", 'N');
        jmiFindNext.setDisplayedMnemonicIndex(5);
        JMenuItem jmiReplace = new JMenuItem("Replace...", 'R');
        jmiReplace.setAccelerator(KeyStroke.getKeyStroke("control H"));
        JMenuItem jmiGoTo = new JMenuItem("Go To...", 'G');
        jmiGoTo.setAccelerator(KeyStroke.getKeyStroke("control G"));
        JMenuItem jmiSelectAll = new JMenuItem("Select All", 'A');
        jmiSelectAll.setAccelerator(KeyStroke.getKeyStroke("control A"));
        JMenuItem jmiTimeDate = new JMenuItem("Time/Date", 'D');
        jmiTimeDate.setAccelerator(KeyStroke.getKeyStroke("F5"));

        jmEdit.add(jmiUndo);
        jmEdit.addSeparator();
        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.add(jmiDelete);
        jmEdit.addSeparator();
        jmEdit.add(jmiFind);
        jmEdit.add(jmiFindNext);
        jmEdit.add(jmiReplace);
        jmEdit.add(jmiGoTo);
        jmEdit.addSeparator();
        jmEdit.add(jmiSelectAll);
        jmEdit.add(jmiTimeDate);
        jmb.add(jmEdit);

        // Create the Format menu. 
        JMenu jmFormat = new JMenu("Format");
        jmFormat.setMnemonic('o');
        JCheckBoxMenuItem jmiWordWrap = new JCheckBoxMenuItem("Word Wrap");
        jmiWordWrap.setMnemonic('W');
        JMenuItem jmiFont = new JMenuItem("Font...", 'F');

        jmFormat.add(jmiWordWrap);
        jmFormat.add(jmiFont);
        jmb.add(jmFormat);

        // Create the View menu. 
        JMenu jmView = new JMenu("View");
        jmView.setMnemonic('V');
        JMenuItem jmiStatus = new JMenuItem("Status Bar", 'S');

        jmView.add(jmiStatus);
        jmb.add(jmView);

        // Create the Help menu. 
        JMenu jmHelp = new JMenu("Help");
        jmHelp.setMnemonic('H');
        JMenuItem jmiViewHelp = new JMenuItem("View Help", 'H');
        JMenuItem jmiAbout = new JMenuItem("About JNotepad", 'A');

        jmHelp.add(jmiViewHelp);
        jmHelp.addSeparator();
        jmHelp.add(jmiAbout);
        jmb.add(jmHelp);

        jmiPageSet.setEnabled(false);
        jmiPrint.setEnabled(false);
        jmiReplace.setEnabled(false);
        jmiGoTo.setEnabled(false);
        jmiStatus.setEnabled(false);
        jmiViewHelp.setEnabled(false);

        // Creat the popup menu
        JPopupMenu jpu = new JPopupMenu();

        JMenuItem cut = new JMenuItem("Cut", 't');
        JMenuItem paste = new JMenuItem("Paste", 'P');
        JMenuItem copy = new JMenuItem("Copy", 'C');

        jpu.add(cut);
        jpu.add(copy);
        jpu.add(paste);

        // Add a listener for for the popup trigger. 
        jtxt.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                jtxt.getHighlighter().removeAllHighlights();
                findDlg.setCaretPos(true);
                if (me.isPopupTrigger()) {
                    jpu.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });

        // Add action listener for Cut
        cut.addActionListener((ae) -> {
            jtxt.cut();
        });

        // Add action listener for Paste
        paste.addActionListener((ae) -> {
            jtxt.paste();
        });

        // Add action listener for Copy
        copy.addActionListener((ae) -> {
            jtxt.copy();
        });

        // Add action listener for Cut
        jmiCut.addActionListener((ae) -> {
            jtxt.cut();
        });

        // Add action listener for Copy
        jmiCopy.addActionListener((ae) -> {
            jtxt.copy();
        });

        // Add action listener for Paste
        jmiPaste.addActionListener((ae) -> {
            jtxt.paste();
        });

        // Add action listener for Delete
        jmiDelete.addActionListener((ae) -> {
            jtxt.replaceSelection("");
        });

        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saved = false;
                jtxt.getHighlighter().removeAllHighlights();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saved = false;
                jtxt.getHighlighter().removeAllHighlights();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saved = false;
                jtxt.getHighlighter().removeAllHighlights();
            }
        });

        jfrm.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                jmiExit.doClick();
            }
        });

        jtxt.getDocument().addUndoableEditListener(
                new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        // Add action listener for Undo
        jmiUndo.addActionListener((ae) -> {
            try {
                undo.undo();
            } catch (CannotRedoException cre) {
                cre.printStackTrace();
            }
        });

        // Add action listener for Select All
        jmiSelectAll.addActionListener((ae) -> {
            jtxt.selectAll();
        });

        // Add action listener for New 
        jmiNew.addActionListener((ae) -> {
            if (saved) {
                jtxt.setText("");
                jfrm.setTitle("Untitled - JNotepad");
                docTitle = "Untitled";
                saved = false;
            } else if (!saved && docTitle.equals("Untitled")) {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "Do you want to save changes to Untitled ?",
                        "Exit?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.NO_OPTION) {
                    jtxt.setText("");
                    jfrm.setTitle("Untitled - JNotepad");
                    docTitle = "Untitled";
                    saved = false;
                } else if (num == JOptionPane.YES_OPTION) {
                    jmiSaveAs.doClick();
                    jtxt.setText("");
                    jfrm.setTitle("Untitled - JNotepad");
                    docTitle = "Untitled";
                    saved = false;
                }
            } else {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "<html>Do you want to save changes to<br>" + fc.getSelectedFile().getAbsolutePath() + " ?",
                        "Exit?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.NO_OPTION) {
                    jtxt.setText("");
                    jfrm.setTitle("Untitled - JNotepad");
                    docTitle = "Untitled";
                    saved = false;
                } else if (num == JOptionPane.YES_OPTION) {
                    jmiSave.doClick();
                    jtxt.setText("");
                    jfrm.setTitle("Untitled - JNotepad");
                    docTitle = "Untitled";
                    saved = false;
                }
            }
            jfrm.setVisible(true);
        });

        // Add action listener for Open 
        jmiOpen.addActionListener((ae) -> {
            if (!saved && docTitle.equals("Untitled")) {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "Do you want to save changes to Untitled?",
                        "Save?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.YES_OPTION) {
                    jmiSaveAs.doClick();
                } else if (num == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            } else if (!saved) {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "<html>Do you want to save changes to<br>" + fc.getSelectedFile().getAbsolutePath() + "?",
                        "Save?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.YES_OPTION) {
                    jmiSave.doClick();
                } else if (num == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            FileFilter javFilter = new FileNameExtensionFilter("Java source file (*.java)", "java");
            FileFilter txtFilter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(javFilter);
            fc.addChoosableFileFilter(txtFilter);
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selFile = new File(fc.getSelectedFile().getAbsolutePath());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(selFile));
                    String st;
                    jtxt.setText("");
                    while ((st = br.readLine()) != null) {
                        jtxt.append(st + "\n");
                    }
                    FileFilter selFilter = fc.getFileFilter();
                    if (selFilter.equals(javFilter)) {
                        docTitle = selFile.getName().replaceAll(".java", "");
                        fileType = "java";
                    } else if (selFilter.equals(txtFilter)) {
                        docTitle = selFile.getName().replaceAll(".txt", "");
                        fileType = "txt";
                    }
                    jfrm.setTitle(docTitle + " - JNotepad");
                    saved = true;
                } catch (Exception e) {
                }

            } else {
                JOptionPane.showMessageDialog(jfrm,
                        "<html>File Chooser cancelled<br>No file selected",
                        "File Chooser",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        // Add action listener for Save 
        jmiSave.addActionListener((ae) -> {
            if (docTitle.equals("Untitled")) {
                jmiSaveAs.doClick();
            } else {
                try {
                    File selFile = new File(fc.getSelectedFile().getAbsolutePath());
                    FileFilter selFilter = fc.getFileFilter();
                    FileWriter fw = null;
                    fw = new FileWriter(selFile);
                    fw.write(jtxt.getText());
                    fw.close();
                    saved = true;
                    jmiSave.enableInputMethods(false);
                    jfrm.setVisible(true);

                } catch (Exception e) {
                    System.out.println("Save Failed");
                }
            }
        });

        // Add action listener for Save As 
        jmiSaveAs.addActionListener((ae) -> {
            fc = new JFileChooser(".");
            FileFilter javFilter = new FileNameExtensionFilter("Java source file (*.java)", "java");
            FileFilter txtFilter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(javFilter);
            fc.addChoosableFileFilter(txtFilter);
            if (fc.showSaveDialog(jfrm) == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = fc.getSelectedFile().getAbsolutePath();
                    path = path.replaceAll(".java", "");
                    path = path.replaceAll(".txt", "");
                    File selFile = new File(path);
                    FileFilter selFilter = fc.getFileFilter();
                    FileWriter fw = null;
                    if (selFilter.equals(javFilter)) {
                        fw = new FileWriter(selFile + ".java");
                    } else if (selFilter.equals(txtFilter)) {
                        fw = new FileWriter(selFile + ".txt");
                    }
                    fw.write(jtxt.getText());
                    fw.close();
                    docTitle = selFile.getName().replaceAll(".java", "");
                    docTitle = docTitle.replaceAll(".txt", "");
                    jfrm.setTitle(docTitle + " - JNotepad");
                    saved = true;
                    jmiSave.enableInputMethods(false);
                } catch (Exception ex) {
                }
            } else {
                JOptionPane.showMessageDialog(jfrm,
                        "<html>File Chooser cancelled<br>No file saved",
                        "File Chooser",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        // Add action listener for Exit
        jmiExit.addActionListener((ae) -> {
            if (saved) {
                System.exit(0);
            } else if (!saved && docTitle.equals("Untitled")) {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "Do you want to save changes to Untitled and exit?",
                        "Exit?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else if (num == JOptionPane.YES_OPTION) {
                    jmiSaveAs.doClick();
                }
            } else {
                Object[] options = {"Save", "Don't Save", "Cancel"};
                int num = JOptionPane.showOptionDialog(jfrm,
                        "<html>Do you want to save changes to<br>" + fc.getSelectedFile().getAbsolutePath() + "<br> and exit?",
                        "Exit?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (num == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else if (num == JOptionPane.YES_OPTION) {
                    jmiSave.doClick();
                }
            }
        });

        // Add action listener for Find
        jmiFind.addActionListener((ActionEvent ae) -> {
            findDlg.setInputBox();
            findDlg.showDialog(jfrm, jtxt);
        });

        // Add action listener for Find Next
        jmiFindNext.addActionListener((ActionEvent ae) -> {
            findDlg.findNext();
        });

        // Add action listener for Time/Date
        jmiTimeDate.addActionListener((ActionEvent ae) -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a MM/dd/yyy"); //format: 12:42 PM 12/04/2018
            LocalDateTime now = LocalDateTime.now();
            jtxt.insert(dtf.format(now), jtxt.getCaretPosition());
        });

        // Add action listener for Word Wrap
        jmiWordWrap.addActionListener((ActionEvent ae) -> {
            if (jmiWordWrap.getState()) {
                jtxt.setLineWrap(true);
            } else {
                jtxt.setLineWrap(false);
            }
        });

        // Add action listener for FontChooser
        jmiFont.addActionListener((ActionEvent ae) -> {
            Font fontSel = JFontChooser.showDialog(jfrm, def);
            if (fontSel != null) {
                jtxt.setFont(fontSel);
            } else {
                JOptionPane.showMessageDialog(jfrm,
                        "<html>Font Chooser cancelled<br>No font selected",
                        "Font Chooser",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        // Add action listener for About
        jmiAbout.addActionListener((ae) -> {
            JOptionPane.showMessageDialog(jfrm,
                    "<html>JNotepad Project Version 0.1<br>Copyright (c) 2018 shernandez",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon("JNotepad.png"));
        });

        // Add the label to the content pane. 
        jfrm.add(jscroll, BorderLayout.CENTER);

        // Add the menu bar to the frame. 
        jfrm.setJMenuBar(jmb);

        jfrm.setLocationRelativeTo(null);

        // Display the frame.   
        jfrm.setVisible(true);

    }

    public static void main(String[] args) {
        // Create the frame on the event dispatching thread.   
        SwingUtilities.invokeLater(() -> {
            new JNotepad();
        });
    }

}
