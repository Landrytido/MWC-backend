package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.FileDto;
import com.mywebcompanion.backendspring.dto.FileUploadResponseDto;
import com.mywebcompanion.backendspring.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Lister tous les fichiers
    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles(Authentication authentication) {
        String clerkId = authentication.getName();
        List<FileDto> files = fileService.getAllFilesByClerkId(clerkId);
        return ResponseEntity.ok(files);
    }

    // Lister les images seulement
    @GetMapping("/images")
    public ResponseEntity<List<FileDto>> getImages(Authentication authentication) {
        String clerkId = authentication.getName();
        List<FileDto> images = fileService.getImagesByClerkId(clerkId);
        return ResponseEntity.ok(images);
    }

    // Upload d'un fichier
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            String clerkId = authentication.getName();
            FileUploadResponseDto response = fileService.uploadFile(clerkId, file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Télécharger un fichier
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String clerkId = authentication.getName();

            // Récupérer les métadonnées du fichier
            FileDto fileDto = fileService.getFileById(clerkId, id);

            // Récupérer le contenu du fichier
            byte[] fileContent = fileService.downloadFile(clerkId, id);

            // Préparer les headers de réponse
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileDto.getContentType()));
            headers.setContentDispositionFormData("attachment", fileDto.getInitialFilename());
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtenir les métadonnées d'un fichier
    @GetMapping("/{id}")
    public ResponseEntity<FileDto> getFileById(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        try {
            FileDto file = fileService.getFileById(clerkId, id);
            return ResponseEntity.ok(file);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un fichier
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String clerkId = authentication.getName();
            fileService.deleteFile(clerkId, id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Statistiques des fichiers
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFileStatistics(Authentication authentication) {
        String clerkId = authentication.getName();
        Map<String, Object> stats = fileService.getFileStatistics(clerkId);
        return ResponseEntity.ok(stats);
    }
}