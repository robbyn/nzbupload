package org.tastefuljava.nzbupload;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        initLogging();
        UploadDialog.showDialog();
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
