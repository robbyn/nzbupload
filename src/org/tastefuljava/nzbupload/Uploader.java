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

public class Uploader extends Thread {
    private static final Logger LOG = Logger.getLogger(Uploader.class.getName());

    public static final String PROP_INPUTDIR = "input-dir";
    public static final String PROP_OUTPUTDIR = "output-dir";

    private final Properties props;
    private boolean stopped = false;

    public Uploader(Properties props) {
        this.props = new Properties();
        this.props.putAll(props);
        this.setDaemon(true);
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
        sleep(duration);
    }

    private void uploadAll() throws IOException {
        File inputDir = new File(props.getProperty(PROP_INPUTDIR));
        File outputDir = new File(props.getProperty(PROP_OUTPUTDIR));
        String[] names = findInputFiles(inputDir);
        if (names == null || names.length == 0) {
            LOG.info("no file to upload");
        } else {
            LOG.log(Level.INFO, "{0} files to upload", names.length);
            for (String name: names) {
                LOG.log(Level.INFO, "File to upload: {0}", name);
                File file = new File(inputDir, name);
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
        String requestURL = props.getProperty("request-url");
        Multipart multipart = new Multipart(requestURL, "UTF-8");
        multipart.addFormField("output", "json");
        multipart.addFormField("apikey", props.getProperty("api-key"));
        multipart.addFormField("mode", "addfile");
        multipart.addFilePart("name", file);
        String json = multipart.complete();
        Reader reader = new StringReader(json);
        UploadResult result = JSon.parse(reader, UploadResult.class);
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
