import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

class Main {
    public static JFrame f;
    public static JMenuBar menuBar;
    public static JMenu fileMenu;
    //public static JMenu editMenu;
    public static JMenu viewMenu;
    public static JMenu pluginsMenu;
    public static JMenu helpMenu;

    public static void main(String[] args) {
        f = new JFrame("Jotepad 2");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);

        f.setVisible(true);
    }
}