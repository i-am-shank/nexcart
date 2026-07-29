package com.springProjects.onlineStore.file.repository;

import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    public List<File> findByParentEntityIdAndFileTypeAndDeletedFalse(Integer parentEntityId, FileType fileType);

    public File findByFileIdAndDeletedFalse(Integer fileId);
}
