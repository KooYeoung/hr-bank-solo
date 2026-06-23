package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.response.FileDownloadResponse;
import com.kooyeoung.hrbank.entity.FileInfo;
import com.kooyeoung.hrbank.entity.FileType;
import com.kooyeoung.hrbank.exception.CustomInternalServerException;
import com.kooyeoung.hrbank.exception.fileInfo.*;
import com.kooyeoung.hrbank.repository.FileInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class FileInfoService {

    private final FileInfoRepository repository;
    private final Path rootPath;
    private static final Set<String> AVAILABLE_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg");

    public FileInfoService(FileInfoRepository repository, @Value("${hrbank.file-directory}") String uploadDir) {
        this.repository = repository;
        this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new CustomInternalServerException("업로드 디렉토리 생성 실패 : " + rootPath, e);
        }
    }

    public FileDownloadResponse download(Long id) {
        FileInfo fileInfo = getFileInfoById(id);

        if (fileInfo.getType().equals(FileType.PROFILE_IMAGE))
            throw new FileDownloadNotAllowedException();

        Path filePath = Paths.get(fileInfo.getFilePath()).toAbsolutePath().normalize();

        if (!Files.exists(filePath)) throw new CustomInternalServerException("실제 파일을 찾을수 없습니다.");

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) throw new CustomInternalServerException("파일을 읽을수 없습니다.");

            String contentType = fileInfo.getContentType();

            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            return new FileDownloadResponse(
                    resource,
                    fileInfo.getOriginalFileName(),
                    contentType,
                    fileInfo.getSize()
            );
        } catch (MalformedURLException e) {
            throw new CustomInternalServerException("파일 경로가 올바르지 않습니다.", e);
        }

    }

    public FileInfo save(MultipartFile file, FileType type) {
        if (file == null || file.isEmpty()) return null;

        String originalFilename = getCleanFileName(file.getOriginalFilename());

        int dotIndex = originalFilename.lastIndexOf(".");
        String extension = (dotIndex >= 0) ? originalFilename.substring(dotIndex).toLowerCase()
                : "";

        validateFileType(type, extension);

        String uniqueId = UUID.randomUUID().toString();
        String prefixFolder = uniqueId.substring(0, uniqueId.indexOf("-"));
        String storeFilename = uniqueId.replace("-", "") + extension;
        long size = file.getSize();
        String contentType = file.getContentType();

        Path directory = resolveDirectory(type)
                .resolve(prefixFolder);
        Path storePath = directory.resolve(storeFilename);

        try {
            Files.createDirectories(directory);
            file.transferTo(storePath);

            FileInfo fileInfo = new FileInfo(
                    originalFilename
                    , storeFilename
                    , storePath.toString()
                    , contentType
                    , size
                    , type
            );

            return repository.save(fileInfo);
        } catch (IOException e) {
            throw new CustomInternalServerException("파일 저장 중 오류가 발생했습니다.", e);
        }

    }

    @NonNull
    private String getCleanFileName(String filename) {

        if (filename == null || filename.isBlank()) {
            throw new InvalidFileNameException();
        }

        filename = StringUtils.cleanPath(filename);
        return filename;
    }

    private void validateFileType(FileType type, String extension) {
        if (FileType.PROFILE_IMAGE.equals(type)) {
            if (!AVAILABLE_IMAGE_EXTENSIONS.contains(extension)) {
                throw new UnsupportedFileTypeException("이미지 파일 형식이 아닙니다.");
            }
            return;
        }

        if (FileType.BACKUP_EMPLOYEE_CSV.equals(type)) {
            if (!".csv".equals(extension)) {
                throw new UnsupportedFileTypeException("csv 파일 형식이 아닙니다.");
            }
            return;
        }

        if (FileType.BACKUP_ERROR_LOG.equals(type)) {
            if (!".log".equals(extension) && !".txt".equals(extension)) {
                throw new UnsupportedFileTypeException("로그 파일 형식이 아닙니다.");
            }
            return;
        }

        throw new UnsupportedFileTypeException("현재 지원하는 파일 형식이 아닙니다.");
    }

    public void delete(Long id) {
        FileInfo fileInfo = getFileInfoById(id);

        if (!FileType.PROFILE_IMAGE.equals(fileInfo.getType()))
            throw new FileDeleteNotAllowedException();

        Path path = Paths.get(fileInfo.getFilePath());
        try {
            Files.deleteIfExists(path);
            repository.delete(fileInfo);
        } catch (IOException e) {
            throw new CustomInternalServerException("파일 삭제 중 오류가 발생했습니다.", e);
        }

    }

    @NonNull
    private FileInfo getFileInfoById(Long id) {
        return repository.findById(id).orElseThrow(() -> new FileInfoNotFoundException(id));
    }

    public FileInfo saveGeneratedFile(
            String originalFilename,
            String contentType,
            FileType type,
            GeneratedFileWriter fileWriter
    ) {
        originalFilename = getCleanFileName(originalFilename);

        int dotIndex = originalFilename.lastIndexOf(".");
        String extension = (dotIndex >= 0)
                ? originalFilename.substring(dotIndex).toLowerCase()
                : "";

        validateFileType(type, extension);

        String uniqueId = UUID.randomUUID().toString();
        String prefixFolder = uniqueId.substring(0, uniqueId.indexOf("-"));
        String storeFilename = uniqueId.replace("-", "") + extension;

        Path directory = resolveDirectory(type)
                .resolve(prefixFolder);

        Path storePath = directory.resolve(storeFilename);

        try {
            Files.createDirectories(directory);

            fileWriter.write(storePath);

            long size = Files.size(storePath);

            FileInfo fileInfo = new FileInfo(
                    originalFilename,
                    storeFilename,
                    storePath.toString(),
                    contentType,
                    size,
                    type
            );

            return repository.save(fileInfo);

        } catch (IOException e) {
            deleteQuietly(storePath);
            throw new CustomInternalServerException("파일 저장 중 오류가 발생했습니다.", e);
        } catch (RuntimeException e) {
            deleteQuietly(storePath);
            throw e;
        }

    }

    private void deleteQuietly(Path path) {
        try {
            if (path != null) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.warn("파일 정리 중 오류가 발생했습니다. path={}", path, e);
        }
    }

    private Path resolveDirectory(FileType type) {
        LocalDate now = LocalDate.now();

        return type.resolve(rootPath)
                .resolve(String.valueOf(now.getYear()))
                .resolve(String.format("%02d", now.getMonthValue()));
    }
}
