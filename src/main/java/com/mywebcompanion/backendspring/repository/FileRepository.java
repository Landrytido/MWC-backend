package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<File> findByUserIdAndContentTypeStartingWithOrderByCreatedAtDesc(Long userId, String contentTypePrefix);

    Optional<File> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT SUM(f.fileSize) FROM File f WHERE f.user.id = :userId")
    Long getTotalFileSizeByUserId(@Param("userId") Long userId);

    @Query("SELECT f.contentType, COUNT(f) FROM File f WHERE f.user.id = :userId GROUP BY f.contentType")
    List<Object[]> countFilesByTypeAndUserId(@Param("userId") Long userId);

    Long countByUserId(Long userId);

}