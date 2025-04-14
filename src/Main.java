import javax.swing.*;
import java.awt.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

class Main {
    public static JFrame f;
    public static JTabbedPane tabbedPane;
    public static JMenuBar menuBar;
        public static JMenu fileMenu;
        public static JMenu editMenu;
        public static JMenu viewMenu;
        public static JMenu helpMenu;

    public static final String version = "2.3";
    public static PropertiesX propsX = new PropertiesX() {
        public void update() {
            updateAll();
        }
    };

    public static void main(String[] args) {
        // Set laf
        updateLaf();

        // Create components

        f = new JFrame("Jotepad 2");
        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        f.setSize(600, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tabbedPane = new JTabbedPane();
        f.add(tabbedPane, BorderLayout.CENTER);

            tabbedPane.addTab("New", new Tab());
            updateEditorThemes();

        // Menu bar
        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);
            fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            editMenu = new JMenu("Edit");
            menuBar.add(editMenu);

            viewMenu = new JMenu("View");
            menuBar.add(viewMenu);

            helpMenu = new JMenu("Help");
            menuBar.add(helpMenu);

        f.setVisible(true);
    }

    public static void updateAll() {
        updateLaf();
        updateEditorThemes();
    }

    public static void updateLaf() {
        try {
            String laf = propsX.get("viewTheme");

            if (laf.equals("Cross-Platform")) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } else if (laf.equals("System")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (laf.equals("FlatLightLaf")) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } else if (laf.equals("FlatDarkLaf")) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            } else {
                // Look in installed lafs
                for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
                    if (lafInfo.getName().equals(laf)) {
                        UIManager.setLookAndFeel(lafInfo.getClassName());
                        return;
                    }
                }

                // Assume it is a direct class name
                UIManager.setLookAndFeel(laf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateEditorThemes() {
        try {
            Theme theme = Theme.load(Main.class.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/" + propsX.get("viewEditor Theme").toLowerCase() + ".xml"));
            
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                theme.apply(((Tab) tabbedPane.getComponentAt(i)).textArea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}