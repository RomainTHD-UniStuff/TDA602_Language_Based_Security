package backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Scanner;

public class Wallet {
    private static Scanner scanner = null;

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
            this._file = new RandomAccessFile(
                new File("res/backend/wallet.txt"),
                "rw"
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void delay() {
        System.out.println("Artificial delay (waiting for input to resume...)");
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        scanner.nextLine();
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
    private void setBalance(int newBalance) {
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

    public synchronized boolean safeWithdraw(int valueToWithdraw) throws IOException, InterruptedException {
        FileLock lock;

        while ((lock = _file.getChannel().tryLock()) == null) {
            // We could use `lock()` instead of `tryLock()`, but it is not
            //  really user-friendly as it will block the program.
            System.err.println(
                "Wallet is locked, waiting for it to be unlocked..."
            );
            Thread.sleep(1000);
        }

        int balance = getBalance();

        delay();
        boolean ok = false;

        if (balance >= valueToWithdraw) {
            setBalance(balance - valueToWithdraw);
            ok = true;
        }

        lock.release();
        return ok;
    }
}
