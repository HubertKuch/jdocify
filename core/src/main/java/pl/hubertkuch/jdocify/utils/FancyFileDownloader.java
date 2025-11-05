package pl.hubertkuch.jdocify.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * A utility class to download files with a fancy progress bar in the console
 * and structured logging using SLF4J and MDC.
 */
public class FancyFileDownloader {

    private static final Logger log = LoggerFactory.getLogger(FancyFileDownloader.class);

    /**
     * Downloads a file from a given URL to a destination path.
     *
     * @param fileUrl     The URL of the file to download.
     * @param destination The local path to save the file.
     */
    public static void downloadFile(String fileUrl, Path destination) {
        MDC.put("fileUrl", fileUrl);
        MDC.put("destinationFile", destination.toString());
        log.info("Download initiated.");

        try {
            URL url = URI.create(fileUrl).toURL();
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                long totalFileSize = httpConn.getContentLengthLong();
                long startTime = System.currentTimeMillis();

                try (InputStream inputStream = httpConn.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(destination.toFile())) {

                    long totalBytesRead = 0;
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        printProgressBar(totalBytesRead, totalFileSize);
                    }
                    System.out.println();
                }

                long timeTaken = System.currentTimeMillis() - startTime;
                MDC.put("totalSize", humanReadableByteCount(totalFileSize));
                MDC.put("timeTakenMs", String.valueOf(timeTaken));
                log.info("Download completed successfully.");

            } else {
                MDC.put("responseCode", String.valueOf(responseCode));
                log.error("Server returned non-OK response code.");
            }
        } catch (Exception e) {
            log.error("Download failed due to an exception.", e);
        } finally {
            MDC.clear();
        }
    }

    private static void printProgressBar(long currentBytes, long totalBytes) {
        if (totalBytes <= 0) return;

        int barLength = 40;
        long percentage = (100 * currentBytes) / totalBytes;
        int progress = (int) ((currentBytes * barLength) / totalBytes);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < progress ? "=" : " ");
        }
        bar.append("] ")
           .append(percentage).append("% (")
           .append(humanReadableByteCount(currentBytes)).append(" / ")
           .append(humanReadableByteCount(totalBytes)).append(")");

        System.out.print("\r" + bar);
    }

    /**
     * Converts a byte value into a human-readable string (e.g., 1.2 MB).
     *
     * @param bytes The number of bytes.
     * @return A human-readable string representation.
     */
    public static String humanReadableByteCount(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
