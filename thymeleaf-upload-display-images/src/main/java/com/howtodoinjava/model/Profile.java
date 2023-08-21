package com.howtodoinjava.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String designation;

    @Column
    private String githubLink;

    @Column
    private String twitterLink;

    @Column
    private String email;

    @Lob
    @Column
    private byte[] imageData;

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.imageData);
    }
}
