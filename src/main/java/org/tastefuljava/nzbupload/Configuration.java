package org.tastefuljava.nzbupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Configuration {
    public static final String HOME = System.getProperty("user.home");
    public static final String CONF_FILE = "nzbupload.properties";

    public static Properties load(String name) throws IOException {
        Properties props = new Properties();
        File file = new File(HOME, name);
        if (file.isFile()) {
            try (InputStream stream = new FileInputStream(file);
                    Reader in = new InputStreamReader(stream, "UTF-8")) {
                props.load(in);
            }
        }
        return props;
    }

    public static void save(Properties props, String name) throws IOException {
        File file = new File(Configuration.HOME, name);
        try (OutputStream stream = new FileOutputStream(file);
                Writer out = new OutputStreamWriter(stream, "UTF-8")) {
            props.store(out, "NZB Uploader configuration");
        }
    }
}
