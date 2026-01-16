package ci553.happyshop.systemSetup;

import ci553.happyshop.utility.StorageLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * This class is responsible for seting up the folder structure and orderCounter file required for the order system.
 *
 * ⚠ WARNING:
 *  Running this class will WIPE ALL EXISTING ORDERS by deleting all files inside the orders folder.
 *  It resets the order system to a clean state.
 *
 * It performs the following actions:
 * 1. Deletes all existing files inside the orders folder (but retains the folder structure).
 * 2. Ensures that all required order-related folders exist:
 *    - The main orders folder (`orders/`)
 *    - Subfolders for each order state: `ordered/`, `progressing/`, and `collected/`
 * 3. Creates the orderCounter.txt file inside the 'orders/' folder if it does not already exist, initializing it to "0".
 *   - The `orderCounter.txt`
 *
 * By centralizing file system setup for order storage in this class,
 * any future changes to the order-related directory structure or initialization behavior
 * can be managed in one place, avoiding scattered logic across the codebase.
 */

public class SetOrderFileSystem {

    private static final Lock LOCK = new ReentrantLock();
    private static final Path ORDER_COUNTER_PATH = StorageLocation.orderCounterPath;
    private static final Path[] FOLDERS = {
            StorageLocation.ordersPath,
            StorageLocation.orderedPath,
            StorageLocation.progressingPath,
            StorageLocation.collectedPath
    };

    public static void main(String[] args) {
        try {
            LOCK.lock();
            FileUtils.deleteFiles(FOLDERS[0]);
            FileUtils.createFolders(FOLDERS);
            if (!ORDER_COUNTER_PATH.toFile().exists()) {
                java.nio.file.Files.writeString(ORDER_COUNTER_PATH, "0", java.nio.file.StandardOpenOption.CREATE_NEW);
            }
            System.out.println("Order file system setup completed.");
        } catch (IOException e) {
            System.err.println("Error during order setup: " + e.getMessage());
        } finally {
            LOCK.unlock();
        }
    }
}

