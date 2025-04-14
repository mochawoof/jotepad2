import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

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
            public static JMenuItem helpAboutItem;

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
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Apply saved window size
        f.setSize(600, 400);
        try {
            String[] windowSize = propsX.get("Window Size").split("x");
            f.setSize(Integer.parseInt(windowSize[0]), Integer.parseInt(windowSize[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        f.setExtendedState(propsX.get("Window Maximized").equals("Yes") ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);

        // Window listeners
        f.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (f.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    propsX.set("Window Size", f.getWidth() + "x" + f.getHeight());
                }
            }
        });
        f.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if (f.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    propsX.set("Window Maximized", "Yes");
                } else {
                    propsX.set("Window Maximized", "No");
                }
            }
        });
        
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

            viewMenu = propsX.createJMenu("View", "View");
            menuBar.add(viewMenu);

            helpMenu = new JMenu("Help");
            menuBar.add(helpMenu);
                helpAboutItem = new JMenuItem("About");
                helpAboutItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(f, "Jotepad v" + version + "\nJava " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n\nVisit https://github.com/mochawoof/jotepad2 for extra help.", "About Jotepad 2", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
                    }
                });
                helpMenu.add(helpAboutItem);

        f.setVisible(true);
    }

    public static void updateAll() {
        updateLaf();
        updateEditorThemes();
    }

    public static void updateLaf() {
        try {
            String laf = propsX.get("ViewTheme");

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
                boolean lafNameFound = false;
                for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
                    if (lafInfo.getName().equals(laf)) {
                        UIManager.setLookAndFeel(lafInfo.getClassName());
                        lafNameFound = true;
                        break;
                    }
                }

                if (!lafNameFound) {
                    // Assume it is a direct class name
                    UIManager.setLookAndFeel(laf);
                }
            }

            if (f != null) {
                SwingUtilities.updateComponentTreeUI(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateEditorThemes() {
        try {
            Theme theme = Theme.load(Main.class.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/" + propsX.get("ViewEditor Theme").toLowerCase() + ".xml"));
            
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                theme.apply(((Tab) tabbedPane.getComponentAt(i)).textArea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}