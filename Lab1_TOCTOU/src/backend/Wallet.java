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
    public Wallet() throws Exception {
        this._file = new RandomAccessFile(new File("res/backend/wallet.txt"), "rw");
    }

    /**
     * Gets the wallet balance.
     * @return The content of the wallet file as an integer
     */
    public int getBalance() throws IOException {
        this._file.seek(0);
        return Integer.parseInt(this._file.readLine());
    }

    /**
     * Sets a new balance in the wallet
     * @param newBalance new balance to write in the wallet
     */
    public void setBalance(int newBalance) throws Exception {
        this._file.setLength(0);
        String str = Integer.valueOf(newBalance).toString() + '\n';
        this._file.writeBytes(str);
    }

    /**
     * Closes the RandomAccessFile
     */
    public void close() throws Exception {
        this._file.close();
    }
}
