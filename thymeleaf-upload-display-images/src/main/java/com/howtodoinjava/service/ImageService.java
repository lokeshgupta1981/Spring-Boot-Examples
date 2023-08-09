package com.howtodoinjava.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    void init();

    void save(MultipartFile file);

    List<String> getAll();

    public Resource load(String file);

}
