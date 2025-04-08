import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.io.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

class Main {
    public static final String VERSION = "2.0";

    public static JFrame f;
    public static PropertiesX propsX;
    public static JMenuBar menuBar;
    // Menu
    public static JMenu editMenu;
    public static JMenu charsetMenu;
    public static String[] charsets = new String[] {"UTF-8", "US-ASCII", "ISO-8859-1", "UTF-16", "UTF-16BE", "UTF-16LE"};

    public static JMenu fileMenu;
    public static JMenuItem newItem;
    public static JMenuItem openItem;
    public static JMenuItem saveItem;
    public static JMenuItem saveAsItem;
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

        printLafs();
        propsX = new PropertiesX(new File("jotepad2.properties")) {
            public void update() {
                setLaf(get("ViewTheme"));
                setAllTextAreasTheme(propsX.get("ViewEditor Theme"));

                if (get("FileSave Session").equals("Yes")) {
                    updateSession();
                } else {
                    set("Session", "");
                }
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
                newTab();
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

                    openTab(chooser.getSelectedFile());
                }
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(openItem);
        fileMenu.addSeparator();

        saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveItem);

        saveAsItem = new JMenuItem("Save As");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();

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
        fileMenu.addSeparator();

        // Move save session item to end
        JMenuItem saveSessionItemTmp = fileMenu.getItem(0);
        fileMenu.remove(0);
        fileMenu.add(saveSessionItemTmp);

        // Edit menu
        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        charsetMenu = new JMenu("Charset");
        editMenu.add(charsetMenu);
        for (String charset : charsets) {
            JCheckBoxMenuItem charsetItem = new JCheckBoxMenuItem(charset);

            charsetItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Tab tab = (Tab) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                    tab.setCharset(charset);

                    updateCharsetMenu();
                }
            });

            charsetMenu.add(charsetItem);
        }
        //

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
                JOptionPane.showMessageDialog(f, "Jotepad v" + VERSION, "About Jotepad 2", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
            }
        });
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.add(aboutItem);
        //

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateCharsetMenu();
                updateSession();
            }
        });
        f.add(tabbedPane, BorderLayout.CENTER);

        if (propsX.get("FileSave Session").equals("Yes") && !propsX.get("Session").isEmpty()) {
            for (String fileInfo : propsX.get("Session").split(",")) {
                String[] fileInfoSplit = fileInfo.split(";");

                if (fileInfoSplit.length != 2) {
                    newTab();
                } else {
                    String fileName = fileInfoSplit[0];
                    String fileCharset = fileInfoSplit[1];

                    if (fileName.isEmpty()) {
                        newTab();
                    } else {
                        Tab tab = openTab(new File(fileName));
                        tab.setCharset(fileCharset);
                    }
                }
            }
        } else {
            newTab();
        }
        updateCharsetMenu();

        f.setVisible(true);
    }

    public static void updateCharsetMenu() {
        Tab tab = (Tab) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());

        for (Component i : charsetMenu.getMenuComponents()) {
            JCheckBoxMenuItem ci = (JCheckBoxMenuItem) i;

            if (!ci.getText().equals(tab.charset)) {
                ci.setState(false);
            } else {
                ci.setState(true);
            }
        }
    }

    public static void updateSession() {
        if (tabbedPane != null && propsX.get("FileSave Session").equals("Yes")) {
            String session = "";
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Tab tab = (Tab) tabbedPane.getComponentAt(i);
                if (tab.file != null) {
                    session += (session.isEmpty() ? "" : ",") + tab.file.getAbsolutePath() + ";" + tab.charset;
                } else {
                    session += (session.isEmpty() ? "" : ",") + "New";
                }
            }
            propsX.set("Session", session);
        }
    }

    public static Tab newTab() {
        Tab tab = new Tab();
        tabbedPane.addTab("", tab);
        tab.updateTitle();
        tabbedPane.setSelectedIndex(Main.tabbedPane.getTabCount() - 1);
        
        updateSession();
        return tab;
    }

    public static Tab openTab(File file) {
        Tab tab = newTab();
        tab.updateFile(file);
        tab.reload(true);

        updateSession();
        return tab;
    }

    public static void printLafs() {
        String lafs = "Available lafs: ";
        int i = 0;
        for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
            lafs += (i == 0 ? "" : ", ") + lafInfo.getName();
            i++;
        }
        System.out.println(lafs);
        System.out.println("System: " + UIManager.getSystemLookAndFeelClassName() + " Cross-Platform: " + UIManager.getCrossPlatformLookAndFeelClassName());
    }

    public static void setLaf(String laf) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            if (laf.equals("System")) {
                if (UIManager.getSystemLookAndFeelClassName().endsWith("MetalLookAndFeel")) {
                    setLaf("GTK+");
                    return;
                } else {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } else if (laf.equals("FlatLightLaf") || laf.equals("FlatDarkLaf")) {
                UIManager.setLookAndFeel("com.formdev.flatlaf." + laf);
            } else {
                for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
                    if (lafInfo.getName().equals(laf)) {
                        UIManager.setLookAndFeel(lafInfo.getClassName());
                        break;
                    }
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