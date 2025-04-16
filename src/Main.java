import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.net.URI;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import org.fife.rsta.ui.search.*;

class Main {
    public static JFrame f;
    public static JTabbedPane tabbedPane;
    public static JMenuBar menuBar;
    public static FindToolBar findBar;
    public static ReplaceToolBar replaceBar;
        public static JMenu fileMenu;
            public static JMenuItem newItem;
            public static JMenuItem openItem;
            public static JMenuItem saveItem;
            public static JMenuItem saveAsItem;
            public static JMenuItem reloadItem;
            public static JMenuItem closeItem;
        public static JMenu editMenu;
            public static JMenuItem findItem;
            public static JMenuItem replaceItem;
        public static JMenu viewMenu;
        public static JMenu pluginsMenu;
            public static JMenuItem noneAvailableItem;
        public static JMenu helpMenu;
            public static JMenuItem onlineHelpItem;
            public static JMenuItem aboutItem;

    public static final String version = "2.3.2";
    public static PropertiesX propsX = new PropertiesX() {
        public void update() {
            updateAll();
        }
    };

    // Icon theme
    public static ImageIcon confirmIcon;

    public static void main(String[] args) {
        // Create components

        f = new JFrame("Jotepad 2");

        f.setIconImage(getImageIcon("icon64.png").getImage());
        confirmIcon = getImageIcon("confirm64.png");

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
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        f.add(tabbedPane, BorderLayout.CENTER);

        SearchListener searchListener = new SearchListener() {
            public void searchEvent(SearchEvent e) {
                SearchEvent.Type type = e.getType();
                SearchContext context = e.getSearchContext();
                Tab tab = getSelectedTab();

                if (tab != null) {
                    if (type == SearchEvent.Type.MARK_ALL) {
                        SearchEngine.markAll(tab.textArea, context);
                    } else if (type == SearchEvent.Type.FIND) {
                        SearchEngine.find(tab.textArea, context);
                    } else if (type == SearchEvent.Type.REPLACE) {
                        SearchEngine.replace(tab.textArea, context);
                    } else if (type == SearchEvent.Type.REPLACE_ALL) {
                        SearchEngine.replaceAll(tab.textArea, context);
                    }
                }
            }

            public String getSelectedText() {
                Tab tab = getSelectedTab();
                if (tab != null) {
                    return tab.textArea.getSelectedText();
                }
                return "";
            }
        };

        findBar = new FindToolBar(searchListener);
        replaceBar = new ReplaceToolBar(searchListener);

        // Menu bar
        menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);
            fileMenu = new JMenu("File");
            menuBar.add(fileMenu);
                newItem = new JMenuItem("New");
                newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
                newItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        newTab();
                    }
                });
                fileMenu.add(newItem);
                openItem = new JMenuItem("Open");
                openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
                openItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openAs();
                    }
                });
                fileMenu.add(openItem);
                fileMenu.addSeparator();
                saveItem = new JMenuItem("Save");
                saveItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        save(tabbedPane.getSelectedIndex(), (getSelectedTab()).file);
                    }
                });
                saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
                fileMenu.add(saveItem);
                saveAsItem = new JMenuItem("Save As");
                saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
                saveAsItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveAs();
                    }
                });
                fileMenu.add(saveAsItem);
                reloadItem = new JMenuItem("Reload");
                reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
                reloadItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        load(tabbedPane.getSelectedIndex());
                    }
                });
                fileMenu.add(reloadItem);
                fileMenu.addSeparator();
                closeItem = new JMenuItem("Close");
                closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
                closeItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeCurrent();
                    }
                });
                fileMenu.add(closeItem);

            editMenu = new JMenu("Edit");
            menuBar.add(editMenu);
                findItem = new JMenuItem("Find");
                findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
                findItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeFindReplace();
                        f.add(findBar, BorderLayout.PAGE_START);
                        f.revalidate(); f.repaint();
                    }
                });
                editMenu.add(findItem);

                replaceItem = new JMenuItem("Replace");
                replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
                replaceItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeFindReplace();
                        f.add(replaceBar, BorderLayout.PAGE_START);
                        f.revalidate(); f.repaint();
                    }
                });
                editMenu.add(replaceItem);

            viewMenu = propsX.createJMenu("View", "View", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("ViewCharset")) {
                        if (propsX.get("ViewReload All Tabs on Charset Change").equals("Yes")) {
                            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                                load(i);
                            }
                        }
                    }
                }
            });
            menuBar.add(viewMenu);

            pluginsMenu = new JMenu("Plugins");
            menuBar.add(pluginsMenu);
                noneAvailableItem = new JMenuItem("None Available");
                noneAvailableItem.setEnabled(false);
                pluginsMenu.add(noneAvailableItem);

            helpMenu = new JMenu("Help");
            menuBar.add(helpMenu);
                onlineHelpItem = new JMenuItem("Online Help");
                onlineHelpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
                onlineHelpItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/mochawoof/jotepad2"));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                helpMenu.add(onlineHelpItem);

                aboutItem = new JMenuItem("About");
                aboutItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(f, "Jotepad v" + version + "\nJava " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"), "About Jotepad 2", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
                    }
                });
                helpMenu.add(aboutItem);

        updateAll();
        
        // Open provided file
        if (args.length > 0) {
            open(new File(args[0]));
        } else {
            newTab();
        }

        f.setVisible(true);
    }

    public static void removeFindReplace() {
        f.remove(findBar);
        f.remove(replaceBar);
    }

    public static Tab getSelectedTab() {
        return (Tab) tabbedPane.getSelectedComponent();
    }

    public static boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(f, message, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, confirmIcon) == JOptionPane.YES_OPTION;
    }

    public static ImageIcon getImageIcon(String url) {
        try {
            return new ImageIcon(Main.class.getResource(url).toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

            SwingUtilities.updateComponentTreeUI(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Tab newTab() {
        Tab tab = new Tab();
        tabbedPane.add("New", tab);

        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        updateEditorThemes();
        return tab;
    }

    public static String getSyntaxType(String ext) {
        SyntaxTypes syntaxTypes = new SyntaxTypes();

        try {
            Field[] types = SyntaxTypes.class.getDeclaredFields();
            for (Field f : types) {
                String[] typePair = ((String) f.get(syntaxTypes)).split(";");
                if (typePair.length == 2) {
                    String[] exts = typePair[1].split(",");
                
                    for (String e : exts) {
                        if (e.equals(ext)) {
                            return typePair[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return syntaxTypes.SYNTAX_STYLE_NONE;
    }

    public static void updateTitle(int i) {
        Tab tab = (Tab) tabbedPane.getComponentAt(i);

        if (tab.file != null) {
            tabbedPane.setTitleAt(i, tab.file.getName());
            tab.textArea.setSyntaxEditingStyle(getSyntaxType(tab.file.getName().substring(tab.file.getName().lastIndexOf(".") + 1, tab.file.getName().length())));
        } else {
            tab.textArea.setSyntaxEditingStyle(new SyntaxTypes().SYNTAX_STYLE_NONE);
        }

        SwingUtilities.updateComponentTreeUI(f);
    }

    public static void load(int i) {
        Tab tab = (Tab) tabbedPane.getComponentAt(i);

        if (tab.file != null) {
            try {
                updateTitle(i);
                tab.bytes = Files.readAllBytes(tab.file.toPath());
                loadIntoEditor(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadIntoEditor(int i) {
        try {
            Tab tab = (Tab) tabbedPane.getComponentAt(i);
            if (tab.file != null) {
                tab.textArea.setText(new String(tab.bytes, propsX.get("ViewCharset")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void open(File file) {
        Tab tab = newTab();
        tab.file = file;
        load(tabbedPane.getTabCount() - 1);
    }

    public static void save(int i, File file) {
        Tab tab = (Tab) tabbedPane.getComponentAt(i);
        
        if (tab != null && file != null) {
            try {
                tab.file = file;
                updateTitle(i);

                FileOutputStream out = new FileOutputStream(file);
                out.write(tab.textArea.getText().getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            saveAs();
        }
    }

    public static void saveAs() {
        int i = tabbedPane.getSelectedIndex();
        Tab tab = getSelectedTab();

        if (tab != null) {
            DetailsFileChooser chooser = new DetailsFileChooser();
            chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));
            if (tab.file != null) {
                chooser.setCurrentDirectory(tab.file.getParentFile());
            }

            if (chooser.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
                save(i, chooser.getSelectedFile());
            }
        }
    }

    public static void openAs() {
        DetailsFileChooser chooser = new DetailsFileChooser();
        chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));

        if (chooser.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
            open(chooser.getSelectedFile());
            propsX.set("Last Open Directory", chooser.getSelectedFile().getParent());
        }
    }

    public static void close(int i) {
        if (tabbedPane.getTabCount() > 1) {
            tabbedPane.remove(i);
        }
    }

    public static void closeCurrent() {
        close(tabbedPane.getSelectedIndex());
    }
}