import javax.swing.*;
import java.awt.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

class Tab extends JPanel {
    public RSyntaxTextArea textArea;
    public Tab() {
        setLayout(new BorderLayout());
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);
        add(new RTextScrollPane(textArea), BorderLayout.CENTER);
    }
}