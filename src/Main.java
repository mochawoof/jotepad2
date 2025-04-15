import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

class Main {
    public static JFrame f;
    public static JTabbedPane tabbedPane;
    public static JMenuBar menuBar;
        public static JMenu fileMenu;
            public static JMenuItem newItem;
            public static JMenuItem openItem;
            public static JMenuItem saveItem;
            public static JMenuItem saveAsItem;
            public static JMenuItem closeItem;
        public static JMenu editMenu;
        public static JMenu viewMenu;
        public static JMenu helpMenu;
            public static JMenuItem aboutItem;

    public static final String version = "2.3";
    public static PropertiesX propsX = new PropertiesX() {
        public void update() {
            updateAll();
        }
    };

    public static void main(String[] args) {
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
                        save(tabbedPane.getSelectedIndex(), ((Tab) tabbedPane.getSelectedComponent()).file);
                    }
                });
                saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
                fileMenu.add(saveItem);
                saveAsItem = new JMenuItem("Save As");
                saveAsItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveAs();
                    }
                });
                saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
                fileMenu.add(saveAsItem);
                fileMenu.addSeparator();
                closeItem = new JMenuItem("Close");
                closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
                closeItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        close();
                    }
                });
                fileMenu.add(closeItem);

            editMenu = new JMenu("Edit");
            menuBar.add(editMenu);

            viewMenu = propsX.createJMenu("View", "View");
            menuBar.add(viewMenu);

            helpMenu = new JMenu("Help");
            menuBar.add(helpMenu);
                aboutItem = new JMenuItem("About");
                aboutItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(f, "Jotepad v" + version + "\nJava " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n\nVisit https://github.com/mochawoof/jotepad2 for extra help.", "About Jotepad 2", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
                    }
                });
                helpMenu.add(aboutItem);

        updateAll();
        
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

    public static Tab newTab() {
        Tab tab = new Tab();
        tabbedPane.add("New", tab);
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
    }

    public static void load(int i) {
        Tab tab = (Tab) tabbedPane.getComponentAt(i);

        if (tab.file != null) {
            try {
                updateTitle(i);
                tab.textArea.setText(new String(Files.readAllBytes(tab.file.toPath())));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        Tab tab = (Tab) tabbedPane.getSelectedComponent();

        if (tab != null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));
            if (tab.file != null) {
                chooser.setCurrentDirectory(tab.file.getParentFile());
            }

            if (chooser.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                save(i, chooser.getSelectedFile());
            }
        }
    }

    public static void openAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(propsX.get("Last Open Directory")));

        if (chooser.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
            open(chooser.getSelectedFile());
            propsX.set("Last Open Directory", chooser.getSelectedFile().getParent());
        }
    }

    public static void close() {
        if (tabbedPane.getTabCount() > 1) {
            tabbedPane.remove(tabbedPane.getSelectedIndex());
        }
    }
}