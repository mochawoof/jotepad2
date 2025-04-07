import javax.swing.*;
import java.awt.*;
import java.io.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

class Tab extends JPanel {
    public RSyntaxTextArea textArea;
    public RTextScrollPane scrollPane;
    public File file;
    public boolean saved = false;
    public String charset;

    public Tab(String aCharset) {
        charset = aCharset;
        setLayout(new BorderLayout());
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);

        scrollPane = new RTextScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}
