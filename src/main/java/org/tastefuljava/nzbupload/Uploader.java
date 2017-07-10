package org.tastefuljava.nzbupload;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.json.JSon;

public class Uploader implements Runnable {
    private static final Logger LOG = Logger.getLogger(Uploader.class.getName());

    public static enum Property {
        INPUT_DIR, OUTPUT_DIR, REQUEST_URL, API_KEY;

        private final String key = name().toLowerCase().replace('_', '-');

        public static Property forKey(String key) {
            for (Property prop: values()) {
                if (prop.key.equals(key)) {
                    return prop;
                }
            }
            return null;
        }

        public String get(Properties props) {
            return props.getProperty(key);
        }

        public String get(Properties props, String def) {
            return props.getProperty(key, def);
        }

        public void set(Properties props, String value) {
            props.setProperty(key, value);
        }
    }

    private final Properties props;
    private boolean stopped = false;

    public Uploader(Properties props) {
        this.props = new Properties();
        this.props.putAll(props);
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public synchronized void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public void run() {
        do {
            try {
                uploadAll();
                delay(3000L);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
                setStopped(true);
            }
        } while (!isStopped());
    }

    private void delay(long duration) throws InterruptedException {
        Thread.sleep(duration);
    }

    private void uploadAll() throws IOException {
        File inputDir = new File(Property.INPUT_DIR.get(props));
        File outputDir = new File(Property.OUTPUT_DIR.get(props));
        String[] names = findInputFiles(inputDir);
        if (names == null || names.length == 0) {
            LOG.info("no file to upload");
        } else {
            LOG.log(Level.INFO, "{0} files to upload", names.length);
            for (String name: names) {
                LOG.log(Level.INFO, "File to upload: {0}", name);
                File file = new File(inputDir, name);
                if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
                    String msg = "Could not create dir: " + outputDir;
                    LOG.severe(msg);
                    throw new IOException(msg);
                }
                File outFile = new File(outputDir, name);
                if (outFile.exists()) {
                    LOG.log(Level.WARNING, "Overwriting file: {0}", outFile);
                    outFile.delete();
                }
                if (file.renameTo(outFile)) {
                    upload(outFile);
                } else {
                    LOG.log(Level.INFO, "Skipping file: {0}", outFile);
                }
            }
        }
    }

    private String[] findInputFiles(File inputDir) {
        return inputDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isFile() && name.toLowerCase().endsWith(".nzb");
            }
        });
    }

    private void upload(File file) throws IOException {
        String requestURL = Property.REQUEST_URL.get(props);
        Multipart multipart = new Multipart(requestURL, "UTF-8");
        multipart.addField("output", "json");
        multipart.addField("apikey", Property.API_KEY.get(props));
        multipart.addField("mode", "addfile");
        multipart.addFile("name", file);
        String json = multipart.complete();
        Reader reader = new StringReader(json);
        UploadResult result = JSon.read(reader, UploadResult.class);
        if (!result.status) {
            throw new IOException("Upload failed: " + result.error);
        }
    }

    public static class UploadResult {
        public boolean status;
        public String error;
        public String[] nzo_ids;
    }
}
