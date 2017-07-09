package org.tastefuljava.nzbupload;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Multipart implements Closeable {
    private static final String EOL = "\r\n";

    private final String boundary;
    private final HttpURLConnection cnt;
    private final String encoding;
    private final OutputStream stream;
    private final Writer out;

    @Override
    public void close() throws IOException {
        try {
            out.close();
        } finally {
            cnt.disconnect();
        }
    }

    public Multipart(String requestURL, String encoding)
            throws IOException {
        this.encoding = encoding;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        cnt = (HttpURLConnection) url.openConnection();
        cnt.setUseCaches(false);
        cnt.setDoOutput(true); // indicates POST method
        cnt.setDoInput(true);
        cnt.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        stream = cnt.getOutputStream();
        Writer writer = new OutputStreamWriter(stream, encoding);
        out = new PrintWriter(new BufferedWriter(writer));
    }

    public void addFormField(String name, String value) throws IOException {
        println("--" + boundary);
        println("Content-Disposition: form-data; name=\"" + name + "\"");
        println("Content-Type: text/plain; charset=" + encoding);
        println();
        println(value);
    }

    public void addFilePart(String name, File file)
            throws IOException {
        String fileName = file.getName();
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        println("--" + boundary);
        println("Content-Disposition: form-data; name=\"" + name
                        + "\"; filename=\"" + fileName + "\"");
        println("Content-Type: " + mimeType);
        println("Content-Transfer-Encoding: binary");
        println();
        out.flush();

        try (FileInputStream in = new FileInputStream(file)) {
            byte[] buff = new byte[4096];
            for (int n = in.read(buff); n > -1; n = in.read(buff)) {
                stream.write(buff, 0, n);
            }
        }

        println();
    }

    public void addHeaderField(String name, String value) throws IOException {
        println(name + ": " + value);
    }

    public String complete() throws IOException {
        println();
        println("--" + boundary + "--");
        out.flush();

        // checks server's status code first
        int status = cnt.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned non-OK status: " + status);
        }
        try (InputStream istream = cnt.getInputStream();
                InputStreamReader reader
                    = new InputStreamReader(istream, encoding);
                BufferedReader in = new BufferedReader(reader)) {
            StringBuilder response = new StringBuilder();
            for (String line = in.readLine(); line != null;
                    line = in.readLine()) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void println(String s) throws IOException {
        out.write(s);
        println();
    }

    private void println() throws IOException {
        out.write(EOL);
    }
}