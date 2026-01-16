package ci553.happyshop.systemSetup;

import ci553.happyshop.systemSetup.FileUtils;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    @Test
    public void testDeleteFiles() throws Exception {
        Path folder = Paths.get("testOrders");
        Files.createDirectories(folder);
        Files.createFile(folder.resolve("temp.txt"));

        FileUtils.deleteFiles(folder);

        assertTrue(Files.exists(folder));
        assertEquals(0, Files.list(folder).count());
    }
}
