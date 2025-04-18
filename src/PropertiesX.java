import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PropertiesX {
    public Properties props;
    public File file = new File("jotepad2.properties");
    public HashMap<String, String> userChoices;
    public String outNote = "Jotepad 2 Properties";
    
    public PropertiesX() {
        props = new Properties();
        userChoices = new HashMap<String, String>();
        
        props.setProperty("Version", Main.version);
        setUserChoice("ViewTheme", "Cross-Platform", "Cross-Platform,System,Nimbus,CDE/Motif,FlatLightLaf,FlatDarkLaf,Tiny");
        setUserChoice("ViewEditor Theme", "Default", "Default,Default-Alt,Dark,Druid,Eclipse,Idea,Monokai,VS");
        props.setProperty("Window Size", "800x400");
        props.setProperty("Window Maximized", "No");
        props.setProperty("Last Open Directory", ".");
        setUserChoice("ViewCharset", "US-ASCII", "US-ASCII,ISO-8859-1,UTF-8,UTF-16BE,UTF-16LE,UTF-16");
        setUserChoice("ViewReload All Tabs on Charset Change", "No", "Yes,No");
        
        setUserChoice("FileSave Session", "No", "Yes,No");
        props.setProperty("Session", "");
        
        try {
            FileInputStream in = new FileInputStream(file.getPath());
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // To override
    }

    private void setUserChoice(String key, String val, String choices) {
        props.setProperty(key, val);
        userChoices.put(key, choices);
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public void set(String key, String val) {
        props.setProperty(key, val);
        save();
    }

    public void save() {
        // Save on new thread to prevent blocking on slow disks
        new Thread() {
            public void run() {
                try {
                    FileOutputStream out = new FileOutputStream(file.getPath());
                    props.store(out, outNote);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public JMenu createJMenu(String title, String prefix, ActionListener customListener) {
        JMenu menu = new JMenu(title);
        for (Map.Entry<String, String> e : userChoices.entrySet()) {
            String key = e.getKey();
            String[] choices = e.getValue().split(",");

            String val = get(key);

            if (key.startsWith(prefix)) {
                String keySubbed = key.substring(prefix.length(), key.length());

                if (choices.length == 2 && ((choices[0].equals("Yes") && choices[1].equals("No")) || (choices[0].equals("On") && choices[1].equals("Off")))) {
                    JCheckBoxMenuItem item = new JCheckBoxMenuItem(keySubbed);
                    item.setActionCommand(key);
                    if (val.equals(choices[0])) {
                        item.setState(true);
                    }
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            set(key, (item.getState() ? choices[0] : choices[1]));
                            update();

                            // Ensure custom is ran after normal listener
                            if (customListener != null) {
                                customListener.actionPerformed(e);
                            }
                        }
                    });

                    menu.add(item);
                } else {
                    JMenu subMenu = new JMenu(keySubbed);
                    for (String c : choices) {
                        JCheckBoxMenuItem item = new JCheckBoxMenuItem(c);
                        item.setActionCommand(key);
                        if (c.equals(val)) {
                            item.setState(true);
                        }
                        item.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                set(key, c);
                                Component[] items = subMenu.getMenuComponents();
                                for (Component i : items) {
                                    JCheckBoxMenuItem ci = (JCheckBoxMenuItem) i;
                                    if (!ci.getText().equals(c)) {
                                        ci.setState(false);
                                    } else {
                                        ci.setState(true);
                                    }
                                }
                                update();

                                // Ensure custom is ran after normal listener
                                if (customListener != null) {
                                    customListener.actionPerformed(e);
                                }
                            }
                        });

                        subMenu.add(item);
                    }
                    menu.add(subMenu);
                }
            }
        }

        return menu;
    }
}