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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.tastefuljava.nzbupload.Uploader.Property;

public class UploadDialog extends JDialog {
    private static final Logger LOG
            = Logger.getLogger(UploadDialog.class.getName());


    private final Properties props = new Properties();
    private final String start;
    private final String stop;
    private Uploader uploader;

    public UploadDialog(java.awt.Frame parent) throws IOException {
        super(parent, true);
        initComponents();
        String text = startStop.getText();
        int ix = text.indexOf('/');
        start = text.substring(0, ix);
        stop = text.substring(ix+1);
        startStop.setText(start);
        propsToScreen();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        changeInput = new javax.swing.JButton();
        input = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        changeOutput = new javax.swing.JButton();
        output = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        requestURL = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        apiKey = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        startStop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Input folder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        getContentPane().add(jLabel1, gridBagConstraints);

        changeInput.setText("Browse...");
        changeInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 11);
        getContentPane().add(changeInput, gridBagConstraints);

        input.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        getContentPane().add(input, gridBagConstraints);

        jLabel3.setText("Output folder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        getContentPane().add(jLabel3, gridBagConstraints);

        changeOutput.setText("Browse...");
        changeOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeOutputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 11);
        getContentPane().add(changeOutput, gridBagConstraints);

        output.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        getContentPane().add(output, gridBagConstraints);

        jLabel2.setText("Request URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        getContentPane().add(jLabel2, gridBagConstraints);

        requestURL.setText("http://localhost:8800/sabnzbd/api");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        getContentPane().add(requestURL, gridBagConstraints);

        jLabel4.setText("NZB Key:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        getContentPane().add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        getContentPane().add(apiKey, gridBagConstraints);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        startStop.setText("Start/Stop");
        startStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopActionPerformed(evt);
            }
        });
        jPanel1.add(startStop);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 11, 11);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeInputActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(new File(input.getText()));
        chooser.setDialogTitle("Input folder");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                input.setText(filename);
                screenToProps();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Exception was raised", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_changeInputActionPerformed

    private void changeOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeOutputActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(new File(output.getText()));
        chooser.setDialogTitle("Output folder");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                output.setText(filename);
                screenToProps();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Exception was raised", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_changeOutputActionPerformed

    private void startStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopActionPerformed
        if (uploader == null) {
            start();
        } else {
            stop();
        }
    }//GEN-LAST:event_startStopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField apiKey;
    private javax.swing.JButton changeInput;
    private javax.swing.JButton changeOutput;
    private javax.swing.JLabel input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel output;
    private javax.swing.JTextField requestURL;
    private javax.swing.JButton startStop;
    // End of variables declaration//GEN-END:variables

    private void loadConf(String name) throws IOException {
        props.putAll(Configuration.load(name));
    }

    private void saveConf(String name) throws IOException {
        Configuration.save(props, name);
    }

    private void propsToScreen() throws IOException {
        loadConf(Configuration.CONF_FILE);
        String inputDir = new File(Configuration.HOME, "Downloads").toString();
        input.setText(Property.INPUT_DIR.get(props, inputDir));
        String outputDir = new File(inputDir, "_nzb").toString();
        output.setText(Property.OUTPUT_DIR.get(props, outputDir));
        String url = Property.REQUEST_URL.get(props,
                "http://localhost:8800/sabnzbd/api");
        requestURL.setText(url);
        String key = Property.API_KEY.get(props, "");
        apiKey.setText(key);
        pack();
    }

    private void screenToProps() throws IOException {
        Property.INPUT_DIR.set(props, input.getText());
        Property.OUTPUT_DIR.set(props, output.getText());
        Property.REQUEST_URL.set(props, requestURL.getText());
        Property.API_KEY.set(props, apiKey.getText());
        saveConf(Configuration.CONF_FILE);
    }

    private void start() {
        try {
            screenToProps();
            uploader = new Uploader(props);
            Thread thread = new Thread(uploader);
            thread.setDaemon(true);
            thread.start();
            startStop.setText(stop);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Exception was raised", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stop() {
        uploader.setStopped(true);
        uploader = null;
        startStop.setText(start);
    }
}
