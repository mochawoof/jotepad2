import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.io.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.rsta.ui.*;

class Main {
    public static final String VERSION = "2.0.1";

    public static JFrame f;
    public static PropertiesX propsX;
    public static JMenuBar menuBar;
    // Menu
    public static JMenu editMenu;
    public static JMenuItem findItem;
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
        f.setSize(800, 600);
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
                DetailsFileChooser chooser = new DetailsFileChooser();
                chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));

                if (chooser.showOpenDialog(f) == DetailsFileChooser.APPROVE_OPTION) {
                    propsX.set("Last Open Directory", chooser.getSelectedFile().getParentFile().getAbsolutePath());

                    openTab(chooser.getSelectedFile());
                }
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(openItem);
        fileMenu.addSeparator();

        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTab(-1, null);
            }
        });
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveItem);

        saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });
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
        closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeTab(-1);
            }
        });
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

        findItem = new JMenuItem("Find");
        findItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        editMenu.add(findItem);

        charsetMenu = new JMenu("Charset");
        editMenu.add(charsetMenu);
        for (String charset : charsets) {
            JCheckBoxMenuItem charsetItem = new JCheckBoxMenuItem(charset);

            charsetItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Tab tab = getSelectedTab();
                    tab.setCharset(charset, false);

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
                        tab.setCharset(fileCharset, true);
                    }
                }
            }
        } else {
            newTab();
        }

        propsX.update();
        updateCharsetMenu();

        f.setVisible(true);
    }

    public static Tab getSelectedTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            return (Tab) tabbedPane.getComponentAt(index);
        } else {
            return null;
        }
    }

    public static void saveAs() {
        DetailsFileChooser chooser = new DetailsFileChooser();
        chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));

        if (chooser.showSaveDialog(f) == DetailsFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(f, "This file already exists. Are you sure you want to replace it?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            saveTab(-1, chooser.getSelectedFile());
        }
    }

    public static void saveTab(int tabIndex, File file) {
        int index = tabIndex;
        File saveFile = file;

        if (tabIndex == -1) {
            // If -1, save current tab
            index = tabbedPane.getSelectedIndex();
        }

        Tab tab = (Tab) tabbedPane.getComponentAt(index);

        if (file == null) {
            saveFile = tab.file;

            if (tab.file == null) {
                saveAs();
                return;
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(saveFile);
            out.write(tab.textArea.getText().getBytes());
            out.close();

            tab.file = saveFile;
            tab.saved = true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(f, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        tab.updateTitle();
        updateSession();
    }

    public static void closeTab(int tabIndex) {
        if (tabbedPane.getTabCount() > 1) {

            int index = tabIndex;

            if (tabIndex == -1) {
                // If -1, close current tab
                index = tabbedPane.getSelectedIndex();
            }

            Tab tab = (Tab) tabbedPane.getComponentAt(index);
            if (!tab.saved) {
                if (JOptionPane.showConfirmDialog(f, "Are you sure you want to close this file? Your unsaved changes will be lost.", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            tabbedPane.remove(index);

            updateCharsetMenu();
            updateSession();
        }
    }

    public static void updateCharsetMenu() {
        Tab tab = getSelectedTab();

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
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));

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
                // Some Linux systems don't bother defining the system laf with GTK+
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