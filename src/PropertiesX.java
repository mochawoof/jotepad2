import java.io.*;
import java.util.*;
import javax.swing.*;

class PropertiesX {
    public Properties props;
    public File file;
    
    public PropertiesX(File f) {
        file = f;
        props = new Properties();

        try {
            FileInputStream in = new FileInputStream(file.getPath());
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key, String def) {
        String got = props.getProperty(key);
        if (got == null) {
            return def;
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
            System.out.println(e.getKey());
        }
        return menu;
    }
}