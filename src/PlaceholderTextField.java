import javax.swing.*;
import java.awt.*;

class PlaceholderTextField extends JTextField {
    public String placeholder = "";

    public PlaceholderTextField(int cols) {
        super(cols);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (getText().isEmpty()) {
            g2d.setColor(UIManager.getColor("Label.disabledForeground"));
            g2d.setFont(getFont());
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            try {
                Rectangle rect = modelToView(0);
                g2d.drawString(placeholder, (int) rect.getX(), (int) rect.getY() + getFont().getSize() + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}