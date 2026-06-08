package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}
