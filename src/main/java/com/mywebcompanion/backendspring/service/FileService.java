package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.FileDto;
import com.mywebcompanion.backendspring.dto.FileUploadResponseDto;
import com.mywebcompanion.backendspring.model.File;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final UserService userService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:10485760}") // 10MB par défaut
    private Long maxFileSize;

    public List<FileDto> getAllFilesByClerkId(String clerkId) {
        return fileRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FileDto> getImagesByClerkId(String clerkId) {
        return fileRepository.findByUserClerkIdAndContentTypeStartingWithOrderByCreatedAtDesc(clerkId, "image/")
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FileUploadResponseDto uploadFile(String clerkId, MultipartFile file) throws IOException {
        // Validations
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("Le fichier est trop volumineux (max: " + (maxFileSize / 1024 / 1024) + "MB)");
        }

        User user = userService.findByClerkId(clerkId);

        // Générer un nom unique pour le fichier
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Créer le dossier de destination si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Créer un sous-dossier par utilisateur
        Path userUploadPath = uploadPath.resolve(clerkId);
        if (!Files.exists(userUploadPath)) {
            Files.createDirectories(userUploadPath);
        }

        // Sauvegarder le fichier
        Path destinationPath = userUploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Créer l'entrée en base de données
        File fileEntity = new File();
        fileEntity.setFilename(uniqueFileName);
        fileEntity.setInitialFilename(originalFileName);
        fileEntity.setPath(destinationPath.toString());
        fileEntity.setUri("/api/files/" + fileEntity.getId() + "/download"); // Sera mis à jour après save
        fileEntity.setContentType(file.getContentType());
        fileEntity.setFileSize(file.getSize());
        fileEntity.setUser(user);

        File savedFile = fileRepository.save(fileEntity);

        // Mettre à jour l'URI avec l'ID réel
        savedFile.setUri("/api/files/" + savedFile.getId() + "/download");
        savedFile = fileRepository.save(savedFile);

        // Retourner la réponse
        FileUploadResponseDto response = new FileUploadResponseDto();
        response.setId(savedFile.getId());
        response.setFilename(savedFile.getFilename());
        response.setUri(savedFile.getUri());
        response.setMessage("Fichier uploadé avec succès");

        return response;
    }

    public byte[] downloadFile(String clerkId, Long fileId) throws IOException {
        File file = fileRepository.findByIdAndUserClerkId(fileId, clerkId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        Path filePath = Paths.get(file.getPath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Fichier physique non trouvé sur le serveur");
        }

        return Files.readAllBytes(filePath);
    }

    public FileDto getFileById(String clerkId, Long fileId) {
        File file = fileRepository.findByIdAndUserClerkId(fileId, clerkId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        return convertToDto(file);
    }

    public void deleteFile(String clerkId, Long fileId) throws IOException {
        File file = fileRepository.findByIdAndUserClerkId(fileId, clerkId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        // Supprimer le fichier physique
        Path filePath = Paths.get(file.getPath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Supprimer l'entrée en base
        fileRepository.delete(file);
    }

    public Map<String, Object> getFileStatistics(String clerkId) {
        Long totalSize = fileRepository.getTotalFileSizeByUserClerkId(clerkId);
        List<Object[]> countByType = fileRepository.countFilesByTypeAndUserClerkId(clerkId);

        Map<String, Long> typeStats = countByType.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]));

        return Map.of(
                "totalFiles", fileRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId).size(),
                "totalSizeMB", totalSize / 1024 / 1024,
                "filesByType", typeStats);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    private FileDto convertToDto(File file) {
        FileDto dto = new FileDto();
        dto.setId(file.getId());
        dto.setFilename(file.getFilename());
        dto.setInitialFilename(file.getInitialFilename());
        dto.setUri(file.getUri());
        dto.setContentType(file.getContentType());
        dto.setFileSize(file.getFileSize());
        dto.setCreatedAt(file.getCreatedAt());
        dto.setUpdatedAt(file.getUpdatedAt());
        return dto;
    }
}