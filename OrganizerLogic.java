import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OrganizerLogic {

    private static final Map<String, String> FILE_CATEGORIES = new HashMap<>();

    static {
        FILE_CATEGORIES.put("jpg", "Images");
        FILE_CATEGORIES.put("jpeg", "Images");
        FILE_CATEGORIES.put("png", "Images");
        FILE_CATEGORIES.put("gif", "Images");
        FILE_CATEGORIES.put("bmp", "Images");
        
        FILE_CATEGORIES.put("pdf", "PDFs");
        FILE_CATEGORIES.put("doc", "Documents");
        FILE_CATEGORIES.put("docx", "Documents");
        FILE_CATEGORIES.put("txt", "Documents");
        FILE_CATEGORIES.put("xlsx", "Documents");
        
        FILE_CATEGORIES.put("mp3", "Audio");
        FILE_CATEGORIES.put("wav", "Audio");
        FILE_CATEGORIES.put("flac", "Audio");
        
        FILE_CATEGORIES.put("mp4", "Videos");
        FILE_CATEGORIES.put("mov", "Videos");
        FILE_CATEGORIES.put("avi", "Videos");
        
        FILE_CATEGORIES.put("zip", "Archives");
        FILE_CATEGORIES.put("rar", "Archives");
        
        FILE_CATEGORIES.put("java", "Code");
        FILE_CATEGORIES.put("py", "Code");
        FILE_CATEGORIES.put("html", "Code");
    }
    
    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "misc";
    }

    public static Map<String, Integer> organize(Path targetDir, Consumer<String> logCallback) 
            throws IOException {
        
        logCallback.accept("Starting organization in: " + targetDir.toString());
        Map<String, Integer> summary = new HashMap<>();
        
        try (var stream = Files.list(targetDir)) {
            
            List<Path> filesToProcess = stream
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            for (Path file : filesToProcess) {
                String extension = getFileExtension(file);
                String folderName = FILE_CATEGORIES.getOrDefault(extension, "Others");
                
                Path destDir = targetDir.resolve(folderName);
                
                try {
                    Files.createDirectories(destDir);
                    
                    Path destFile = destDir.resolve(file.getFileName());
                    
                    Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    
                    logCallback.accept("MOVED: " + file.getFileName() + " -> " + folderName);
                    
                    summary.put(folderName, summary.getOrDefault(folderName, 0) + 1);
                    
                } catch (FileAlreadyExistsException e) {
                    logCallback.accept("ERROR: Cannot move " + file.getFileName() + ". Target file already exists.");
                } catch (IOException e) {
                    logCallback.accept("ERROR: Failed to move " + file.getFileName() + ". Reason: " + e.getMessage());
                }
            }
        }
        
        return summary;
    }
}