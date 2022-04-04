package backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class Pocket {
    /**
     * The RandomAccessFile of the pocket file
     */
    private final RandomAccessFile _file;

    /**
     * Creates a Pocket object
     *
     * A Pocket object interfaces with the pocket RandomAccessFile.
     */
    public Pocket() {
        try {
            this._file = new RandomAccessFile(
                new File("res/backend/pocket.txt"),
                "rw"
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Adds a product to the pocket.
     * @param product product name to add to the pocket (e.g. "car")
     */
    public void addProduct(String product) throws IOException {
        FileLock lock = this._file.getChannel().lock();
        this._file.seek(this._file.length());
        this._file.writeBytes(product + '\n');
        lock.release();
    }

    /**
     * Generates a string representation of the pocket
     * @return a string representing the pocket
     */
    public String getPocket() {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            this._file.seek(0);
            while ((line = this._file.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return sb.toString();
    }

    /**
     * Closes the RandomAccessFile
     */
    public void close() {
        try {
            this._file.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
