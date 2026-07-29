package com.springProjects.onlineStore.file.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import com.springProjects.onlineStore.file.constants.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    private String fileName;

    private String contentType;

    // EnumType.STRING  :  Persist enum type field as string in db
    // FileType.USER_IMAGE will be stored in DB  :  "USER_IMAGE"

    // Another option  :  EnumType.ORDINAL
    // Stores 0-indexed nos. treating enum as array of values
    //      Bad idea, if enum ordering changes later in code
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private Integer parentEntityId;

    // Storing actual file bytes in DB
    // byte[]  :  raw binary content  :  multipartFile.getBytes()
    // LOB  :  Large Object
    // Types of LOB  :  CLOB (Character Large Object)  ,  BLOB (Binary Large Object)

    // MySQL column types (for BLOB)  :
    //     BLOB (64 KB)  ,  MEDIUMBLOB (16 MB)  ,  LONGBLOB (4 GB)
    // MySQL column types (for CLOB)  :
    //     LONGTEXT
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;
}
