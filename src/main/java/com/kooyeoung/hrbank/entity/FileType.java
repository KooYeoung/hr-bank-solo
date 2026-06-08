package com.kooyeoung.hrbank.entity;

import java.nio.file.Path;

public enum FileType {
    PROFILE_IMAGE("profile-images"),
    BACKUP_EMPLOYEE_CSV("backups", "employee-csv");

    private final String[] directories;

    FileType(String... directories) {
        this.directories = directories;
    }

    public Path resolve(Path rootPath) {
        Path path = rootPath;

        for (String directory : directories) {
            path = path.resolve(directory);
        }

        return path;
    }
}
