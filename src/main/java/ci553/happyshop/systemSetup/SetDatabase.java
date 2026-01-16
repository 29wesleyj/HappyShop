package ci553.happyshop.systemSetup;

import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SetDatabase {

    private static final String DB_URL = DatabaseRWFactory.dbURL + ";create=true";
    private static final Lock LOCK = new ReentrantLock();

    private static final Path IMAGE_FOLDER = StorageLocation.imageFolderPath;
    private static final Path IMAGE_BACKUP = StorageLocation.imageResetFolderPath;

    private final String[] TABLES = {"ProductTable"};

    public static void main(String[] args) {
        SetDatabase dbSetup = new SetDatabase();
        try {
            LOCK.lock();
            dbSetup.clearTables();
            dbSetup.initializeTable();
            dbSetup.queryTable();
            FileUtils.deleteFiles(IMAGE_FOLDER);
            FileUtils.copyFolderContents(IMAGE_BACKUP, IMAGE_FOLDER);
            System.out.println("Database setup completed successfully.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    private void clearTables() throws SQLException {
        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement()) {
            for (String table : TABLES) {
                try { stmt.executeUpdate("DROP TABLE " + table); }
                catch (SQLException e) { if (!"42Y55".equals(e.getSQLState())) throw e; }
            }
        }
    }

    private void initializeTable() throws SQLException {
        String[] commands = {
                "CREATE TABLE ProductTable(productID CHAR(4) PRIMARY KEY, description VARCHAR(100), unitPrice DOUBLE, image VARCHAR(100), inStock INT CHECK (inStock>=0))",
                "INSERT INTO ProductTable VALUES('0001','40 inch TV',269.00,'0001.jpg',100)",
                "INSERT INTO ProductTable VALUES('0002','DAB Radio',29.99,'0002.jpg',100)",
                "INSERT INTO ProductTable VALUES('0003','Toaster',19.99,'0003.jpg',100)",
                "INSERT INTO ProductTable VALUES('0004','Watch',29.99,'0004.jpg',100)"
        };
        try (Connection con = DriverManager.getConnection(DB_URL)) {
            con.setAutoCommit(false);
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate(commands[0]);
                for (int i = 1; i < commands.length; i++) stmt.addBatch(commands[i]);
                stmt.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void queryTable() throws SQLException {
        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ProductTable")) {
            System.out.println("-------------Product Information-------------");
            while (rs.next()) {
                System.out.printf("%s - %s - %.2f - %d - %s%n",
                        rs.getString("productID"), rs.getString("description"),
                        rs.getDouble("unitPrice"), rs.getInt("inStock"), rs.getString("image"));
            }
        }
    }
}

