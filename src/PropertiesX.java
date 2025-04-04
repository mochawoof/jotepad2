import java.io.*;
import java.util.*;
import javax.swing.*;

class PropertiesX {
    public Properties props;
    public File file;
    public HashMap<String, String> defaults;
    
    public PropertiesX(File f) {
        file = f;
        props = new Properties();
        defaults = new HashMap<String, String>();
        // Add defaults here
        defaults.put("E")

        try {
            FileInputStream in = new FileInputStream(file.getPath());
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        String got = props.getProperty(key);
        if (got == null) {
            return defaults.get(key);
        }
        return got;
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
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            String key = (String) e.getKey();
            String val = (String) e.getValue();

            String[] defs = defaults.get(key).split(",");
            if (key.startsWith(prefix)) {
                if (defs.length == 2 && defs[0].equals("Y") && defs[1].equals("N")) {
                    JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(key.substring(prefix.length()));
                    if (val.equals("Y")) {
                        checkBoxMenuItem.setState(true);
                    }
                    menu.add(checkBoxMenuItem);
                } else {
                    JMenuItem menuItem = new JMenuItem(key);
                    menu.add(menuItem);
                }
            }
        }
        return menu;
    }
}