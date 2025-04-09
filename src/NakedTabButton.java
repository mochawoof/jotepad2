import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.*;

class NakedTabButton extends JButton {
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    
    public NakedTabButton(Action a) {
        super(a);

        ButtonTabComponent.doConstructor(this);
    }

    //we don't want to update UI for this button
    public void updateUI() {}

    //paint the cross
    protected void paintComponent(Graphics g) {
        ButtonTabComponent.doPaint(this, g);
    }
}