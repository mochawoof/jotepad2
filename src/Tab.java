import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

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

        scrollPane = new RTextScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        Main.tabbedPane.addTab("New", this);
        Main.tabbedPane.setSelectedIndex(Main.tabbedPane.getTabCount() - 1);
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
        Main.tabbedPane.setTitleAt(getTabIndex(), fileName + (saved ? "" : " *"));
    }

    public void reload(boolean force) {
        if (!force && !saved) {
            if (JOptionPane.showConfirmDialog(Main.f, "Are you sure you want to reload? Your unsaved changes will be lost.", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            textArea.setText("");
            if (file != null) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                textArea.setText(new String(bytes, charset).replace("\r\n", "\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
