package fraud.util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for file-based persistence (save, load, backup, recovery).
 * Demonstrates: Character streams (BufferedReader/Writer), byte streams (FileInputStream/OutputStream),
 * try-with-resources, exception handling, file I/O.
 */
public final class FileHandler {

    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = "data/backups";

    private FileHandler() {}

    /** Ensure data directories exist. */
    public static void initDirectories() {
        new File(DATA_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
    }

    /**
     * Write lines to a file using character streams (BufferedWriter).
     * Demonstrates: Character streams, try-with-resources, throw/throws.
     */
    public static void writeLines(String filename, List<String> lines) throws IOException {
        initDirectories();
        Path path = Paths.get(DATA_DIR, filename);
        // Using character stream (BufferedWriter)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Read lines from a file using character streams (BufferedReader).
     * Demonstrates: Character streams, try-with-resources, ArrayList.
     */
    public static List<String> readLines(String filename) throws IOException {
        Path path = Paths.get(DATA_DIR, filename);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(path)) return lines;
        // Using character stream (BufferedReader)
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    /**
     * Create a backup using byte streams (FileInputStream/FileOutputStream).
     * Demonstrates: Byte streams, file copy at byte level.
     */
    public static String createBackup(String filename) throws IOException {
        initDirectories();
        Path source = Paths.get(DATA_DIR, filename);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Source file not found: " + source);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = filename.replace(".", "_" + timestamp + ".");
        Path dest = Paths.get(BACKUP_DIR, backupName);

        // Using byte streams for backup (demonstrates byte-level I/O)
        try (FileInputStream fis = new FileInputStream(source.toFile());
             FileOutputStream fos = new FileOutputStream(dest.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        return dest.toString();
    }

    /**
     * Restore from a backup file.
     */
    public static void restoreBackup(String backupFilename, String targetFilename) throws IOException {
        Path source = Paths.get(BACKUP_DIR, backupFilename);
        Path dest = Paths.get(DATA_DIR, targetFilename);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Backup file not found: " + source);
        }
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * List available backup files.
     */
    public static List<String> listBackups() {
        List<String> backups = new ArrayList<>();
        File backupDir = new File(BACKUP_DIR);
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        backups.add(f.getName());
                    }
                }
            }
        }
        return backups;
    }

    /**
     * Check if a data file exists.
     */
    public static boolean dataFileExists(String filename) {
        return Files.exists(Paths.get(DATA_DIR, filename));
    }

    /**
     * Append a single line to a file.
     * Demonstrates: FileWriter with append mode.
     */
    public static void appendLine(String filename, String line) throws IOException {
        initDirectories();
        Path path = Paths.get(DATA_DIR, filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(line);
            writer.newLine();
        }
    }
}
