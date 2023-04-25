package com.springapi.uploading.model;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.Entity;
import lombok.*;

@Builder
@Entity
@Data@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] data;

    public Product(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}