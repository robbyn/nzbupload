package org.tastefuljava.nzbupload;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.tastefuljava.nzbupload.Uploader.Property;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        initLogging();
        try {
            boolean doDialog = false;
            Properties props = Configuration.load(Configuration.CONF_FILE);
            props.putAll(System.getProperties());
            int st = 0;
            Property prop = null;
            for (String arg: args) {
                switch (st) {
                    case 0:
                        if (arg.equals("--dialog")) {
                            doDialog = true;
                        } else if (arg.startsWith("--")) {
                            prop = Property.forKey(arg.substring(2));
                            if (prop == null) {
                                LOG.log(Level.SEVERE,
                                        "Unknown option: {0}", arg);
                            } else {
                                st = 1;
                            }
                        }
                        break;
                    case 1:
                        assert prop != null;
                        prop.set(props, arg);
                        st = 0;
                        break;
                }
            }
            if (doDialog) {
                UploadDialog dialog = new UploadDialog(new JFrame());
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            } else {
                // This is especially useful on Mac OS to avoid the default app
                // to be launched and appear in the dock and in the menu bar
                System.setProperty("java.awt.headless", "true");
                new Uploader(props).run();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private static void initLogging() {
        if (System.getProperty("java.util.logging.config.file") == null) {
            // Use default logging configuration
            try (InputStream inputStream = Main.class.getResourceAsStream(
                    "default-logging.properties")) {
                LogManager.getLogManager().readConfiguration(inputStream);
            } catch (final IOException e) {
                LOG.severe(e.getMessage());
            }
        }
    }
}
