import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

class Main {
    public static JFrame f;
    public static PropertiesX propsX;
    public static JMenuBar menuBar;
    public static JMenu fileMenu;
    //public static JMenu editMenu;
    public static JMenu viewMenu;
    public static JMenu settingsMenu;
    public static JMenu pluginsMenu;
    public static JMenu helpMenu;

    public static void main(String[] args) {
        f = new JFrame("Jotepad 2");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        propsX = new PropertiesX(new File("jotepad2.properties"));
        setLaf("System");

        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        settingsMenu = propsX.createJMenu("Settings", "S");
        menuBar.add(settingsMenu);
        pluginsMenu = new JMenu("Plugins");
        menuBar.add(pluginsMenu);
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        f.setVisible(true);
    }

    public static void setLaf(String laf) {
        try {
            if (laf.equals("System")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (laf.equals("Cross-Platform")) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(laf);
            }
            SwingUtilities.updateComponentTreeUI(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}