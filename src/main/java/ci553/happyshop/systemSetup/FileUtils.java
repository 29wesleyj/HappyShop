package ci553.happyshop.systemSetup;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class FileUtils {
//De;etes all files inside the folder while keeping the folder itsself
    public static void deleteFiles(Path folder) throws IOException {
        if (!Files.exists(folder)) return;
        Files.walkFileTree(folder, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void copyFolderContents(Path source, Path destination) throws IOException {
        if (!Files.exists(destination)) Files.createDirectories(destination);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.copy(file, destination.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static void createFolders(Path... folders) throws IOException {
        for (Path folder : folders) {
            if (Files.notExists(folder)) Files.createDirectories(folder);
        }
    }
}
