/*
 * CS 193A, Marty Stepp
 * This is just a helper class that we used in the downloader app.
 * It does the actual work of downloading a file from a given URL.
 * Since this part is not Android-specific, we didn't write it in class.
 * It also has "fake" download methods to simulate downloading the file
 * without actually using the network, just for testing.
 */

package com.downloader;

import android.os.Environment;
import android.util.Log;
import org.w3c.dom.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;

public class Downloader {
    /**
     * Downloads the file found at the given URL,
     * into the Android device's Downloads folder in its external storage,
     * and returns the file name it was saved to.
     */
    public static String download(String urlString) {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.v("Downloader", "downloading " + urlString + " to " + folder);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // empty
        }

        if (!folder.exists()) {
            folder.mkdirs();
        }

        // download the file into a memory buffer
        byte[] bytes = downloadToByteArray(urlString);

        // store the memory buffer contents into a file
        File urlFile = new File(urlString);
        String filename = urlFile.getName();
        File outFile = new File(folder, filename);
        try {
            FileOutputStream out = new FileOutputStream(outFile);
            out.write(bytes);
            out.close();
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pretends to download the file found at the given URL,
     * and returns the file name it would have been saved to.
     */
    public static String downloadFake(String urlString) {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File urlFile = new File(urlString);
        String filename = urlFile.getName();
        File outFile = new File(folder, filename);
        Log.v("Downloader", "downloading " + urlString + " to " + outFile);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // empty
        }

        return filename;
    }

    /**
     * Retrieves all of the links like <a href="http://example.com/foo.html">...</a>
     * from the page and returns their href URLs as an array.
     * Doesn't work on some pages due to invalid HTML content.
     */
    public static String[] getAllLinks(String webPageURL) {
        try {
            byte[] bytes = downloadToByteArray(webPageURL);
            ArrayList<String> list = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(bytes));
            NodeList linkNodes = document.getElementsByTagName("a");
            for (int i = 0 ; i < linkNodes.getLength(); i++) {
                Node node = linkNodes.item(i);
                Node hrefNode = node.getAttributes().getNamedItem("href");
                if (hrefNode != null) {
                    String href = hrefNode.getNodeValue();
                    try {
                        URL url = new URL(href);
                        list.add(href);
                    } catch (MalformedURLException mfurle) {
                        // invalid URL; don't add
                    }
                }
            }

            return list.toArray(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the entire contents of the given file from the device's Downloads folder,
     * returning the file's contents as a text string.
     */
    public static String readEntireFile(String filename) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, filename);
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                sb.append((char) reader.read());
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Downloads the file found at the given URL into a memory buffer of bytes,
     * then returns the bytes as an array.
     * This is used internally as a temporary helper method.
     */
    private static byte[] downloadToByteArray(String urlString) {
        try {
            // download the file into a memory buffer
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            URL url = new URL(urlString);
            InputStream stream = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            int ch;
            while ((ch = reader.read()) != -1) {
                bytes.write(ch);
            }
            stream.close();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
