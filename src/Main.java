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
    // Menu
    public static JMenu editMenu;

    public static JMenu fileMenu;
    public static JMenuItem newItem;
    public static JMenuItem openItem;
    public static JMenuItem reloadItem;
    public static JMenuItem closeItem;

    public static JMenu viewMenu;

    public static JMenu pluginsMenu;
    public static JMenuItem noneAvailableItem;

    public static JMenu helpMenu;
    public static JMenuItem aboutItem;
    //
    
    public static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        f = new JFrame("Jotepad 2");
        f.setSize(600, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        propsX = new PropertiesX(new File("jotepad2.properties")) {
            public void update() {
                setLaf(get("ViewTheme"));
                setAllTextAreasTheme(propsX.get("ViewEditor Theme"));
            }
        };
        propsX.update();

        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);

        // Menu
        fileMenu = propsX.createJMenu("File", "File");
        menuBar.add(fileMenu);
        newItem = new JMenuItem("New");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Tab();
            }
        });
        fileMenu.add(newItem);
        openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(propsX.get("LastOpenDirectory")));

                Action detailsAction = chooser.getActionMap().get("viewTypeDetails");
                if (detailsAction != null) {detailsAction.actionPerformed(null);}

                if (chooser.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                    propsX.set("LastOpenDirectory", chooser.getSelectedFile().getAbsolutePath());

                    Tab tab = new Tab();
                    tab.updateFile(chooser.getSelectedFile());
                    tab.reload(true);
                }
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(openItem);
        reloadItem = new JMenuItem("Reload");
        reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        reloadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Tab) tabbedPane.getSelectedComponent()).reload(false);
            }
        });
        fileMenu.add(reloadItem);
        closeItem = new JMenuItem("Close");
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(closeItem);
        // Move save session item to end
        JMenuItem saveSessionItemTmp = fileMenu.getItem(0);
        fileMenu.remove(0);
        fileMenu.add(saveSessionItemTmp);

        editMenu = new JMenu("Edit");
        //TODO
        menuBar.add(editMenu);

        viewMenu = propsX.createJMenu("View", "View");
        menuBar.add(viewMenu);

        pluginsMenu = new JMenu("Plugins");
        menuBar.add(pluginsMenu);
        noneAvailableItem = new JMenuItem("None Available");
        noneAvailableItem.setEnabled(false);
        pluginsMenu.add(noneAvailableItem);

        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f, "Jotepad v2.0", "About Jotepad 2", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
            }
        });
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.add(aboutItem);
        //

        tabbedPane = new JTabbedPane();
        f.add(tabbedPane, BorderLayout.CENTER);

        new Tab();

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

    public static Theme getThemeFromName(String themeName) {
        try {
            Theme theme = Theme.load(Main.class.getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/" + themeName.toLowerCase() + ".xml"));
            return theme;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setAllTextAreasTheme(String themeName) {
        if (tabbedPane != null) {
            Theme theme = getThemeFromName(Main.propsX.get("ViewEditor Theme"));
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                ((Tab) tabbedPane.getComponentAt(i)).setTheme(theme);
            }
        }
    }
}