package com.example.campusMaster.application.Services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.max-size:10485760}") // 10MB par défaut
    private long maxFileSize;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "pdf", "doc", "docx", "txt", "zip", "rar",
        "jpg", "jpeg", "png", "gif",
        "xls", "xlsx", "ppt", "pptx"
    );

    /**
     * Upload un fichier
     */
    public String uploadFile(MultipartFile file, String subDirectory) {
        validateFile(file);

        try {
            // Créer le répertoire de destination
            Path uploadPath = createUploadDirectory(subDirectory);

            // Générer un nom de fichier unique
            String fileName = generateUniqueFileName(file.getOriginalFilename());

            // Copier le fichier
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Fichier uploadé avec succès: {}", fileName);

            // Retourner le chemin relatif
            return String.format("/uploads/%s/%s", subDirectory, fileName);

        } catch (IOException e) {
            log.error("Erreur lors de l'upload du fichier: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload du fichier: " + e.getMessage());
        }
    }

    /**
     * Supprimer un fichier
     */
    public void deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(uploadDir + fileUrl);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Fichier supprimé: {}", fileUrl);
            } else {
                log.warn("Fichier non trouvé pour suppression: {}", fileUrl);
            }
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier {}: {}", fileUrl, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du fichier");
        }
    }

    /**
     * Valider le fichier
     */
    private void validateFile(MultipartFile file) {
        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        // Vérifier la taille
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException(
                String.format("Le fichier est trop volumineux. Taille max: %d MB", 
                    maxFileSize / (1024 * 1024))
            );
        }

        // Vérifier l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            throw new RuntimeException(
                "Type de fichier non autorisé. Extensions autorisées: " + ALLOWED_EXTENSIONS
            );
        }
    }

    /**
     * Vérifier l'extension du fichier
     */
    private boolean hasValidExtension(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * Extraire l'extension du fichier
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Générer un nom de fichier unique
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s.%s", timestamp, uniqueId, extension);
    }

    /**
     * Créer le répertoire d'upload s'il n'existe pas
     */
    private Path createUploadDirectory(String subDirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Répertoire créé: {}", uploadPath);
        }
        
        return uploadPath;
    }

    /**
     * Obtenir le chemin complet d'un fichier
     */
    public Path getFilePath(String fileUrl) {
        return Paths.get(uploadDir + fileUrl);
    }

    /**
     * Vérifier si un fichier existe
     */
    public boolean fileExists(String fileUrl) {
        return Files.exists(getFilePath(fileUrl));
    }

}
