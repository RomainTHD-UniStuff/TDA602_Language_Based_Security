package backend;

import java.io.File;
import java.io.RandomAccessFile;

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
    public Pocket() throws Exception {
        this._file = new RandomAccessFile(new File("res/backend/pocket.txt"), "rw");
    }

    /**
     * Adds a product to the pocket.
     * @param product product name to add to the pocket (e.g. "car")
     */
    public void addProduct(String product) throws Exception {
        this._file.seek(this._file.length());
        this._file.writeBytes(product + '\n');
    }

    /**
     * Generates a string representation of the pocket
     * @return a string representing the pocket
     */
    public String getPocket() throws Exception {
        StringBuilder sb = new StringBuilder();
        this._file.seek(0);
        String line;
        while ((line = this._file.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Closes the RandomAccessFile
     */
    public void close() throws Exception {
        this._file.close();
    }
}
