import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

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
    
    public static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        f = new JFrame("Jotepad 2");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        propsX = new PropertiesX(new File("jotepad2.properties")) {
            public void update() {
                setLaf(get("Theme"));
            }
        };
        setLaf(propsX.get("Theme"));

        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        settingsMenu = propsX.createJMenu("Settings");
        menuBar.add(settingsMenu);
        pluginsMenu = new JMenu("Plugins");
        menuBar.add(pluginsMenu);
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        tabbedPane = new JTabbedPane();
        f.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("New", new Tab());

        f.setVisible(true);
    }

    public static void setLaf(String laf) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
                if (lafInfo.getName().equals(laf)) {
                    UIManager.setLookAndFeel(lafInfo.getClassName());
                    break;
                }
            }
            SwingUtilities.updateComponentTreeUI(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}