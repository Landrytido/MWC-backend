package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.FileDto;
import com.mywebcompanion.backendspring.dto.FileUploadResponseDto;
import com.mywebcompanion.backendspring.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<FileDto> files = fileService.getAllFilesByUserEmail(email);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/images")
    public ResponseEntity<List<FileDto>> getImages(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<FileDto> images = fileService.getImagesByUserEmail(email);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {
        String email = userDetails.getUsername();
        FileUploadResponseDto response = fileService.uploadFile(email, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long fileId) throws IOException {
        String email = userDetails.getUsername();
        byte[] fileData = fileService.downloadFile(email, fileId);
        FileDto fileInfo = fileService.getFileById(email, fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getInitialFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, fileInfo.getContentType())
                .body(fileData);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileDto> getFileById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long fileId) {
        String email = userDetails.getUsername();
        FileDto file = fileService.getFileById(email, fileId);
        return ResponseEntity.ok(file);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long fileId) throws IOException {
        String email = userDetails.getUsername();
        fileService.deleteFile(email, fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFileStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Map<String, Object> stats = fileService.getFileStatistics(email);
        return ResponseEntity.ok(stats);
    }
}