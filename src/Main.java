import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

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
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        propsX = new PropertiesX(new File("jotepad2.properties")) {
            public void update() {
                setLaf(get("ViewTheme"));
            }
        };
        setLaf(propsX.get("ViewTheme"));

        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);

        // Menu
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        newItem = new JMenuItem("New");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Tab(tabbedPane);
            }
        });
        fileMenu.add(newItem);
        openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Tab tab = new Tab(tabbedPane);
                tab.updateFile(new File("Main.java"));
                tab.reload();
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(openItem);
        reloadItem = new JMenuItem("Reload");
        reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        reloadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Tab) tabbedPane.getSelectedComponent()).reload();
            }
        });
        fileMenu.add(reloadItem);
        closeItem = new JMenuItem("Close");
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(closeItem);

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

        new Tab(tabbedPane);

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
