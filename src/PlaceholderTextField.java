import javax.swing.*;
import java.awt.*;

class PlaceholderTextField extends JTextField {
    public String placeholder;
    public boolean placeholderVisible = true;

    public PlaceholderTextField(int l) {
        super(l);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            g.setFont(getFont());
            g.setColor(UIManager.getColor("Label.disabledForeground"));
            g.drawString(placeholder, 2, getHeight() - 9);
        }
    }
}