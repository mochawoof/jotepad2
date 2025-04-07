import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PropertiesX {
    public Properties props;
    public File file;
    public HashMap<String, String> userChoices;
    
    public PropertiesX(File f) {
        file = f;
        props = new Properties();
        userChoices = new HashMap<String, String>();
        
        setUserChoice("ViewTheme", "System", "System,Metal,Nimbus,CDE/Motif");

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
        try {
            FileOutputStream out = new FileOutputStream(file.getPath());
            props.store(out, "");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JMenu createJMenu(String title, String prefix) {
        JMenu menu = new JMenu(title);
        for (Map.Entry<String, String> e : userChoices.entrySet()) {
            String key = e.getKey();
            String[] choices = e.getValue().split(",");

            String val = get(key);

            if (key.startsWith(prefix)) {
                String keySubbed = key.substring(prefix.length(), key.length());

                if (choices.length == 2 && ((choices[0].equals("Yes") && choices[1].equals("No")) || (choices[0].equals("On") && choices[1].equals("Off")))) {
                    JCheckBoxMenuItem item = new JCheckBoxMenuItem(keySubbed);
                    if (val.equals(choices[0])) {
                        item.setState(true);
                    }
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            set(key, (item.getState() ? choices[0] : choices[1]));
                            update();
                        }
                    });
                    menu.add(item);
                } else {
                    JMenu subMenu = new JMenu(keySubbed);
                    for (String c : choices) {
                        JCheckBoxMenuItem item = new JCheckBoxMenuItem(c);
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