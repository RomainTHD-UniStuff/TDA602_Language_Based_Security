package backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Wallet {
    /**
     * The RandomAccessFile of the wallet file
     */
    private final RandomAccessFile _file;

    /**
     * Creates a Wallet object
     *
     * A Wallet object interfaces with the wallet RandomAccessFile
     */
    public Wallet() {
        try {
            this._file = new RandomAccessFile(new File("res/backend/wallet.txt"), "rw");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets the wallet balance.
     * @return The content of the wallet file as an integer
     */
    public int getBalance() {
        try {
            this._file.seek(0);
            return Integer.parseInt(this._file.readLine());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sets a new balance in the wallet
     * @param newBalance new balance to write in the wallet
     */
    public void setBalance(int newBalance) {
        String str = Integer.valueOf(newBalance).toString() + '\n';
        try {
            this._file.setLength(0);
            this._file.writeBytes(str);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
