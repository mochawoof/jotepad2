import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.event.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

import java.lang.reflect.*;

class Tab extends JPanel {
    public RSyntaxTextArea textArea;
    public RTextScrollPane scrollPane;
    public File file = null;
    public boolean saved = false;
    public String charset = "UTF-8";

    public Tab() {
        setLayout(new BorderLayout());
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            public void onChange() {
                saved = false;
                updateTitle();
            }
        });

        scrollPane = new RTextScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setTheme(Main.getThemeFromName(Main.propsX.get("ViewEditor Theme")));
    }

    public int getTabIndex() {
        for (int i = 0; i < Main.tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(this, Main.tabbedPane.getComponentAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public String getSyntaxConstant(String ext) {
        SyntaxTypes syn = new SyntaxTypes();
        try {
            for (Field f : SyntaxTypes.class.getDeclaredFields()) {
                String val = (String) f.get(syn);
                String[] valSplit = val.split(";");
                if (valSplit.length != 1) {
                    for (String checkExt : valSplit[1].split(",")) {
                        if (checkExt.equals(ext)) {
                            return valSplit[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syn.SYNTAX_STYLE_NONE;
    }

    public void updateFile(File newFile) {
        file = newFile;
        String fileName = file.getName();
        textArea.setSyntaxEditingStyle(getSyntaxConstant(fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())));
        updateTitle();
    }

    public void updateTitle() {
        String fileName = "";
        if (file == null) {
            fileName = "New";
        } else {
            fileName = file.getName();
        }
        Main.tabbedPane.setTitleAt(getTabIndex(), fileName + (saved ? "" : " *"));
    }

    public void setTheme(Theme theme) {
        theme.apply(textArea);
    }

    public boolean reload(boolean force) {
        if (!force && !saved) {
            if (JOptionPane.showConfirmDialog(Main.f, "Are you sure you want to reload? Your unsaved changes will be lost.", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        try {
            textArea.setText("");
            if (file != null) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                textArea.setText(new String(bytes, charset).replace("\r\n", "\n"));
                saved = true;
                updateTitle();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCharset(String charsetName, boolean force) {
        String oldCharset = charset;
        charset = charsetName;
        if (!reload(force)) {
            charset = oldCharset;
        }
    }
}
