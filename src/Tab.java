import javax.swing.*;
import java.awt.*;
import java.io.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

class Tab extends JPanel {
    public RSyntaxTextArea textArea;
    public File file = null;
    public byte[] bytes;

    public Tab() {
        setLayout(new BorderLayout());
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);
        add(new RTextScrollPane(textArea), BorderLayout.CENTER);
    }
}