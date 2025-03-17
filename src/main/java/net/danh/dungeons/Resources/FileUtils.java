package net.danh.dungeons.Resources;

import net.danh.dungeons.DungeonsMain;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {

    public static boolean deleteFolder(File file) {
        try {
            // Use Files.walk() for better performance and simplicity
            try (Stream<Path> paths = Files.walk(file.toPath())) {
                paths.sorted(Comparator.reverseOrder()) // Sort in reverse order to delete children before parents
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                DungeonsMain.getDungeonCore().getLogger().warning("Failed to delete file: " + path + " - " + e.getMessage());
                            }
                        });
            }
            return true; // If we reach here, deletion was successful
        } catch (IOException e) {
            DungeonsMain.getDungeonCore().getLogger().warning("Failed to delete folder: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteFolderContents(File file) {
        try {
            // Use Files.list() to iterate through the contents
            try (Stream<Path> paths = Files.list(file.toPath())) {
                paths.forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        DungeonsMain.getDungeonCore().getLogger().warning("Failed to delete file: " + path + " - " + e.getMessage());
                    }
                });
            }
            return true;
        } catch (IOException e) {
            DungeonsMain.getDungeonCore().getLogger().warning("Failed to delete folder contents: " + e.getMessage());
            return false;
        }
    }

    public static boolean copyFolder(File source, File target) {
        return copyFolder(source, target, Optional.empty());
    }

    public static boolean copyFolder(File source, File target, Optional<List<String>> excludeFiles) {
        Path sourceDir = source.toPath();
        Path targetDir = target.toPath();

        try {
            // Use Files.walk() for better performance and simplicity
            try (Stream<Path> paths = Files.walk(sourceDir)) {
                paths.forEach(file -> {
                    Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                    try {
                        if (Files.isDirectory(file)) {
                            // Create target directory
                            Files.createDirectories(targetFile);
                        } else {
                            // Skip excluded files
                            if (excludeFiles.isPresent() && excludeFiles.get().contains(file.getFileName().toString())) {
                                return; // Skip this file
                            }
                            // Copy file
                            copyFileUsingStream(file.toFile(), targetFile.toFile());
                        }
                    } catch (IOException e) {
                        DungeonsMain.getDungeonCore().getLogger().warning("Unable to copy file: " + file + " - " + e.getMessage());
                    }
                });
            }
            return true;
        } catch (IOException e) {
            DungeonsMain.getDungeonCore().getLogger().warning("Unable to copy directory: " + e.getMessage());
            return false;
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        // Use try-with-resources for automatic resource management
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[65536]; // Increased buffer size for better performance
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}