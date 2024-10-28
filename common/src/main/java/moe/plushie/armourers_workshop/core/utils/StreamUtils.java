package moe.plushie.armourers_workshop.core.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public final class StreamUtils {

    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToByteArray(file);
    }

    /**
     * Reads the contents of input stream into a byte array.
     * The input stream is always closed.
     */
    public static byte[] readStreamToByteArray(InputStream inputStream) throws IOException {
        var outputStream = new ByteArrayOutputStream(5 * 1024);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(inputStream);
        return outputStream.toByteArray();
    }

    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     */
    public static String readFileToString(File file, Charset encoding) throws IOException {
        return IOUtils.toString(new FileInputStream(file), encoding);
    }

    public static String readStreamToString(InputStream input, Charset encoding) throws IOException {
        return IOUtils.toString(input, encoding);
    }

    /**
     * Reads all bytes from this input stream and writes the bytes to the given output stream in the order that they are read. On return, this input stream will be at end of stream. This method does not close either stream.
     * This method may block indefinitely reading from the input stream, or writing to the output stream. The behavior for the case where the input and/ or output stream is asynchronously closed, or the thread interrupted during the transfer, is highly input and output stream specific, and therefore not specified.
     * If the total number of bytes transferred is greater than Long. MAX_VALUE, then Long. MAX_VALUE will be returned.
     * If an I/ O error occurs reading from the input stream or writing to the output stream, then it may do so after some bytes have been read or written. Consequently the input stream may not be at end of stream and one, or both, streams may be in an inconsistent state. It is strongly recommended that both streams be promptly closed if an I/ O error occurs.
     */
    public static void transferTo(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        IOUtils.copy(inputStream, outputStream);
    }

    /**
     * Closes a Closeable unconditionally.
     * Equivalent to Closeable. close(), except any exceptions will be ignored.
     * This is typically used in finally blocks to ensure that the closeable is closed even if an Exception was thrown before the normal close statement was reached. It should not be used to replace the close statement(s) which should be present for the non-exceptional case. It is only intended to simplify tidying up where normal processing has already failed and reporting close failure as well is not necessary or useful.
     */
    public static void closeQuietly(Closeable... closeables) {
        IOUtils.closeQuietly(closeables);
    }
}
