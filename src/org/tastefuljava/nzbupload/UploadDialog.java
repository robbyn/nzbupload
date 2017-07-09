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

public class UploadDialog extends JDialog {
    private static final Logger LOG
            = Logger.getLogger(UploadDialog.class.getName());

    private static final String CONF_FILE = "nzbupload.properties";
    private static final String HOME = System.getProperty("user.home");
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

    public static void showDialog() {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UploadDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UploadDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UploadDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UploadDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadDialog dialog = new UploadDialog(new javax.swing.JFrame());
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeInput;
    private javax.swing.JButton changeOutput;
    private javax.swing.JLabel input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel output;
    private javax.swing.JButton startStop;
    // End of variables declaration//GEN-END:variables

    private void loadConf(String name) throws IOException {
        File file = new File(HOME, name);
        if (file.isFile()) {
            try (InputStream stream = new FileInputStream(file);
                    Reader in = new InputStreamReader(stream, "UTF-8")) {
                props.load(in);
            }
        }
    }

    private void saveConf(String name) throws IOException {
        File file = new File(HOME, name);
        try (OutputStream stream = new FileOutputStream(file);
                Writer out = new OutputStreamWriter(stream, "UTF-8")) {
            props.store(out, "NZB Uploader configuration");
        }
    }

    private void propsToScreen() throws IOException {
        loadConf(CONF_FILE);
        String inputDir = new File(HOME, "Downloads").toString();
        input.setText(props.getProperty(Uploader.PROP_INPUTDIR, inputDir));
        String outputDir = new File(inputDir, "_nzb").toString();
        output.setText(props.getProperty(Uploader.PROP_OUTPUTDIR, outputDir));
        pack();
    }

    private void screenToProps() throws IOException {
        props.setProperty(Uploader.PROP_INPUTDIR, input.getText());
        props.setProperty(Uploader.PROP_OUTPUTDIR, output.getText());
        saveConf(CONF_FILE);
    }

    private void start() {
        try {
            screenToProps();
            uploader = new Uploader(props);
            uploader.start();
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
