import javax.swing.*;
import java.awt.event.*;

class DetailsFileChooser extends JFileChooser {
    public DetailsFileChooser() {
        Action detailsAction = getActionMap().get("viewTypeDetails");
        if (detailsAction != null) {detailsAction.actionPerformed(null);}
    }
}